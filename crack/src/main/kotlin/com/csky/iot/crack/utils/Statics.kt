package com.csky.iot.crack.utils

import org.springframework.util.ResourceUtils

/**
 *  @author xyy
 *  @time 2018/02/03 14:35.
 */
object Statics {
    lateinit var imageHeader: ByteArray
    init {
        val fileStream = ResourceUtils.getURL("classpath:jpg.h").openStream()
        imageHeader = fileStream.readBytes()
        fileStream.close()
    }
}