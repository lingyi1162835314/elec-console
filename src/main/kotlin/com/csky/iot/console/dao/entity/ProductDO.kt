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

package com.csky.iot.data.entity

import java.util.Date
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "base_product_info")
data class ProductDO (
        var name: String = "",

        var model: String = "",

        var vendorId: Long = 0,

        var chipId: Long = 0,

        var description: String? = "",

        var signatureId: Long = 0,

        var digestId: Long = 0,

        var primaryImageManifestAddress: Long? = null,

        var recoveryImageManifestAddress: Long? = null,

        var lowPowerContextAddress: Long? = null,

        var socConfig: Long? = null,

        var flashSection: Int? = null,

        var trustBootKeySn: String? = null,

        var jtagKeySn: String? = null,

        var diffMode: Int? = null,

        var iotType: Int = 1,

        @Column(name = "gmt_create")
        val createTime: Date = Date(),

        @Column(name = "gmt_modified")
        var modifiedTime: Date? = null,

        @Id
        var id: Long = 0
)
