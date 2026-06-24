package com.shefivan.aezaapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ServiceBackupListResponseDto(
    val items: List<ServiceBackupResponseDto> = emptyList(),
    val total: Int = 0,
)

@Serializable
data class ServiceBackupResponseDto(
    val id: Long = 0L,
    val name: String = "",
    val size: Long? = null,
    val createdAt: String = "",
    val source: String = "",
    val status: String = "",
)

@Serializable
data class CreateServiceBackupRequestDto(
    val name: String,
)

@Serializable
data class SetServiceBackupScheduleRequestDto(
    val limit: Int,
    val type: String,
    val weekDay: Int? = null,
    val monthDay: Int? = null,
)
