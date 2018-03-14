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

package com.csky.iot.utils

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.stereotype.Component

@Component
class RedisUtil(val redisTemplate: RedisTemplate<String, Any>) {

    val SCOPE_ALIYUN_DEVICE_SHADOW_MAP = "aliyun_device_map"
    val SCOPE_ONENET_DEVICE_SHADOW_MAP = "onenet_device_map"

    fun valueSet(scope: String, value: Any) {
        redisTemplate.opsForValue().set(scope, value)
    }

    fun valueGet(scope: String) = redisTemplate.opsForValue().get(scope)

    fun remove(scope: String) {
        redisTemplate.delete(scope)
    }

    fun valueHasKey(scope: String, key: String) = redisTemplate.hasKey("${scope}_$key")

    fun valueSetIfAbsent(scope: String, key: String, value: Any) =
            redisTemplate.opsForValue().setIfAbsent("${scope}_$key", value)


    fun hashPut(scope: String, hashKey: String, value: Any) {
        redisTemplate.opsForHash<String, Any>().put(scope, hashKey, value)
    }

    fun hashGet(scope: String, hashKey: String) = redisTemplate.opsForHash<String, Any>().get(scope, hashKey)


    fun hashDelete(scope: String, hashKey: String) {
         redisTemplate.opsForHash<String, Any>().delete(scope, hashKey)
    }

    fun hashHasKey(scope: String, hashKey: String): Boolean =
            redisTemplate.opsForHash<String, Any>().hasKey(scope, hashKey)

    fun hashPutIfAbsent(scope: String, hashKey: String, value: Any) =
            redisTemplate.opsForHash<String, Any>().putIfAbsent(scope, hashKey, value)

   // fun hashGetCursor(scope: String) = redisTemplate.opsForHash<String, Any>().scan(scope, ScanOptions.NONE)

    fun hashEntries(scope: String) = redisTemplate.opsForHash<String, Any>().entries(scope)


    fun listRightPush(scope: String, value: Any) {
        redisTemplate.opsForList().rightPush(scope, value)
    }

    fun listLeftPop(scope: String) = redisTemplate.opsForList().leftPop(scope)

    fun listSize(scop: String) = redisTemplate.opsForList().size(scop)

}