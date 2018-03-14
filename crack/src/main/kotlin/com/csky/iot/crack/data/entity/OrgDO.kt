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

package com.csky.iot.crack.data.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "b_org_info")
data class OrgDO(
        @Column(name = "userId")
        var userId: Long = -1,

        @Column(name = "parent_id")
        var parentId: Long = -1,

        var type: Boolean = false, //false: 文件夹， true：设备

        var name: String = "",

        var cid: String? = "",

        var ip: String? = "",

        var port: String? = "",

        @Column(name = "duration_time")
        var durationTime: Long? = 3000,

        @Column(name = "crack_x")
        var crackX: Int? = null,

        @Column(name = "crack_y")
        var crackY: Int? = null,

        var enable: Boolean? = false,

        var pix: Int? = 0,

        @Column(name = "device_address")
        var deviceAddress: String? = "",

        var status: Int? = 0, //0：OFFLINE; 1: ONLINE; 2: UPGRADE

        var power: Int? = 0,

        var version: String? = "",

        var model: String? = "",

        @Column(name = "aliyun_product_key")
        var aliyunProductKey: String? = "",

        @Column(name = "aliyun_device_name")
        var aliyunDeviceName: String? = "",

        var lastSessionTime: Date? = null,

        @Column(name = "gmt_create")
        var createTime: Date = Date(),

        @Column(name = "gmt_modified")
        var modifiedTime: Date? = Date(),

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = 0
)