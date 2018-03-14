package com.csky.iot.crack.service

import com.alibaba.fastjson.JSONObject
import com.csky.iot.crack.common.LogUtil
import com.csky.iot.crack.common.ResponseCode
import com.csky.iot.crack.common.codeCheck
import com.csky.iot.crack.data.CrackRepository
import com.csky.iot.crack.data.OrgRepository
import com.csky.iot.crack.data.entity.CrackDO
import com.csky.iot.crack.manager.AliIotManager
import com.csky.iot.crack.manager.CskyManager
import com.csky.iot.crack.service.deviceShadow.*
import com.csky.iot.crack.utils.OssUtil
import com.csky.iot.crack.utils.Statics
import com.jorchi.Crevice
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

interface DeviceService {

    fun activeDevice(cid: String, aliyunProductKey: String, aliyunDeviceName: String, model: String, version: String)

    fun timesyncOfDevice(aliyunProductKey: String, aliyunDeviceName: String)

    fun upgradedOfDevice(aliyunDeviceName: String, upgradedResult: String)

    fun setDurationTimeOfDevice(aliyunProductKey: String, aliyunDeviceName: String)

    fun setDurationTimeOfDevice(aliyunProductKey: String, aliyunDeviceName: String, durationTime: Long)

    fun updateDeviceStatus(aliyunDeviceName: String, status: String)

    fun updateLastSessionTime(aliyunDeviceName: String)

    fun updateDeviceCrack(aliyunDeviceName: String, temperature: String, power: Int, imageLength: Int, imagePacketNumber: Int)

    fun crackImageProcess(aliyunDeviceName: String, bytesData: ByteArray): Boolean

    fun sendUpgradMsg(aliyunProductKey: String, aliyunDeviceName: String, version: String)

    fun getNewVersion(aliyunDeviceName: String): String?
}

