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

package com.csky.iot.console.web

import com.alibaba.fastjson.JSONObject
import com.csky.iot.console.service.ConsoleService
import com.csky.iot.exception.CskyException
import com.csky.iot.console.web.vo.DeviceVO
import com.csky.iot.console.web.vo.SendMessageVO
import com.csky.iot.utils.LogUtil
import com.csky.iot.utils.WebsocketUtil
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestBody

@Controller
class ConsoleController(val consoleService: ConsoleService, val websocketUtil: WebsocketUtil) {

    @MessageMapping("/devEnable")
    fun devEnable(@Validated @RequestBody deviceVO: DeviceVO) {
        LogUtil.i(javaClass,"[${deviceVO.cid}] DevEnable Request begins")
        val cid = deviceVO.cid!!

        try {
            consoleService.enableDevice(cid)
        } catch (e: CskyException) {
            LogUtil.e(javaClass, e.msg)

            val errorJson = JSONObject()
            errorJson["msgType"] = MsgTypeEnum.CONSOLE_ERROR.value
            errorJson["payload"] = e.msg
            websocketUtil.publishWebSocketMessage("/topic/$cid", errorJson)

            return
        }

        val logJson = JSONObject()
        logJson["msgType"] = MsgTypeEnum.CONSOLE_DEBUG.value
        logJson["payload"] = "ready"
        websocketUtil.publishWebSocketMessage("/topic/$cid", logJson)

        LogUtil.i(javaClass, "[$cid] DevEnable request ends")
    }

    @MessageMapping("/sendMessage")
    fun sendMessage(@Validated @RequestBody sendMessageVO: SendMessageVO) {
        LogUtil.i(javaClass,"[${sendMessageVO.cid}] Sending message begins")

        val cid = sendMessageVO.cid!!
        val message = sendMessageVO.message!!

        val res = try {
            consoleService.sendMessageToDevice(cid, message)
        } catch (e: CskyException) {
            LogUtil.e(javaClass, e.msg)

            val errorJson = JSONObject()
            errorJson["msgType"] = MsgTypeEnum.CONSOLE_ERROR.value
            errorJson["payload"] = e.msg
            websocketUtil.publishWebSocketMessage("/topic/$cid", errorJson)

            return
        }

        val logJson = JSONObject()
        logJson["msgType"] = MsgTypeEnum.CONSOLE_INFO.value
        logJson["payload"] = res + "[Sending message succeed]"
        websocketUtil.publishWebSocketMessage("/topic/$cid", logJson)
        LogUtil.i(javaClass,"[$cid]Sending message ends")
    }
}
