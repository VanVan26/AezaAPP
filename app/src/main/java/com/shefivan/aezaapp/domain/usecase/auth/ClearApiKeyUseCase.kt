package com.shefivan.aezaapp.domain.usecase.auth

import com.shefivan.aezaapp.domain.repository.AuthRepository
import javax.inject.Inject

class ClearApiKeyUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke() = repository.clearApiKey()
}
