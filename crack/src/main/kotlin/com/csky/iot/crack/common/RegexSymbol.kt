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

object RegexSymbol {
    val MOBILE_REGEX: String = "^1[34578]\\d{9}$"

    val EMAIL_REGEX: String = "^[\\.a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$"

    val PRODUCT_REGEX: String = "^[a-zA-Z0-9\\u4E00-\\u9FA5_]+$"
}