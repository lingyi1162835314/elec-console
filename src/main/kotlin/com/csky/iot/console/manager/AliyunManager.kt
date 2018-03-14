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
import com.aliyun.mns.client.CloudAccount
import com.aliyun.mns.client.CloudQueue
import com.aliyun.mns.common.ClientException
import com.aliyun.mns.common.ServiceException
import com.aliyun.mns.model.Message
import com.csky.iot.console.dao.DeviceRepository
import com.csky.iot.console.manager.dto.AliyunMsgDTO
import com.csky.iot.console.service.entity.aliyun_iot.AliyunDeviceShadow
import com.csky.iot.console.service.entity.aliyun_iot.MnsTypeEnum
import com.csky.iot.constants.Constant
import com.csky.iot.exception.CskyException
import com.csky.iot.utils.LogUtil
import org.springframework.stereotype.Service
import org.springframework.util.Base64Utils
import java.nio.charset.StandardCharsets
import java.util.*

interface AliyunManager {
    fun receiveDeleteMessageList(aliyunDeviceShadow: AliyunDeviceShadow): List<AliyunMsgDTO>?

    fun isQueueExist(aliyunDeviceShadow: AliyunDeviceShadow): Boolean
}

@Service
class AliyunManagerImpl(val deviceRepository: DeviceRepository): AliyunManager {
    private val MESSAGE_NUMBER = 16
  //  private val WAIT_SECONDES = 15
    private val IOT_TAG = "aliyun_iot"


    override fun receiveDeleteMessageList(aliyunDeviceShadow: AliyunDeviceShadow): List<AliyunMsgDTO>? {
        val account = CloudAccount(
                aliyunDeviceShadow.accessKey.first,
                aliyunDeviceShadow.accessKey.second,
                aliyunDeviceShadow.endPoint)

        val aliyunClient = account.mnsClient
        val aliyunQueue = aliyunClient.getQueueRef("aliyun-iot-${aliyunDeviceShadow.productKey}")
        val messageList = getQueueMessageList(aliyunQueue)
        messageList?.let { deleteQueueMessageList(aliyunQueue, messageList) }

        aliyunClient.close()

        val res = arrayListOf<AliyunMsgDTO>()
        messageList?.map {
            val aliyunMsgDTO = parseAliyunMsg(it)
            aliyunMsgDTO?.let { res.add(aliyunMsgDTO) }
        }

        return res
    }

    fun getQueueMessageList(cloudQueue: CloudQueue): List<Message>? {
       return try {
            cloudQueue.batchPopMessage(MESSAGE_NUMBER)
        } catch (ce: ClientException) {
            throw CskyException(Constant.PARAM_ERROR,  "[$IOT_TAG]" + ce.errorCode + ':' + ce.message)
        } catch (se: ServiceException) {
            throw CskyException(Constant.PARAM_ERROR,  "[$IOT_TAG]" + se.errorCode + ':' + se.message)
        } catch (e: Exception) {
            LogUtil.e(javaClass, "[$IOT_TAG]" + e.message)

            throw CskyException(Constant.PARAM_ERROR, "[$IOT_TAG]Pop AliMns queue failed, unknown exception occurred")
        }
    }

    fun deleteQueueMessageList(cloudQueue: CloudQueue, messageList: List<Message>) {
        val receiptHandleList = arrayListOf<String>()
        try {
            messageList.mapTo(receiptHandleList){it.receiptHandle}
            cloudQueue.batchDeleteMessage(receiptHandleList)

            LogUtil.i(javaClass, "[$IOT_TAG]Deleting message successfully")
        } catch (e: Exception) {
            LogUtil.e(javaClass, "[$IOT_TAG]" + e.message)

            throw CskyException(Constant.PARAM_ERROR, "Deleting device mns failed")
        }
    }

    override fun isQueueExist(aliyunDeviceShadow: AliyunDeviceShadow): Boolean {
        val account = CloudAccount(
                aliyunDeviceShadow.accessKey.first,
                aliyunDeviceShadow.accessKey.second,
                aliyunDeviceShadow.endPoint)

        val aliyunClient = account.mnsClient
        val aliyunQueue = aliyunClient.getQueueRef("aliyun-iot-${aliyunDeviceShadow.productKey}")

        val res =  try {
            aliyunQueue.isQueueExist
        } catch (ce: ClientException) {
            throw CskyException(Constant.PARAM_ERROR, "[$IOT_TAG]" + ce.errorCode + ':' + ce.message)
        } catch (se: ServiceException) {
            throw CskyException(Constant.PARAM_ERROR,  "[$IOT_TAG]" + se.errorCode + ':' + se.message)
        } catch (e: Exception) {
            LogUtil.e(javaClass, "[$IOT_TAG]" + e.message )

            throw CskyException(Constant.PARAM_ERROR, "[$IOT_TAG]Popping AliMns queue failed, unknown exception occurred")
        }

        aliyunClient.close()
        return res
    }

    fun parseAliyunMsg(message: Message): AliyunMsgDTO? {
        LogUtil.i(javaClass, "[@$IOT_TAG]Parsing aliyun Msg begins")

        val msgStr = message.messageBodyAsString
        LogUtil.i(javaClass, "messageBody$msgStr")

        val msgBodyJson = JSONObject.parseObject(msgStr)

        val timestamp = msgBodyJson.getLong("timestamp")
        val curTimestamp = Date().time / 1000

        LogUtil.i(javaClass, "[$IOT_TAG]$curTimestamp / $timestamp")

        //当消息上云时间超过2min，丢弃消息
        if ((curTimestamp - timestamp) > 120) {
            LogUtil.i(javaClass, "[$IOT_TAG]The message is timeout")

            return null
        }

        val payload = msgBodyJson.getString("payload")
        val strPayload = String(Base64Utils.decodeFromString(payload), StandardCharsets.UTF_8)
        val payloadJson = JSONObject.parseObject(strPayload)

        val messageType = msgBodyJson.getString("messagetype")
        if (messageType == "status") {
            val deviceName = payloadJson.getString("deviceName")

            val mnsDataJson = JSONObject()
            mnsDataJson["status"] = payloadJson.getString("status")
            mnsDataJson["lastTime"] = payloadJson.getString("lastTime")

            val cid = (deviceRepository.findByName(deviceName)
                    ?: throw CskyException(Constant.PARAM_ERROR,
                    "[$deviceName@$IOT_TAG] DeviceName does not bind any cid")).cid

            LogUtil.i(javaClass, "${mnsDataJson["lastTime"]}: [$cid@$IOT_TAG] device is ${mnsDataJson["status"]}")

            return AliyunMsgDTO(MnsTypeEnum.STATUS, mnsDataJson, cid)
        } else {
            val topic = msgBodyJson.getString("topic")
            val topicArray = topic.split("/")
            val deviceName = topicArray[2]
            val cid = (deviceRepository.findByName(deviceName)
                    ?: throw CskyException(Constant.PARAM_ERROR,
                    "[$deviceName@$IOT_TAG] DeviceName does not bind any cid")).cid

            LogUtil.i(javaClass, "[$IOT_TAG]Parsing aliyun Msg ends")

            return AliyunMsgDTO(MnsTypeEnum.UPLOAD, payloadJson, cid)
        }
    }
}
