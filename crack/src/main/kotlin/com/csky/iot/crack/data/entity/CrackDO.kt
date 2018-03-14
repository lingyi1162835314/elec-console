package com.csky.iot.crack.data.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "b_crack_info")
data class CrackDO (
        @Column(name = "org_id")
        var orgId: Long = 0,

        var temperature: String? = "",

        var width: Double? = 0.0,

        @Column(name = "oss_url")
        var ossUrl: String? = "",

        @Column(name = "gmt_create")
        var createTime: Date = Date(),

        @Column(name = "gmt_modified")
        var modifiedTime: Date? = Date(),

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = 0
)