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

package com.csky.iot.console.manager

import com.alibaba.fastjson.JSONObject
import com.csky.iot.console.dao.DeviceOneNetRepository
import com.csky.iot.console.manager.dto.OneNetDevicesMnsDTO
import com.csky.iot.console.service.entity.onenet.NbiotShadow
import com.csky.iot.console.service.entity.onenet.OneNetDeviceShadow
import com.csky.iot.constants.Constant
import com.csky.iot.exception.CskyException
import com.csky.iot.utils.LogUtil
import com.csky.iot.utils.httpclient.HttpClientUtil
import com.csky.iot.utils.httpclient.common.HttpConfig
import com.csky.iot.utils.httpclient.common.HttpHeader
import org.springframework.stereotype.Service

interface OnenetManager {

    fun nbiotGetResource(oneNetDeviceShadow: OneNetDeviceShadow, nbiotShadow: NbiotShadow): OneNetDevicesMnsDTO

    fun nbiotExecute(oneNetDeviceShadow: OneNetDeviceShadow, message: String, nbiotShadow: NbiotShadow): String

}

@Service
class OnenetManagerImpl(val deviceOneNetRepository: DeviceOneNetRepository): OnenetManager {
    private val IOT_TAG = "onenet_iot"

    override fun nbiotGetResource(oneNetDeviceShadow: OneNetDeviceShadow, nbiotShadow: NbiotShadow): OneNetDevicesMnsDTO {
        val imei = oneNetDeviceShadow.imei
        val apiKey = oneNetDeviceShadow.apiKey
        val url = "http://api.heclouds.com/nbiot?imei=$imei&obj_id=${nbiotShadow.OBJ_ID}&obj_inst_id=" +
                "${nbiotShadow.OBJ_INST_ID}&res_id=${nbiotShadow.RES_ID}"
        val headers = HttpHeader.custom().contentType("application/json").other("api-key", apiKey).build()
        val config = HttpConfig.custom().url(url).headers(headers).encoding("utf-8")
        try {
            val result = JSONObject.parse(HttpClientUtil.get(config)) as JSONObject
            val errno = result.getIntValue("errno")
            if (errno == 0) {
                val mnsData = result.getJSONArray("data")
                val res_json = mnsData.get(0) as JSONObject
                val res = res_json.getJSONArray("res")
                val res_result = res.get(0) as JSONObject
                res_result.put("type", 1)
                if (res_result.get("res_id") == 1) {
                    res_result.put("status", "online")
                } else {
                    res_result.put("status", "offline")
                }
                val cid = (deviceOneNetRepository.findByImei(imei)
                        ?: throw CskyException(Constant.PARAM_ERROR, "[$imei] device imei does not bind any cid")).cid
                return OneNetDevicesMnsDTO(errno, res_result, cid)
            }
            throw CskyException(Constant.PARAM_ERROR, result.getString("error"))
        } catch (e: Exception) {
            LogUtil.i(javaClass, "[@$IOT_TAG]${e.message}")

            throw CskyException(Constant.PARAM_ERROR, "[@$IOT_TAG]${e.message}")
        }
    }

    override fun nbiotExecute(oneNetDeviceShadow: OneNetDeviceShadow, message: String, nbiotShadow: NbiotShadow): String{
        val res: String
        val imei = oneNetDeviceShadow.imei
        val apiKey = oneNetDeviceShadow.apiKey
        val url = "http://api.heclouds.com/nbiot/execute?imei=$imei&obj_id=${nbiotShadow.OBJ_ID}&obj_inst_id=" +
                "${nbiotShadow.OBJ_INST_ID}&res_id=${nbiotShadow.RES_ID}"
        val headers = HttpHeader.custom().contentType("application/json").other("api-key", apiKey).build()
        val map = mutableMapOf<String, Any>()
        map.put("args", message)
        val config = HttpConfig.custom().url(url).headers(headers).map(map).encoding("utf-8")
        try {
            res = HttpClientUtil.post(config)
            LogUtil.d(javaClass, res)
        } catch (e: Exception) {
            LogUtil.i(javaClass, "[@$IOT_TAG]OneNET publish message got an exception: ${e.message}")

            throw CskyException(Constant.PARAM_ERROR, "[@$IOT_TAG]${e.message}")
        }
        LogUtil.i(javaClass, "[@$IOT_TAG]Publishing onenet message ends")
        return res
    }

}