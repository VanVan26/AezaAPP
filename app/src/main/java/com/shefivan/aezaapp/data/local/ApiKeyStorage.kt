package com.shefivan.aezaapp.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class ApiKeyStorage @Inject constructor(
    @ApplicationContext context: Context,
) : ApiKeyProvider {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun save(apiKey: String) {
        preferences.edit {
            putString(KEY_API_KEY, apiKey)
        }
    }

    override fun get(): String? = preferences.getString(KEY_API_KEY, null)

    fun clear() {
        preferences.edit {
            remove(KEY_API_KEY)
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "aeza_auth"
        const val KEY_API_KEY = "api_key"
    }
}
