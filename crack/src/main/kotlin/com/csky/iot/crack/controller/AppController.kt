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

import com.csky.iot.crack.common.ApiResult
import com.csky.iot.crack.common.LogUtil
import com.csky.iot.crack.controller.vo.AppVO
import com.csky.iot.crack.service.AppService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
open class AppController(val appService: AppService) {

    @PostMapping("/api/master/app_version")
    fun getAppVersion(): ApiResult<AppVO> {
        LogUtil.i(javaClass, "start get appVersion")
        val vo = appService.getAppVersion()
        LogUtil.i(javaClass, "end get appVersion")
        return ApiResult(
                0,
                "",
                vo
        )
    }
}