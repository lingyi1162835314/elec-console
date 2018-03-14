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
package com.csky.iot.crack.manager

import com.alibaba.fastjson.JSONObject
import com.aliyuncs.DefaultAcsClient
import com.aliyuncs.exceptions.ClientException
import com.aliyuncs.iot.model.v20170420.RRpcRequest
import com.aliyuncs.iot.model.v20170620.BatchGetDeviceStateRequest
import com.aliyuncs.iot.model.v20170620.BatchGetDeviceStateResponse
import com.aliyuncs.iot.model.v20170620.PubRequest
import com.aliyuncs.profile.DefaultProfile
import com.csky.iot.crack.common.DeviceInfo
import com.csky.iot.crack.common.LogUtil
import com.csky.iot.crack.common.ResponseCode
import com.csky.iot.crack.exception.CskyException
import org.apache.commons.codec.binary.Base64
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import com.aliyuncs.utils.ParameterHelper
import com.aliyuncs.push.model.v20160801.PushRequest
//import com.csky.iot.crack.service.DeviceService
import java.util.*


interface AliIotManager {
    fun getDevicesStatus(productKey: String, deviceNames: ArrayList<String>): List<BatchGetDeviceStateResponse.DeviceStatus>

    fun sendMsg(productKey: String, deviceName: String, msg: String)

    fun sendMsgResp(productKey: String, deviceName: String, msg: String)

    fun sendMsgSync(productKey: String,
                        deviceName: String,
                        msg: JSONObject,
                        timeout: Long
    )

    fun pushMsg(title: String, body: String, target: String)
}

@Service
class AliIotManagerImpl() : AliIotManager {

   // @Value("\${aliyun.accessKey}")
    private val accessKey: String? = "LTAIrGTPHmeA4EKu"

  //  @Value("\${aliyun.accessSecret}")
    private val accessSecret: String? = "cb69bhmXYCmhZ2wr7tSdqgD7o9uysc"

    @Value("\${aliyun.appKey}")
    private val appKey: Long? = null

    override fun sendMsgResp(productKey: String, deviceName: String, msg: String) {

        DefaultProfile.addEndpoint("cn-shanghai", "cn-shanghai", "Iot", "iot.cn-shanghai.aliyuncs.com")
        val profile = DefaultProfile.getProfile("cn-shanghai", accessKey, accessSecret)
        val client = DefaultAcsClient(profile)
        val rrpcRequest = RRpcRequest()
        rrpcRequest.productKey = productKey //设备所属产品的Key
        rrpcRequest.deviceName = deviceName //设备名称
        rrpcRequest.requestBase64Byte = Base64.encodeBase64String(msg.toByteArray()) //发给设备的数据，要求二进制数据做一次Base64编码
        rrpcRequest.timeout = 5000
        val rrpcResponse = client.getAcsResponse(rrpcRequest)
        LogUtil.d(javaClass, rrpcResponse.payloadBase64Byte)
        LogUtil.d(javaClass, rrpcResponse.rrpcCode)
    }

    override fun sendMsgSync(productKey: String, deviceName: String,
                                 msg: JSONObject, timeout: Long) {
        val latch = CountDownLatch(1)
        sendMsg(productKey, deviceName, msg.toJSONString())
        val timestamp = msg.getString("TimeStamp")
        DeviceInfo.syncReq.put("${deviceName}_$timestamp", latch)
        if (latch.await(timeout, TimeUnit.SECONDS)) {
            //success
//            successCallback()
		} else {
            // failed
//            failureCallback()
            throw CskyException(ResponseCode.PARAM_TIMEOUT)
        }
    }

    override fun sendMsg(productKey: String, deviceName: String, msg: String){

        DefaultProfile.addEndpoint("cn-shanghai", "cn-shanghai", "Iot", "iot.cn-shanghai.aliyuncs.com")
        val profile = DefaultProfile.getProfile("cn-shanghai", accessKey, accessSecret)
        val client = DefaultAcsClient(profile)
        val pub = PubRequest()
        pub.productKey = productKey
        pub.topicFullName = "/$productKey/$deviceName/get" //消息发送到的Topic全名.
        pub.messageContent = Base64.encodeBase64String(msg.toByteArray()) //hello world Base64 String.
        pub.qos = 0 //设置Qos为1的话，设备如果不在线，重新上线会收到离线消息，消息最多在IoT套件中保存7天.
        val response = client.getAcsResponse(pub)

        LogUtil.i(javaClass, "sendMsg: ${response.success}")
    }

    override fun getDevicesStatus(productKey: String, deviceNames: ArrayList<String>): List<BatchGetDeviceStateResponse.DeviceStatus> {
        DefaultProfile.addEndpoint("cn-shanghai", "cn-shanghai", "Iot", "iot.cn-shanghai.aliyuncs.com")
        val profile = DefaultProfile.getProfile("cn-shanghai", accessKey, accessSecret)
        val client = DefaultAcsClient(profile)
        val request = BatchGetDeviceStateRequest()
        request.productKey = productKey
        request.deviceNames = deviceNames
        val response = client.getAcsResponse(request)
        return response.deviceStatusList
    }

