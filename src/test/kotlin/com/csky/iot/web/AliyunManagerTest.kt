///*
// * Copyright (C) 2017 C-SKY Microsystems Co., Ltd.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.csky.iot.web
//
//import com.alibaba.fastjson.JSONObject
//import com.aliyun.mns.client.CloudAccount
//import com.csky.iot.Application
//import com.csky.iot.console.manager.AliyunManager
//import com.csky.iot.console.service.entity.aliyun_iot.AliyunDeviceShadow
//import com.csky.iot.constants.Constant
//import com.csky.iot.exception.CskyException
//import com.csky.iot.utils.LogUtil
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.context.annotation.ComponentScan
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
//import org.springframework.test.context.web.WebAppConfiguration
//import org.springframework.test.web.servlet.MockMvc
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers
//import org.springframework.test.web.servlet.setup.MockMvcBuilders
//import org.springframework.util.Base64Utils
//import org.springframework.web.context.WebApplicationContext
//import java.io.ByteArrayInputStream
//import java.io.File
//import java.io.IOException
//import java.nio.charset.StandardCharsets
//import java.text.SimpleDateFormat
//import java.util.*
//import javax.imageio.ImageIO
//
//@RunWith(SpringJUnit4ClassRunner::class)
//@SpringBootTest(classes = arrayOf(Application::class))
//@WebAppConfiguration
//@ComponentScan("com.csky.iot")
//class aliyunManagerTest : MockMvcResultMatchers() {
//    var mvc: MockMvc? = null
//
////    val TEST_CID = TestData.TEST_CID
////    val TEST_ACCESS_ID = TestData.TEST_ACCESS_ID
////    val TEST_ACCESS_SECRET = TestData.TEST_ACCESS_SECRET
////    val TEST_ACCESS_KEY = TestData.TEST_ACCESS_KEY
////    val TEST_PRODUCT_KEY = TestData.TEST_PRODUCT_KEY
////    val TEST_DEVICE_NAME = TestData.TEST_DEVICE_NAME
////    val TEST_DEVICE_SECRET = TestData.TEST_DEVICE_SECRET
////    val TEST_MNS_ENDPOINT = TestData.TEST_MNS_ENDPOINT
////    val TEST_DEVICE_SHADOW = TestData.TEST_DEVICE_SHADOW
////    val TEST_QUEUE_SHADOW = TestData.TEST_QUEUE_SHADOW
////    val TEST_QUEUE_SHADOW_MAP = TestData.SCOPE_QUEUE_SHADOW_MAP
//
//    @Autowired
//    lateinit var wac: WebApplicationContext
//
//    @Autowired
//    lateinit var aliyunManager: AliyunManager
//
//    @Before
//    fun setUp() {
//        mvc = MockMvcBuilders.webAppContextSetup(wac).build()
//    }
//    /***************************************
//     ***************************************
//     */
//    @Test
//    fun crackTest() {
//        val accessKeyID = "LTAIrGTPHmeA4EKu"//"LTAIV70zhVOagYy2" //"LTAIrGTPHmeA4EKu"
//        val accessKeySecret = "cb69bhmXYCmhZ2wr7tSdqgD7o9uysc"//"Bv8bJlfIgccU24ZxeKjgdTH2MYuEW6" //"cb69bhmXYCmhZ2wr7tSdqgD7o9uysc"
//        val endPoint =  "https://1067899827885620.mns.cn-shanghai.aliyuncs.com/"//"http://1450567618250870.mns.cn-shanghai.aliyuncs.com/" //"https://1067899827885620.mns.cn-shanghai.aliyuncs.com/"
//        val productName = "xlhhRUrsD0m"//"DBY3OSololD" //"xlhhRUrsD0m"
//        val deviceName = "M01nCVMfLNGS2nBEGfsm"
//        val account = CloudAccount(
//                accessKeyID,
//                accessKeySecret,
//                endPoint)
//        val aliyunClient = account.mnsClient
//        val aliyunQueue = aliyunClient.getQueueRef("aliyun-iot-$productName")
//
//        val jpgHead = byteArrayOf(0x11, 0x22, 0x33, 0x44)
//
//        var jpgLength = -1
//        var jpgBytes = byteArrayOf()
//        var receiveBytesCount = 0
//        var receiveJpgCount = 0
//        println("polling begins")
//        while(true) {
//            val message = aliyunQueue.popMessage() ?: continue
//
//            val msgStr = message.messageBodyAsString
//            aliyunQueue.deleteMessage(message.receiptHandle)
//            LogUtil.i(javaClass, "messageBody$msgStr")
//
//            val msgBodyJson = JSONObject.parseObject(msgStr)
//
//            val topic = msgBodyJson.getString("topic")
//            val topicArray = topic.split("/")
//            val deviceName_2 = topicArray[2]
//            if (deviceName_2 != deviceName) continue
//
//            val payload = msgBodyJson.getString("payload")
//            var rawPayload = Base64Utils.decodeFromString(payload)
//
//            if (rawPayload.copyOfRange(0, 4).contentEquals(jpgHead)) { //jpg head
//                //clear buf
//                LogUtil.i(javaClass, "----------------------------------------------------------------------------------")
//                LogUtil.i(javaClass, "总接收字节数： $receiveBytesCount；总接收图片数：$receiveJpgCount")
//                LogUtil.i(javaClass, "----------------------------------------------------------------------------------")
//                jpgLength = rawPayload[6].toInt().and(0xff) * 256 + rawPayload[7].toInt().and(0xff)
//                receiveJpgCount++
//
//                LogUtil.i(javaClass, "待接收当前图片总字节数：$jpgLength")
//                jpgBytes = byteArrayOf()
//                rawPayload = rawPayload.drop(8).toByteArray()
//            }
//            jpgBytes += rawPayload
//            jpgLength -= rawPayload.size
//            println("leftLength: $jpgLength")
//
//            if (jpgLength == 0) {
//                receiveBytesCount += jpgBytes.size
//                LogUtil.i(javaClass, "已接受当前图片总字节：${jpgBytes.size}")
////                jpgBytes.forEach {
////                    val a = it.toInt().and(0xff)
////                    print("$a, ")
////                }
//                bytesToImage(jpgBytes, SimpleDateFormat("yyyy-MM-dd HH.mm.ss").format(Date()))
//            }
//        }
//    }
//
//    fun bytesToImage(bytes: ByteArray, name: String) {
//        try {
//            val bais = ByteArrayInputStream(bytes)
//            val bi1 = ImageIO.read(bais)
//            val w2 = File("F://crack/$name.jpg")// 可以是jpg,png,gif格式
//            ImageIO.write(bi1, "jpg", w2)// 不管输出什么格式图片，此处不需改动
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//    }
//
////    @Test
////    fun getQueueMessageTest1() {
////        val testAccessKey = Pair("AAA", "BBB")
////        val client = aliyunManager.getMnsClient(testAccessKey, TEST_MNS_ENDPOINT)
////        val queue = aliyunManager.getMnsQueue(client, TEST_PRODUCT_KEY)
////       try {
////           aliyunManager.getQueueMessage(queue)
////       } catch (e: CskyException) {
////           assert(e.code == Constant.PARAM_ERROR)
////       }
////    }
////
////    @Test
////    fun getQueueMessageTest2() {
////        val testAccessKey = Pair("", "")
////        val client = aliyunManager.getMnsClient(testAccessKey, TEST_MNS_ENDPOINT)
////        val queue = aliyunManager.getMnsQueue(client, TEST_PRODUCT_KEY)
////        try {
////            aliyunManager.getQueueMessage(queue)
////        } catch (e: CskyException) {
////            assert(e.code == Constant.PARAM_ERROR)
////        }
////    }
////
////    @Test
////    fun getQueueMessageTest3() {
////        val client = aliyunManager.getMnsClient(TEST_ACCESS_KEY, "http://SSS")
////        val queue = aliyunManager.getMnsQueue(client, TEST_PRODUCT_KEY)
////        try {
////            aliyunManager.getQueueMessage(queue)
////        } catch (e: CskyException) {
////            assert(e.code == Constant.PARAM_ERROR)
////        }
////    }
//}