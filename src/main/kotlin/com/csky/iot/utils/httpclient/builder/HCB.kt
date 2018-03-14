package com.csky.iot.utils.httpclient.builder

import com.csky.iot.utils.httpclient.exception.HttpProcessException
import java.io.InterruptedIOException
import java.net.UnknownHostException

import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException

import org.apache.http.HttpEntityEnclosingRequest
import org.apache.http.HttpHost
import org.apache.http.NoHttpResponseException
import org.apache.http.client.HttpRequestRetryHandler
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.conn.ConnectTimeoutException
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.socket.PlainConnectionSocketFactory
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.conn.DefaultProxyRoutePlanner
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.config.RegistryBuilder

class HCB internal constructor() : HttpClientBuilder() {

    var isSetPool = false//记录是否设置了连接池

    fun custom(): HCB {
        return HCB()
    }

    /**
     * 设置超时时间以及是否允许网页重定向（自动跳转 302）

     * @param timeout        超时时间，单位-毫秒
     * *
     * @param redirectEnable        自动跳转
     * *
     * @return
     */
    @JvmOverloads fun timeout(timeout: Int, redirectEnable: Boolean = true): HCB {
        // 配置请求的超时设置
        val config = RequestConfig.custom().setConnectionRequestTimeout(timeout).setConnectTimeout(timeout).setSocketTimeout(timeout).setRedirectsEnabled(redirectEnable).build()
        return this.setDefaultRequestConfig(config) as HCB
    }

    /**
     * 设置连接池（默认开启https）

     * @param maxTotal                    最大连接数
     * *
     * @param defaultMaxPerRoute    每个路由默认连接数
     * *
     * @return
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    fun pool(maxTotal: Int, defaultMaxPerRoute: Int): HCB {
        val socketFactoryRegistry = RegistryBuilder.create<ConnectionSocketFactory>().register("http", PlainConnectionSocketFactory.INSTANCE).build()
        //设置连接池大小
        val connManager = PoolingHttpClientConnectionManager(socketFactoryRegistry)
        connManager.maxTotal = maxTotal// Increase max total connection to $maxTotal
        connManager.defaultMaxPerRoute = defaultMaxPerRoute// Increase default max connection per route to $defaultMaxPerRoute
        //connManager.setMaxPerRoute(route, max);// Increase max connections for $route(eg：localhost:80) to 50
        isSetPool = true
        return this.setConnectionManager(connManager) as HCB
    }

    /**
     * 设置代理

     * @param hostOrIP        代理host或者ip
     * *
     * @param port            代理端口
     * *
     * @return
     */
    fun proxy(hostOrIP: String, port: Int): HCB {
        // 依次是代理地址，代理端口号，协议类型
        val proxy = HttpHost(hostOrIP, port, "http")
        val routePlanner = DefaultProxyRoutePlanner(proxy)
        return this.setRoutePlanner(routePlanner) as HCB
    }

//    /**
//     * 重试（如果请求是幂等的，就再次尝试）
//
//     * @param tryTimes                        重试次数
//     * *
//     * @param retryWhenInterruptedIO        连接拒绝时，是否重试
//     * *
//     * @return
//     */
//    @JvmOverloads fun retry(tryTimes: Int, retryWhenInterruptedIO: Boolean = false): HCB {
//        // 请求重试处理
//        val httpRequestRetryHandler = HttpRequestRetryHandler { exception, executionCount, context ->
//            if (executionCount >= tryTimes) {// 如果已经重试了n次，就放弃
//                return@HttpRequestRetryHandler false
//            }
//            if (exception is NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
//                return@HttpRequestRetryHandler true
//            }
//            if (exception is SSLHandshakeException) {// 不要重试SSL握手异常
//                return@HttpRequestRetryHandler false
//            }
//            if (exception is InterruptedIOException) {// 超时
//                //return false;
//                return@HttpRequestRetryHandler retryWhenInterruptedIO
//            }
//            if (exception is UnknownHostException) {// 目标服务器不可达
//                return@HttpRequestRetryHandler true
//            }
//            if (exception is ConnectTimeoutException) {// 连接被拒绝
//                return@HttpRequestRetryHandler false
//            }
//            if (exception is SSLException) {// SSL握手异常
//                return@HttpRequestRetryHandler false
//            }
//
//            val clientContext = HttpClientContext.adapt(context)
//            val request = clientContext.request as? HttpEntityEnclosingRequest ?: return@HttpRequestRetryHandler true
//            // 如果请求是幂等的，就再次尝试
//            false
//        }
//        this.setRetryHandler(httpRequestRetryHandler)
//        return this
//    }
}
