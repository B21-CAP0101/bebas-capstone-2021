package com.capstone101.core.utils

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.securepreferences.SecurePreferences

class SessionManager(context: Context) {
    companion object {
        private const val KEY_LOGIN = "com.capstone101.core.utils.login"
    }

    private var pref = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val spec = KeyGenParameterSpec.Builder(
            MasterKey.DEFAULT_MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build()
        val masterKey = MasterKey.Builder(context).setKeyGenParameterSpec(spec).build()
        EncryptedSharedPreferences.create(
            context, "78w3445", masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } else SecurePreferences(context, "BeBaS B21-0101", "78w3445")
    private var editor = pref.edit()

    fun createLogin() {
        editor.putBoolean(KEY_LOGIN, true).commit()
    }

    val isLogin = pref.getBoolean(KEY_LOGIN, false)
}