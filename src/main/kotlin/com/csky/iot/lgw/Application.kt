package com.csky.iot.lgw

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableScheduling


@EnableAutoConfiguration
@EnableScheduling
//@EnableAsync
@ComponentScan("com.csky.iot.lgw")
@SpringBootApplication
abstract class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}