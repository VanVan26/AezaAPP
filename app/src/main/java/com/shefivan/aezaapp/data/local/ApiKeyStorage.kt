package com.shefivan.aezaapp.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyStorage @Inject constructor(
    @ApplicationContext context: Context,
) : ApiKeyProvider {
    private val preferences = EncryptedSharedPreferences.create(
        context,
        PREFERENCES_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun save(apiKey: String) {
        preferences.edit().putString(KEY_API_KEY, apiKey).apply()
    }

    override fun get(): String? = preferences.getString(KEY_API_KEY, null)

    fun clear() {
        preferences.edit().remove(KEY_API_KEY).apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "aeza_auth_encrypted"
        const val KEY_API_KEY = "api_key"
    }
}
