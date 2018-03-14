package com.csky.iot.crack.service.deviceShadow

enum class DeviceStatusEnum(var value: Int) {
    OFFLINE(0),
    ONLINE(1),
    UPGRADE(2),
    TRANSPARENT(3)
}

fun getDeviceStatusCode(status: String): Int {
    return when(status) {
        "OFFLINE" ->  0
        "ONLINE" ->  1
        else ->  -1
    }
}