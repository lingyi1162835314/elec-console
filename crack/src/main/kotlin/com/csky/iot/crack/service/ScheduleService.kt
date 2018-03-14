package com.csky.iot.crack.service

import com.csky.iot.crack.data.CodeRepository
import com.csky.iot.crack.data.CrackRepository
import com.csky.iot.crack.data.OrgRepository
import com.csky.iot.crack.service.deviceShadow.DeviceStatusEnum
import com.csky.iot.crack.utils.OssUtil
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*

@Component
class ScheduleService(val orgRepository: OrgRepository,
                      val crackRepository: CrackRepository,
                      val codeRepository: CodeRepository,
                      val deviceService: DeviceService,
                      val ossUtil: OssUtil) {

    @Scheduled(cron="0 0 0/1 * * ?")
    fun syncTime() {
        val devices = orgRepository.findByTypeAndStatus(true, DeviceStatusEnum.ONLINE.value)

        devices?.map {
            if (!it.aliyunProductKey.isNullOrBlank() && !it.aliyunDeviceName.isNullOrBlank()) {
                deviceService.timesyncOfDevice(it.aliyunProductKey!!, it.aliyunDeviceName!!)
            }
        }
    }

    @Scheduled(cron="0 0 0/1 * * ?")
    fun clearVerification() {
        val timeOutDate = Date().time - 60 * 5 * 1000
        val codeDOList = codeRepository.findByCreateTimeLessThan(Date(timeOutDate))

        codeRepository.delete(codeDOList)
    }

    //每天清理一次crack，过期时间为3天
    @Scheduled(cron="0 0 1 * * ?")
    fun clearCrack() {
        val timeOutDate = Date().time - 3600 * 24 * 3 * 1000

        val crackDOList = crackRepository.findByCreateTimeLessThan(Date(timeOutDate))

        crackDOList?.map {
            ossUtil.deleteJpg(it.ossUrl!!)
        }
        crackRepository.delete(crackDOList)
    }
}