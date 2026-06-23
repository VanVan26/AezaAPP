package com.shefivan.aezaapp.domain.model

import java.time.Instant

enum class ServiceBackupSource {
    MANUAL,
    SCHEDULE,
}

enum class ServiceBackupStatus {
    CREATING,
    ACTIVE,
    DELETED,
}

enum class ServiceBackupScheduleType {
    DAILY,
    WEEKLY,
    MONTHLY,
}

data class ServiceBackup(
    val id: Long,
    val name: String,
    val size: Long?,
    val createdAt: Instant,
    val source: ServiceBackupSource,
    val status: ServiceBackupStatus,
)

data class ServiceBackupSchedule(
    val limit: Int,
    val type: ServiceBackupScheduleType,
    val weekDay: Int? = null,
    val monthDay: Int? = null,
)

