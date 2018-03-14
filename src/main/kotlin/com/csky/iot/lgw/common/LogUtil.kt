package com.csky.iot.lgw.common

import org.slf4j.LoggerFactory

object LogUtil {
    fun <T> i(javaClass: Class<T>, s: String?) {
        LoggerFactory.getLogger(javaClass).info(s)
    }

    fun <T> e(javaClass: Class<T>, s: String) {
        LoggerFactory.getLogger(javaClass).error(s)
    }

    fun <T> e(javaClass: Class<T>, s: String?, t: Throwable) {
        LoggerFactory.getLogger(javaClass).error(s, t)
    }

    fun <T> d(javaClass: Class<T>, s: String?) {
        LoggerFactory.getLogger(javaClass).debug(s)
    }
}
