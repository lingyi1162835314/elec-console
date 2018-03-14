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

package com.csky.iot.console.manager.dto

import com.alibaba.fastjson.JSONObject
import com.csky.iot.console.service.entity.aliyun_iot.MnsTypeEnum
import java.io.Serializable

class AliyunMsgDTO(
        var msgTypeEnum: MnsTypeEnum = MnsTypeEnum.UPLOAD,
        var msgData: JSONObject = JSONObject(),
        var cid: String = ""
): Serializable