@Service
class DeviceServiceImpl(val orgRepository: OrgRepository,
                        val crackRepository: CrackRepository,
                        val ossUtil: OssUtil,
                        val aliIotManager: AliIotManager,
                        val tcpClient: TcpClient,
                        val cskyManager: CskyManager) : DeviceService {

    override fun activeDevice(cid: String, aliyunProductKey: String, aliyunDeviceName: String,
                              model: String, version: String) {
        val orgDO = orgRepository.findByCid(cid)
        codeCheck(orgDO == null, ResponseCode.ILLEGAL_OPERATION, "请先使用app添加设备")

        orgDO!!.cid = cid
        orgDO.aliyunProductKey = aliyunProductKey
        orgDO.aliyunDeviceName = aliyunDeviceName
        orgDO.model = model
        orgDO.version = version
        orgDO.status = DeviceStatusEnum.ONLINE.value
        orgDO.modifiedTime = Date()

        orgRepository.save(orgDO)
        LogUtil.i(javaClass, "[$cid]device is online")
    }

    override fun getNewVersion(aliyunDeviceName: String): String? {
        val orgDO = orgRepository.findByAliyunDeviceName(aliyunDeviceName)
        codeCheck(orgDO == null, ResponseCode.ILLEGAL_OPERATION, "[$aliyunDeviceName]device not found")
        val newVersion = cskyManager.checkNewVersion(orgDO!!.model!!, orgDO.version!!, orgDO.cid!!)
        return newVersion
    }

    override fun timesyncOfDevice(aliyunProductKey: String, aliyunDeviceName: String) {
        val responseJson = JSONObject()

        val currentTimeMillis = System.currentTimeMillis().toString()
        val second = "${currentTimeMillis.substring(0, 10)}.${currentTimeMillis.substring(10, 13)}"

        responseJson["MessageType"] = DeviceMsgTypeEnum.TIMESYNC.value
        responseJson["TimeStamp"] = second

        aliIotManager.sendMsg(aliyunProductKey, aliyunDeviceName, responseJson.toJSONString())
        LogUtil.i(javaClass, "[$aliyunDeviceName]send timesync :${responseJson.toJSONString()}")
    }

    override fun sendUpgradMsg(aliyunProductKey: String, aliyunDeviceName: String, version: String) {
        val responseJson = JSONObject()

        val currentTimeMillis = System.currentTimeMillis().toString()
        val second = "${currentTimeMillis.substring(0, 10)}.${currentTimeMillis.substring(10, 13)}"

        responseJson["MessageType"] = DeviceMsgTypeEnum.UPGRADE.value
        responseJson["TimeStamp"] = second
        responseJson["Version"] = version

        aliIotManager.sendMsg(aliyunProductKey, aliyunDeviceName, responseJson.toJSONString())
        LogUtil.i(javaClass, "[$aliyunDeviceName]send upgrade :${responseJson.toJSONString()}")
    }

    override fun upgradedOfDevice(aliyunDeviceName: String, upgradedResult: String) {
        //w升级之后，设备返回信息变更
        val orgDO = orgRepository.findByAliyunDeviceName(aliyunDeviceName)
        codeCheck(orgDO == null, ResponseCode.ILLEGAL_OPERATION, "[$aliyunDeviceName]device not found")

        when (upgradedResult) {
            DeviceOtaAckEnum.SUCCESS.value -> {
//                aliIotManager.pushMsg("智能裂缝仪",
//                        "设备${orgDO!!.name}已经升级成功",
//                        deviceService.getAdminDevices()!!)
            }
            DeviceOtaAckEnum.FAILED.value -> {
//                aliIotManager.pushMsg("智能门控",
//                        "设备${deviceDO[0].name}设备升级失败。",
//                        deviceService.getAdminDevices()!!)
            }
            DeviceOtaAckEnum.ERROR.value -> {
//                aliIotManager.pushMsg("智能门控",
//                        "设备${deviceDO[0].name}no version设备出现问题。",
//                        deviceService.getAdminDevices()!!)
            }
        }
    }

    override fun setDurationTimeOfDevice(aliyunProductKey: String, aliyunDeviceName: String) {
        val orgDO = orgRepository.findByAliyunDeviceName(aliyunDeviceName)
        codeCheck(orgDO == null, ResponseCode.ILLEGAL_OPERATION, "请先使用app添加设备")

        val responseJson = JSONObject()

        val currentTimeMillis = System.currentTimeMillis().toString()
        val second = "${currentTimeMillis.substring(0, 10)}.${currentTimeMillis.substring(10, 13)}"

        responseJson["MessageType"] = DeviceMsgTypeEnum.SETPARAM.value
        responseJson["Interval"] = orgDO!!.durationTime
        responseJson["TimeStamp"] = second

        aliIotManager.sendMsg(aliyunProductKey, aliyunDeviceName, responseJson.toJSONString())
        LogUtil.i(javaClass, "[$aliyunDeviceName]send durationTime :${responseJson.toJSONString()}")
    }

    override fun setDurationTimeOfDevice(aliyunProductKey: String, aliyunDeviceName: String, durationTime: Long) {
        val responseJson = JSONObject()

        val currentTimeMillis = System.currentTimeMillis().toString()
        val second = "${currentTimeMillis.substring(0, 10)}.${currentTimeMillis.substring(10, 13)}"

        responseJson["MessageType"] = DeviceMsgTypeEnum.SETPARAM.value
        responseJson["Interval"] = durationTime
        responseJson["TimeStamp"] = second

        aliIotManager.sendMsg(aliyunProductKey, aliyunDeviceName, responseJson.toJSONString())
        LogUtil.i(javaClass, "[$aliyunDeviceName]send durationTime :$${responseJson.toJSONString()}")
    }

    override fun updateDeviceStatus(aliyunDeviceName: String, status: String) {
        val orgDO = orgRepository.findByAliyunDeviceName(aliyunDeviceName)
        codeCheck(orgDO == null, ResponseCode.ILLEGAL_OPERATION, "请先使用app添加设备")

        if (status == DeviceMsgTypeEnum.OFFLINE.value) {
            orgDO!!.status = DeviceStatusEnum.OFFLINE.value
        } else {
            orgDO!!.status = DeviceStatusEnum.ONLINE.value
        }
        orgDO.modifiedTime = Date()
        orgRepository.save(orgDO)

        LogUtil.i(javaClass, "[${orgDO.name}@${orgDO.id}]device is $status")
    }

    override fun updateLastSessionTime(aliyunDeviceName: String) {
        val orgDO = orgRepository.findByAliyunDeviceName(aliyunDeviceName)
        orgDO ?: return

        orgDO.lastSessionTime = Date()
        orgDO.modifiedTime = Date()
        orgRepository.save(orgDO)

        LogUtil.i(javaClass, "[${orgDO.name}@${orgDO.id}]device's lastSessionTime is ${orgDO.lastSessionTime}")
    }

    override fun updateDeviceCrack(aliyunDeviceName: String, temperature: String, power: Int,
                                   imageLength: Int, imagePacketNumber: Int) {
        val orgDO = orgRepository.findByAliyunDeviceName(aliyunDeviceName)
        codeCheck(orgDO == null, ResponseCode.ILLEGAL_OPERATION, "请先使用app添加设备")

        //update device' battery power
        orgDO!!.power = power
        orgDO.status = DeviceStatusEnum.ONLINE.value
        orgDO.modifiedTime = Date()
        orgRepository.save(orgDO)

        //add crack
        val orgId = orgDO.id
        val crackDO = CrackDO(orgId, temperature)

        //add imageMap
        deviceImageShadowMap[aliyunDeviceName] = DeviceImageShadow(crackDO, orgDO, imageLength,
                imagePacketNumber, byteArrayOf())

        LogUtil.i(javaClass, "[${orgDO.name}@${orgDO.id}]device is in transparent transmission mode")
    }

    override fun crackImageProcess(aliyunDeviceName: String, bytesData: ByteArray): Boolean {
        if (!deviceImageShadowMap.containsKey(aliyunDeviceName)) return false

        val deviceImageShadow = deviceImageShadowMap[aliyunDeviceName]!!
        deviceImageShadow.length -= bytesData.size
        deviceImageShadow.packetNumber--
        deviceImageShadow.bytesData += bytesData

        LogUtil.i(javaClass, "[$aliyunDeviceName]leftLength: ${deviceImageShadow.length}, " +
                "packetNumber: ${deviceImageShadow.packetNumber}")

        if (deviceImageShadow.length == 0 && deviceImageShadow.packetNumber == 0) {
            LogUtil.i(javaClass, "[$aliyunDeviceName]图片接收结束")
            val image = ByteArray(Statics.imageHeader.size + deviceImageShadow.bytesData.size)
            System.arraycopy(Statics.imageHeader, 0, image, 0, Statics.imageHeader.size)

            System.arraycopy(deviceImageShadow.bytesData, 0, image, Statics.imageHeader.size, deviceImageShadow.bytesData.size)

//
//            val buffer = ByteBuffer.allocate(Statics.imageHeader.size + deviceImageShadow.bytesData.size)
//            buffer.put(Statics.imageHeader)
//            buffer.put(deviceImageShadow.bytesData)

            //oss 存储
            val res = getProCrackInfo(image,
                    Pair(deviceImageShadow.orgDO.crackX!!, deviceImageShadow.orgDO.crackY!!))

            val ossUrl = ossUtil.uploadJpg(deviceImageShadow.orgDO.cid!!, res.second)

            val crackDO = deviceImageShadow.crackDO
            crackDO.ossUrl = ossUrl
            val width = res.first.toDouble() / deviceImageShadow.orgDO.pix!!
            crackDO.width = BigDecimal(width).setScale(1, BigDecimal.ROUND_DOWN).toDouble()
            crackDO.modifiedTime = Date()
            crackRepository.save(crackDO)

            if (deviceImageShadow.orgDO.enable!!) {
                LogUtil.i(javaClass, "Image is ready to send")
                tcpClient.sendServerImage(crackDO, deviceImageShadow.orgDO.ip!!, deviceImageShadow.orgDO.port!!.toInt())
            }

            deviceImageShadowMap.remove(aliyunDeviceName)

        } else if (deviceImageShadow.length < 0 || deviceImageShadow.packetNumber < 0) {
            LogUtil.e(javaClass, "[$aliyunDeviceName]图片接收失败")

            deviceImageShadowMap.remove(aliyunDeviceName)
        }
        return true
    }

    fun getProCrackInfo(rawImageBytes: ByteArray, point: Pair<Int, Int>): Pair<Int, ByteArray> {
        val imagePro = Crevice.getCreviceWidth(rawImageBytes, point.first, point.second)
        val newImageBytes = imagePro[0] as ByteArray
        val width = imagePro[1] as Int

        LogUtil.i(javaClass, "处理后的图片宽度为: $width mm")

        return Pair(width, newImageBytes)
    }
}