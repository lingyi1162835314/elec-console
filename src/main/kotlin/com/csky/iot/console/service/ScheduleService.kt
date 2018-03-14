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

import com.alibaba.fastjson.JSONObject
import com.csky.iot.console.manager.AliyunManager
import com.csky.iot.console.manager.OnenetManager
import com.csky.iot.console.service.entity.aliyun_iot.AliyunDeviceShadow
import com.csky.iot.console.service.entity.onenet.NbiotShadow
import com.csky.iot.console.service.entity.onenet.OneNetDeviceShadow
import com.csky.iot.console.web.MsgTypeEnum
import com.csky.iot.exception.CskyException
import com.csky.iot.utils.LogUtil
import com.csky.iot.utils.RedisUtil
import com.csky.iot.utils.WebsocketUtil
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*

@Component
class ScheduleService(val redisUtil: RedisUtil,
                      val aliyunDeviceService: AliyunDeviceService,
                      val aliyunManager: AliyunManager,
                      val onenetManager: OnenetManager,
                      val websocketUtil: WebsocketUtil) {
//    @Async
//    @Scheduled(fixedRate = 1000)
    fun pollingAndPubAliyunMsg() {
        println("${Thread.currentThread()}:aliyun@${LocalTime.now()}")

        val aliyunDeviceShadowList = redisUtil.hashEntries(redisUtil.SCOPE_ALIYUN_DEVICE_SHADOW_MAP)

        aliyunDeviceShadowList?.forEach {
            val aliyunDeviceShadow = it.value as AliyunDeviceShadow

//            val startTime = System.currentTimeMillis()
            val aliyunMsgList = try {
                aliyunManager.receiveDeleteMessageList(aliyunDeviceShadow)
            } catch (e: CskyException) {
                val errorJson = JSONObject()
                errorJson["msgType"] = MsgTypeEnum.CONSOLE_ERROR.value
                errorJson["payload"] = e.msg

                websocketUtil.publishWebSocketMessage("/topic/${aliyunDeviceShadow.cid}", errorJson)
                null
            }
//            val endTime = System.currentTimeMillis()
//            println("POLLING ALI TIME:" + (endTime-startTime))

            aliyunMsgList?.forEach {
                aliyunDeviceService.aliyunDeviceStatusPro(it)
                val responseJson = JSONObject()
                responseJson["msgType"] = it.msgTypeEnum.value
                responseJson["payload"] = it.msgData

                LogUtil.i(javaClass, "$responseJson")

                websocketUtil.publishWebSocketMessage("/topic/${it.cid}", responseJson)
                }
        }
    }

    // OneNET
//    @Async
//    @Scheduled(fixedRate = 1000)
    fun pollingAndPubOnenetMsg() {
        println("${Thread.currentThread()}:onenet@${LocalTime.now()}")

        val onenetDeviceShadowMapList = redisUtil.hashEntries(redisUtil.SCOPE_ONENET_DEVICE_SHADOW_MAP)
        onenetDeviceShadowMapList.forEach {
            val oneNetDeviceShadow = it.value as OneNetDeviceShadow
            val oneNetMessage = try {
                onenetManager.nbiotGetResource(oneNetDeviceShadow, NbiotShadow())
            } catch (e: CskyException) {
                val errorJson = JSONObject()
                errorJson["msgType"] = MsgTypeEnum.CONSOLE_ERROR.value
                errorJson["payload"] = e.msg

                websocketUtil.publishWebSocketMessage("/topic/${oneNetDeviceShadow.cid}", errorJson)
                null
            } ?: return

            val responseJson = JSONObject()
            if (oneNetMessage.mnsErrno == 0) {
                responseJson["msgType"] = "upload"
            } else {
                responseJson["msgType"] = "status"
            }

            val payload = oneNetMessage.resResult
            payload.put("time", SimpleDateFormat("yyyy-MM-dd:HH:mm:ss").format(Date()))
            responseJson.put("payload", payload)
            websocketUtil.publishWebSocketMessage("/topic/${oneNetMessage.cid}", responseJson)
        }
    }

   // @Scheduled(cron = "0 0 1 * * ?")
    fun clearDevices() {
        redisUtil.remove(redisUtil.SCOPE_ALIYUN_DEVICE_SHADOW_MAP)
        redisUtil.remove(redisUtil.SCOPE_ONENET_DEVICE_SHADOW_MAP)
        LogUtil.i(javaClass, "devices are all released")
    }

}