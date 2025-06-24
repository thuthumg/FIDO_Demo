package com.example.fido_demo


import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import net.openid.appauth.AuthState


object AuthStateManager {
    private const val PREF_NAME = "secure_auth_prefs"
    private const val KEY = "auth_state"

    private fun getEncryptedPrefs(context: Context): EncryptedSharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences
    }

    fun readAuthState(context: Context): AuthState? {
        return runCatching {
            val prefs = getEncryptedPrefs(context)
            val json = prefs.getString(KEY, null)
            json?.let { AuthState.jsonDeserialize(it) }
        }.getOrNull()
    }

    fun writeAuthState(context: Context, authState: AuthState) {
        runCatching {
            val prefs = getEncryptedPrefs(context)
            val json = authState.jsonSerializeString()
            prefs.edit().putString(KEY, json).apply()
        }.onFailure {
            it.printStackTrace()
        }
    }

    fun clear(context: Context) {
        runCatching {
            val prefs = getEncryptedPrefs(context)
            prefs.edit().remove(KEY).apply()
        }
    }
}
