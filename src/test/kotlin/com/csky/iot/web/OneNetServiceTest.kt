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
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import com.csky.iot.Application
import com.csky.iot.console.dao.*
import com.csky.iot.console.dao.entity.*
import com.csky.iot.console.manager.AliyunManager
import com.csky.iot.console.manager.OnenetManager
import com.csky.iot.console.manager.dto.AliyunMsgDTO
import com.csky.iot.console.service.AliyunDeviceServiceImpl
import com.csky.iot.console.service.OneNetDeviceServiceImpl
import com.csky.iot.console.service.entity.aliyun_iot.MnsTypeEnum
import com.csky.iot.console.service.entity.onenet.NbiotShadow
import com.csky.iot.constants.Constant
import com.csky.iot.exception.CskyException
import com.csky.iot.utils.RedisUtil
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(classes = arrayOf(Application::class))
@WebAppConfiguration
@ComponentScan("com.csky.iot")
class OneNetServiceTest: MockMvcResultMatchers() {
    lateinit var mvc: MockMvc

    @Autowired
    var wac: WebApplicationContext? = null

    @Before
    fun setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build()
    }

    @Autowired
    lateinit var redisUtil: RedisUtil

    @InjectMocks
    lateinit var oneNetDeviceServiceImplMock: OneNetDeviceServiceImpl

    @Mock
    lateinit var productOneNetRepositoryMock: ProductOneNetRepository

    @Mock
    lateinit var deviceOneNetRepositoryMock: DeviceOneNetRepository

    @Mock
    lateinit var licenseCidRepositoryMock: LicenseCidRepository

    @Mock
    lateinit var licenseRepositoryMock: LicenseRepository

    @Mock
    lateinit var onenetManagerMock: OnenetManager

    @Mock
    lateinit var redisUtilMock: RedisUtil

    val TEST_CID = TestData.TEST_CID
    val TEST_ONENET_DEVICE_SHADOW = TestData.TEST_ONENET_DEVICE_SHADOW

    @Test
    fun enableOnenetDeviceTest1() {
        val licenseCidDO = LicenseCidDO(TEST_CID, null)

        `when`(licenseCidRepositoryMock.findByCid(TEST_CID)).thenReturn(null).thenReturn(licenseCidDO)

        try {
            oneNetDeviceServiceImplMock.enableOnenetDevice(TEST_CID)
        } catch (e: CskyException) {
            assert(e.code == Constant.PARAM_ERROR)
        }

        try {
            oneNetDeviceServiceImplMock.enableOnenetDevice(TEST_CID)
        } catch (e: CskyException) {
            assert(e.code == Constant.PARAM_ERROR)
        }
    }

    @Test
    fun enableOnenetDeviceTest2() {
        val testLicenseId: Long = 66
        val licenseCidDO = LicenseCidDO(TEST_CID, testLicenseId)

        `when`(licenseCidRepositoryMock.findByCid(TEST_CID)).thenReturn(licenseCidDO)

        val licenseDO = FactoryLicenseDO(TEST_CID, null)
        `when`(licenseRepositoryMock.findById(testLicenseId)).thenReturn(null).thenReturn(licenseDO)

        try {
            oneNetDeviceServiceImplMock.enableOnenetDevice(TEST_CID)
        } catch (e: CskyException) {
            assert(e.code == Constant.PARAM_ERROR)
        }

        try {
            oneNetDeviceServiceImplMock.enableOnenetDevice(TEST_CID)
        } catch (e: CskyException) {
            assert(e.code == Constant.PARAM_ERROR)
        }
    }

    @Test
    fun enableOnenetDeviceTest3() {
        val testLicenseId: Long = 66
        val testProductId: Long = 66
        val licenseCidDO = LicenseCidDO(TEST_CID, testLicenseId)

        `when`(licenseCidRepositoryMock.findByCid(TEST_CID)).thenReturn(licenseCidDO)

        val licenseDO = FactoryLicenseDO(TEST_CID, testProductId)
        `when`(licenseRepositoryMock.findById(testLicenseId)).thenReturn(licenseDO)

        `when`(productOneNetRepositoryMock.findByProductId(testProductId)).thenReturn(null)

        try {
            oneNetDeviceServiceImplMock.enableOnenetDevice(TEST_CID)
        } catch (e: CskyException) {
            assert(e.code == Constant.PARAM_ERROR)
        }
    }

    @Test
    fun enableOnenetDeviceTest4() {
        val testLicenseId: Long = 66
        val testProductId: Long = 66
        val licenseCidDO = LicenseCidDO(TEST_CID, testLicenseId)

        `when`(licenseCidRepositoryMock.findByCid(TEST_CID)).thenReturn(licenseCidDO)

        val licenseDO = FactoryLicenseDO(TEST_CID, testProductId)
        `when`(licenseRepositoryMock.findById(testLicenseId)).thenReturn(licenseDO)

        val productOneNetDO  = ProductOneNetDO(0, "", "apiKey")
        `when`(productOneNetRepositoryMock.findByProductId(testProductId)).thenReturn(productOneNetDO)

        val deviceOneNetDO = DeviceOneNetDO("","","",null)
        `when`( deviceOneNetRepositoryMock.findByCid(TEST_CID)).thenReturn(null).thenReturn(deviceOneNetDO)

        try {
            oneNetDeviceServiceImplMock.enableOnenetDevice(TEST_CID)
        } catch (e: CskyException) {
            assert(e.code == Constant.PARAM_ERROR)
        }

        try {
            oneNetDeviceServiceImplMock.enableOnenetDevice(TEST_CID)
        } catch (e: CskyException) {
            assert(e.code == Constant.PARAM_ERROR)
        }
    }

    @Test
    fun enableOnenetDeviceTest5() {
        val testLicenseId: Long = 66
        val testProductId: Long = 66
        val licenseCidDO = LicenseCidDO(TEST_CID, testLicenseId)

        `when`(licenseCidRepositoryMock.findByCid(TEST_CID)).thenReturn(licenseCidDO)

        val licenseDO = FactoryLicenseDO(TEST_CID, testProductId)
        `when`(licenseRepositoryMock.findById(testLicenseId)).thenReturn(licenseDO)

        val productOneNetDO  = ProductOneNetDO(0, "", "apiKey")
        `when`(productOneNetRepositoryMock.findByProductId(testProductId)).thenReturn(productOneNetDO)

        val deviceOneNetDO = DeviceOneNetDO("","","","imei")
        `when`( deviceOneNetRepositoryMock.findByCid(TEST_CID)).thenReturn(deviceOneNetDO)

        val onenetDeviceServiceImpl = OneNetDeviceServiceImpl(productOneNetRepositoryMock, deviceOneNetRepositoryMock,
                licenseCidRepositoryMock, licenseRepositoryMock, onenetManagerMock, redisUtil)

        onenetDeviceServiceImpl.enableOnenetDevice(TEST_CID)

        onenetDeviceServiceImpl.enableOnenetDevice(TEST_CID)

        redisUtil.hashDelete(redisUtil.SCOPE_ONENET_DEVICE_SHADOW_MAP, TEST_CID)
    }

    @Test
    fun disableOnenetDeviceTest1() {
        oneNetDeviceServiceImplMock.disableOnenetDevice(TEST_CID)
    }

    @Test
    fun pubMessageTest1() {
        val onenetDeviceServiceImpl = OneNetDeviceServiceImpl(productOneNetRepositoryMock, deviceOneNetRepositoryMock,
                licenseCidRepositoryMock, licenseRepositoryMock, onenetManagerMock, redisUtil)

        try {
            onenetDeviceServiceImpl.pubMessage(TEST_CID, "OK")
        } catch (e: CskyException) {
            assert(e.code == Constant.PARAM_ERROR)
        }

        redisUtil.hashPut(redisUtil.SCOPE_ONENET_DEVICE_SHADOW_MAP, TEST_CID, TEST_ONENET_DEVICE_SHADOW)
        `when`(onenetManagerMock.nbiotExecute(TEST_ONENET_DEVICE_SHADOW, "OK", NbiotShadow())).thenReturn("OK")
        onenetDeviceServiceImpl.pubMessage(TEST_CID, "OK")

        redisUtil.hashDelete(redisUtil.SCOPE_ONENET_DEVICE_SHADOW_MAP, TEST_CID)
    }
}