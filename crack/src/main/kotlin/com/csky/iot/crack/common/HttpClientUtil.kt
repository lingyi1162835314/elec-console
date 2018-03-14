package com.csky.iot.crack.common

import com.alibaba.fastjson.JSONObject
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils

import java.io.IOException
import java.util.ArrayList
import org.apache.http.entity.StringEntity
import java.nio.charset.Charset


/**
 * Created by yuzu on 17/9/20.
 */
class HttpClientUtil {
    fun doPost(url: String, jsonObj: JSONObject): String? {
        //创建HttpClientBuilder
        val httpClientBuilder = HttpClientBuilder.create()
        //HttpClient
        val closeableHttpClient = httpClientBuilder.build()
        var httpPost: HttpPost? = null
        var result: String? = null
        try {
            httpPost = HttpPost(url)
            httpPost.setHeader("Content-type", "application/json; charset=utf-8")

            val entity = StringEntity(jsonObj.toString(), Charset.forName("UTF-8"))
            httpPost.entity = entity

            val response = closeableHttpClient.execute(httpPost)
            if (response != null) {
                val resEntity = response.entity
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, "UTF-8")
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            try {
                closeableHttpClient.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return result
    }
}