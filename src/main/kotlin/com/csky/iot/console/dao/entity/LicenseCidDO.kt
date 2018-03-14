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

package com.csky.iot.console.dao.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "factory_license_cid_info")
data class LicenseCidDO(
        val cid: String? = null,

        val licenseId: Long? = null,

        val keySn: String? = "",

        @Column(name = "is_used")
        var used: Boolean? = null,

        @Column(name = "gmt_create")
        val createTime: Date? = Date(),

        @Column(name = "gmt_modified")
        val modifiedTime: Date? = null,

        var tagId: Long? = 0,

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long? = 0
)