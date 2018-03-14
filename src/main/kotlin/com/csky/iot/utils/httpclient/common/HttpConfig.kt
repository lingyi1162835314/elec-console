package com.csky.iot.utils.httpclient.common

import java.io.OutputStream
import java.nio.charset.Charset
import java.util.HashMap

import org.apache.http.Header
import org.apache.http.client.HttpClient
import org.apache.http.protocol.HttpContext


class HttpConfig private constructor() {

    /**
     * HttpClient对象
     */
    private var client: HttpClient? = null

    /**
     * Header头信息
     */
    private var headers: Array<Header?>? = null

    /**
     * 是否返回response的headers
     */
    var sReturnRespHeaders: Boolean = false
        private set

    /**
     * 请求方法
     */
    private var method = HttpMethods.GET

    /**
     * 请求方法名称
     */
    private var methodName: String? = null

    /**
     * 用于cookie操作
     */
    private var context: HttpContext? = null

    /**
     * 传递参数
     */
    private var map: MutableMap<String, Any>? = null

    /**
     * 以json格式作为输入参数
     */
    private var json: String? = null

    /**
     * 输入输出编码
     */
    private var encoding = Charset.defaultCharset().displayName()

    /**
     * 输入编码
     */
    private var inenc: String? = null

    /**
     * 输出编码
     */
    private var outenc: String? = null

    /**
     * HttpClient对象
     */
    fun client(client: HttpClient): HttpConfig {
        this.client = client
        return this
    }

    /**
     * 资源url
     */
    fun url(url: String): HttpConfig {
        urls.set(url)
        return this
    }

    /**
     * Header头信息
     */
    fun headers(headers: Array<Header?>): HttpConfig {
        this.headers = headers
        return this
    }

    /**
     * Header头信息(是否返回response中的headers)
     */
    fun headers(headers: Array<Header?>, isReturnRespHeaders: Boolean): HttpConfig {
        this.headers = headers
        this.sReturnRespHeaders = isReturnRespHeaders
        return this
    }

    /**
     * 请求方法
     */
    fun method(method: HttpMethods): HttpConfig {
        this.method = method
        return this
    }

    /**
     * 请求方法
     */
    fun methodName(methodName: String): HttpConfig {
        this.methodName = methodName
        return this
    }

    /**
     * cookie操作相关
     */
    fun context(context: HttpContext): HttpConfig {
        this.context = context
        return this
    }

    /**
     * 传递参数
     */
    fun map(map: MutableMap<String, Any>?): HttpConfig {
        synchronized(javaClass) {
            if (this.map == null || map == null) {
                this.map = map
            } else {
                this.map!!.putAll(map)
            }
        }
        return this
    }

    /**
     * 以json格式字符串作为参数
     */
    fun json(json: String): HttpConfig {
        this.json = json
        map = HashMap<String, Any>()
        map!!.put(Utils.ENTITY_STRING, json)
        return this
    }

    /**
     * 上传文件时用到
     * @param filePaths            待上传文件所在路径
     * *
     * @param inputName        即file input 标签的name值，默认为file
     * *
     * @param forceRemoveContentTypeChraset
     * *
     * @return
     */
    @JvmOverloads
    fun files(filePaths: Array<String>, inputName: String = "file", forceRemoveContentTypeChraset: Boolean = false): HttpConfig {
        synchronized(javaClass) {
            if (this.map == null) {
                this.map = HashMap<String, Any>()
            }
        }
        map!!.put(Utils.ENTITY_MULTIPART, filePaths)
        map!!.put(Utils.ENTITY_MULTIPART + ".name", inputName)
        map!!.put(Utils.ENTITY_MULTIPART + ".rmCharset", forceRemoveContentTypeChraset)
        return this
    }

    /**
     * 输入输出编码
     */
    fun encoding(encoding: String): HttpConfig {
        //设置输入输出
        inenc(encoding)
        outenc(encoding)
        this.encoding = encoding
        return this
    }

    /**
     * 输入编码
     */
    fun inenc(inenc: String): HttpConfig {
        this.inenc = inenc
        return this
    }

    /**
     * 输出编码
     */
    fun outenc(outenc: String): HttpConfig {
        this.outenc = outenc
        return this
    }

    /**
     * 输出流对象
     */
    fun out(out: OutputStream): HttpConfig {
        outs.set(out)
        return this
    }

    fun client(): HttpClient? {
        return client
    }

    fun headers(): Array<Header?>? {
        return headers
    }

    fun url(): String {
        return urls.get()
    }

    fun method(): HttpMethods {
        return method
    }

    fun methodName(): String {
        return methodName!!
    }

    fun context(): HttpContext? {
        return context
    }

    fun map(): MutableMap<String, Any>? {
        return map
    }

    fun json(): String? {
        return json
    }

    fun encoding(): String {
        return encoding
    }

    fun inenc(): String {
        return if (inenc == null) encoding else inenc!!
    }

    fun outenc(): String {
        return if (outenc == null) encoding else outenc!!
    }

    fun out(): OutputStream {
        return outs.get()
    }

    companion object {

        /**
         * 获取实例
         * @return
         */
        fun custom(): HttpConfig {
            return HttpConfig()
        }

        /**
         * 解决多线程下载时，strean被close的问题
         */
        private val outs = ThreadLocal<OutputStream>()

        /**
         * 解决多线程处理时，url被覆盖问题
         */
        private val urls = ThreadLocal<String>()
    }

}