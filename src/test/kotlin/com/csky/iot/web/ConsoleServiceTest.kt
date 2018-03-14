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

import com.csky.iot.Application
import com.csky.iot.console.dao.LicenseCidRepository
import com.csky.iot.console.dao.LicenseRepository
import com.csky.iot.console.dao.ProductRepository
import com.csky.iot.console.dao.entity.FactoryLicenseDO
import com.csky.iot.console.dao.entity.LicenseCidDO
import com.csky.iot.console.service.*
import com.csky.iot.constants.Constant
import com.csky.iot.data.entity.ProductDO
import com.csky.iot.exception.CskyException
import com.csky.iot.utils.RedisUtil
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.redis.core.RedisTemplate
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
class ConsoleServiceTest : MockMvcResultMatchers() {
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
    lateinit var consoleServiceImplMock: ConsoleServiceImpl

    @Mock
    lateinit var aliyunDeviceServiceMock: AliyunDeviceService

    @Mock
    lateinit var oneNetDeviceServiceMock: OneNetDeviceService

    @Mock
    lateinit var licenseCidRepository: LicenseCidRepository

    @Mock
    lateinit var licenseRepository: LicenseRepository

    @Mock
    lateinit var productRepository: ProductRepository

    val TEST_CID = TestData.TEST_CID

    @Test
    fun test() {
        redisUtil.remove(redisUtil.SCOPE_ALIYUN_DEVICE_SHADOW_MAP)
        redisUtil.remove(redisUtil.SCOPE_ONENET_DEVICE_SHADOW_MAP)
    }

    @Test(expected = CskyException::class)
    fun enableMnsDeviceConsoleTest1() {
        `when`(licenseCidRepository.findByCid(TEST_CID)).thenReturn(null)

        consoleServiceImplMock.enableDevice(TEST_CID)
    }

    @Test(expected = CskyException::class)
    fun enableMnsDeviceConsoleTest2() {
        `when`(licenseCidRepository.findByCid(TEST_CID)).thenReturn(null)

        try {
            consoleServiceImplMock.enableDevice(TEST_CID)
        } catch (e: CskyException) {
            assert(e.code == Constant.PARAM_ERROR)
        }

        val licenseCidDO = LicenseCidDO(TEST_CID, null)
        `when`(licenseCidRepository.findByCid(TEST_CID)).thenReturn(licenseCidDO)

        consoleServiceImplMock.enableDevice(TEST_CID)
    }

    @Test(expected = CskyException::class)
    fun enableMnsDeviceConsoleTest3() {
        val testLicenseId: Long = 66
        val licenseCidDO = LicenseCidDO(TEST_CID, testLicenseId)
        `when`(licenseCidRepository.findByCid(TEST_CID)).thenReturn(licenseCidDO)
        `when`(licenseRepository.findById(testLicenseId)).thenReturn(null)

        try {
            consoleServiceImplMock.enableDevice(TEST_CID)
        } catch (e: CskyException) {
            assert(e.code == Constant.PARAM_ERROR)
        }

        val factoryLicenseDO = FactoryLicenseDO(TEST_CID, null)
        `when`(licenseRepository.findById(testLicenseId)).thenReturn(factoryLicenseDO)

        consoleServiceImplMock.enableDevice(TEST_CID)
    }

    @Test(expected = CskyException::class)
    fun enableMnsDeviceConsoleTest4() {
        val testLicenseId: Long = 66
        val licenseCidDO = LicenseCidDO(TEST_CID, testLicenseId)
        `when`(licenseCidRepository.findByCid(TEST_CID)).thenReturn(licenseCidDO)

        val testProductId: Long = 99
        val factoryLicenseDO = FactoryLicenseDO(TEST_CID, testProductId)
        `when`(licenseRepository.findById(testLicenseId)).thenReturn(factoryLicenseDO)

        `when`(productRepository.getOne(testProductId)).thenReturn(null)
        consoleServiceImplMock.enableDevice(TEST_CID)
    }

