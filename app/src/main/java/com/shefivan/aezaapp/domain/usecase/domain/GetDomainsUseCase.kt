package com.shefivan.aezaapp.domain.usecase.domain

import com.shefivan.aezaapp.domain.model.PageQuery
import com.shefivan.aezaapp.domain.repository.DomainRepository
import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.error.safeApiCall
import javax.inject.Inject

class GetDomainsUseCase @Inject constructor(
    private val repository: DomainRepository,
    private val errors: AppErrorEmitter,
) {
    suspend operator fun invoke(query: PageQuery = PageQuery()) = errors.safeApiCall { repository.getDomains(query) }
}
