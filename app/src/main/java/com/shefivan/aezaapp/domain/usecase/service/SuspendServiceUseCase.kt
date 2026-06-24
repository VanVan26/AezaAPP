package com.shefivan.aezaapp.domain.usecase.service

import com.shefivan.aezaapp.domain.repository.ServiceRepository
import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.error.safeApiCall
import javax.inject.Inject

class SuspendServiceUseCase @Inject constructor(
    private val repository: ServiceRepository,
    private val errors: AppErrorEmitter,
) {
    suspend operator fun invoke(id: Long, force: Boolean = false) = errors.safeApiCall { repository.suspend(id, force) }
}
