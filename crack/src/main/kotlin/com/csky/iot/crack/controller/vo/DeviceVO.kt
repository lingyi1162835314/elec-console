package com.csky.iot.crack.controller.vo

data class DeviceVO(

        var name: String,

        var productKey: String? = "",

        var deviceName: String? = "",

        var cid: String? = "",

        var status: String? = "",

        var model: String? = "",

        var version: String? = "",

        var enable: Boolean = false,

        var id: Long
)