package com.shefivan.aezaapp.domain.usecase.auth

import com.shefivan.aezaapp.domain.repository.AuthRepository
import javax.inject.Inject

class GetApiKeyUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke() = repository.getApiKey()
}
