package com.shefivan.aezaapp.domain.repository

import com.shefivan.aezaapp.domain.model.ApiKey
import com.shefivan.aezaapp.domain.model.ApiKeyInput
import com.shefivan.aezaapp.domain.model.Page

interface ApiKeyRepository {
    suspend fun getApiKeys(): Page<ApiKey>

    suspend fun createApiKey(input: ApiKeyInput): ApiKey

    suspend fun editApiKey(id: Long, input: ApiKeyInput)

    suspend fun deleteApiKey(id: Long)
}

