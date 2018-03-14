package com.csky.iot.crack.service.deviceShadow

enum class DeviceMsgTypeEnum(var value: String) {
    ONLINE("online"),
    OFFLINE("offline"),
    TIMESYNC("timesync"),
    ACK("ack"),
    UPGRADE("upgrade"),
    UPGRADED("upgraded"),
    UPLOAD("upload"),
    SETPARAM("setparam")
}