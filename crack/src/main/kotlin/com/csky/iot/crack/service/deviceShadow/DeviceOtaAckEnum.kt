package com.csky.iot.crack.service.deviceShadow

enum class DeviceOtaAckEnum(var value: String) {
    SUCCESS("OTA success"),
    FAILED("OTA failed"),
    ERROR("OTA no version")
}