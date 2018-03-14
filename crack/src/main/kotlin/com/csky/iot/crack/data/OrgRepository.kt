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

package com.csky.iot.crack.data

import com.csky.iot.crack.data.entity.OrgDO
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface OrgRepository : CrudRepository<OrgDO, Long> {
    //根目录parentId = -1
    fun findByUserIdAndParentId(userId: Long, parentId: Long = -1): List<OrgDO>?

    fun findByUserIdAndCid(userId: Long, cid: String): OrgDO?

    fun findByParentIdAndName(parentId: Long, name: String): OrgDO?

    fun countByNameAndTypeAndUserId(name: String, type: Boolean, userId: Long): Int

    @Query("select v from OrgDO v where v.userId = ?1 and v.type = ?2 and v.power <= ?3")
    fun findByPowerRange(userId: Long, type: Boolean, power: Int = 100): List<OrgDO>?

    @Query("select v from OrgDO v where v.userId = ?1 and v.type = ?2 and v.status = ?3")
    fun findByStatus(userId: Long, type: Boolean, status: Int): List<OrgDO>?

    fun findByTypeAndStatus(type: Boolean, status: Int): List<OrgDO>?

    fun findByCid(cid: String): OrgDO?

    fun findByAliyunDeviceName(aliyunDeviceName: String): OrgDO?
}