package com.shefivan.aezaapp.data.repository

import com.shefivan.aezaapp.data.local.ApiKeyStorage
import com.shefivan.aezaapp.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiKeyStorage: ApiKeyStorage,
) : AuthRepository {
    override suspend fun saveApiKey(apiKey: String) {
        apiKeyStorage.save(apiKey)
    }

    override suspend fun getApiKey(): String? = apiKeyStorage.get()

    override suspend fun clearApiKey() {
        apiKeyStorage.clear()
    }
}
