package com.csky.iot.crack.manager

import com.alibaba.fastjson.JSONObject
import com.csky.iot.crack.common.HttpClientUtil
import com.csky.iot.crack.common.LogUtil
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.net.URLEncoder
import org.apache.catalina.manager.StatusTransformer.setContentType
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType


interface CskyManager {
    fun checkNewVersion(model: String, version: String, cid: String): String?
}

@Service
class CskyManagerImpl(val restTemplate: RestTemplate) : CskyManager {

    override fun checkNewVersion(model: String, version: String, cid: String): String? {
        val requestJson = JSONObject()
        requestJson["cid"] = cid
        requestJson["version"] = version
        requestJson["model"] = model
        try {
            val httpClient = HttpClientUtil()
            val rst = httpClient.doPost("http://update.c-sky.com/api/image/ota/pull", requestJson)
            var rstJSON = JSONObject.parseObject(rst)
            if (rstJSON.getInteger("code") == 0) {
                rstJSON = rstJSON.getJSONObject("result")
                if (rstJSON != null) {
                    return rstJSON.getString("version")
                }
            }
            return null
        } catch (e: Exception) {
            LogUtil.e(javaClass, "通讯失败：update.c-sky.com/api/image/ota/pull", e)
        }

        return null
    }

}

