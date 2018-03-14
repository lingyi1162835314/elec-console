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

package com.csky.iot.console.service

import com.csky.iot.console.dao.DeviceRepository
import com.csky.iot.console.dao.LicenseCidRepository
import com.csky.iot.console.dao.LicenseRepository
import com.csky.iot.console.dao.ProductAliyunRepository
import com.csky.iot.console.manager.AliyunManager
import com.csky.iot.console.manager.dto.AliyunMsgDTO
import com.csky.iot.console.service.entity.aliyun_iot.AliyunDeviceShadow
import com.csky.iot.console.service.entity.aliyun_iot.MnsTypeEnum
import com.csky.iot.constants.Constant
import com.csky.iot.exception.CskyException
import com.csky.iot.utils.LogUtil
import com.csky.iot.utils.RedisUtil
import org.springframework.stereotype.Service

interface AliyunDeviceService {
    fun enableAliyunDevice(cid: String)

    fun disableAliyunDevice(cid: String)

    fun aliyunDeviceStatusPro(aliyunMsgDTO: AliyunMsgDTO)
}

@Service
class AliyunDeviceServiceImpl(val licenseCidRepository: LicenseCidRepository,
                              val licenseRepository: LicenseRepository,
                              val deviceRepository: DeviceRepository,
                              val productAliyunRepository: ProductAliyunRepository,
                              val aliyunManager: AliyunManager,
                              val redisUtil: RedisUtil) : AliyunDeviceService {

    private val IOT_TAG = "aliyun_iot"

    override fun enableAliyunDevice(cid: String) {
        val aliyunDeviceShadow = getAliyunDeviceShadow(cid)
        
        LogUtil.i(javaClass, "aliyunDeviceShadow { accessKey: ${aliyunDeviceShadow.accessKey.first}, " +
                "${aliyunDeviceShadow.accessKey.second}, endPoint: ${aliyunDeviceShadow.endPoint}, " +
                "productKey: ${aliyunDeviceShadow.productKey} }")

        if (!aliyunManager.isQueueExist(aliyunDeviceShadow)) {
            throw CskyException(Constant.PARAM_ERROR, "[${aliyunDeviceShadow.productKey}] Queue is not exist")
        }

        val isDeviceAbsent = redisUtil.hashPutIfAbsent(redisUtil.SCOPE_ALIYUN_DEVICE_SHADOW_MAP, cid, aliyunDeviceShadow)

        if(isDeviceAbsent) {//new device
            LogUtil.i(javaClass, "[$cid@$IOT_TAG] Device is enabled")
        } else {
            LogUtil.i(javaClass, "[$cid@$IOT_TAG] Device is already enabled")
        }
    }

    override fun disableAliyunDevice(cid: String) {
        redisUtil.hashDelete(redisUtil.SCOPE_ALIYUN_DEVICE_SHADOW_MAP, cid)
        LogUtil.i(javaClass, "[$cid@$IOT_TAG]] Device is released")
    }

    override fun aliyunDeviceStatusPro(aliyunMsgDTO: AliyunMsgDTO) {
        val aliyunDeviceShadow = redisUtil.hashGet(redisUtil.SCOPE_ALIYUN_DEVICE_SHADOW_MAP, aliyunMsgDTO.cid)
                as AliyunDeviceShadow? ?: return

        if (!aliyunDeviceShadow.status) {
            aliyunDeviceShadow.status = true
            redisUtil.hashPut(redisUtil.SCOPE_ALIYUN_DEVICE_SHADOW_MAP, aliyunDeviceShadow.cid, aliyunDeviceShadow)

            LogUtil.i(javaClass, "[${aliyunDeviceShadow.cid}@$IOT_TAG] Device is online")
        }

        if (aliyunMsgDTO.msgTypeEnum == MnsTypeEnum.STATUS) {
            val status = aliyunMsgDTO.msgData["status"]
            if(status == "offline"){
                aliyunDeviceShadow.status = false
                LogUtil.i(javaClass, "[${aliyunDeviceShadow.cid}@$IOT_TAG] Device is offline")
                disableAliyunDevice(aliyunDeviceShadow.cid)
            }
        }
    }

    fun getAliyunDeviceShadow(cid: String): AliyunDeviceShadow {
        val deviceDO = deviceRepository.findByCid(cid)
                ?: throw CskyException(Constant.PARAM_ERROR, "[$cid@$IOT_TAG] Device is not exist, please check your cid")

        val deviceName = deviceDO.name
        val deviceSecret = deviceDO.deviceSecret
        if (deviceName == null || deviceSecret == null) {
            throw CskyException(Constant.PARAM_ERROR, "[$cid@$IOT_TAG] Device is not activated, please active cid first")
        }

        val licenseId = licenseCidRepository.findByCid(cid)?.licenseId
                ?: throw CskyException(Constant.PARAM_ERROR, "[$cid@$IOT_TAG] Device's licenseId is not exist")
        val productId = licenseRepository.findById(licenseId)?.productId
                ?: throw CskyException(Constant.PARAM_ERROR, "[$cid@$IOT_TAG] Device's productId is not exist")
        val productAliyunD0 = productAliyunRepository.findByProductId(productId)
                ?: throw CskyException(Constant.PARAM_ERROR, "[$cid@$IOT_TAG] Device's productAliyunD0 is not exist")

        val accessID = productAliyunD0.accessKey
                ?: throw CskyException(Constant.PARAM_ERROR, "[$cid@$IOT_TAG] Device's accessID is null")
        val accessSecret = productAliyunD0.accessSecret
                ?: throw CskyException(Constant.PARAM_ERROR, "[$cid@$IOT_TAG] Device's accessSecret is null")
        val productKey = productAliyunD0.productKey
                ?: throw CskyException(Constant.PARAM_ERROR, "[$cid@$IOT_TAG] Device's productKey is null")
        val endPoint = productAliyunD0.endPoint
                ?: throw CskyException(Constant.PARAM_ERROR, "[$cid@$IOT_TAG] Device's endPoint is null")

        return AliyunDeviceShadow(cid, Pair(accessID, accessSecret), productKey, deviceName,
                deviceSecret, byteArrayOf(), endPoint)
    }

}