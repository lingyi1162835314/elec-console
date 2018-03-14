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

package com.csky.iot.web

import com.csky.iot.console.service.entity.aliyun_iot.AliyunDeviceShadow
import com.csky.iot.console.service.entity.onenet.OneNetDeviceShadow

object TestData {
    val TEST_CID = "c97d0fe3044000004231b07c2ebae1f6"
    val TEST_PRODUCT_KEY = "Gr7jIhThNdm"
    val TEST_DEVICE_NAME = "q0rqdsfSeDDf4WEuz67p"
    val TEST_DEVICE_SECRET = "QBrJTPmmYTnPxO6UPJsmBlFQxoAR5Iax"
    val TEST_ACCESS_ID = "LTAIkz2Iw6Dj5ZEP"
    val TEST_ACCESS_SECRET = "dpMqG6Y1zmCUwvnIB9mHMbLY61c7ZK"
    val TEST_MNS_ENDPOINT = "https://1055699048151813.mns.cn-shanghai.aliyuncs.com/"
    val TEST_UPLOAD_MESSAGE = "{\"payload\":\"eyJ0eXBlIjo0LCJ2YWwiOiIxNTUiLCJ0aW1lIjoiMjAwMTEwMjQxODQzNDAifQ==\"," +
            "\"messagetype\":\"upload\",\"messageid\":1,\"topic\":\"/6DELh5rje3y/GxHbnPjD7hZIinwMWvMG/e9076161044000" +
            "008be3e9b7ccf3704d/update\",\"timestamp\":1505206807}"
    val TEST_STATUS_MESSAGE = "{\"payload\":\"eyJsYXN0VGltZSI6IjIwMTctMDktMTIgMTc6MDI6MjUuMTA4IiwidGltZSI6IjIwMTctMD" +
            "ktMTIgMTc6MDQ6MzQuNjUzIiwicHJvZHVjdEtleSI6IjZERUxoNXJqZTN5IiwiZGV2aWNlTmFtZSI6Ikd4SGJuUGpEN2haSWlud01Xd" +
            "k1HIiwic3RhdHVzIjoib2ZmbGluZSJ9\",\"messagetype\":\"status\",\"timestamp\":1505207074}"
    val TEST_ACCESS_KEY = Pair(TEST_ACCESS_ID, TEST_ACCESS_SECRET)
    val TEST_ALIYUN_DEVICE_SHADOW = AliyunDeviceShadow(TEST_CID, TEST_ACCESS_KEY, TEST_PRODUCT_KEY, TEST_DEVICE_NAME,
            TEST_DEVICE_SECRET, byteArrayOf(), TEST_MNS_ENDPOINT,false)
    val TEST_ONENET_DEVICE_SHADOW = OneNetDeviceShadow(TEST_CID, "apiKey", "imei", "title")
}