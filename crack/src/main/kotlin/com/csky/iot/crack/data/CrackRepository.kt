package com.csky.iot.crack.data

import com.csky.iot.crack.data.entity.CrackDO
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface CrackRepository: CrudRepository<CrackDO, Long> {
    fun findByOrgIdOrderByCreateTimeDesc(orgId: Long): List<CrackDO>?

    fun findByCreateTimeLessThan(createTime: Date): List<CrackDO>?

    @Query("select v from CrackDO v where v.orgId = ?1 and v.modifiedTime > ?2")
    fun findByModifiedTimeRange(orgId: Long, offlineBoundary: Date): List<CrackDO>?
}