package com.csky.iot.crack.controller

import com.alibaba.fastjson.JSON
import com.aliyun.mns.client.CloudAccount
import com.aliyun.mns.common.ClientException
import com.aliyun.mns.common.ServiceException
import com.csky.iot.crack.common.AliyunProp
import com.csky.iot.crack.common.LogUtil
import com.csky.iot.crack.common.SpringUtil
import com.csky.iot.crack.service.DeviceService
import com.csky.iot.crack.service.deviceShadow.DeviceMsgTypeEnum
import org.springframework.util.Base64Utils

//@Service
class MessageReceiver {
//    fun test() {
////        println("okok")
////        println(System.getProperty("java.library.path"))
////        println(Core.NATIVE_LIBRARY_NAME)
////        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
////        println("opencv")
////        val mat = Mat(5, 10, CvType.CV_8UC1, Scalar(0.0))
////        println("openv MAT")
////        val mr1 = mat.row(1)
////        mr1.setTo(Scalar(1.0))
////        val mc5 = mat.col(5)
////        mc5.setTo(Scalar(5.0))
////        println(mat.dump())
//////        val deviceService = SpringUtil.getBean("deviceServiceImpl") as DeviceService
//////
//        println("okok")
//        val f = File("F://crack/crack.jpg")
//        val bi: BufferedImage
//
//        bi = ImageIO.read(f)
//        val baos = ByteArrayOutputStream()
//        ImageIO.write(bi, "jpg", baos)
//        val bytes = baos.toByteArray()
//
//        val res = Crevice.getCreviceWidth(bytes, 20, 200)
//
//        val c = 22
//    }

