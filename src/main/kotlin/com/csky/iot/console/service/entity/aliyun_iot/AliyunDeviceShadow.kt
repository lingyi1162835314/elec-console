/*
 * Copyright (C) 2017 C-SKY Microsystems Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.csky.iot.console.service.entity.aliyun_iot

import java.io.Serializable

data class AliyunDeviceShadow(
        var cid: String,
        var accessKey: Pair<String, String>,
        var productKey: String,
        var deviceName: String,
        var deviceSecret: String,
        var sessionKey: ByteArray,
        var endPoint: String,
        var status: Boolean = false
): Serializable {

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is AliyunDeviceShadow) return false

        return cid == other.cid && accessKey == other.accessKey && productKey == other.productKey
                && deviceName == other.deviceName && deviceSecret == other.deviceSecret
                && sessionKey.contentEquals(other.sessionKey) && endPoint == other.endPoint && status == other.status
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}