    override fun pushMsg(title: String, body: String, target: String) {
        val profile = DefaultProfile.getProfile("cn-hangzhou", accessKey, accessSecret)
        val client = DefaultAcsClient(profile)
        val pushRequest = PushRequest()
        // 推送目标
        pushRequest.appKey = appKey
        pushRequest.target = "DEVICE" //推送目标: DEVICE:按设备推送 ALIAS : 按别名推送 ACCOUNT:按帐号推送  TAG:按标签推送; ALL: 广播推送

        pushRequest.targetValue = target //根据Target来设定，如Target=DEVICE, 则对应的值为 设备id1,设备id2. 多个值使用逗号分隔.(帐号与设备有一次最多100个的限制)
//        pushRequest.targetValue = "ce3837fe90ff4b69a618297bc28fb6c0"
//        pushRequest.setTarget("ALL"); //推送目标: DEVICE:推送给设备; ACCOUNT:推送给指定帐号,TAG:推送给自定义标签; ALL: 推送给全部
//        pushRequest.setTargetValue("ALL"); //根据Target来设定，如Target=DEVICE, 则对应的值为 设备id1,设备id2. 多个值使用逗号分隔.(帐号与设备有一次最多100个的限制)
        pushRequest.pushType = "NOTICE" // 消息类型 MESSAGE NOTICE
        pushRequest.deviceType = "ANDROID" // 设备类型 ANDROID iOS ALL.
        // 推送配置
        pushRequest.title = title // 消息的标题
        pushRequest.body = body // 消息的内容
        // 推送配置: iOS
//        pushRequest.iosBadge = 5 // iOS应用图标右上角角标
//        pushRequest.iosMusic = "default" // iOS通知声音
//        pushRequest.iosSubtitle = "iOS10 subtitle"//iOS10通知副标题的内容
//        pushRequest.iosNotificationCategory = "iOS10 Notification Category"//指定iOS10通知Category
//        pushRequest.iosMutableContent = true//是否允许扩展iOS通知内容
//        pushRequest.iosApnsEnv = "DEV"//iOS的通知是通过APNs中心来发送的，需要填写对应的环境信息。"DEV" : 表示开发环境 "PRODUCT" : 表示生产环境
//        pushRequest.iosRemind = true // 消息推送时设备不在线（既与移动推送的服务端的长连接通道不通），则这条推送会做为通知，通过苹果的APNs通道送达一次。注意：离线消息转通知仅适用于生产环境
//        pushRequest.iosRemindBody = "iOSRemindBody"//iOS消息转通知时使用的iOS通知内容，仅当iOSApnsEnv=PRODUCT && iOSRemind为true时有效
//        pushRequest.iosExtParameters = "{\"_ENV_\":\"DEV\",\"k2\":\"v2\"}" //通知的扩展属性(注意 : 该参数要以json map的格式传入,否则会解析出错)
        // 推送配置: Android
        pushRequest.androidNotifyType = "BOTH"//通知的提醒方式 "VIBRATE" : 震动 "SOUND" : 声音 "BOTH" : 声音和震动 NONE : 静音
        pushRequest.androidNotificationBarType = 1//通知栏自定义样式0-100
        pushRequest.androidNotificationBarPriority = 1//通知栏自定义样式0-100
        pushRequest.androidOpenType = "APPLICATION" //点击通知后动作 "APPLICATION" : 打开应用 "ACTIVITY" : 打开AndroidActivity "URL" : 打开URL "NONE" : 无跳转
//        pushRequest.androidActivity = "com.alibaba.push2.demo.XiaoMiPushActivity" // 设定通知打开的activity，仅当AndroidOpenType="Activity"有效
        pushRequest.androidMusic = "default" // Android通知音乐
//        pushRequest.androidPopupActivity = "com.ali.demo.PopupActivity"//设置该参数后启动辅助弹窗功能, 此处指定通知点击后跳转的Activity（辅助弹窗的前提条件：1. 集成第三方辅助通道；2. StoreOffline参数设为true）
//        pushRequest.androidPopupTitle = "Popup Title"
//        pushRequest.androidPopupBody = "Popup Body"
//        pushRequest.androidExtParameters = "{\"k1\":\"android\",\"k2\":\"v2\"}" //设定通知的扩展属性。(注意 : 该参数要以 json map 的格式传入,否则会解析出错)
        // 推送控制
        val pushDate = Date(System.currentTimeMillis()) // 30秒之间的时间点, 也可以设置成你指定固定时间
        val pushTime = ParameterHelper.getISO8601Time(pushDate)
        pushRequest.pushTime = pushTime // 延后推送。可选，如果不设置表示立即推送
        val expireTime = ParameterHelper.getISO8601Time(Date(System.currentTimeMillis() + 12 * 3600 * 1000)) // 12小时后消息失效, 不会再发送
        pushRequest.expireTime = expireTime
        pushRequest.storeOffline = true // 离线消息是否保存,若保存, 在推送时候，用户即使不在线，下一次上线则会收到
        val pushResponse = client.getAcsResponse(pushRequest)
        System.out.printf("RequestId: %s, MessageID: %s\n",
                pushResponse.requestId, pushResponse.messageId)
    }
}