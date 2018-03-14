package com.csky.iot.lgw.common


enum class ResponseCode private constructor(
        val code: Int,
        val msg: String
){
    SUCCESS(0,"成功"),

    ILLEGAL_OPERATION(-1,"非法操作"),

    ILLEGAL_USER(-2,"非法用户"),

    PARAM_ERROR(-3,"参数错误"),

    PARAM_LACK(-4,"参数缺失"),

    PARAM_BEYONG_RANGE(-5,"参数超出范围"),

    REPEAT_INSERT(-6,"重复添加"),

    SERVICE_UNAVAILABLE(-7,"服务不可用"),

    PARAM_TIMEOUT(-8,"请求超时"),

    NO_DATA_FOR_MODIFY(1,"修改不存在的数据"),

    NO_DATA_FOR_DELETE(2,"删除不存在的数据"),

    USER_UNACTIVE(-9,"用户未激活"),

    USER_FREEZE(-10,"用户已冻结"),

    USER_DELETE(-11,"用户已删除"),

    UPDATE_DEVICE_STATUS(-12,"请求超时同时更新设备状态"),

}