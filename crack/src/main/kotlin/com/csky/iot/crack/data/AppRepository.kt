package com.csky.iot.crack.data

import com.csky.iot.crack.data.entity.AppDO
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface AppRepository : CrudRepository<AppDO, Long> {
    @Query(value = "select * from b_app_info v order by id desc limit 1", nativeQuery = true)
    fun getMaxVersionId(): AppDO?
}