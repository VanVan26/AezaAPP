package com.shefivan.aezaapp.domain.usecase.system

import com.shefivan.aezaapp.domain.repository.SystemRepository
import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.error.safeApiCall
import javax.inject.Inject

class GetVersionUseCase @Inject constructor(
    private val repository: SystemRepository,
    private val errors: AppErrorEmitter,
) {
    suspend operator fun invoke() = errors.safeApiCall { repository.getVersion() }
}
