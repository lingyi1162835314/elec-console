package com.csky.iot.crack.data.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "b_app_info")
data  class AppDO(
        var version: String? = "",

        var versionCode: String? = "",

        var downloadUrl: String? = "",

        var updateType: Int? = 0,

        var message: String? = "",

        var isCurrent: Boolean? = true,

        @Column(name = "gmt_create")
        var createTime: Date = Date(),

        @Column(name = "gmt_modified")
        var modifiedTime: Date? = Date(),

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = 0
)