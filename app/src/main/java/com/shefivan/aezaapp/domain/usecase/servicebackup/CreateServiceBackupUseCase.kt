package com.shefivan.aezaapp.domain.usecase.servicebackup

import com.shefivan.aezaapp.domain.repository.ServiceBackupRepository
import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.error.safeApiCall
import javax.inject.Inject

class CreateServiceBackupUseCase @Inject constructor(
    private val repository: ServiceBackupRepository,
    private val errors: AppErrorEmitter,
) {
    suspend operator fun invoke(serviceId: Long, name: String) = errors.safeApiCall { repository.createBackup(serviceId, name) }
}
