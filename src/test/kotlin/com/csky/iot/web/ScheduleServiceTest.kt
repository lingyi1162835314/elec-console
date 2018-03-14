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

package com.csky.iot.web

import com.alibaba.fastjson.JSONObject
import com.csky.iot.Application
import com.csky.iot.console.manager.AliyunManager
import com.csky.iot.console.manager.OnenetManager
import com.csky.iot.console.manager.dto.AliyunMsgDTO
import com.csky.iot.console.manager.dto.OneNetDevicesMnsDTO
import com.csky.iot.console.service.*
import com.csky.iot.console.service.entity.aliyun_iot.MnsTypeEnum
import com.csky.iot.console.service.entity.onenet.NbiotShadow
import com.csky.iot.constants.Constant
import com.csky.iot.exception.CskyException
import com.csky.iot.utils.RedisUtil
import com.csky.iot.utils.WebsocketUtil
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(classes = arrayOf(Application::class))
@WebAppConfiguration
@ComponentScan("com.csky.iot")
class ScheduleServiceTest : MockMvcResultMatchers() {
    lateinit var mvc: MockMvc

    @Autowired
    var wac: WebApplicationContext? = null

    @Before
    fun setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build()
    }

    @Autowired
    lateinit var redisUtil: RedisUtil

    @Autowired
    lateinit var scheduleService: ScheduleService

    @InjectMocks
    lateinit var scheduleServiceMock: ScheduleService

    @Mock
    lateinit var redisUtilMock: RedisUtil

    @Mock
    lateinit var aliyunDeviceServiceMock: AliyunDeviceService

    @Mock
    lateinit var aliyunManagerMock: AliyunManager

    @Mock
    lateinit var onenetManagerMock: OnenetManager

    @Mock
    lateinit var websocketUtilMock: WebsocketUtil

    val TEST_CID = TestData.TEST_CID
    val TEST_ALIYUN_DEVICE_SHADOW = TestData.TEST_ALIYUN_DEVICE_SHADOW
    val TEST_ONENET_DEVICE_SHADOW = TestData.TEST_ONENET_DEVICE_SHADOW

    @Test
    fun pollingAndPubAliyunMsgTest1() {
        scheduleServiceMock.pollingAndPubAliyunMsg()

        redisUtil.hashPut(redisUtil.SCOPE_ALIYUN_DEVICE_SHADOW_MAP, TEST_CID, TEST_ALIYUN_DEVICE_SHADOW)
        val scheduleService = ScheduleService(redisUtil, aliyunDeviceServiceMock, aliyunManagerMock,
                onenetManagerMock, websocketUtilMock)
        scheduleService.pollingAndPubAliyunMsg()

        `when`(aliyunManagerMock.receiveDeleteMessageList(TEST_ALIYUN_DEVICE_SHADOW)).thenThrow(
                CskyException(Constant.PARAM_ERROR, "error"))

        try {
            scheduleService.pollingAndPubAliyunMsg()
        } catch (e: CskyException) {
            assert(e.code == Constant.PARAM_ERROR)
        }

        redisUtil.hashDelete(redisUtil.SCOPE_ALIYUN_DEVICE_SHADOW_MAP, TEST_CID)
    }

    @Test
    fun pollingAndPubAliyunMsgTest2() {
        redisUtil.hashPut(redisUtil.SCOPE_ALIYUN_DEVICE_SHADOW_MAP, TEST_CID, TEST_ALIYUN_DEVICE_SHADOW)
        val scheduleService = ScheduleService(redisUtil, aliyunDeviceServiceMock, aliyunManagerMock,
                onenetManagerMock, websocketUtilMock)

        val msgData = JSONObject()
        msgData["status"] = "offline"
        val aliyunMsgDTO = AliyunMsgDTO( MnsTypeEnum.STATUS, msgData, TEST_CID)
        `when`(aliyunManagerMock.receiveDeleteMessageList(TEST_ALIYUN_DEVICE_SHADOW)).thenReturn(listOf(aliyunMsgDTO))

        scheduleService.pollingAndPubAliyunMsg()

        redisUtil.hashDelete(redisUtil.SCOPE_ALIYUN_DEVICE_SHADOW_MAP, TEST_CID)
    }

    @Test
    fun pollingAndPubOnenetMsgTest1() {
        scheduleServiceMock.pollingAndPubOnenetMsg()

        redisUtil.hashPut(redisUtil.SCOPE_ONENET_DEVICE_SHADOW_MAP, TEST_CID, TEST_ONENET_DEVICE_SHADOW)
        val scheduleService = ScheduleService(redisUtil, aliyunDeviceServiceMock, aliyunManagerMock,
                onenetManagerMock, websocketUtilMock)
        scheduleService.pollingAndPubOnenetMsg()

        `when`(onenetManagerMock.nbiotGetResource(TEST_ONENET_DEVICE_SHADOW, NbiotShadow())).thenThrow(
                CskyException(Constant.PARAM_ERROR, "error"))

        try {
            scheduleService.pollingAndPubOnenetMsg()
        } catch (e: CskyException) {
            assert(e.code == Constant.PARAM_ERROR)
        }

        redisUtil.hashDelete(redisUtil.SCOPE_ONENET_DEVICE_SHADOW_MAP, TEST_CID)
    }

    @Test
    fun pollingAndPubOnenetMsgTest2() {
        redisUtil.hashPut(redisUtil.SCOPE_ONENET_DEVICE_SHADOW_MAP, TEST_CID, TEST_ONENET_DEVICE_SHADOW)
        val scheduleService = ScheduleService(redisUtil, aliyunDeviceServiceMock, aliyunManagerMock,
                onenetManagerMock, websocketUtilMock)

        val msgData = JSONObject()
        msgData["status"] = "offline"
        val onenetDeviceMnsDTO = OneNetDevicesMnsDTO( 0, msgData, TEST_CID)
        `when`(onenetManagerMock.nbiotGetResource(TEST_ONENET_DEVICE_SHADOW, NbiotShadow())).thenReturn(onenetDeviceMnsDTO)

        scheduleService.pollingAndPubOnenetMsg()

        onenetDeviceMnsDTO.mnsErrno = 1
        `when`(onenetManagerMock.nbiotGetResource(TEST_ONENET_DEVICE_SHADOW, NbiotShadow())).thenReturn(onenetDeviceMnsDTO)

        scheduleService.pollingAndPubOnenetMsg()

        redisUtil.hashDelete(redisUtil.SCOPE_ALIYUN_DEVICE_SHADOW_MAP, TEST_CID)
    }

    @Test
    fun disableDeviceTest1() {
        scheduleService.clearDevices()
    }
}