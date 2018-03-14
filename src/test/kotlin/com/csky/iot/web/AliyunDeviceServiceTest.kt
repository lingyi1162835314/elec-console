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
import com.aliyuncs.DefaultAcsClient
import com.aliyuncs.iot.model.v20170420.CreateProductRequest
import com.aliyuncs.iot.model.v20170420.CreateProductResponse
import com.aliyuncs.iot.model.v20170420.UpdateProductRequest
import com.aliyuncs.iot.model.v20170420.UpdateProductResponse
import com.aliyuncs.profile.DefaultProfile
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import com.csky.iot.Application
import com.csky.iot.console.dao.DeviceRepository
import com.csky.iot.console.dao.LicenseCidRepository
import com.csky.iot.console.dao.LicenseRepository
import com.csky.iot.console.dao.ProductAliyunRepository
import com.csky.iot.console.dao.entity.DeviceDO
import com.csky.iot.console.dao.entity.FactoryLicenseDO
import com.csky.iot.console.dao.entity.LicenseCidDO
import com.csky.iot.console.dao.entity.ProductAliyunDO
import com.csky.iot.console.manager.AliyunManager
import com.csky.iot.console.manager.dto.AliyunMsgDTO
import com.csky.iot.console.service.AliyunDeviceServiceImpl
import com.csky.iot.console.service.entity.aliyun_iot.MnsTypeEnum
import com.csky.iot.constants.Constant
import com.csky.iot.exception.CskyException
import com.csky.iot.utils.LogUtil
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
class AliyunDeviceServiceTest : MockMvcResultMatchers() {
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
    lateinit var aliyunDeviceServiceImplMock: AliyunDeviceServiceImpl

    @Mock
    lateinit var licenseCidRepositoryMock: LicenseCidRepository

    @Mock
    lateinit var licenseRepositoryMock: LicenseRepository

    @Mock
    lateinit var deviceRepositoryMock: DeviceRepository

    @Mock
    lateinit var productAliyunRepositoryMock: ProductAliyunRepository

    @Mock
    lateinit var aliyunManagerMock: AliyunManager

    @Mock
    lateinit var redisUtilMock: RedisUtil

    val TEST_CID = TestData.TEST_CID
    val TEST_DEVICE_SECRET = TestData.TEST_DEVICE_SECRET
    val TEST_DEVICE_NAME = TestData.TEST_DEVICE_NAME
    val TEST_ACCESS_ID = TestData.TEST_ACCESS_ID
    val TEST_ACCESS_SECRET = TestData.TEST_ACCESS_SECRET
    val TEST_MNS_ENDPOINT = TestData.TEST_MNS_ENDPOINT
    val TEST_PRODUCT_KEY = TestData.TEST_PRODUCT_KEY
    val TEST_ALIYUN_DEVICE_SHADOW = TestData.TEST_ALIYUN_DEVICE_SHADOW

    @Test
    fun test1() {
        val accessKey = "LTAIkz2Iw6Dj5ZEP"//"LTAIUWYCLzCL7AKA"//"LTAIrGTPHmeA4EKu" //"LTAIUWYCLzCL7AKA"
        val accessSecret = "dpMqG6Y1zmCUwvnIB9mHMbLY61c7ZK "//"h6JAvA3pILwSLpQ289oUMrH4QEqeul"//"cb69bhmXYCmhZ2wr7tSdqgD7o9uysc" //"h6JAvA3pILwSLpQ289oUMrH4QEqeul"
        val productName = "yeahTESTTEST"

        DefaultProfile.addEndpoint("cn-shanghai", "cn-shanghai", "Iot", "iot.cn-shanghai.aliyuncs.com")
        val profile = DefaultProfile.getProfile("cn-shanghai", accessKey, accessSecret)
        val client = DefaultAcsClient(profile)
//        val request = CreateProductRequest()
//        request.encoding = "UTF-8"
//        request.name = productName
//
//        LogUtil.i(javaClass, "call Aliyun IoT to create product ${productName}")
//
//        val productKey: String
//        val response: CreateProductResponse
//
//        response = client.getAcsResponse(request)

        val request = UpdateProductRequest()
        request.encoding = "UTF-8"
        request.productName = "yeahTESTMODIFIED666"
        request.productKey = "nS3210EYqE1"
        LogUtil.i(javaClass, "call Aliyun IoT to update product")
        val response: UpdateProductResponse =  client.getAcsResponse(request)

        val c = 2

    }