    fun receiver() {
        val deviceService = SpringUtil.getBean("deviceServiceImpl") as DeviceService
        //    val aliyunProp = SpringUtil.getBean("aliyunProp") as AliyunProp

        val aliyunProp = AliyunProp()
        aliyunProp.accessKey = "LTAIrGTPHmeA4EKu"
        aliyunProp.accessSecret = "cb69bhmXYCmhZ2wr7tSdqgD7o9uysc"
        aliyunProp.endpoint = "https://1067899827885620.mns.cn-shanghai.aliyuncs.com/"
        aliyunProp.queue = "aliyun-iot-CpC8afk87Mg"
        val account = CloudAccount(aliyunProp.accessKey, aliyunProp.accessSecret, aliyunProp.endpoint)
        val client = account.mnsClient
        val queue = client.getQueueRef(aliyunProp.queue)

        while (true) {
            try {
                val popMsg = queue.popMessage()
                popMsg ?: continue

                val msg = popMsg.messageBodyAsString
                LogUtil.i(javaClass, "aliyunMessageBody:$msg")

                //删除已经取出消费的消息
                queue.deleteMessage(popMsg.receiptHandle)
                LogUtil.d(javaClass, "delete message successfully")

                val json = JSON.parseObject(msg)
                val aliyunMessageType = json.getString("messagetype")
                if (aliyunMessageType == "upload") {
                    val topic = json.getString("topic")
                    val productKey = topic.split("/")[1]
                    val deviceName = topic.split("/")[2]


                    val payloadBytes = Base64Utils.decodeFromString(json.getString("payload"))
                    if (deviceService.crackImageProcess(deviceName, payloadBytes)) continue////////////

                    val payload = String(payloadBytes)

                    val deviceMessage = try {
                        JSON.parseObject(payload)
                    } catch (e: Exception) {
                        LogUtil.e(javaClass, e.message + "")
                        continue
                    }

                    val deviceMessageType = deviceMessage.getString("MessageType")
                    when (deviceMessageType) {

                        DeviceMsgTypeEnum.ACK.value -> {
                            LogUtil.i(javaClass, "receive ack:$payload")
                            // response from device

                            val lastCommand = deviceMessage.getString("LastCommand")
                            when (lastCommand) {
                                DeviceMsgTypeEnum.TIMESYNC.value -> {
//                                    val timestamp = deviceMessage.getString("LastTimeStamp").substring(0, 14)
//                                    val syncReq = DeviceInfo.syncReq["${deviceName}_$timestamp"]
//                                    if (syncReq != null) {
//                                        syncReq.countDown()
//                                        DeviceInfo.syncReq.remove("${deviceName}_$timestamp")
//                                    }
                                    LogUtil.i(javaClass, "receive timesync ack:$payload")

                                    val newVersion = deviceService.getNewVersion(deviceName)
                                    if (newVersion != null) {
                                        deviceService.sendUpgradMsg(productKey, deviceName, newVersion)
                                    } else {
                                        deviceService.setDurationTimeOfDevice(productKey, deviceName)
                                    }
                                }
                                DeviceMsgTypeEnum.SETPARAM.value -> {
                                    LogUtil.i(javaClass, "receive setparam ack:$payload")

                                }
                                DeviceMsgTypeEnum.UPGRADE.value -> {
                                    LogUtil.i(javaClass, "receive upgrade ack:$payload")
                                }
                                else -> {
                                }
                            }

                        }

                        DeviceMsgTypeEnum.ONLINE.value -> {
                            LogUtil.i(javaClass, "receive online:$payload")
                            // device online
                            val cid = deviceMessage.getString("Cid")
                            val model = deviceMessage.getString("Model")
                            val version = deviceMessage.getString("Version")
                            deviceService.activeDevice(cid, productKey, deviceName, model, version)

                            //timesync
                            deviceService.timesyncOfDevice(productKey, deviceName)

                        }

                        DeviceMsgTypeEnum.UPGRADED.value -> {
                            LogUtil.i(javaClass, "receive upgraded:$payload")

                            val upgradedResult = deviceMessage.getString("Result")
                            deviceService.upgradedOfDevice(deviceName, upgradedResult)
                        }

                        DeviceMsgTypeEnum.UPLOAD.value -> {
                            LogUtil.i(javaClass, "receive upload:$payload")

                            val temperature = deviceMessage.getString("Temperature")
                            val power = deviceMessage.getIntValue("Battery")
                            val imageLen = deviceMessage.getIntValue("ImageLen")
                            val imagePacket = deviceMessage.getIntValue("ImagePacket")

                            deviceService.updateDeviceCrack(deviceName, temperature, power, imageLen, imagePacket)
                        }

                        else -> {
                            LogUtil.e(javaClass, "receive unknow data:$payload")
                        }
                    }

                    //更新设备最后对话时间
                    deviceService.updateLastSessionTime(deviceName)

                } else if (aliyunMessageType == "status") {
//                    val payload = String(Base64.decodeBase64(json.getString("payload")))
//                    LogUtil.i(javaClass, "receive status:$payload")
//
//                    val status = JSON.parseObject(payload)
//                    val deviceName = status.getString("deviceName")
//                    deviceService.updateDeviceStatus(deviceName, status.getString("status").toLowerCase())
                }

            } catch (ce: ClientException) {
                LogUtil.e(javaClass, "Something wrong with the network connection between client and MNS service." + "Please check your network and DNS availablity.")
                ce.printStackTrace()
            } catch (se: ServiceException) {
                se.printStackTrace()
                LogUtil.e(javaClass, "MNS exception requestId:" + se.requestId)
                if (se.errorCode != null) {
                    if (se.errorCode.equals("QueueNotExist")) {
                        LogUtil.e(javaClass, "Queue is not exist.Please create before use")
                    } else if (se.errorCode.equals("TimeExpired")) {
                        LogUtil.e(javaClass, "The request is time expired. Please check your local machine timeclock")
                    }
                    /*
                you can get more MNS service error code from following link:
                https://help.aliyun.com/document_detail/mns/api_reference/error_code/error_code.html
                */
                }
            } catch (e: Exception) {
                LogUtil.e(javaClass, e.message + "")
                e.printStackTrace()
            }
        }
        client.close()
    }

}