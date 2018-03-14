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

package com.csky.iot.crack.service

import com.alibaba.fastjson.JSONObject
import com.csky.iot.crack.common.LogUtil
import com.csky.iot.crack.common.ResponseCode
import com.csky.iot.crack.common.codeCheck
import com.csky.iot.crack.controller.vo.org.OrgVO
import com.csky.iot.crack.data.CrackRepository
import com.csky.iot.crack.data.OrgRepository
import com.csky.iot.crack.data.entity.OrgDO
import com.csky.iot.crack.manager.AliIotManager
import com.csky.iot.crack.manager.CskyManager
import com.csky.iot.crack.service.deviceShadow.DeviceStatusEnum
import org.springframework.stereotype.Service
import java.util.*

interface OrgService {

    fun addOrg(orgVO: OrgVO)

    fun getOrgList(userId: Long, parentOrgId: Long): List<JSONObject>?

    fun updateOrg(orgVO: OrgVO)

    fun getOfflineDevices(userId: Long): List<JSONObject>?

    fun getLowPowerDevices(userId: Long): List<JSONObject>?

    //强制删除文件
    fun forceDeleteOrg(userId: Long, orgId: Long)

    //只允许删除无子文件的文件
    fun deleteOrg(userId: Long, orgId: Long)

    fun getCrackDetails(userId: Long, orgId: Long): List<JSONObject>?
}