    @Test(expected = CskyException::class)
    fun enableAliyunDeviceTest1() {
        `when`(deviceRepositoryMock.findByCid(TEST_CID)).thenReturn(null)

        aliyunDeviceServiceImplMock.enableAliyunDevice(TEST_CID)
    }

    @Test(expected = CskyException::class)
    fun enableAliyunDeviceTest2() {
        val testDeviceDO = DeviceDO(TEST_CID, null, 1, "", TEST_DEVICE_SECRET)
        `when`(deviceRepositoryMock.findByCid(TEST_CID)).thenReturn(testDeviceDO)

        try {
            aliyunDeviceServiceImplMock.enableAliyunDevice(TEST_CID)
        } catch (e:  CskyException) {
            assert(e.code == Constant.PARAM_ERROR)
        }

        testDeviceDO.name = TEST_DEVICE_NAME
        testDeviceDO.deviceSecret = null

        `when`(deviceRepositoryMock.findByCid(TEST_CID)).thenReturn(testDeviceDO)
        aliyunDeviceServiceImplMock.enableAliyunDevice(TEST_CID)
    }

    @Test(expected = CskyException::class)
    fun enableAliyunDeviceTest3() {
        val testDeviceDO = DeviceDO(TEST_CID, TEST_DEVICE_NAME, 1, "", TEST_DEVICE_SECRET)
        `when`(deviceRepositoryMock.findByCid(TEST_CID)).thenReturn(testDeviceDO)

        val licenseCidDO = LicenseCidDO(TEST_CID, null)
        `when`(licenseCidRepositoryMock.findByCid(TEST_CID)).thenReturn(null).thenReturn(licenseCidDO)

        try {
            aliyunDeviceServiceImplMock.enableAliyunDevice(TEST_CID)
        } catch (e:  CskyException) {
            assert(e.code == Constant.PARAM_ERROR)
        }

        aliyunDeviceServiceImplMock.enableAliyunDevice(TEST_CID)
    }

    @Test(expected = CskyException::class)
    fun enableAliyunDeviceTest4() {
        val testLicenseId: Long = 66
        val testDeviceDO = DeviceDO(TEST_CID, TEST_DEVICE_NAME, 1, "", TEST_DEVICE_SECRET)
        val licenseCidDO = LicenseCidDO(TEST_CID, testLicenseId)
        val licenseDO = FactoryLicenseDO(TEST_CID, null)

        `when`(deviceRepositoryMock.findByCid(TEST_CID)).thenReturn(testDeviceDO)
        `when`(licenseCidRepositoryMock.findByCid(TEST_CID)).thenReturn(licenseCidDO)
        `when`(licenseRepositoryMock.findById(testLicenseId)).thenReturn(licenseDO)

        aliyunDeviceServiceImplMock.enableAliyunDevice(TEST_CID)
    }

    @Test(expected = CskyException::class)
    fun enableAliyunDeviceTest5() {
        val testLicenseId: Long = 66
        val testDeviceDO = DeviceDO(TEST_CID, TEST_DEVICE_NAME, 1, "", TEST_DEVICE_SECRET)
        val licenseCidDO = LicenseCidDO(TEST_CID, 656)
        val licenseDO = FactoryLicenseDO(TEST_CID, null)

        `when`(deviceRepositoryMock.findByCid(TEST_CID)).thenReturn(testDeviceDO)
        `when`(licenseCidRepositoryMock.findByCid(TEST_CID)).thenReturn(licenseCidDO)
        `when`(licenseRepositoryMock.findById(testLicenseId)).thenReturn(null).thenReturn(licenseDO)

        try {
            aliyunDeviceServiceImplMock.enableAliyunDevice(TEST_CID)
        } catch (e: CskyException) {
            assert(e.code == Constant.PARAM_ERROR)    
        }

        `when`(licenseRepositoryMock.findById(testLicenseId)).thenReturn(licenseDO)
        aliyunDeviceServiceImplMock.enableAliyunDevice(TEST_CID)
    }

