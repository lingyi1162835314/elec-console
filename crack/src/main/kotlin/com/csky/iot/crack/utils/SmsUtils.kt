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

package com.csky.iot.crack.utils

import com.taobao.api.ApiException
import com.taobao.api.DefaultTaobaoClient
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest

object SmsUtils {
    private val url = "http://gw.api.taobao.com/router/rest"
    private val appkey = "23381481"
    private val appsecret = "7f7c45dd9064c1ea920685865002d7cd"
    private val paramString = "{code:'%s'}"

    val SUCCESS_RESPONSE_SIGN = "alibaba_aliqin_fc_sms_num_send_response"
    val ERROR_RESPONSE_SIGN = "error_response"
    val SUB_MSG = "sub_msg"

    /**
     * 通用发送短信方法
     *
     * @param extend       公共回传参数
     * @param params       短信模板参数
     * @param templateCode 模板id
     * @param signName     短信签名
     * @param mobile       短信接收手机号码(必为11手机号码)
     * @return string
     * @throws ApiException the api exception
     */
    @Throws(ApiException::class)
    fun sendMsg(extend: String, params: String, templateCode: String, signName: String, mobile: String): String {
        val client = DefaultTaobaoClient(url, appkey, appsecret)
        val request = AlibabaAliqinFcSmsNumSendRequest()
        request.extend = extend
        request.smsType = "normal"
        request.smsFreeSignName = signName
        request.setSmsParamString(params)
        request.smsTemplateCode = templateCode
        request.recNum = mobile
        val response = client.execute(request)
        return response.body
    }

    /**
     * 通用发送短信方法(批量发送)
     *
     * @param extend       公共回传参数
     * @param params       短信模板参数
     * @param templateCode 模板id
     * @param singName     短信签名
     * @param mobiles      短信接收手机号码集合(必为11手机号码)
     * @return string
     * @throws ApiException the api exception
     */
    @Throws(ApiException::class)
    fun sendMsg(extend: String, params: String, templateCode: String, singName: String, mobiles: List<String>): String {
        var mobile = ""
        for (m in mobiles) {
            mobile += m + ","
        }
        return sendMsg(extend, params, templateCode, singName, mobile)
    }

    /**
     * 发送注册验证
     *
     * @param mobile 手机号
     * @param code   验证码
     * @return string
     * @throws ApiException the api exception
     */
    @Throws(ApiException::class)
    fun sendRegisterMsg(mobile: String, code: String): String {
        return sendMsg("extend", String.format(paramString, code), "SMS_125765072", "蛟驰科技", mobile)
    }

    /**
     * 身份验证验证码
     *
     * @param mobile 手机号
     * @param code   验证码
     * @return string
     * @throws ApiException the api exception
     */
    @Throws(ApiException::class)
    fun sendAuthMsg(mobile: String, code: String): String {
        return sendMsg("extend", String.format(paramString, code), "SMS_10275860", "蛟驰科技", mobile)
    }

    /**
     * 生成5位数的验证码
     *
     * @return string
     */
    fun createVerifyCode(): String {
        return (Math.random() * 90000 + 10000).toInt().toString()
    }

    /**
     * 生成由纯数字组成指定位数的验证码
     *
     * @param size the size
     * @return string
     */
    fun createVerifyCode(size: Int): String {
        var a = 1
        for (i in 1..size - 1) {
            a = a * 10
        }
        return (Math.random() * (9 * a) + a).toInt().toString()
    }
}