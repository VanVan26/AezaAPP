package com.shefivan.aezaapp.data.repository

import com.shefivan.aezaapp.data.mapper.toCreateServiceBackupRequestDto
import com.shefivan.aezaapp.data.mapper.toDomain
import com.shefivan.aezaapp.data.mapper.toDto
import com.shefivan.aezaapp.data.remote.api.AezaApiService
import com.shefivan.aezaapp.domain.model.Page
import com.shefivan.aezaapp.domain.model.PageQuery
import com.shefivan.aezaapp.domain.model.ServiceBackup
import com.shefivan.aezaapp.domain.model.ServiceBackupSchedule
import com.shefivan.aezaapp.domain.repository.ServiceBackupRepository
import javax.inject.Inject

class ServiceBackupRepositoryImpl @Inject constructor(
    private val api: AezaApiService,
) : ServiceBackupRepository {
    override suspend fun getBackups(serviceId: Long, query: PageQuery): Page<ServiceBackup> = api.getServiceBackups(
        serviceId = serviceId,
        offset = query.offset,
        limit = query.limit,
        sort = query.sort,
        filter = query.filter,
    ).toDomain()

    override suspend fun createBackup(serviceId: Long, name: String): ServiceBackup =
        api.createServiceBackup(serviceId, name.toCreateServiceBackupRequestDto()).toDomain()

    override suspend fun deleteBackup(serviceId: Long, backupId: Long) {
        api.deleteServiceBackup(serviceId, backupId)
    }

    override suspend fun restoreBackup(serviceId: Long, backupId: Long) {
        api.restoreServiceBackup(serviceId, backupId)
    }

    override suspend fun setSchedule(serviceId: Long, schedule: ServiceBackupSchedule) {
        api.setServiceBackupSchedule(serviceId, schedule.toDto())
    }

    override suspend fun deleteSchedule(serviceId: Long) {
        api.deleteServiceBackupSchedule(serviceId)
    }
}