    @Test(expected = CskyException::class)
    fun enableAliyunDeviceTest6() {
        val testLicenseId: Long = 66
        val testDeviceDO = DeviceDO(TEST_CID, TEST_DEVICE_NAME, 1, "", TEST_DEVICE_SECRET)
        val licenseCidDO = LicenseCidDO(TEST_CID, 656)
        val licenseDO = FactoryLicenseDO(TEST_CID, null)

        `when`(deviceRepositoryMock.findByCid(TEST_CID)).thenReturn(testDeviceDO)
        `when`(licenseCidRepositoryMock.findByCid(TEST_CID)).thenReturn(licenseCidDO)
        `when`(licenseRepositoryMock.findById(testLicenseId)).thenReturn(null).thenReturn(licenseDO)

        try {
            aliyunDeviceServiceImplMock.enableAliyunDevice(TEST_CID)
        } catch (e: CskyException) {
            assert(e.code == Constant.PARAM_ERROR)
        }

        aliyunDeviceServiceImplMock.enableAliyunDevice(TEST_CID)
    }

    @Test(expected = CskyException::class)
    fun enableAliyunDeviceTest7() {
        val testLicenseId: Long = 66
        val testProductId: Long = 66
        val testDeviceDO = DeviceDO(TEST_CID, TEST_DEVICE_NAME, 1, "", TEST_DEVICE_SECRET)
        val licenseCidDO = LicenseCidDO(TEST_CID, testLicenseId)
        val licenseDO = FactoryLicenseDO(TEST_CID, testProductId)

        `when`(deviceRepositoryMock.findByCid(TEST_CID)).thenReturn(testDeviceDO)
        `when`(licenseCidRepositoryMock.findByCid(TEST_CID)).thenReturn(licenseCidDO)
        `when`(licenseRepositoryMock.findById(testLicenseId)).thenReturn(licenseDO)
        `when`(productAliyunRepositoryMock.findByProductId(66)).thenReturn(null)

        aliyunDeviceServiceImplMock.enableAliyunDevice(TEST_CID)
    }

    @Test
    fun enableAliyunDeviceTest8() {
        val testLicenseId: Long = 66
        val testProductId: Long = 66
        val testDeviceDO = DeviceDO(TEST_CID, TEST_DEVICE_NAME, 1, "", TEST_DEVICE_SECRET)
        val licenseCidDO = LicenseCidDO(TEST_CID, testLicenseId)
        val licenseDO = FactoryLicenseDO(TEST_CID, testProductId)
        val productAliyunD0 = ProductAliyunDO(66, null, null, TEST_PRODUCT_KEY,
                 null, TEST_ACCESS_SECRET, null)

        `when`(deviceRepositoryMock.findByCid(TEST_CID)).thenReturn(testDeviceDO)
        `when`(licenseCidRepositoryMock.findByCid(TEST_CID)).thenReturn(licenseCidDO)
        `when`(licenseRepositoryMock.findById(testLicenseId)).thenReturn(licenseDO)
        `when`(productAliyunRepositoryMock.findByProductId(66)).thenReturn(productAliyunD0)
        try {
            aliyunDeviceServiceImplMock.enableAliyunDevice(TEST_CID)
        } catch (e: CskyException) {
            assert(e.code == Constant.PARAM_ERROR)
        }

        productAliyunD0.accessKey = TEST_ACCESS_ID
        productAliyunD0.accessSecret = null
        try {
            aliyunDeviceServiceImplMock.enableAliyunDevice(TEST_CID)
        } catch (e: CskyException) {
            assert(e.code == Constant.PARAM_ERROR)
        }

        productAliyunD0.accessSecret = TEST_ACCESS_SECRET
        productAliyunD0.productKey = null
        try {
            aliyunDeviceServiceImplMock.enableAliyunDevice(TEST_CID)
        } catch (e: CskyException) {
            assert(e.code == Constant.PARAM_ERROR)
        }

        productAliyunD0.productKey = TEST_PRODUCT_KEY
        try {
            aliyunDeviceServiceImplMock.enableAliyunDevice(TEST_CID)
        } catch (e: CskyException) {
            assert(e.code == Constant.PARAM_ERROR)
        }
    }

