package com.shefivan.aezaapp.domain.usecase.domain

import com.shefivan.aezaapp.domain.model.EditDomainRecordInput
import com.shefivan.aezaapp.domain.repository.DomainRepository
import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.error.safeApiCall
import javax.inject.Inject

class EditDomainRecordUseCase @Inject constructor(
    private val repository: DomainRepository,
    private val errors: AppErrorEmitter,
) {
    suspend operator fun invoke(domainId: Long, recordId: Long, input: EditDomainRecordInput) = errors.safeApiCall { repository.editRecord(domainId, recordId, input) }
}
