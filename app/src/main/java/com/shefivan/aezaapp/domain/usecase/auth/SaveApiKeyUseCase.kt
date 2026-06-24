package com.shefivan.aezaapp.domain.usecase.auth

import com.shefivan.aezaapp.domain.repository.AuthRepository
import javax.inject.Inject

class SaveApiKeyUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(apiKey: String) = repository.saveApiKey(apiKey)
}
