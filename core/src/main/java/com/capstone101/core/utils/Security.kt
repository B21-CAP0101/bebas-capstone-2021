package com.capstone101.core.utils

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Security {

    private fun initSecureRandom(): SecureRandom {
        val iv = ByteArray(16)
        return SecureRandom().apply { nextBytes(iv) }
    }

    private fun initSecretKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(256)
        return keyGenerator.generateKey()
    }

    fun encrypt(data: String): String {
        val cipher = Cipher.getInstance("AES")
        val keyS = initSecretKey().encoded
        val secretKeySpec = SecretKeySpec(keyS, "AES")
        val ivSpec = IvParameterSpec(initSecureRandom().generateSeed(cipher.blockSize))

        setSecretKey(keyS)

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec)
        val ciphered = cipher.doFinal(data.toByteArray())
        return Base64.encodeToString(ciphered, Base64.DEFAULT)
    }

    @Volatile
    var key: Array<String> = Array(1) { i -> i.toString() }

    private fun setSecretKey(keyS: ByteArray) {
        key = Array(keyS.size) { it.toString() }
        for (i in keyS.indices) key[i] = keyS[i].toString()
    }

    fun decrypt(data: String, key: Array<String>): String {
        val ciphered = Base64.decode(data, Base64.DEFAULT)

        val cipher = Cipher.getInstance("AES")

        val secretKey = ByteArray(key.size)
        for (i in key.indices) secretKey[i] = key[i].toByte()

        val secretKeySpec = SecretKeySpec(secretKey, "AES")
        val ivSpec = IvParameterSpec(initSecureRandom().generateSeed(cipher.blockSize))

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec)
        return String(cipher.doFinal(ciphered))
    }
}