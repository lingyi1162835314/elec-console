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

package com.csky.iot.console.service.entity

enum class ProductIotTypeEnum(var value: Byte) {
    AliMQTT(1),

    OneNET_NBCoAP(2),

    AliCoAP(3)
}

fun productIotTypeGetEnum(value: Byte): ProductIotTypeEnum?{
    when (value) {
        ProductIotTypeEnum.AliMQTT.value -> return ProductIotTypeEnum.AliMQTT
        ProductIotTypeEnum.OneNET_NBCoAP.value -> return ProductIotTypeEnum.OneNET_NBCoAP
        ProductIotTypeEnum.AliCoAP.value -> return ProductIotTypeEnum.AliCoAP
        else -> return null
    }
}