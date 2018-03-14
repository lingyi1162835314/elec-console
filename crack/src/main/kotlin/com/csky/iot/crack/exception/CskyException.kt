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

import com.csky.iot.crack.common.ResponseCode


class CskyException(code: Int, msg: String = "") : RuntimeException(msg) {
    val code: Int = code

    constructor(msg: String) : this(-1, msg)

    constructor(resp: ResponseCode) : this(resp.code, resp.msg)
}