    @Test(expected = CskyException::class)
    fun enableAliyunDeviceTest9() {
        val testLicenseId: Long = 66
        val testProductId: Long = 66
        val testDeviceDO = DeviceDO(TEST_CID, TEST_DEVICE_NAME, 1, "", TEST_DEVICE_SECRET)
        val licenseCidDO = LicenseCidDO(TEST_CID, testLicenseId)
        val licenseDO = FactoryLicenseDO(TEST_CID, testProductId)
        val productAliyunD0 = ProductAliyunDO(66, null, null, TEST_PRODUCT_KEY,
                TEST_ACCESS_ID, TEST_ACCESS_SECRET, TEST_MNS_ENDPOINT)

        `when`(deviceRepositoryMock.findByCid(TEST_CID)).thenReturn(testDeviceDO)
        `when`(licenseCidRepositoryMock.findByCid(TEST_CID)).thenReturn(licenseCidDO)
        `when`(licenseRepositoryMock.findById(testLicenseId)).thenReturn(licenseDO)
        `when`(productAliyunRepositoryMock.findByProductId(66)).thenReturn(productAliyunD0)

        `when`(aliyunManagerMock.isQueueExist(TEST_ALIYUN_DEVICE_SHADOW)).thenReturn(false)

        aliyunDeviceServiceImplMock.enableAliyunDevice(TEST_CID)
    }

    @Test
    fun enableAliyunDeviceTest10() {
        val testLicenseId: Long = 66
        val testProductId: Long = 66
        val testDeviceDO = DeviceDO(TEST_CID, TEST_DEVICE_NAME, 1, "", TEST_DEVICE_SECRET)
        val licenseCidDO = LicenseCidDO(TEST_CID, testLicenseId)
        val licenseDO = FactoryLicenseDO(TEST_CID, testProductId)
        val productAliyunD0 = ProductAliyunDO(66, null, null, TEST_PRODUCT_KEY,
                TEST_ACCESS_ID, TEST_ACCESS_SECRET, TEST_MNS_ENDPOINT)

        `when`(deviceRepositoryMock.findByCid(TEST_CID)).thenReturn(testDeviceDO)
        `when`(licenseCidRepositoryMock.findByCid(TEST_CID)).thenReturn(licenseCidDO)
        `when`(licenseRepositoryMock.findById(testLicenseId)).thenReturn(licenseDO)
        `when`(productAliyunRepositoryMock.findByProductId(66)).thenReturn(productAliyunD0)

        `when`(aliyunManagerMock.isQueueExist(TEST_ALIYUN_DEVICE_SHADOW)).thenReturn(true)

        val aliyunDeviceServiceImpl = AliyunDeviceServiceImpl(licenseCidRepositoryMock, licenseRepositoryMock, deviceRepositoryMock,
                productAliyunRepositoryMock, aliyunManagerMock, redisUtil)

        aliyunDeviceServiceImpl.enableAliyunDevice(TEST_CID)

        aliyunDeviceServiceImpl.enableAliyunDevice(TEST_CID)

        redisUtil.hashDelete(redisUtil.SCOPE_ALIYUN_DEVICE_SHADOW_MAP, TEST_CID)
    }

    @Test
    fun disableAliyunDeviceTest1() {
        aliyunDeviceServiceImplMock.disableAliyunDevice(TEST_CID)
    }

    @Test
    fun aliyunDeviceStatusProTest1() {
        val aliyunMsgDTO = AliyunMsgDTO( MnsTypeEnum.UPLOAD, JSONObject(), TEST_CID)
        val aliyunDeviceServiceImpl = AliyunDeviceServiceImpl(licenseCidRepositoryMock, licenseRepositoryMock, deviceRepositoryMock,
                productAliyunRepositoryMock, aliyunManagerMock, redisUtil)

        aliyunDeviceServiceImpl.aliyunDeviceStatusPro(aliyunMsgDTO)
    }

