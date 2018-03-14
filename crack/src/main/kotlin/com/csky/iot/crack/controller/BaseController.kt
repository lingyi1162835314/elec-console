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

package com.csky.iot.crack.controller

import com.csky.iot.crack.common.ResponseCode
import com.csky.iot.crack.common.codeCheck
import org.springframework.beans.factory.annotation.Autowired
import javax.servlet.http.HttpServletRequest

open class BaseController{

    @Autowired
    lateinit var request: HttpServletRequest

    private val AUTH_KEY = "Authorization"

    fun getHeaderToken(): String {
        val token = request.getHeader(AUTH_KEY)

        codeCheck(token == null, ResponseCode.ILLEGAL_USER, "非法用户")

        return token
    }
}