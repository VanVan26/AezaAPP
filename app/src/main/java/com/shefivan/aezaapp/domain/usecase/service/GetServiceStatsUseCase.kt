package com.shefivan.aezaapp.domain.usecase.service

import com.shefivan.aezaapp.domain.model.ServiceStatsRequest
import com.shefivan.aezaapp.domain.repository.ServiceRepository
import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.error.safeApiCall
import javax.inject.Inject

class GetServiceStatsUseCase @Inject constructor(
    private val repository: ServiceRepository,
    private val errors: AppErrorEmitter,
) {
    suspend operator fun invoke(id: Long, request: ServiceStatsRequest) = errors.safeApiCall { repository.getStats(id, request) }
}
