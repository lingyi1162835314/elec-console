package com.csky.iot.crack.service.deviceShadow

import com.csky.iot.crack.data.entity.CrackDO
import com.csky.iot.crack.data.entity.OrgDO

class DeviceImageShadow (
        val crackDO: CrackDO,
        val orgDO: OrgDO,
        var length: Int,
        var packetNumber: Int,
        var bytesData: ByteArray
)

val deviceImageShadowMap = mutableMapOf<String, DeviceImageShadow>()