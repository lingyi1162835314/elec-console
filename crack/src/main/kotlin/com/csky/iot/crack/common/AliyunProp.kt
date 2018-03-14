package com.csky.iot.crack.common

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "aliyun")
open class AliyunProp {
    var accessKey: String? = null
    var accessSecret: String? = null
    var endpoint: String? = null
    var queue: String? = null
}
