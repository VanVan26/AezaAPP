package com.shefivan.aezaapp.domain.usecase.servicebackup

import com.shefivan.aezaapp.domain.model.PageQuery
import com.shefivan.aezaapp.domain.repository.ServiceBackupRepository
import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.error.safeApiCall
import javax.inject.Inject

class GetServiceBackupsUseCase @Inject constructor(
    private val repository: ServiceBackupRepository,
    private val errors: AppErrorEmitter,
) {
    suspend operator fun invoke(serviceId: Long, query: PageQuery = PageQuery()) = errors.safeApiCall { repository.getBackups(serviceId, query) }
}
