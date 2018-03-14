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
import com.csky.iot.console.service.ConsoleService
import com.csky.iot.console.web.ConsoleController
import com.csky.iot.console.web.vo.DeviceVO
import com.csky.iot.console.web.vo.SendMessageVO
import com.csky.iot.constants.Constant
import com.csky.iot.exception.CskyException
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
import java.net.URI
import javax.websocket.ContainerProvider

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(classes = arrayOf(Application::class))
@WebAppConfiguration
@ComponentScan("com.csky.iot")
class ConsoleControllerTest : MockMvcResultMatchers() {
    var mvc: MockMvc? = null

    @Autowired
    var wac: WebApplicationContext? = null

    @Before
    fun setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build()
    }

    @InjectMocks
    lateinit var consoleControllerMock: ConsoleController

    @Mock
    lateinit var consoleServiceMock: ConsoleService

    @Mock
    lateinit var websocketUtilMock: WebsocketUtil

    val TEST_CID = TestData.TEST_CID

    @Test
    fun devEnableTest1() {
        `when`(consoleServiceMock.enableDevice(TEST_CID))
                .thenThrow(CskyException(Constant.PARAM_ERROR, "enableMnsQueue failed"))

        val devEnableVO = DeviceVO()
        devEnableVO.cid = TEST_CID

        consoleControllerMock.devEnable(devEnableVO)
    }

    @Test
    fun devEnableTest2() {
        val logJson = JSONObject()
        logJson["msgType"] = "debugInfo"
        logJson["payload"] = "Enable device succeed"

        val devEnableVO = DeviceVO()
        devEnableVO.cid = TEST_CID

        consoleControllerMock.devEnable(devEnableVO)
    }

    @Test
    fun sendMessageTest1() {
        `when`(consoleServiceMock.sendMessageToDevice(TEST_CID, "ok"))
                .thenThrow(CskyException(Constant.PARAM_ERROR, "error"))

        val sendMessageVO = SendMessageVO()
        sendMessageVO.cid = TEST_CID
        sendMessageVO.message = "ok"

        consoleControllerMock.sendMessage(sendMessageVO)
    }

    @Test
    fun sendMessageTest2() {
        val sendMessageVO = SendMessageVO()
        sendMessageVO.cid = TEST_CID
        sendMessageVO.message = "ok"

        consoleControllerMock.sendMessage(sendMessageVO)
    }

  //  @Test
    fun devEnableTest99() {
        try {
            val container = ContainerProvider.getWebSocketContainer()
            val uri = "ws://localhost:5050/device/console/websocket"
            val session = container.connectToServer(WebsocketClient::class.java, URI(uri))
            val lf: Char = 10.toChar() // 这个是换行
            val nl: Char = 0.toChar() // 这个是消息结尾的标记，一定要
            val sb = StringBuilder()
            sb.append("SEND").append(lf) // 请求的命令策略
            sb.append("destination:/app/devEnable").append(lf) // 请求的资源
            sb.append("content-length:13").append(lf).append(lf) // 消息体的长度
            sb.append("{\"cid\":\"123\"}").append(nl) // 消息体

            session.basicRemote.sendText(sb.toString()) // 发送消息
         //   Thread.sleep(50000) // 等待一小会
            session.close() // 关闭连接
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}