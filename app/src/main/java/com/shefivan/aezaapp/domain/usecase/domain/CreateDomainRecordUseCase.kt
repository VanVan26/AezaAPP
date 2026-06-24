package com.shefivan.aezaapp.domain.usecase.domain

import com.shefivan.aezaapp.domain.model.CreateDomainRecordInput
import com.shefivan.aezaapp.domain.repository.DomainRepository
import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.error.safeApiCall
import javax.inject.Inject

class CreateDomainRecordUseCase @Inject constructor(
    private val repository: DomainRepository,
    private val errors: AppErrorEmitter,
) {
    suspend operator fun invoke(domainId: Long, input: CreateDomainRecordInput) = errors.safeApiCall { repository.createRecord(domainId, input) }
}
