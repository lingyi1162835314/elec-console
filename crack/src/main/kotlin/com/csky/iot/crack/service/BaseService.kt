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

import com.csky.iot.crack.common.ResponseCode
import com.csky.iot.crack.common.codeCheck
import com.csky.iot.crack.data.CodeRepository
import com.csky.iot.crack.data.UserRepository
import org.springframework.stereotype.Service
import java.util.*

interface BaseService {

    fun checkCode(mobile: String, code: String): Boolean

    fun checkToken(userId: Long, token: String): Boolean
}

@Service
class BaseServiceImpl(val codeRepository: CodeRepository,
                      val userRepository: UserRepository): BaseService{

    override fun checkCode(mobile: String, code: String): Boolean {
        val codeDO = codeRepository.findByMobile(mobile)

        codeCheck(codeDO == null, ResponseCode.ILLEGAL_USER, "请先获取校验码")

        val createCodeTime = codeDO!!.createTime.time / 1000
        val currentTime = Date().time / 1000

        if (codeDO.code != code) return false

        //大于5min，验证码失效
        if ((currentTime - createCodeTime) > 300) {
            codeRepository.delete(codeDO)

            codeCheck(false, ResponseCode.PARAM_TIMEOUT, "验证码已过期，请重新申请")
        }

        //验证成功，删除验证码信息
        //改到注册成功后删除
//        codeRepository.delete(codeDO)

        return true
    }

    override fun checkToken(userId: Long, token: String): Boolean {
        val userDO = userRepository.findOne(userId)

        codeCheck(userDO == null, ResponseCode.ILLEGAL_USER, "请先注册")

        if (token != userDO.userToken) return false

        return true
    }
}