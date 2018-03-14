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

package com.csky.iot.crack.exception

import com.csky.iot.crack.common.ApiResult
import com.csky.iot.crack.common.ResponseCode
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody


@ControllerAdvice
class GlobalExceptionHandler {

    @Autowired
    val messageSource: MessageSource? = null
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(CskyException::class)
    @ResponseBody
    fun defaultExcepitonHander(e: CskyException): ApiResult<Nothing?> {
        logger.error("returned an skyException {}", e)
        return ApiResult(
                e.code,
                getMessage(e.message!!),
                null
        )

    }

    @ExceptionHandler(MethodArgumentNotValidException :: class)
    @ResponseBody
    fun handleResourceNotFoundException(e : MethodArgumentNotValidException) : ApiResult<Nothing?> {
        val bindingResult  = e.bindingResult

        val errors = bindingResult.allErrors

        return ApiResult(
                ResponseCode.ILLEGAL_OPERATION.code,
                errors[0].defaultMessage,
                null
        )
    }

    private fun getMessage(key: String): String {
        val locale = LocaleContextHolder.getLocale()
        try {
            return messageSource!!.getMessage(key, null, locale)
        } catch (e: Exception) {
            return key
        }
    }

}
