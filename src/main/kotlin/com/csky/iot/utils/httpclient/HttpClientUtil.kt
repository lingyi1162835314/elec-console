package com.csky.iot.utils.httpclient

import java.io.IOException
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpOptions
import org.apache.http.client.methods.HttpPatch
import org.apache.http.client.methods.HttpTrace
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpPut
import org.apache.http.client.methods.HttpHead
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpRequestBase
import com.csky.iot.utils.httpclient.exception.HttpProcessException
import org.apache.http.util.EntityUtils
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase
import java.util.ArrayList
import com.csky.iot.utils.LogUtil
import com.csky.iot.utils.httpclient.builder.HCB
import com.csky.iot.utils.httpclient.common.HttpConfig
import com.csky.iot.utils.httpclient.common.HttpMethods
import com.csky.iot.utils.httpclient.common.Utils
import org.apache.http.Header
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.HttpClient
import org.apache.http.protocol.HttpContext
import java.io.OutputStream


object HttpClientUtil {

    //默认采用的http协议的HttpClient对象
    private var client4HTTP: HttpClient? = null

    init {
        try {
            client4HTTP = HCB().custom().build()
        } catch (e: HttpProcessException) {
            LogUtil.e(javaClass, "创建https协议的HttpClient对象出错!")
        }

    }

