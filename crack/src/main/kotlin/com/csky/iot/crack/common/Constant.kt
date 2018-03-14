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
package com.csky.iot.crack.common


object Constant {

    // 系统异常
    val DEVICE_UNKNOW: String = "UNKNOW"

    // 成功
    val DEVICE_SUCCESS: String = "SUCCESS"

    // 设备响应超时
    val DEVICE_TIMEOUT: String = "TIMEOUT"

    // 设备离线
    val DEVICE_OFFLINE: String = "OFFLINE"

    // 设备离线(设备连接断开但是断开时间未超过一个心跳周期)
    val DEVICE_HALFCONN: String = "HALFCONN"

    // 失败
    val DEVICE_FAILED: String = "FAILED"

    // online
    val DEVICE_STATUS_ONLINE: String = "online"

    // offline
    val DEVICE_STATUS_OFFLINE: String = "offline"

    // unactive
    val DEVICE_STATUS_UNACTIVE: String = "unactive"

    // upgrade
    val DEVICE_STATUS_UPGRADE: String = "upgrade"

    val ROLE_ADMIN: Int = 1

    val ROLE_USER: Int = 2

    val USER_STATUS_VALID: Int = 1
    val USER_STATUS_FREEZE: Int = 3

    val CMD_OPEN: Int = 1
    val CMD_CLOSE: Int = 2
    val CMD_STOP: Int = 3
}