package com.shefivan.aezaapp.data.mapper

import com.shefivan.aezaapp.data.remote.dto.CreateServiceBackupRequestDto
import com.shefivan.aezaapp.data.remote.dto.ServiceBackupListResponseDto
import com.shefivan.aezaapp.data.remote.dto.ServiceBackupResponseDto
import com.shefivan.aezaapp.data.remote.dto.SetServiceBackupScheduleRequestDto
import com.shefivan.aezaapp.domain.model.Page
import com.shefivan.aezaapp.domain.model.ServiceBackup
import com.shefivan.aezaapp.domain.model.ServiceBackupSchedule
import com.shefivan.aezaapp.domain.model.ServiceBackupScheduleType
import com.shefivan.aezaapp.domain.model.ServiceBackupSource
import com.shefivan.aezaapp.domain.model.ServiceBackupStatus

internal fun ServiceBackupListResponseDto.toDomain(): Page<ServiceBackup> = Page(
    items = items.map { it.toDomain() },
    total = total,
)

internal fun ServiceBackupResponseDto.toDomain(): ServiceBackup = ServiceBackup(
    id = id,
    name = name,
    size = size,
    createdAt = createdAt.toInstantOrEpoch(),
    source = source.toServiceBackupSource(),
    status = status.toServiceBackupStatus(),
)

internal fun String.toCreateServiceBackupRequestDto(): CreateServiceBackupRequestDto =
    CreateServiceBackupRequestDto(name = this)

internal fun ServiceBackupSchedule.toDto(): SetServiceBackupScheduleRequestDto = SetServiceBackupScheduleRequestDto(
    limit = limit,
    type = type.toApiValue(),
    weekDay = weekDay,
    monthDay = monthDay,
)

private fun String.toServiceBackupSource(): ServiceBackupSource = when (this) {
    "manual" -> ServiceBackupSource.MANUAL
    "schedule" -> ServiceBackupSource.SCHEDULE
    else -> ServiceBackupSource.UNKNOWN
}

private fun String.toServiceBackupStatus(): ServiceBackupStatus = when (this) {
    "creating" -> ServiceBackupStatus.CREATING
    "active" -> ServiceBackupStatus.ACTIVE
    "deleted" -> ServiceBackupStatus.DELETED
    else -> ServiceBackupStatus.UNKNOWN
}

private fun ServiceBackupScheduleType.toApiValue(): String = when (this) {
    ServiceBackupScheduleType.DAILY -> "daily"
    ServiceBackupScheduleType.WEEKLY -> "weekly"
    ServiceBackupScheduleType.MONTHLY -> "monthly"
    ServiceBackupScheduleType.UNKNOWN -> error("Unknown service backup schedule type cannot be sent")
}
