package com.csky.iot.lgw.common

data class ApiResult<T>(

        var code: Int,

        var msg: String? = null,

        var result: T? = null
)