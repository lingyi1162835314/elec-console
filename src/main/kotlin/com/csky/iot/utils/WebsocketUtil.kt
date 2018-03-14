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

package com.csky.iot.utils

import com.alibaba.fastjson.JSONObject
import com.csky.iot.constants.Constant
import com.csky.iot.exception.CskyException
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
class WebsocketUtil(val messageTemplate: SimpMessagingTemplate) {
    fun publishWebSocketMessage(topic: String, payload: JSONObject?) {
        LogUtil.i(javaClass, "Publishing websocket $topic begins")

        payload ?: return

        try {
            messageTemplate.convertAndSend(topic, payload)
        } catch (e: Exception) {
            LogUtil.e(javaClass, e.message + "")

            throw(CskyException(Constant.PARAM_ERROR, "Publish websocket $topic failed"))
        }
        LogUtil.i(javaClass, "Publishing websocket $topic ends")
    }
}