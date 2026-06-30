package com.shefivan.aezaapp.domain.repository

interface AuthRepository {
    suspend fun saveApiKey(apiKey: String)

    suspend fun getApiKey(): String?

    suspend fun clearApiKey()
}