    /**
     * 判定是否开启连接池、及url是http还是https <br></br>
     * 如果已开启连接池，则自动调用build方法，从连接池中获取client对象<br></br>
     * 否则，直接返回相应的默认client对象<br></br>

     * @param config        请求参数配置
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    private fun create(config: HttpConfig) {
        if (config.client() == null) {//如果为空，设为默认client对象
            config.client(client4HTTP!!)
        }
    }

    /**
     * 以Get方式，请求资源或服务

     * @param client                client对象
     * *
     * @param url                    资源地址
     * *
     * @param headers            请求头信息
     * *
     * @param context            http上下文，用于cookie操作
     * *
     * @param encoding        编码
     * *
     * @return                        返回处理结果
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    operator fun get(client: HttpClient, url: String, headers: Array<Header?>, context: HttpContext, encoding: String): String {
        return get(HttpConfig.custom().client(client).url(url).headers(headers).context(context).encoding(encoding))
    }

    /**
     * 以Get方式，请求资源或服务

     * @param config        请求参数配置
     * *
     * @return
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    operator fun get(config: HttpConfig): String {
        return send(config.method(HttpMethods.GET))
    }

    /**
     * 以Post方式，请求资源或服务

     * @param client                client对象
     * *
     * @param url                    资源地址
     * *
     * @param headers            请求头信息
     * *
     * @param parasMap        请求参数
     * *
     * @param context            http上下文，用于cookie操作
     * *
     * @param encoding        编码
     * *
     * @return                        返回处理结果
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    fun post(client: HttpClient, url: String, headers: Array<Header?>, parasMap: MutableMap<String, Any>, context: HttpContext, encoding: String): String {
        return post(HttpConfig.custom().client(client).url(url).headers(headers).map(parasMap).context(context).encoding(encoding))
    }

    /**
     * 以Post方式，请求资源或服务

     * @param config        请求参数配置
     * *
     * @return
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    fun post(config: HttpConfig): String {
        return send(config.method(HttpMethods.POST))
    }

    /**
     * 以Put方式，请求资源或服务

     * @param client                client对象
     * *
     * @param url                    资源地址
     * *
     * @param parasMap        请求参数
     * *
     * @param headers            请求头信息
     * *
     * @param context            http上下文，用于cookie操作
     * *
     * @param encoding        编码
     * *
     * @return                        返回处理结果
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    fun put(client: HttpClient, url: String, parasMap: MutableMap<String, Any>, headers: Array<Header?>, context: HttpContext, encoding: String): String {
        return put(HttpConfig.custom().client(client).url(url).headers(headers).map(parasMap).context(context).encoding(encoding))
    }

    /**
     * 以Put方式，请求资源或服务

     * @param config        请求参数配置
     * *
     * @return
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    fun put(config: HttpConfig): String {
        return send(config.method(HttpMethods.PUT))
    }

    /**
     * 以Delete方式，请求资源或服务

     * @param client                client对象
     * *
     * @param url                    资源地址
     * *
     * @param headers            请求头信息
     * *
     * @param context            http上下文，用于cookie操作
     * *
     * @param encoding        编码
     * *
     * @return                        返回处理结果
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    fun delete(client: HttpClient, url: String, headers: Array<Header?>, context: HttpContext, encoding: String): String {
        return delete(HttpConfig.custom().client(client).url(url).headers(headers).context(context).encoding(encoding))
    }

    /**
     * 以Delete方式，请求资源或服务

     * @param config        请求参数配置
     * *
     * @return
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    fun delete(config: HttpConfig): String {
        return send(config.method(HttpMethods.DELETE))
    }

    /**
     * 以Patch方式，请求资源或服务

     * @param client                client对象
     * *
     * @param url                    资源地址
     * *
     * @param parasMap        请求参数
     * *
     * @param headers            请求头信息
     * *
     * @param context            http上下文，用于cookie操作
     * *
     * @param encoding        编码
     * *
     * @return                        返回处理结果
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    fun patch(client: HttpClient, url: String, parasMap: MutableMap<String, Any>, headers: Array<Header?>, context: HttpContext, encoding: String): String {
        return patch(HttpConfig.custom().client(client).url(url).headers(headers).map(parasMap).context(context).encoding(encoding))
    }

    /**
     * 以Patch方式，请求资源或服务

     * @param config        请求参数配置
     * *
     * @return
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    fun patch(config: HttpConfig): String {
        return send(config.method(HttpMethods.PATCH))
    }

    /**
     * 以Head方式，请求资源或服务

     * @param client                client对象
     * *
     * @param url                    资源地址
     * *
     * @param headers            请求头信息
     * *
     * @param context            http上下文，用于cookie操作
     * *
     * @param encoding        编码
     * *
     * @return                        返回处理结果
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    fun head(client: HttpClient, url: String, headers: Array<Header?>, context: HttpContext, encoding: String): String {
        return head(HttpConfig.custom().client(client).url(url).headers(headers).context(context).encoding(encoding))
    }

    /**
     * 以Head方式，请求资源或服务

     * @param config        请求参数配置
     * *
     * @return
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class) fun head(config: HttpConfig): String {
        return send(config.method(HttpMethods.HEAD))
    }

    /**
     * 以Options方式，请求资源或服务

     * @param client                client对象
     * *
     * @param url                    资源地址
     * *
     * @param headers            请求头信息
     * *
     * @param context            http上下文，用于cookie操作
     * *
     * @param encoding        编码
     * *
     * @return                        返回处理结果
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    fun options(client: HttpClient, url: String, headers: Array<Header?>, context: HttpContext, encoding: String): String {
        return options(HttpConfig.custom().client(client).url(url).headers(headers).context(context).encoding(encoding))
    }

    /**
     * 以Options方式，请求资源或服务

     * @param config        请求参数配置
     * *
     * @return
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    fun options(config: HttpConfig): String {
        return send(config.method(HttpMethods.OPTIONS))
    }

    /**
     * 以Trace方式，请求资源或服务

     * @param client                client对象
     * *
     * @param url                    资源地址
     * *
     * @param headers            请求头信息
     * *
     * @param context            http上下文，用于cookie操作
     * *
     * @param encoding        编码
     * *
     * @return                        返回处理结果
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    fun trace(client: HttpClient, url: String, headers: Array<Header?>, context: HttpContext, encoding: String): String {
        return trace(HttpConfig.custom().client(client).url(url).headers(headers).context(context).encoding(encoding))
    }

    /**
     * 以Trace方式，请求资源或服务

     * @param config        请求参数配置
     * *
     * @return
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    fun trace(config: HttpConfig): String {
        return send(config.method(HttpMethods.TRACE))
    }

    /**
     * 下载文件

     * @param client                client对象
     * *
     * @param url                    资源地址
     * *
     * @param headers            请求头信息
     * *
     * @param context            http上下文，用于cookie操作
     * *
     * @param out                    输出流
     * *
     * @return                        返回处理结果
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    fun down(client: HttpClient, url: String, headers: Array<Header?>, context: HttpContext, out: OutputStream): OutputStream {
        return down(HttpConfig.custom().client(client).url(url).headers(headers).context(context).out(out))
    }

    /**
     * 下载文件

     * @param config        请求参数配置
     * *
     * @return                        返回处理结果
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    fun down(config: HttpConfig): OutputStream {
        return fmt2Stream(execute(config.method(HttpMethods.GET)), config.out())
    }

    /**
     * 上传文件

     * @param client                client对象
     * *
     * @param url                    资源地址
     * *
     * @param headers            请求头信息
     * *
     * @param context            http上下文，用于cookie操作
     * *
     * @return                        返回处理结果
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    fun upload(client: HttpClient, url: String, headers: Array<Header?>, context: HttpContext): String {
        return upload(HttpConfig.custom().client(client).url(url).headers(headers).context(context))
    }

    /**
     * 上传文件

     * @param config        请求参数配置
     * *
     * @return                返回处理结果
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class) fun upload(config: HttpConfig): String {
        if (config.method() !== HttpMethods.POST && config.method() !== HttpMethods.PUT) {
            config.method(HttpMethods.POST)
        }
        return send(config)
    }

    /**
     * 查看资源链接情况，返回状态码

     * @param client                client对象
     * *
     * @param url                    资源地址
     * *
     * @param headers            请求头信息
     * *
     * @param context            http上下文，用于cookie操作
     * *
     * @return                        返回处理结果
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    fun status(client: HttpClient, url: String, headers: Array<Header?>, context: HttpContext, method: HttpMethods): Int {
        return status(HttpConfig.custom().client(client).url(url).headers(headers).context(context).method(method))
    }

    /**
     * 查看资源链接情况，返回状态码

     * @param config        请求参数配置
     * *
     * @return                返回处理结果
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    fun status(config: HttpConfig): Int {
        return fmt2Int(execute(config))
    }

    /**
     * 请求资源或服务

     * @param config        请求参数配置
     * *
     * @return
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    fun send(config: HttpConfig): String {
        return fmt2String(execute(config), config.outenc())
    }

    /**
     * 请求资源或服务

     * @param config        请求参数配置
     * *
     * @return                返回HttpResponse对象
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    private fun execute(config: HttpConfig): HttpResponse {
        create(config)//获取链接
        val resp: HttpResponse
        try {
            //创建请求对象
            val request = getRequest(config.url(), config.method())

            //设置header信息
            request.setHeaders(config.headers())

            //判断是否支持设置entity(仅HttpPost、HttpPut、HttpPatch支持)
            if (HttpEntityEnclosingRequestBase::class.java.isAssignableFrom(request.javaClass)) {
                val nvps = ArrayList<NameValuePair>()

                //检测url中是否存在参数
                config.url(Utils.checkHasParas(config.url()))

                //装填参数
                val entity = Utils.map2HttpEntity(config.map(), config.inenc())

                //设置参数到请求对象中
                (request as HttpEntityEnclosingRequestBase).entity = entity

                LogUtil.i(javaClass,"请求地址：" + config.url())
                if (nvps.size > 0) {
                    LogUtil.i(javaClass,"请求参数：" + nvps.toString())
                }
                if (config.json() != null) {
                    LogUtil.i(javaClass,"请求参数：" + config.json())
                }
            } else {
                val idx = config.url().indexOf("?")
                LogUtil.i(javaClass,"请求地址：" + config.url().substring(0, if (idx > 0) idx else config.url().length))
                if (idx > 0) {
                    LogUtil.i(javaClass,"请求参数：" + config.url().substring(idx + 1))
                }
            }
            //执行请求操作，并拿到结果（同步阻塞）
            resp = if (config.context() == null) config.client()!!.execute(request) else config.client()!!.execute(request, config.context())

            if (config.sReturnRespHeaders) {
                //获取所有response的header信息
                config.headers(resp.allHeaders)
            }

            //获取结果实体
            return resp

        } catch (e: IOException) {
            throw HttpProcessException(e)
        }

    }

    /**
     * 转化为字符串

     * @param resp            响应对象
     * *
     * @param encoding    编码
     * *
     * @return
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    private fun fmt2String(resp: HttpResponse, encoding: String): String {
        val body : String
        try {
            if (resp.entity != null) {
                // 按指定编码转换结果实体为String类型
                body = EntityUtils.toString(resp.entity, encoding)
                LogUtil.i(javaClass, body)
            } else {//有可能是head请求
                body = resp.statusLine.toString()
            }
            EntityUtils.consume(resp.entity)
        } catch (e: IOException) {
            throw HttpProcessException(e)
        } finally {
            close(resp)
        }
        return body
    }

    /**
     * 转化为数字

     * @param resp            响应对象
     * *
     * @return
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    private fun fmt2Int(resp: HttpResponse): Int {
        val statusCode: Int
        try {
            statusCode = resp.statusLine.statusCode
            EntityUtils.consume(resp.entity)
        } catch (e: IOException) {
            throw HttpProcessException(e)
        } finally {
            close(resp)
        }
        return statusCode
    }

    /**
     * 转化为流

     * @param resp            响应对象
     * *
     * @param out                输出流
     * *
     * @return
     * *
     * @throws HttpProcessException
     */
    @Throws(HttpProcessException::class)
    fun fmt2Stream(resp: HttpResponse, out: OutputStream): OutputStream {
        try {
            resp.entity.writeTo(out)
            EntityUtils.consume(resp.entity)
        } catch (e: IOException) {
            throw HttpProcessException(e)
        } finally {
            close(resp)
        }
        return out
    }

