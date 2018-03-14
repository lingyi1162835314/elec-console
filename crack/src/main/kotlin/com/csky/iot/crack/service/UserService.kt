/*
 * Copyright (C) 2017 C-SKY Microsystems Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.csky.iot.crack.service

import com.alibaba.fastjson.JSONObject
import com.csky.iot.crack.common.LogUtil
import com.csky.iot.crack.common.Md5Utils
import com.csky.iot.crack.common.ResponseCode
import com.csky.iot.crack.common.codeCheck
import com.csky.iot.crack.controller.vo.user.RegisterVO
import com.csky.iot.crack.data.CodeRepository
import com.csky.iot.crack.data.OrgRepository
import com.csky.iot.crack.data.UserRepository
import com.csky.iot.crack.data.entity.CodeDO
import com.csky.iot.crack.data.entity.UserDO
import com.csky.iot.crack.utils.SmsUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

interface UserService {

    fun saveAndSendCode(mobile: String)

    fun registerUser(registerVO: RegisterVO)

    fun login(mobile: String, password: String): JSONObject

    fun updateUser(jsonObject: JSONObject)

    fun resetPassword(mobile: String, password: String)

}

@Service
class UserServiceImpl(val codeRepository: CodeRepository,
                      val userRepository: UserRepository,
                      val orgRepository: OrgRepository
) : UserService {

    @Value("\${door.password_salt}")
    private val passwordSalt: String? = null

    override fun saveAndSendCode(mobile: String) {
        val code = ((1 + Math.random()) * 1000000).toLong().toString()
//        smsUtil.sendValificationCode(mobile, code)
        SmsUtils.sendRegisterMsg(mobile, code)
        val codeDO = codeRepository.findByMobile(mobile)
        if (codeDO != null) {
            codeDO.code = code
            codeRepository.save(codeDO)
        } else {
            codeRepository.save(CodeDO(mobile, code))
        }

        LogUtil.i(javaClass, "[$mobile]code: $code")
    }

    override fun registerUser(registerVO: RegisterVO) {

        codeCheck(userRepository.count() >= 100, ResponseCode.ILLEGAL_USER, "当前用户数已到达100上限，无法创建新用户")

        var repeatUser = userRepository.findByMobile(registerVO.mobile!!)
        codeCheck(repeatUser != null, ResponseCode.ILLEGAL_OPERATION, "手机号码已存在")

        repeatUser = userRepository.findByUserName(registerVO.userName!!)
        codeCheck(repeatUser != null, ResponseCode.ILLEGAL_OPERATION, "用户姓名已存在")

        val md5EncryptPwd = md5ForPassword(registerVO.userPwd!!)
        val userDO = UserDO(
                registerVO.userName!!,
                registerVO.mobile!!,
                md5EncryptPwd,
                null
        )

        userRepository.save(userDO)
        val code = codeRepository.findByMobile(registerVO.mobile!!)
        codeRepository.delete(code)
    }

    override fun login(mobile: String, password: String): JSONObject {
        val pwd = md5ForPassword(password)
        val userDO = userRepository.findByMobileAndUserPwd(mobile, pwd)

        codeCheck(userDO == null, ResponseCode.PARAM_ERROR, "用户名或密码错误")

        val token = UUID.randomUUID().toString().replace("-", "")
        userDO!!.userToken = token
        userDO.modifiedTime = Date()
        userRepository.save(userDO)

        val responseJson = JSONObject()
        responseJson["userId"] = userDO.id
        responseJson["userName"] = userDO.userName
        responseJson["userToken"] = token

        return responseJson
    }

    override fun updateUser(jsonObject: JSONObject) {
        val id = jsonObject.getLong("userId")
        val user = userRepository.findOne(id)

        codeCheck(user == null, ResponseCode.PARAM_ERROR, "userId 错误")

        jsonObject["name"]?.let {
            val repeatUser = userRepository.findByUserName(it as String)
            codeCheck(repeatUser != null && repeatUser.id != id, ResponseCode.PARAM_ERROR, "用户姓名已存在")

            user.userName = it
        }
        jsonObject["mobile"]?.let {
            val repeatUser = userRepository.findByMobile(it as String)
            codeCheck(repeatUser != null && repeatUser.id != id, ResponseCode.PARAM_ERROR, "手机号码已存在")

            user.mobile = it
        }
        user.modifiedTime = Date()

        userRepository.save(user)
    }

    override fun resetPassword(mobile: String, password: String) {
        val user = userRepository.findByMobile(mobile)
        codeCheck(user == null, ResponseCode.ILLEGAL_OPERATION, "手机号码未注册")

        user!!.userPwd = md5ForPassword(password)
        user.modifiedTime = Date()

        userRepository.save(user)
        val code = codeRepository.findByMobile(mobile)
        codeRepository.delete(code)
    }

    private fun md5ForPassword(password: String) = Md5Utils.getMD5Code("$password$passwordSalt")

}