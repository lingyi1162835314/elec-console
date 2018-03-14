package com.csky.iot.crack.data.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "b_code_info")
data class CodeDO (

    var mobile: String = "",

    var code: String = "",

    @Column(name = "gmt_create")
    var createTime: Date = Date(),

    @Column(name = "gmt_modified")
    var modifiedTime: Date? = Date(),

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

)