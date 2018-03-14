package com.csky.iot.crack.controller

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.csky.iot.crack.common.*
import com.csky.iot.crack.controller.vo.user.LoginVO
import com.csky.iot.crack.controller.vo.user.RegisterVO
import com.csky.iot.crack.controller.vo.user.ResetPasswordVO
import com.csky.iot.crack.exception.CskyException
import com.csky.iot.crack.service.BaseService
import com.csky.iot.crack.service.UserService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserController(val baseService: BaseService,
                     val userService: UserService
): BaseController() {

    @PostMapping("/verification")
    fun getVerificationCode(@RequestBody params: JSONObject): ApiResult<JSONObject> {
        LogUtil.i(javaClass, "get verification code start:${params.toJSONString()}")

        val mobile = params.getString("mobile")

        try {
            mobileCheck(mobile)

            userService.saveAndSendCode(mobile)

        } catch (e: CskyException) {
            LogUtil.e(javaClass, "[$mobile]${e.code}:${e.message}")

            return ApiResult(e.code, e.message)
        }

        LogUtil.i(javaClass, "[$mobile]get verification code ends")

        return ApiResult(
                ResponseCode.SUCCESS.code,
                "验证码发送成功"
        )
    }

    // 注册用户
    @PostMapping("/register")
    fun addUser(@Validated @RequestBody registerVO: RegisterVO): ApiResult<JSONObject> {
        LogUtil.i(javaClass, "add user start:${JSON.toJSONString(registerVO)}")

        val mobile = registerVO.mobile!!
        val code = registerVO.verification!!

        try {
            mobileCheck(mobile)

            codeCheck(!baseService.checkCode(mobile, code), ResponseCode.ILLEGAL_OPERATION, "验证码错误")

            userService.registerUser(registerVO)

        } catch (e: CskyException) {
            LogUtil.e(javaClass, "[${registerVO.mobile}]${e.code}:${e.message}")

            return ApiResult(e.code, e.message)
        }

        LogUtil.i(javaClass, "[${registerVO.mobile}]add user start ends")

        return ApiResult(
                ResponseCode.SUCCESS.code,
                "注册成功")
    }

    @PostMapping("/login")
    fun login(@Validated @RequestBody loginVO: LoginVO): ApiResult<JSONObject> {
        LogUtil.i(javaClass, "login start:${JSON.toJSONString(loginVO)}")

        val mobile = loginVO.mobile!!
        val password = loginVO.userPwd!!
        val responseJson: JSONObject

        try {
            mobileCheck(mobile)

            responseJson = userService.login(mobile, password)

        } catch (e: CskyException) {
            LogUtil.e(javaClass, "[$mobile]${e.code}:${e.message}")

            return ApiResult(e.code, e.message)
        }

        LogUtil.i(javaClass, "[${loginVO.mobile}]login ends: $responseJson")

        return ApiResult(
                ResponseCode.SUCCESS.code,
                "登录成功",
                responseJson
        )
    }

    // 更新用户
    @PostMapping("/update")
    fun updateUser(@Validated @RequestBody userInfo: JSONObject): ApiResult<JSONObject> {
        LogUtil.i(javaClass, "update user start:${userInfo.toJSONString()}")

        val userId = userInfo.getLong("userId")

        try {
            codeCheck(!baseService.checkToken(userId, getHeaderToken()), ResponseCode.ILLEGAL_USER, "用户权限错误")

            userService.updateUser(userInfo)

        } catch (e: CskyException) {
            LogUtil.e(javaClass, "[$userId]${e.code}:${e.message}")

            return ApiResult(e.code, e.message)
        }

        LogUtil.i(javaClass, "[$userId]update ends")
        return ApiResult(
                ResponseCode.SUCCESS.code,
                "修改成功"
        )
    }

    //忘记密码
    @PostMapping("/password")
    fun resetPassword(@Validated @RequestBody resetPasswordVO: ResetPasswordVO): ApiResult<JSONObject> {
        LogUtil.i(javaClass, "reset password start:${JSON.toJSONString(resetPasswordVO)}")

        val mobile = resetPasswordVO.mobile!!
        val password = resetPasswordVO.userPwd!!
        val code = resetPasswordVO.verification!!

        try {
            mobileCheck(mobile)

            codeCheck(!baseService.checkCode(mobile, code), ResponseCode.ILLEGAL_OPERATION, "验证码错误")

            userService.resetPassword(mobile, password)

        } catch (e: CskyException) {
            LogUtil.e(javaClass, "[$mobile]${e.code}:${e.message}")

            return ApiResult(e.code, e.message)
        }

        LogUtil.i(javaClass, "[${resetPasswordVO.mobile}]reset password ends")
        return ApiResult(ResponseCode.SUCCESS.code, "修改成功")
    }
}