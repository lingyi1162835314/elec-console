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

import com.csky.iot.crack.data.UserRepository
import com.csky.iot.crack.exception.CskyException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthInterceptor() : HandlerInterceptorAdapter() {
    private val logger = LoggerFactory.getLogger(AuthInterceptor::class.java)

    @Autowired
    lateinit var userRepository: UserRepository

    @Throws(CskyException::class)
    override fun preHandle(request: HttpServletRequest?, response: HttpServletResponse?, handler: Any?): Boolean {
        val authorization = request!!.getHeader("Authorization")
        logger.info("The authorization is: {}", authorization)
        val user = userRepository.findByUserToken(authorization)
        valueCheck(user == null, "登录信息已过期,请重新登录")
        request.setAttribute("user", user)
        return super.preHandle(request, response, handler)
    }


}
