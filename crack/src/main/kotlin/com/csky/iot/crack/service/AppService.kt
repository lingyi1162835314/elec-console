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

package com.csky.iot.crack.service

import com.csky.iot.crack.controller.vo.AppVO
import com.csky.iot.crack.data.AppRepository
import org.springframework.stereotype.Service

@Service
class AppService(val appRepository: AppRepository) {

    fun getAppVersion(): AppVO {
        val appDO = appRepository.getMaxVersionId()
        if (appDO == null) {
            return AppVO("0.0.0", "0", "", "", 1)
        }
        return AppVO(appDO.version!!, appDO.versionCode!!, appDO.downloadUrl, appDO.message, appDO.updateType!!)
    }

}