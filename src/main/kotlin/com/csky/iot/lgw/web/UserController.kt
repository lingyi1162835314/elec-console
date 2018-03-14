package com.csky.iot.lgw.web

import com.alibaba.fastjson.JSONObject
import com.csky.iot.lgw.common.ApiResult
import com.csky.iot.lgw.common.LogUtil
import com.csky.iot.lgw.common.ResponseCode
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserController {

    @PostMapping("/test")
    fun test(@RequestBody params: JSONObject): ApiResult<JSONObject> {
        LogUtil.i(javaClass, "test")

        return ApiResult(
                ResponseCode.SUCCESS.code,
                "OKOKOKOKS"
        )
    }
}