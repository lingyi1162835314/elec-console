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

import org.springframework.util.Base64Utils
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.Signature
import java.util.Random
import javax.crypto.KeyGenerator

object SignUtils {
    val KEY_ALGORITHM = "RSA"
    val SIGNATURE_ALGORITHM = "MD5withRSA"
    fun RSASign(privateKey: RSAPrivateKey, data: ByteArray, signAlgorithm: String = SIGNATURE_ALGORITHM): ByteArray {
        val signature = Signature.getInstance(signAlgorithm)
        signature.initSign(privateKey)
        signature.update(data)
        return signature.sign()
    }

    fun RSAVerify(publicKey: RSAPublicKey, data: ByteArray, sign: ByteArray, signAlgorithm: String = SIGNATURE_ALGORITHM): Boolean {
        val signature = Signature.getInstance(signAlgorithm)
        signature.initVerify(publicKey)
        signature.update(data)
        val verifyRes = signature.verify(sign)
        return verifyRes
    }
}

object RSACipher1024 {
    //公钥加密
    fun pubkEncrypt(publicKey: RSAPublicKey, obj: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val result = cipher.doFinal(obj)
        return result
    }

    //私钥加密
    fun prikEncrypt(privateKey: RSAPrivateKey, obj: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, privateKey)
        val result = cipher.doFinal(obj)
        return result
    }
    //公钥解密
    fun pubkDecrypt(publicKey: RSAPublicKey, obj: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, publicKey)
        return cipher.doFinal(obj)
    }
    //私钥解密
    fun prikDecrypt(privateKey: RSAPrivateKey, obj: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return cipher.doFinal(obj)
    }

    fun getPublicKey(strPubKey: String): RSAPublicKey {
        //获取公钥
        val pkBytes = Base64Utils.decodeFromString(strPubKey)
        val kSpec = X509EncodedKeySpec (pkBytes)
        val pubKey = KeyFactory.getInstance("RSA").generatePublic(kSpec) as RSAPublicKey
        return pubKey
    }
    fun getPrivateKey(strPriKey: String): RSAPrivateKey {
        //获取公钥
        val pkBytes = Base64Utils.decodeFromString(strPriKey)
        val kSpec = PKCS8EncodedKeySpec (pkBytes)
        val priKey = KeyFactory.getInstance("RSA").generatePrivate(kSpec) as RSAPrivateKey
        return priKey
    }
}

object HmacMd5 {
    fun hmacMd5Encrypt(hmacMd5sk: ByteArray, text: ByteArray): ByteArray {
        val sk = SecretKeySpec(hmacMd5sk,"HmacMD5")
        val mac = Mac.getInstance(sk.algorithm)
        mac.init(sk)
        return mac.doFinal(text)
    }
}

object AES128CBC {
    fun encrypt(sessionKey: ByteArray, data: ByteArray, paddingType: String = "PKCS5Padding"): ByteArray {
        val key = sessionKey.dropLast(16)
        val IV = sessionKey.drop(16)
        val skeySpec = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/CBC/$paddingType")//"算法/模式/补码方式"
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec,IvParameterSpec(IV.toByteArray()))
        val result = cipher.doFinal(data)
        return result
    }

    fun decrypt(sessionKey: ByteArray, data: ByteArray, paddingType: String = "PKCS5Padding"): ByteArray {
        val key = sessionKey.dropLast(16)
        val IV = sessionKey.drop(16)
        val skeySpec = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/CBC/$paddingType") ///////////////////////////////
        cipher.init(Cipher.DECRYPT_MODE, skeySpec,IvParameterSpec(IV.toByteArray()))
        val result = cipher.doFinal(data)
        return result
    }

    fun getAESKey(keyLength: Int): ByteArray {
        val kg = KeyGenerator.getInstance("AES")
        kg.init(keyLength)
        val sk = kg.generateKey()
        return sk.encoded
    }
}

object IntToByteArray {
    fun intToBytes(value: Int): ByteArray {
        val src = ByteArray(4)
        src[3] = (value shr 24 and 0xFF).toByte()
        src[2] = (value shr 16 and 0xFF).toByte()
        src[1] = (value shr 8 and 0xFF).toByte()
        src[0] = (value and 0xFF).toByte()
        return src
    }
}

object RandomData {
    fun getRandomString(length: Int): String {
        val base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val random = Random()
        val sb = StringBuffer()
        for (i in 0..length - 1) {
            val number = random.nextInt(base.length)
            sb.append(base[number])
        }
        return sb.toString()
    }

    fun getRandomByte(length: Int): ByteArray {
        val byteArr = ByteArray(length)
        for(i in byteArr.indices) {
            byteArr[i] = Random().nextInt().toByte()
        }
        return byteArr
    }

}


