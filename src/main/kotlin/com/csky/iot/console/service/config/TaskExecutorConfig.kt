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

import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import java.lang.reflect.Method
import java.util.concurrent.Executor

@Configuration
@EnableAsync
@EnableScheduling
class TaskExecutorConfig : AsyncConfigurer, SchedulingConfigurer {
    private val logger = LoggerFactory.getLogger(TaskExecutorConfig::class.java)

    override fun getAsyncExecutor(): Executor {
        return taskScheduler()
    }

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        taskRegistrar.setTaskScheduler(taskScheduler())
    }

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler {
        return AsyncUncaughtExceptionHandler(fun(th: Throwable, method: Method, params) {
            logger.error("Async uncaught exception: {} {} {}", th.message, method.toString(), params.toString())
        })
    }

    @Bean(name = arrayOf("task-scheduler"))
    fun taskScheduler(): ThreadPoolTaskScheduler {
        val schedulerThreadPool = ThreadPoolTaskScheduler()
        schedulerThreadPool.poolSize = 30
        schedulerThreadPool.threadNamePrefix = "scheduled task-"
        schedulerThreadPool.initialize()
        return schedulerThreadPool
    }
}