@Service
class OrgServiceImpl(val orgRepository: OrgRepository,
                     val crackRepository: CrackRepository,
                     val deviceService: DeviceService,
                     val aliIotManager: AliIotManager,
                     val cskyManager: CskyManager) : OrgService {
    private val LOW_POWER = 20

    override fun addOrg(orgVO: OrgVO) {
        val userId = orgVO.userId!!
        val parentId = orgVO.parentOrgId!!
        val name = orgVO.name!!
        val type = orgVO.type!!

        if (parentId != -1L) { //非根目录添加，校验父目录是否存在
            val doesParentOrgIdExist = orgRepository.exists(parentId)

            codeCheck(!doesParentOrgIdExist, ResponseCode.ILLEGAL_OPERATION,
                    "[@$parentId]文件不存在")

            val parentOrgDO = orgRepository.findOne(parentId)
            codeCheck(parentOrgDO.type, ResponseCode.ILLEGAL_OPERATION,
                    "[@$parentId]不允许在设备下添加文件")

            codeCheck(parentOrgDO.userId != userId, ResponseCode.ILLEGAL_OPERATION,
                    "[$userId@$parentId]用户没有操作该文件的权限")
        }

        //校验同目录下文件名是否重复
        val doesOrgNameExist = orgRepository.findByParentIdAndName(parentId, name)
        LogUtil.e(javaClass, "[$name@$parentId]文件夹或设备名已存在")
        codeCheck(doesOrgNameExist != null, ResponseCode.ILLEGAL_OPERATION, "文件夹或设备名已存在")

        //如果添加设备，判断该用户cid是否已经存在，cid:user=1:n
        if (type) {
            val cid = orgVO.cid!!
            val doesCidExist = orgRepository.findByCid(cid)

            LogUtil.e(javaClass, "[$cid]该设备已存在")
            codeCheck(doesCidExist != null, ResponseCode.PARAM_ERROR, "该设备已存在")
            val existedName = orgRepository.countByNameAndTypeAndUserId(name, true, orgVO.userId!!) > 0
            codeCheck(existedName, ResponseCode.ILLEGAL_OPERATION, "设备名不允许重复")

        }

        val newOrgDO = OrgDO(
                userId,
                parentId,
                type,
                name,
                orgVO.cid,
                orgVO.ip,
                orgVO.port,
                orgVO.durationTime,
                orgVO.crackX,
                orgVO.crackY,
                true,
                orgVO.pix,
                orgVO.deviceAddress
        )
        orgRepository.save(newOrgDO)
    }

    override fun getOrgList(userId: Long, parentOrgId: Long): List<JSONObject>? {
        val orgList = orgRepository.findByUserIdAndParentId(userId, parentOrgId)

        val updatedDeviceList = arrayListOf<OrgDO>()
        val jsonList = arrayListOf<JSONObject>()

        orgList?.map {
            if (it.type && it.durationTime != null) {
                val responseJson = JSONObject.toJSON(it) as JSONObject

//                var aliyunStatus = 0
//                //获取阿里云设备上下线状态
//                if (!it.aliyunProductKey.isNullOrBlank() && !it.aliyunDeviceName.isNullOrBlank()) {
//                    val status = aliIotManager.getDevicesStatus(it.aliyunProductKey!!, arrayListOf(it.aliyunDeviceName!!))
//                    aliyunStatus = getDeviceStatusCode(status[0].status)
////                    if (statusCode != it.status) {
////                        it.status = statusCode
////                        updatedDeviceList.add(it)
////
////                        responseJson["status"] = statusCode
////                    }
//                }

                //检查设备在durationTime内是否有数据上传，判断设备上下线
                val offlineBoundary = Date().time - it.durationTime!! * 1000
                if (it.lastSessionTime == null || it.lastSessionTime!!.time < offlineBoundary) {
                    //设备下线
                    if (it.status != DeviceStatusEnum.OFFLINE.value) {
                        it.status = DeviceStatusEnum.OFFLINE.value
                        responseJson["status"] = it.status

                        it.modifiedTime = Date()
                        updatedDeviceList.add(it)
                    }
                } else if (it.status != DeviceStatusEnum.ONLINE.value) { //设备上线
                    it.status = DeviceStatusEnum.ONLINE.value
                    responseJson["status"] = it.status

                    it.modifiedTime = Date()
                    updatedDeviceList.add(it)
                }

//                val doesOnline = crackRepository.findByModifiedTimeRange(it.id, Date(offlineBoundary))
//
//                if (doesOnline!!.isEmpty() && aliyunStatus == DeviceStatusEnum.OFFLINE.value) {
//                    //设备下线
//                    if (it.status != DeviceStatusEnum.OFFLINE.value) {
//                        it.status = DeviceStatusEnum.OFFLINE.value
//                        responseJson["status"] = it.status
//
//                        it.modifiedTime = Date()
//                        updatedDeviceList.add(it)
//                    }
//                } else {
//                    //设备在线
//                    if (it.status != DeviceStatusEnum.ONLINE.value) {
//                        it.status = DeviceStatusEnum.ONLINE.value
//                        responseJson["status"] = it.status
//
//                        it.modifiedTime = Date()
//                        updatedDeviceList.add(it)
//                    }
//                }

                //检查设备是否有新版本
                if (!it.model.isNullOrBlank() && !it.version.isNullOrBlank() && !it.cid.isNullOrBlank()) {
                    val newVersion = cskyManager.checkNewVersion(it.model!!, it.version!!, it.cid!!)
                    newVersion?.let { responseJson["newVersion"] = newVersion }
                }

                responseJson.remove("userId")
                responseJson.remove("id")
                responseJson.remove("parent_id")
                responseJson.remove("createTime")
                responseJson.remove("modifiedTime")
                responseJson.remove("crack_x")
                responseJson.remove("crack_y")

                responseJson["orgId"] = it.id
                responseJson["crackX"] = it.crackX
                responseJson["crackY"] = it.crackY
                responseJson["parentId"] = it.parentId

                jsonList.add(responseJson)
            } else {
                val responseJson = JSONObject()

                responseJson["orgId"] = it.id
                responseJson["parentId"] = it.parentId
                responseJson["name"] = it.name
                responseJson["type"] = it.type

                jsonList.add(responseJson)
            }
        }

        //保存更新状态后的设备
        if (updatedDeviceList.isNotEmpty()) {
            orgRepository.save(updatedDeviceList)
        }

        return jsonList
    }

    override fun updateOrg(orgVO: OrgVO) {
        val userId = orgVO.userId!!
        val orgId = orgVO.orgId!!

        val doesOrgDOExist = orgRepository.exists(orgId)
        codeCheck(!doesOrgDOExist, ResponseCode.ILLEGAL_OPERATION, "[$orgId]文件不存在")

        val orgDO = orgRepository.findOne(orgId)

        codeCheck(orgDO.userId != userId, ResponseCode.ILLEGAL_OPERATION,
                "[$userId]用户没有操作该文件的权限")

        val newName = orgVO.name
        val parentId = orgDO.parentId

        if (newName != orgDO.name) {
            val checkNameOrgDO = orgRepository.findByParentIdAndName(parentId, newName!!)

            LogUtil.e(javaClass, "[$newName@$parentId]文件名已存在")
            codeCheck(checkNameOrgDO != null,
                    ResponseCode.ILLEGAL_OPERATION, "文件名已存在")

            orgDO.name = newName
        }

        if (orgDO.type) {
            if (orgVO.ip != null && orgVO.ip != orgDO.ip) orgDO.ip = orgVO.ip
            if (orgVO.port != null && orgVO.port != orgDO.port) orgDO.port = orgVO.port
            if (orgVO.deviceAddress != null && orgVO.deviceAddress != orgDO.deviceAddress) orgDO.deviceAddress = orgVO.deviceAddress
            if (orgVO.durationTime != null && orgVO.durationTime != orgDO.durationTime) {
                orgDO.durationTime = orgVO.durationTime

                //如果设备上线，则
                if (orgDO.status == DeviceStatusEnum.ONLINE.value) {
                    deviceService.setDurationTimeOfDevice(orgDO.aliyunProductKey!!, orgDO.aliyunDeviceName!!, orgVO.durationTime!!)
                }
            }
        }

        orgDO.modifiedTime = Date()

        orgRepository.save(orgDO)
    }

    override fun getOfflineDevices(userId: Long): List<JSONObject>? {
        val offlineDeviceList = orgRepository.findByStatus(userId, true, DeviceStatusEnum.OFFLINE.value)

        val jsonList = arrayListOf<JSONObject>()

        offlineDeviceList?.map {
            val responseJson = JSONObject.toJSON(it) as JSONObject
            responseJson.remove("userId")
            responseJson.remove("id")
            responseJson.remove("parent_id")
            responseJson.remove("createTime")
            responseJson.remove("modifiedTime")
            responseJson.remove("crack_x")
            responseJson.remove("crack_y")

            responseJson["orgId"] = it.id
            responseJson["crackX"] = it.crackX
            responseJson["crackY"] = it.crackY
            responseJson["parentId"] = it.parentId

            jsonList.add(responseJson)
        }

        return jsonList
    }

    override fun getLowPowerDevices(userId: Long): List<JSONObject>? {
        val lowPowerDeviceList = orgRepository.findByPowerRange(userId, true, LOW_POWER)

        val jsonList = arrayListOf<JSONObject>()

        lowPowerDeviceList?.map {
            val responseJson = JSONObject.toJSON(it) as JSONObject
            responseJson.remove("userId")
            responseJson.remove("id")
            responseJson.remove("parent_id")
            responseJson.remove("createTime")
            responseJson.remove("modifiedTime")
            responseJson.remove("crack_x")
            responseJson.remove("crack_y")

            responseJson["orgId"] = it.id
            responseJson["crackX"] = it.crackX
            responseJson["crackY"] = it.crackY
            responseJson["parentId"] = it.parentId

            jsonList.add(responseJson)
        }

        return jsonList
    }

    override fun forceDeleteOrg(userId: Long, orgId: Long) {
        codeCheck(orgId == -1L, ResponseCode.ILLEGAL_OPERATION, "[$orgId]不允许删除用户根目录")

        val doesOrgDOExist = orgRepository.exists(orgId)
        codeCheck(!doesOrgDOExist, ResponseCode.ILLEGAL_OPERATION, "[$orgId]文件不存在")

        val orgDO = orgRepository.findOne(orgId)

        codeCheck(orgDO.userId != userId, ResponseCode.ILLEGAL_OPERATION,
                "[$userId@$orgId]用户没有操作该文件的权限")

        val orgQueue: Queue<Long> = LinkedList<Long>()
        orgQueue.add(orgId) // add root node

        while (orgQueue.isNotEmpty()) {
            val sonNodeList = orgRepository.findByUserIdAndParentId(userId, orgQueue.peek())

            sonNodeList?.map {
                orgQueue.offer(it.id)
            }

            //如果是设备需要先删除crack
            if (orgDO.type) {
                val crackList = crackRepository.findByOrgIdOrderByCreateTimeDesc(orgId)
                crackRepository.delete(crackList)
            }

            orgRepository.delete(orgQueue.poll())
        }
    }

    override fun deleteOrg(userId: Long, orgId: Long) {
        codeCheck(orgId == -1L, ResponseCode.ILLEGAL_OPERATION, "[$orgId]不允许删除用户根目录")

        val doesOrgDOExist = orgRepository.exists(orgId)
        codeCheck(!doesOrgDOExist, ResponseCode.ILLEGAL_OPERATION, "[$orgId]文件不存在")

        val orgDO = orgRepository.findOne(orgId)
        val type = orgDO.type

        codeCheck(orgDO.userId != userId, ResponseCode.ILLEGAL_OPERATION,
                "[$userId@$orgId]用户没有操作该文件的权限")

        if (type) { //如果是设备，直接删除
            orgRepository.delete(orgDO)
            LogUtil.i(javaClass, "[${orgDO.name}@$userId]文件已删除")

            return
        }

        //如果是文件夹，则判断是否有子目录
        val sonOrgList = orgRepository.findByUserIdAndParentId(userId, orgId)
        codeCheck(sonOrgList!!.isNotEmpty(), ResponseCode.ILLEGAL_OPERATION, "请先删除子文件")

        orgRepository.delete(orgDO)
        LogUtil.i(javaClass, "[${orgDO.name}@$userId]文件已删除")
    }

    override fun getCrackDetails(userId: Long, orgId: Long): List<JSONObject>? {
        val orgDO = orgRepository.findOne(orgId)

        codeCheck(orgDO == null, ResponseCode.ILLEGAL_OPERATION, "[$orgId]文件不存在")

        codeCheck(orgDO.userId != userId, ResponseCode.ILLEGAL_OPERATION,
                "[$userId@$orgId]用户没有操作该文件的权限")


        codeCheck(!orgDO.type, ResponseCode.ILLEGAL_OPERATION, "[$userId@$orgId]orgId应为设备")

        val crackDOList = crackRepository.findByOrgIdOrderByCreateTimeDesc(orgId)

        val jsonList = arrayListOf<JSONObject>()

        crackDOList?.map {
            val jsonObject = JSONObject()
            jsonObject["name"] = orgDO.name
            jsonObject["temperature"] = it.temperature
            jsonObject["width"] = it.width
            jsonObject["ossUrl"] = it.ossUrl
            jsonObject["gmtCreate"] = it.createTime

            jsonList.add(jsonObject)
        }

        return jsonList

    }
}