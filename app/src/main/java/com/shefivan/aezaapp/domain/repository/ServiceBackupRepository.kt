package com.shefivan.aezaapp.domain.repository

import com.shefivan.aezaapp.domain.model.Page
import com.shefivan.aezaapp.domain.model.PageQuery
import com.shefivan.aezaapp.domain.model.ServiceBackup
import com.shefivan.aezaapp.domain.model.ServiceBackupSchedule

interface ServiceBackupRepository {
    suspend fun getBackups(serviceId: Long, query: PageQuery = PageQuery()): Page<ServiceBackup>

    suspend fun createBackup(serviceId: Long, name: String): ServiceBackup

    suspend fun deleteBackup(serviceId: Long, backupId: Long)

    suspend fun restoreBackup(serviceId: Long, backupId: Long)

    suspend fun setSchedule(serviceId: Long, schedule: ServiceBackupSchedule)

    suspend fun deleteSchedule(serviceId: Long)
}