    @Test
    fun enableMnsDeviceConsoleTest5() {
        val testLicenseId: Long = 66
        val licenseCidDO = LicenseCidDO(TEST_CID, testLicenseId)
        `when`(licenseCidRepository.findByCid(TEST_CID)).thenReturn(licenseCidDO)

        val testProductId: Long = 99
        val factoryLicenseDO = FactoryLicenseDO(TEST_CID, testProductId)
        `when`(licenseRepository.findById(testLicenseId)).thenReturn(factoryLicenseDO)

        val productDO = ProductDO()
        productDO.iotType = 1
        `when`(productRepository.getOne(testProductId)).thenReturn(productDO)
        consoleServiceImplMock.enableDevice(TEST_CID)

        productDO.iotType = 2
        `when`(productRepository.getOne(testProductId)).thenReturn(productDO)
        consoleServiceImplMock.enableDevice(TEST_CID)

        productDO.iotType = 3
        `when`(productRepository.getOne(testProductId)).thenReturn(productDO)
        consoleServiceImplMock.enableDevice(TEST_CID)

        productDO.iotType = 4
        `when`(productRepository.getOne(testProductId)).thenReturn(productDO)
        try {
            consoleServiceImplMock.enableDevice(TEST_CID)
        } catch (e: CskyException) {
            assert(e.code == Constant.PARAM_ERROR)
        }
    }

    @Test
    fun disableDeviceTest1() {
        val testLicenseId: Long = 66
        val licenseCidDO = LicenseCidDO(TEST_CID, testLicenseId)
        `when`(licenseCidRepository.findByCid(TEST_CID)).thenReturn(licenseCidDO)

        val testProductId: Long = 99
        val factoryLicenseDO = FactoryLicenseDO(TEST_CID, testProductId)
        `when`(licenseRepository.findById(testLicenseId)).thenReturn(factoryLicenseDO)

        val productDO = ProductDO()
        productDO.iotType = 1
        `when`(productRepository.getOne(testProductId)).thenReturn(productDO)
        consoleServiceImplMock.disableDevice(TEST_CID)

        productDO.iotType = 2
        `when`(productRepository.getOne(testProductId)).thenReturn(productDO)
        consoleServiceImplMock.disableDevice(TEST_CID)

        productDO.iotType = 3
        `when`(productRepository.getOne(testProductId)).thenReturn(productDO)
        consoleServiceImplMock.disableDevice(TEST_CID)

        productDO.iotType = 4
        `when`(productRepository.getOne(testProductId)).thenReturn(productDO)
        try {
            consoleServiceImplMock.disableDevice(TEST_CID)
        } catch (e: CskyException) {
            assert(e.code == Constant.PARAM_ERROR)
        }
    }

    @Test
    fun sendMessageToDeviceTest1() {
        val testLicenseId: Long = 66
        val licenseCidDO = LicenseCidDO(TEST_CID, testLicenseId)
        `when`(licenseCidRepository.findByCid(TEST_CID)).thenReturn(licenseCidDO)

        val testProductId: Long = 99
        val factoryLicenseDO = FactoryLicenseDO(TEST_CID, testProductId)
        `when`(licenseRepository.findById(testLicenseId)).thenReturn(factoryLicenseDO)

        val productDO = ProductDO()
        productDO.iotType = 1
        `when`(productRepository.getOne(testProductId)).thenReturn(productDO)
        try {
            consoleServiceImplMock.sendMessageToDevice(TEST_CID, "ok")
        } catch (e: CskyException) {
            assert(e.code == Constant.PARAM_ERROR)
        }

        productDO.iotType = 3
        `when`(productRepository.getOne(testProductId)).thenReturn(productDO)
        try {
            consoleServiceImplMock.sendMessageToDevice(TEST_CID, "ok")
        } catch (e: CskyException) {
            assert(e.code == Constant.PARAM_ERROR)
        }

        productDO.iotType = 4
        `when`(productRepository.getOne(testProductId)).thenReturn(productDO)
        try {
            consoleServiceImplMock.sendMessageToDevice(TEST_CID, "ok")
        } catch (e: CskyException) {
            assert(e.code == Constant.PARAM_ERROR)
        }

        `when`(oneNetDeviceServiceMock.pubMessage(TEST_CID, "ok")).thenReturn("OK")
        productDO.iotType = 2
        `when`(productRepository.getOne(testProductId)).thenReturn(productDO)
        val res = consoleServiceImplMock.sendMessageToDevice(TEST_CID, "ok")
        assert(res == "OK")
    }
}