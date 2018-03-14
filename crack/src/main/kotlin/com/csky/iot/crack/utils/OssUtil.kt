package com.csky.iot.crack.utils

import com.aliyun.oss.OSSClient
import com.csky.iot.crack.common.LogUtil
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*
import javax.annotation.PostConstruct


@Component
class OssUtil {

    //   @Value("\${oss.endpoint}")
    var endpoint = "http://oss-cn-shanghai.aliyuncs.com"

    // @Value("\${oss.access_key_id}")
    var accessKeyId = "LTAIrGTPHmeA4EKu"

    //   @Value("\${oss.access_key_secret}")
    var accessKeySecret = "cb69bhmXYCmhZ2wr7tSdqgD7o9uysc"

    //    @Value("\${oss.bucket_name}")
    var bucketName = "crack-test3"

    var url = "http://$bucketName.oss-cn-shanghai.aliyuncs.com"

    var ossClient: OSSClient? = null

    @PostConstruct
    fun init() {
        ossClient = OSSClient(endpoint, accessKeyId, accessKeySecret)
    }

    fun uploadJpg(path: String, byteArray: ByteArray): String {
        val timeName = SimpleDateFormat("yyyyMMddHHmmssSSS").format(Date())
        val filePath = "$path/$timeName.jpg"
        ossClient!!.putObject(bucketName, filePath, java.io.ByteArrayInputStream(byteArray), null)
        return "$url/$filePath"
    }

    fun deleteJpg(path: String) {
        try {
            val key = path.substring(url.length + 1)
            if (ossClient!!.doesObjectExist(bucketName, key))
                ossClient!!.deleteObject(bucketName, key)
        } catch (e: Exception) {
            LogUtil.i(javaClass, e.message.toString())
            return
        }
    }

}