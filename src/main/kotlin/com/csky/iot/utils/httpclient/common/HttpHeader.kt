package com.csky.iot.utils.httpclient.common

import org.apache.http.Consts
import org.apache.http.Header
import org.apache.http.message.BasicHeader
import java.util.HashMap


class HttpHeader private constructor() {

    //记录head头信息
    private var headerMaps: MutableMap<String, Header>? = mutableMapOf<String, Header>()

    /**
     * 指定客户端能够接收的内容类型
     * 例如：Accept: text/plain, text/html

     * @param key,value
     */
    fun other(key: String, value: String): HttpHeader {
        headerMaps!!.put(key, BasicHeader(key, value))
        return this
    }

    /**
     * 指定客户端能够接收的内容类型
     * 例如：Accept: text/plain, text/html

     * @param accept
     */
    fun accept(accept: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.ACCEPT, BasicHeader(HttpReqHead.ACCEPT, accept))
        return this
    }

    /**
     * 浏览器可以接受的字符编码集
     * 例如：Accept-Charset: iso-8859-5

     * @param acceptCharset
     */
    fun acceptCharset(acceptCharset: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.ACCEPT_CHARSET, BasicHeader(HttpReqHead.ACCEPT_CHARSET, acceptCharset))
        return this
    }

    /**
     * 指定浏览器可以支持的web服务器返回内容压缩编码类型
     * 例如：Accept-Encoding: compress, gzip

     * @param acceptEncoding
     */
    fun acceptEncoding(acceptEncoding: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.ACCEPT_ENCODING, BasicHeader(HttpReqHead.ACCEPT_ENCODING, acceptEncoding))
        return this
    }

    /**
     * 浏览器可接受的语言
     * 例如：Accept-Language: en,zh

     * @param acceptLanguage
     */
    fun acceptLanguage(acceptLanguage: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.ACCEPT_LANGUAGE, BasicHeader(HttpReqHead.ACCEPT_LANGUAGE, acceptLanguage))
        return this
    }

    /**
     * 可以请求网页实体的一个或者多个子范围字段
     * 例如：Accept-Ranges: bytes

     * @param acceptRanges
     */
    fun acceptRanges(acceptRanges: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.ACCEPT_RANGES, BasicHeader(HttpReqHead.ACCEPT_RANGES, acceptRanges))
        return this
    }

    /**
     * HTTP授权的授权证书
     * 例如：Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==

     * @param authorization
     */
    fun authorization(authorization: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.AUTHORIZATION, BasicHeader(HttpReqHead.AUTHORIZATION, authorization))
        return this
    }

    /**
     * 指定请求和响应遵循的缓存机制
     * 例如：Cache-Control: no-cache

     * @param cacheControl
     */
    fun cacheControl(cacheControl: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.CACHE_CONTROL, BasicHeader(HttpReqHead.CACHE_CONTROL, cacheControl))
        return this
    }

    /**
     * 表示是否需要持久连接（HTTP 1.1默认进行持久连接）
     * 例如：Connection: close 短链接； Connection: keep-alive 长连接

     * @param connection
     * *
     * @return
     */
    fun connection(connection: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.CONNECTION, BasicHeader(HttpReqHead.CONNECTION, connection))
        return this
    }

    /**
     * HTTP请求发送时，会把保存在该请求域名下的所有cookie值一起发送给web服务器
     * 例如：Cookie: $Version=1; Skin=new;

     * @param cookie
     */
    fun cookie(cookie: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.COOKIE, BasicHeader(HttpReqHead.COOKIE, cookie))
        return this
    }

    /**
     * 请求内容长度
     * 例如：Content-Length: 348

     * @param contentLength
     */
    fun contentLength(contentLength: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.CONTENT_LENGTH, BasicHeader(HttpReqHead.CONTENT_LENGTH, contentLength))
        return this
    }

    /**
     * 请求的与实体对应的MIME信息
     * 例如：Content-Type: application/x-www-form-urlencoded

     * @param contentType
     */
    fun contentType(contentType: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.CONTENT_TYPE, BasicHeader(HttpReqHead.CONTENT_TYPE, contentType))
        return this
    }

    /**
     * 请求发送的日期和时间
     * 例如：Date: Tue, 15 Nov 2010 08:12:31 GMT

     * @param date
     * *
     * @return
     */
    fun date(date: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.DATE, BasicHeader(HttpReqHead.DATE, date))
        return this
    }

    /**
     * 请求的特定的服务器行为
     * 例如：Expect: 100-continue

     * @param expect
     */
    fun expect(expect: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.EXPECT, BasicHeader(HttpReqHead.EXPECT, expect))
        return this
    }

    /**
     * 发出请求的用户的Email
     * 例如：From: user@email.com

     * @param from
     */
    fun from(from: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.FROM, BasicHeader(HttpReqHead.FROM, from))
        return this
    }

    /**
     * 指定请求的服务器的域名和端口号
     * 例如：Host: blog.csdn.net

     * @param host
     * *
     * @return
     */
    fun host(host: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.HOST, BasicHeader(HttpReqHead.HOST, host))
        return this
    }

    /**
     * 只有请求内容与实体相匹配才有效
     * 例如：If-Match: “737060cd8c284d8af7ad3082f209582d”

     * @param ifMatch
     * *
     * @return
     */
    fun ifMatch(ifMatch: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.IF_MATCH, BasicHeader(HttpReqHead.IF_MATCH, ifMatch))
        return this
    }

    /**
     * 如果请求的部分在指定时间之后被修改则请求成功，未被修改则返回304代码
     * 例如：If-Modified-Since: Sat, 29 Oct 2010 19:43:31 GMT

     * @param ifModifiedSince
     * *
     * @return
     */
    fun ifModifiedSince(ifModifiedSince: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.IF_MODIFIED_SINCE, BasicHeader(HttpReqHead.IF_MODIFIED_SINCE, ifModifiedSince))
        return this
    }

    /**
     * 如果内容未改变返回304代码，参数为服务器先前发送的Etag，与服务器回应的Etag比较判断是否改变
     * 例如：If-None-Match: “737060cd8c284d8af7ad3082f209582d”

     * @param ifNoneMatch
     * *
     * @return
     */
    fun ifNoneMatch(ifNoneMatch: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.IF_NONE_MATCH, BasicHeader(HttpReqHead.IF_NONE_MATCH, ifNoneMatch))
        return this
    }

    /**
     * 如果实体未改变，服务器发送客户端丢失的部分，否则发送整个实体。参数也为Etag
     * 例如：If-Range: “737060cd8c284d8af7ad3082f209582d”

     * @param ifRange
     * *
     * @return
     */
    fun ifRange(ifRange: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.IF_RANGE, BasicHeader(HttpReqHead.IF_RANGE, ifRange))
        return this
    }

    /**
     * 只在实体在指定时间之后未被修改才请求成功
     * 例如：If-Unmodified-Since: Sat, 29 Oct 2010 19:43:31 GMT

     * @param ifUnmodifiedSince
     * *
     * @return
     */
    fun ifUnmodifiedSince(ifUnmodifiedSince: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.IF_UNMODIFIED_SINCE, BasicHeader(HttpReqHead.IF_UNMODIFIED_SINCE, ifUnmodifiedSince))
        return this
    }

    /**
     * 限制信息通过代理和网关传送的时间
     * 例如：Max-Forwards: 10

     * @param maxForwards
     * *
     * @return
     */
    fun maxForwards(maxForwards: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.MAX_FORWARDS, BasicHeader(HttpReqHead.MAX_FORWARDS, maxForwards))
        return this
    }

    /**
     * 用来包含实现特定的指令
     * 例如：Pragma: no-cache

     * @param pragma
     * *
     * @return
     */
    fun pragma(pragma: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.PRAGMA, BasicHeader(HttpReqHead.PRAGMA, pragma))
        return this
    }

    /**
     * 连接到代理的授权证书
     * 例如：Proxy-Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==

     * @param proxyAuthorization
     */
    fun proxyAuthorization(proxyAuthorization: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.PROXY_AUTHORIZATION, BasicHeader(HttpReqHead.PROXY_AUTHORIZATION, proxyAuthorization))
        return this
    }

    /**
     * 只请求实体的一部分，指定范围
     * 例如：Range: bytes=500-999

     * @param range
     */
    fun range(range: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.RANGE, BasicHeader(HttpReqHead.RANGE, range))
        return this
    }

    /**
     * 先前网页的地址，当前请求网页紧随其后,即来路
     * 例如：Referer: http://www.zcmhi.com/archives/71.html

     * @param referer
     */
    fun referer(referer: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.REFERER, BasicHeader(HttpReqHead.REFERER, referer))
        return this
    }

    /**
     * 客户端愿意接受的传输编码，并通知服务器接受接受尾加头信息
     * 例如：TE: trailers,deflate;q=0.5

     * @param te
     */
    fun te(te: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.TE, BasicHeader(HttpReqHead.TE, te))
        return this
    }

    /**
     * 向服务器指定某种传输协议以便服务器进行转换（如果支持）
     * 例如：Upgrade: HTTP/2.0, SHTTP/1.3, IRC/6.9, RTA/x11

     * @param upgrade
     */
    fun upgrade(upgrade: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.UPGRADE, BasicHeader(HttpReqHead.UPGRADE, upgrade))
        return this
    }

    /**
     * User-Agent的内容包含发出请求的用户信息

     * @param userAgent
     * *
     * @return
     */
    fun userAgent(userAgent: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.USER_AGENT, BasicHeader(HttpReqHead.USER_AGENT, userAgent))
        return this
    }

    /**
     * 关于消息实体的警告信息
     * 例如：Warn: 199 Miscellaneous warning

     * @param warning
     * *
     * @return
     */
    fun warning(warning: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.WARNING, BasicHeader(HttpReqHead.WARNING, warning))
        return this
    }

    /**
     * 通知中间网关或代理服务器地址，通信协议
     * 例如：Via: 1.0 fred, 1.1 nowhere.com (Apache/1.1)

     * @param via
     * *
     * @return
     */
    fun via(via: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.VIA, BasicHeader(HttpReqHead.VIA, via))
        return this
    }

    /**
     * 设置此HTTP连接的持续时间（超时时间）
     * 例如：Keep-Alive: 300

     * @param keepAlive
     * *
     * @return
     */
    fun keepAlive(keepAlive: String): HttpHeader {
        headerMaps!!.put(HttpReqHead.KEEP_ALIVE, BasicHeader(HttpReqHead.KEEP_ALIVE, keepAlive))
        return this
    }

    fun accept(): String? {
        return get(HttpReqHead.ACCEPT)
    }

    fun acceptCharset(): String? {
        return get(HttpReqHead.ACCEPT_CHARSET)
    }

    fun acceptEncoding(): String? {
        return get(HttpReqHead.ACCEPT_ENCODING)
    }

    fun acceptLanguage(): String? {
        return get(HttpReqHead.ACCEPT_LANGUAGE)
    }

    fun acceptRanges(): String? {
        return get(HttpReqHead.ACCEPT_RANGES)
    }

    fun authorization(): String? {
        return get(HttpReqHead.AUTHORIZATION)
    }

    fun cacheControl(): String? {
        return get(HttpReqHead.CACHE_CONTROL)
    }

    fun connection(): String? {
        return get(HttpReqHead.CONNECTION)
    }

    fun contentLength(): String? {
        return get(HttpReqHead.CONTENT_LENGTH)
    }

    fun contentType(): String? {
        return get(HttpReqHead.CONTENT_TYPE)
    }

    fun maxForwards(): String? {
        return get(HttpReqHead.MAX_FORWARDS)
    }

    fun pragma(): String? {
        return get(HttpReqHead.PRAGMA)
    }

    fun proxyAuthorization(): String? {
        return get(HttpReqHead.PROXY_AUTHORIZATION)
    }

    fun referer(): String? {
        return get(HttpReqHead.REFERER)
    }

    fun te(): String? {
        return get(HttpReqHead.TE)
    }

    fun upgrade(): String? {
        return get(HttpReqHead.UPGRADE)
    }

    fun userAgent(): String? {
        return get(HttpReqHead.USER_AGENT)
    }

    fun via(): String? {
        return get(HttpReqHead.VIA)
    }

    fun warning(): String? {
        return get(HttpReqHead.WARNING)
    }

    fun keepAlive(): String?{
        return get(HttpReqHead.KEEP_ALIVE)
    }


    /**
     * 获取head信息

     * @return
     */
    private operator fun get(headName: String): String? {
        if (headerMaps!!.containsKey(headName)) {
            return headerMaps!![headName]!!.value
        }
        return null
    }

    /**
     * 返回header头信息

     * @return
     */
    fun build(): Array<Header?> {
        val headers = arrayOfNulls<Header>(headerMaps!!.size)

        for ((i, header) in headerMaps!!.values.withIndex()) {
            headers[i] = header
        }
//
//        val arrayHeaders : Array<Header> = arrayOf(headers.size)
//        for(i in 0..headers.size) {
//            arrayHeaders[i] = headers[i]!!
//        }
        headerMaps!!.clear()
        headerMaps = null
        return headers
    }

    /**
     * Http头信息

     * @author wangjun
     * *
     * @date 2017年11月1日
     * *
     * @version 1.0
     */
    private object HttpReqHead {
        val ACCEPT = "Accept"
        val ACCEPT_CHARSET = "Accept-Charset"
        val ACCEPT_ENCODING = "Accept-Encoding"
        val ACCEPT_LANGUAGE = "Accept-Language"
        val ACCEPT_RANGES = "Accept-Ranges"
        val AUTHORIZATION = "Authorization"
        val CACHE_CONTROL = "Cache-Control"
        val CONNECTION = "Connection"
        val COOKIE = "Cookie"
        val CONTENT_LENGTH = "Content-Length"
        val CONTENT_TYPE = "Content-Type"
        val DATE = "Date"
        val EXPECT = "Expect"
        val FROM = "From"
        val HOST = "Host"
        val IF_MATCH = "If-Match "
        val IF_MODIFIED_SINCE = "If-Modified-Since"
        val IF_NONE_MATCH = "If-None-Match"
        val IF_RANGE = "If-Range"
        val IF_UNMODIFIED_SINCE = "If-Unmodified-Since"
        val KEEP_ALIVE = "Keep-Alive"
        val MAX_FORWARDS = "Max-Forwards"
        val PRAGMA = "Pragma"
        val PROXY_AUTHORIZATION = "Proxy-Authorization"
        val RANGE = "Range"
        val REFERER = "Referer"
        val TE = "TE"
        val UPGRADE = "Upgrade"
        val USER_AGENT = "User-Agent"
        val VIA = "Via"
        val WARNING = "Warning"
    }

    /**
     * 常用头信息配置

     * @author wangjun
     * *
     * @date 2017年11月18日
     * *
     * @version 1.0
     */
    object Headers {
        val APP_FORM_URLENCODED = "application/x-www-form-urlencoded"
        val TEXT_PLAIN = "text/plain"
        val TEXT_HTML = "text/html"
        val TEXT_XML = "text/xml"
        val TEXT_JSON = "text/json"
        val CONTENT_CHARSET_ISO_8859_1 = Consts.ISO_8859_1.name()
        val CONTENT_CHARSET_UTF8 = Consts.UTF_8.name()
        val DEF_PROTOCOL_CHARSET = Consts.ASCII.name()
        val CONN_CLOSE = "close"
        val KEEP_ALIVE = "keep-alive"
        val EXPECT_CONTINUE = "100-continue"
    }

    companion object {

        fun custom(): HttpHeader {
            return HttpHeader()
        }
    }
}