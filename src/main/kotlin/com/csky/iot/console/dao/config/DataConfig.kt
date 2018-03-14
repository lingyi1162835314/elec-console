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

package com.csky.iot.console.dao.config

import com.alibaba.druid.pool.DruidDataSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class DataConfig {
    @Value("\${spring.datasource.driver-class-name}")
    var driverClassName = ""

    @Value("\${spring.datasource.url}")
    var url = ""

    @Value("\${spring.datasource.username}")
    var username = ""

    @Value("\${spring.datasource.password}")
    var password = ""

    @Bean
    fun dataSource() : DataSource {
        val dataSource = DruidDataSource()
        dataSource.driverClassName = driverClassName
        dataSource.url = url
        dataSource.username = username
        dataSource.password = password

        return dataSource
    }
}