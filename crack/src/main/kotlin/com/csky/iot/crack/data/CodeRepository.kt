package com.csky.iot.crack.data

import com.csky.iot.crack.data.entity.CodeDO
import org.springframework.data.repository.CrudRepository
import java.util.*

interface CodeRepository: CrudRepository<CodeDO, Long> {

    fun findByMobile(mobile: String): CodeDO?

    fun findByCreateTimeLessThan(createTime: Date): List<CodeDO>?
}