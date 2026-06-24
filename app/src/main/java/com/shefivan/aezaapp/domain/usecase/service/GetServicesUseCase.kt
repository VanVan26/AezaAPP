package com.shefivan.aezaapp.domain.usecase.service

import com.shefivan.aezaapp.domain.model.PageQuery
import com.shefivan.aezaapp.domain.repository.ServiceRepository
import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.error.safeApiCall
import javax.inject.Inject

class GetServicesUseCase @Inject constructor(
    private val repository: ServiceRepository,
    private val errors: AppErrorEmitter,
) {
    suspend operator fun invoke(query: PageQuery = PageQuery()) = errors.safeApiCall { repository.getServices(query) }
}
