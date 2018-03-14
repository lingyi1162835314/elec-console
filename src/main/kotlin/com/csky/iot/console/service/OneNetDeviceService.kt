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

import com.csky.iot.console.dao.DeviceOneNetRepository
import com.csky.iot.console.dao.LicenseCidRepository
import com.csky.iot.console.dao.LicenseRepository
import com.csky.iot.console.dao.ProductOneNetRepository
import com.csky.iot.console.manager.OnenetManager
import com.csky.iot.console.service.entity.onenet.NbiotShadow
import com.csky.iot.console.service.entity.onenet.OneNetDeviceShadow
import com.csky.iot.constants.Constant
import com.csky.iot.exception.CskyException
import com.csky.iot.utils.LogUtil
import com.csky.iot.utils.RedisUtil
import org.springframework.stereotype.Service

interface OneNetDeviceService {
    fun enableOnenetDevice(cid: String)

    fun disableOnenetDevice(cid: String)

    fun pubMessage(cid: String, message: String): String
}

@Service
class OneNetDeviceServiceImpl(val productOneNetRepository: ProductOneNetRepository,
                              val deviceOneNetRepository: DeviceOneNetRepository,
                              val licenseCidRepository: LicenseCidRepository,
                              val licenseRepository: LicenseRepository,
                              val onenetManager: OnenetManager,
                              val redisUtil: RedisUtil): OneNetDeviceService {
    private val IOT_TAG = "onenet_iot"

    override fun enableOnenetDevice(cid: String) {
        val oneNetDeviceShadow = getOneNetDeviceShadow(cid)

        val isOneNetDeviceAbsent = redisUtil.hashPutIfAbsent(redisUtil.SCOPE_ONENET_DEVICE_SHADOW_MAP, cid, oneNetDeviceShadow)

        if(isOneNetDeviceAbsent) {//new device
            LogUtil.i(javaClass, "[$cid@$IOT_TAG] Device is enabled")
        } else {
            LogUtil.i(javaClass, "[$cid@$IOT_TAG] Device is already enabled")
        }
    }

    override fun disableOnenetDevice(cid: String) {
        redisUtil.hashDelete(redisUtil.SCOPE_ONENET_DEVICE_SHADOW_MAP, cid)
        LogUtil.i(javaClass, "[$cid@$IOT_TAG]] Device is released")
    }

    override fun pubMessage(cid: String, message: String): String {
        LogUtil.i(javaClass, "[@$IOT_TAG]Publishing onenet message begins")
        val oneNetDeviceShadow = redisUtil.hashGet(redisUtil.SCOPE_ONENET_DEVICE_SHADOW_MAP, cid) as OneNetDeviceShadow?
                ?: throw CskyException(Constant.PARAM_ERROR, "Please enable your device first")

        return onenetManager.nbiotExecute(oneNetDeviceShadow, message, NbiotShadow())
    }

    fun getOneNetDeviceShadow(cid: String): OneNetDeviceShadow {
        val licenseId = licenseCidRepository.findByCid(cid)?.licenseId
                ?: throw CskyException(Constant.PARAM_ERROR, "[$cid@$IOT_TAG] Device's licenseId is not exist")
        val productId = licenseRepository.findById(licenseId)?.productId
                ?: throw CskyException(Constant.PARAM_ERROR, "[$cid@$IOT_TAG] Device's productId is not exist")

        val apiKey = (productOneNetRepository.findByProductId(productId)
                ?: throw CskyException(Constant.PARAM_ERROR, "[$cid@$IOT_TAG] OneNetProduct's apiKey is null")).apiKey

        val deviceOneNetDO = deviceOneNetRepository.findByCid(cid)
                ?: throw CskyException(Constant.PARAM_ERROR, "[$cid@$IOT_TAG] OneNetDevice's deviceOneNetDO is null")

        val imei = deviceOneNetDO.imei
                ?: throw CskyException(Constant.PARAM_ERROR, "[$cid@$IOT_TAG] OneNetDevice's imei is null")
        val title = deviceOneNetDO.title

        return OneNetDeviceShadow(cid, apiKey, imei, title)
    }
}
