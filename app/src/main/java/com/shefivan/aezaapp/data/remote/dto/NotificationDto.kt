package com.shefivan.aezaapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NotificationListResponseDto(
    val items: List<NotificationResponseDto> = emptyList(),
    val total: Int = 0,
)

@Serializable
data class NotificationResponseDto(
    val id: Long = 0L,
    val text: String = "",
    val isRead: Boolean = false,
    val createdAt: String = "",
)

