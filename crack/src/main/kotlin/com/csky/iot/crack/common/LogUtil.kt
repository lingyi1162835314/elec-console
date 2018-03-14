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

import org.slf4j.LoggerFactory

object LogUtil {
    fun <T> i(javaClass: Class<T>, s: String?) {
        LoggerFactory.getLogger(javaClass).info(s)
    }

    fun <T> e(javaClass: Class<T>, s: String) {
        LoggerFactory.getLogger(javaClass).error(s)
    }

    fun <T> e(javaClass: Class<T>, s: String?, t: Throwable) {
        LoggerFactory.getLogger(javaClass).error(s, t)
    }

    fun <T> d(javaClass: Class<T>, s: String?) {
        LoggerFactory.getLogger(javaClass).debug(s)
    }
}
