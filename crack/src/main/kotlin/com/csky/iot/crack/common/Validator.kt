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

package com.csky.iot.crack.common

import com.csky.iot.crack.exception.CskyException
import org.apache.commons.lang.StringUtils
import java.util.regex.Pattern


object Validator {
    fun check(flag: Boolean, msg: String) {

        check(flag, CskyException(msg))
    }


    fun checkCode(flag: Boolean, code: ResponseCode, msg: String = "") {
        check(flag, CskyException(
                code.code,
                if (msg == "") code.msg else msg
        ))
    }

    fun checkMobile(mobile: String) {
        var isMatch = match(RegexSymbol.MOBILE_REGEX, mobile)

        check(!isMatch, "手机号格式不正确")
    }

    fun checkEmail(email: String) {
        var isMatch = match(RegexSymbol.EMAIL_REGEX, email)

        check(!isMatch, "邮箱格式不正确")
    }

    fun checkStrLength(str: String, name: String) {
        check(isBlank(str) || str.length < 3 || str.length > 16,
                name + "长度不正确,请输入3~16个字符")
    }

    private fun check(flag: Boolean, e: Exception) {
        if (flag) {
            throw  e
        }
    }

    fun isNull(o: Any?):Boolean =  o == null

    val match = Pattern::matches
    val isBlank = StringUtils::isBlank
}