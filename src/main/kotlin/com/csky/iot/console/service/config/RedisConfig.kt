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
package com.csky.iot.console.service.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import redis.clients.jedis.JedisPoolConfig

@Configuration
class RedisConfig(val env: Environment) {
    @Value("\${spring.redis.host}")
    var hostName = ""

    @Value("\${spring.redis.port}")
    var port = 0

    @Value("\${spring.redis.password}")
    var password = ""

    @Bean
    fun connectionFactory(): JedisConnectionFactory {
        val factory = JedisConnectionFactory()

        factory.usePool
        factory.hostName = hostName
        factory.port = port
        factory.password = password
        factory.timeout = 100000

        return factory
    }

    @Bean
    fun getRedisTemplate(): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = connectionFactory()
        template.keySerializer = StringRedisSerializer()
        return template
    }

}