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

package com.csky.iot.crack

import com.csky.iot.crack.common.SpringUtil
import com.csky.iot.crack.controller.MessageReceiver
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement

@EnableAutoConfiguration
@EnableTransactionManagement
@EnableScheduling
@ComponentScan("com.csky.iot.crack")
@Import(value = *arrayOf(SpringUtil::class))
@SpringBootApplication
@EnableAsync
class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
    // MessageReceiver().test()
    MessageReceiver().receiver()
}