    @Test
    fun aliyunDeviceStatusProTest2() {
        val aliyunMsgDTO = AliyunMsgDTO( MnsTypeEnum.UPLOAD, JSONObject(), TEST_CID)
        redisUtil.hashPut(redisUtil.SCOPE_ALIYUN_DEVICE_SHADOW_MAP, TEST_CID, TEST_ALIYUN_DEVICE_SHADOW)

        val aliyunDeviceServiceImpl = AliyunDeviceServiceImpl(licenseCidRepositoryMock, licenseRepositoryMock, deviceRepositoryMock,
                productAliyunRepositoryMock, aliyunManagerMock, redisUtil)

        aliyunDeviceServiceImpl.aliyunDeviceStatusPro(aliyunMsgDTO)

        redisUtil.hashDelete(redisUtil.SCOPE_ALIYUN_DEVICE_SHADOW_MAP, TEST_CID)
    }

    @Test
    fun aliyunDeviceStatusProTest3() {
        val msgData = JSONObject()
        msgData["status"] = "offline"
        val aliyunMsgDTO = AliyunMsgDTO( MnsTypeEnum.STATUS, msgData, TEST_CID)
        redisUtil.hashPut(redisUtil.SCOPE_ALIYUN_DEVICE_SHADOW_MAP, TEST_CID, TEST_ALIYUN_DEVICE_SHADOW)

        val aliyunDeviceServiceImpl = AliyunDeviceServiceImpl(licenseCidRepositoryMock, licenseRepositoryMock, deviceRepositoryMock,
                productAliyunRepositoryMock, aliyunManagerMock, redisUtil)

        aliyunDeviceServiceImpl.aliyunDeviceStatusPro(aliyunMsgDTO)

        redisUtil.hashDelete(redisUtil.SCOPE_ALIYUN_DEVICE_SHADOW_MAP, TEST_CID)
    }

    @Test
    fun aliyunDeviceStatusProTest4() {
        val msgData = JSONObject()
        msgData["status"] = "online"
        val testAliyunDeviceShadow = TEST_ALIYUN_DEVICE_SHADOW.copy()
        testAliyunDeviceShadow.status = true
        val aliyunMsgDTO = AliyunMsgDTO( MnsTypeEnum.STATUS, msgData, TEST_CID)
        redisUtil.hashPut(redisUtil.SCOPE_ALIYUN_DEVICE_SHADOW_MAP, TEST_CID, testAliyunDeviceShadow)

        val aliyunDeviceServiceImpl = AliyunDeviceServiceImpl(licenseCidRepositoryMock, licenseRepositoryMock, deviceRepositoryMock,
                productAliyunRepositoryMock, aliyunManagerMock, redisUtil)

        aliyunDeviceServiceImpl.aliyunDeviceStatusPro(aliyunMsgDTO)

        redisUtil.hashDelete(redisUtil.SCOPE_ALIYUN_DEVICE_SHADOW_MAP, TEST_CID)
    }

    @Test
    fun aliyunDeviceStatusProTest5() {
        val msgData = JSONObject()
        val aliyunMsgDTO = AliyunMsgDTO( MnsTypeEnum.UPLOAD, msgData, TEST_CID)
        redisUtil.hashPut(redisUtil.SCOPE_ALIYUN_DEVICE_SHADOW_MAP, TEST_CID, TEST_ALIYUN_DEVICE_SHADOW)

        val aliyunDeviceServiceImpl = AliyunDeviceServiceImpl(licenseCidRepositoryMock, licenseRepositoryMock, deviceRepositoryMock,
                productAliyunRepositoryMock, aliyunManagerMock, redisUtil)

        aliyunDeviceServiceImpl.aliyunDeviceStatusPro(aliyunMsgDTO)

        redisUtil.hashDelete(redisUtil.SCOPE_ALIYUN_DEVICE_SHADOW_MAP, TEST_CID)
    }
    
}