package com.shefivan.aezaapp.domain.usecase.service

import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.error.safeApiCall
import com.shefivan.aezaapp.domain.repository.ServiceRepository
import javax.inject.Inject

class GetServiceTransactionsUseCase @Inject constructor(
    private val repository: ServiceRepository,
    private val errors: AppErrorEmitter,
) {
    suspend operator fun invoke(serviceId: Long) = errors.safeApiCall { repository.getTransactions(serviceId) }
}
