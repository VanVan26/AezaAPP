package com.shefivan.aezaapp.domain.model

import java.time.Instant

enum class ServiceBackupSource {
    MANUAL,
    SCHEDULE,
    UNKNOWN,
}

enum class ServiceBackupStatus {
    CREATING,
    ACTIVE,
    DELETED,
    UNKNOWN,
}

enum class ServiceBackupScheduleType {
    DAILY,
    WEEKLY,
    MONTHLY,
    UNKNOWN,
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