    /**
     * 根据请求方法名，获取request对象

     * @param url                    资源地址
     * *
     * @param method            请求方式
     * *
     * @return
     */
    private fun getRequest(url: String, method: HttpMethods): HttpRequestBase {
        val request: HttpRequestBase
        when (method.code) {
            0// HttpGet
            -> request = HttpGet(url)
            1// HttpPost
            -> request = HttpPost(url)
            2// HttpHead
            -> request = HttpHead(url)
            3// HttpPut
            -> request = HttpPut(url)
            4// HttpDelete
            -> request = HttpDelete(url)
            5// HttpTrace
            -> request = HttpTrace(url)
            6// HttpPatch
            -> request = HttpPatch(url)
            7// HttpOptions
            -> request = HttpOptions(url)
            else -> request = HttpPost(url)
        }
        return request
    }

    /**
     * 尝试关闭response

     * @param resp                HttpResponse对象
     */
    private fun close(resp: HttpResponse?) {
        try {
            if (resp == null) return
            //如果CloseableHttpResponse 是resp的父类，则支持关闭
            if (CloseableHttpResponse::class.java.isAssignableFrom(resp.javaClass)) {
                (resp as CloseableHttpResponse).close()
            }
        } catch (e: IOException) {
            LogUtil.e(javaClass, e.message.toString())
        }
    }
}