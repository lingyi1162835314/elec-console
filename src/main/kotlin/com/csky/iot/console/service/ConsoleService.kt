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

import com.csky.iot.console.dao.LicenseCidRepository
import com.csky.iot.console.dao.LicenseRepository
import com.csky.iot.console.dao.ProductRepository
import com.csky.iot.console.service.entity.ProductIotTypeEnum
import com.csky.iot.console.service.entity.productIotTypeGetEnum
import com.csky.iot.constants.Constant
import com.csky.iot.exception.CskyException
import org.springframework.stereotype.Service

interface ConsoleService {
    fun enableDevice(cid: String)

    fun disableDevice(cid: String)

    fun sendMessageToDevice(cid: String, message: String): String?
}

@Service
class ConsoleServiceImpl(val aliyunDeviceService: AliyunDeviceService,
                         val oneNetDeviceService: OneNetDeviceService,
                         val licenseCidRepository: LicenseCidRepository,
                         val licenseRepository: LicenseRepository,
                         val productRepository: ProductRepository): ConsoleService {

    override fun enableDevice(cid: String) {
        val iotTypeEnum = getDeviceIotType(cid)

        when (iotTypeEnum) {
            ProductIotTypeEnum.AliMQTT -> aliyunDeviceService.enableAliyunDevice(cid)
            ProductIotTypeEnum.AliCoAP -> aliyunDeviceService.enableAliyunDevice(cid)
            ProductIotTypeEnum.OneNET_NBCoAP -> oneNetDeviceService.enableOnenetDevice(cid)
        }
    }

    override fun disableDevice(cid: String) {
        val iotTypeEnum = getDeviceIotType(cid)

        when (iotTypeEnum) {
            ProductIotTypeEnum.AliMQTT -> aliyunDeviceService.disableAliyunDevice(cid)
            ProductIotTypeEnum.AliCoAP -> aliyunDeviceService.disableAliyunDevice(cid)
            ProductIotTypeEnum.OneNET_NBCoAP ->  oneNetDeviceService.disableOnenetDevice(cid)
        }
    }

    override fun sendMessageToDevice(cid: String, message: String): String? {
        val iotTypeEnum = getDeviceIotType(cid)

        when (iotTypeEnum) {
            ProductIotTypeEnum.AliMQTT ->
                throw CskyException(Constant.PARAM_ERROR,
                        "[$cid] Sending message via AliMqtt channel is not supported by the device")
            ProductIotTypeEnum.AliCoAP ->
                throw CskyException(Constant.PARAM_ERROR,
                        "[$cid] Sending message via AliCoap channel is not supported by the device")
            ProductIotTypeEnum.OneNET_NBCoAP ->
                return oneNetDeviceService.pubMessage(cid, message)
        }
    }

    fun getDeviceIotType(cid: String): ProductIotTypeEnum {
        val licenseId = licenseCidRepository.findByCid(cid)?.licenseId
                ?: throw CskyException(Constant.PARAM_ERROR, "[$cid] Device's licenseId is not exist")
        val productId = licenseRepository.findById(licenseId)?.productId
                ?: throw CskyException(Constant.PARAM_ERROR, "[$cid] Device's productId is not exist")
        val iotType =(productRepository.getOne(productId)?.iotType
                ?: throw CskyException(Constant.PARAM_ERROR, "[$cid] Device's iotType is not exist")).toByte()

        val iotTypeEnum = productIotTypeGetEnum(iotType)
                ?: throw CskyException(Constant.PARAM_ERROR, "[$cid] Device's iotType is not exist")

        return iotTypeEnum
    }
}