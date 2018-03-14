package com.csky.iot.utils.httpclient.common


import com.alibaba.fastjson.JSONObject
import com.csky.iot.utils.LogUtil
import org.apache.http.HttpEntity
import org.apache.http.NameValuePair
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.entity.ContentType
import org.apache.http.entity.FileEntity
import org.apache.http.entity.StringEntity
import org.apache.http.message.BasicHeader
import org.apache.http.protocol.HTTP
import java.io.File
import java.io.UnsupportedEncodingException
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.*
import java.util.HashMap


object Utils {

    //传入参数特定类型
    val ENTITY_STRING = "\$ENTITY_STRING$"
    val ENTITY_FILE = "\$ENTITY_FILEE$"
    val ENTITY_BYTES = "\$ENTITY_BYTES$"
    val ENTITY_INPUTSTREAM = "\$ENTITY_INPUTSTREAM$"
    val ENTITY_SERIALIZABLE = "\$ENTITY_SERIALIZABLE$"
    val ENTITY_MULTIPART = "\$ENTITY_MULTIPART$"
    private val SPECIAL_ENTITIY = Arrays.asList(ENTITY_STRING, ENTITY_BYTES, ENTITY_FILE, ENTITY_INPUTSTREAM, ENTITY_SERIALIZABLE, ENTITY_MULTIPART)

    /**
     * 是否开启debug，
     */
    private var debug = false

    /**
     * 检测url是否含有参数，如果有，则把参数加到参数列表中

     * @param url                    资源地址
     * *
     * @param nvps                参数列表
     * *
     * @return    返回去掉参数的url
     * *
     * @throws UnsupportedEncodingException
     */
    @Throws(UnsupportedEncodingException::class)
    fun checkHasParas(url: String): String {
        var _url = url
        // 检测url中是否存在参数
        if (_url.contains("?") && _url.indexOf("?") < _url.indexOf("=")) {
            _url = _url.substring(0, _url.indexOf("?"))
        }
        return _url
    }

    /**
     * 参数转换，将map中的参数，转到参数列表中

     * @param nvps                参数列表
     * *
     * @param map                参数列表（map）
     * *
     * @throws UnsupportedEncodingException
     */
    @Throws(UnsupportedEncodingException::class)
    fun map2HttpEntity(map: MutableMap<String, Any>?, encoding: String): HttpEntity? {
        var entity: HttpEntity ?= null
        val jsonObject : JSONObject = JSONObject()
        if (map != null && map.size > 0) {
            var isSpecial = false
            // 拼接参数
            for ((key, value) in map) {
                if (SPECIAL_ENTITIY.contains(key)) {//判断是否在之中
                    isSpecial = true
                    if (ENTITY_STRING == key) {//string
                        entity = StringEntity(value.toString(), encoding)
                        break
                    } else if (ENTITY_BYTES == key) {//file
                        entity = ByteArrayEntity(value as ByteArray)
                        break
                    } else if (ENTITY_FILE == key) {//file
                        if (File::class.java.isAssignableFrom(value.javaClass)) {
                            entity = FileEntity(value as File, ContentType.APPLICATION_OCTET_STREAM)
                        } else if (value.javaClass == String::class.java) {
                            entity = FileEntity(File(value as String), ContentType.create("text/plain", "UTF-8"))
                        }
                        break
                    } else if (ENTITY_INPUTSTREAM == key) {//inputstream
                        //						entity = new InputStreamEntity();
                        break
                    } else if (ENTITY_SERIALIZABLE == key) {//serializeable
                        //						entity = new SerializableEntity()
                        break
                    }  else {
                        jsonObject.put(key, value.toString())
                        //nvps.add(BasicNameValuePair(key, value.toString()))
                    }
                } else {
                    jsonObject.put(key, value.toString())
                    //nvps.add(BasicNameValuePair(key, value.toString()))
                }
            }
            if (!isSpecial) {

                entity =  StringEntity(jsonObject.toString(),  ContentType.create("application/json", encoding))
                //entity = UrlEncodedFormEntity(nvps, encoding)
            }
        }
        return entity
    }

    /**
     * @param encoding
     * *
     * @param entity
     */
    private fun removeContentTypeChraset(encoding: String, entity: HttpEntity) {
        try {
            val clazz = entity.javaClass
            val field = clazz.getDeclaredField("contentType")
            field.isAccessible = true //将字段的访问权限设为true：即去除private修饰符的影响
            if (Modifier.isFinal(field.modifiers)) {
                val modifiersField = Field::class.java.getDeclaredField("modifiers") //去除final修饰符的影响，将字段设为可修改的
                modifiersField.isAccessible = true
                modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())
            }
            field.set(entity, BasicHeader(HTTP.CONTENT_TYPE, (field.get(entity) as BasicHeader).value.replace("; charset=" + encoding, "")))
        } catch (e: NoSuchFieldException) {
            LogUtil.e(javaClass, e.message.toString())
        } catch (e: SecurityException) {
            LogUtil.e(javaClass, e.message.toString())
        } catch (e: IllegalArgumentException) {
            LogUtil.e(javaClass, e.message.toString())
        } catch (e: IllegalAccessException) {
            LogUtil.e(javaClass, e.message.toString())
        }
    }


    /**
     * 生成参数
     * 参数格式“k1=v1&k2=v2”

     * @param paras                参数列表
     * *
     * @return                        返回参数列表（map）
     */
    fun buildParas(paras: String): MutableMap<String, Any> {
        val p = paras.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val ps = Array<Array<String?>>(p.size) { arrayOfNulls<String>(2) }
        var pos : Int?
        for (i in p.indices) {
            pos = p[i].indexOf("=")
            ps[i][0] = p[i].substring(0, pos)
            ps[i][1] = p[i].substring(pos + 1)
        }
        return buildParas(ps)
    }

    /**
     * 生成参数
     * 参数类型：{{"k1","v1"},{"k2","v2"}}

     * @param paras                参数列表
     * *
     * @return                        返回参数列表（map）
     */
    fun buildParas(paras: Array<Array<String?>>): MutableMap<String, Any> {
        // 创建参数队列
        val map = HashMap<String, Any>()
        for (para in paras) {
            map.put(para[0]!!, para[1]!!)
        }
        return map
    }
}