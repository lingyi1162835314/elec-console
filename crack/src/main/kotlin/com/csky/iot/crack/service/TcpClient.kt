package com.csky.iot.crack.service

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.csky.iot.crack.common.LogUtil
import com.csky.iot.crack.data.entity.CrackDO
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.net.UnknownHostException

@Service
class TcpClient: Socket() {
     var socket: Socket? = null

    @Async
    fun sendServerImage(crackDO: CrackDO, ip: String, port: Int) {
        LogUtil.i(javaClass, "sendServerImage begins")
        LogUtil.i(javaClass, "crackDO:${JSON.toJSONString(crackDO)}; ip:$ip; port:$port")

        val json = JSONObject()
        json["temperature"] = crackDO.temperature
        json["width"] = crackDO.width
        json["ossUrl"] = crackDO.ossUrl
        json["uploadTime"] = crackDO.createTime


        try {
            socket = Socket(ip, port)

            LogUtil.i(javaClass, "socket is connected")
            val output = DataOutputStream(socket!!.getOutputStream())
            val d  = json.toJSONString().toByteArray()
            output.write(json.toJSONString().toByteArray())

            LogUtil.i(javaClass, "send ends:${json.toJSONString()}")

        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (socket != null) {
                try {
                    socket!!.close()
                    LogUtil.i(javaClass, "socket is closed")
                } catch (e: IOException) { e.printStackTrace()}
            }
        }
    }
}