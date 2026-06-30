package com.shefivan.aezaapp.data.mapper

import com.shefivan.aezaapp.data.remote.dto.NotificationListResponseDto
import com.shefivan.aezaapp.data.remote.dto.NotificationResponseDto
import com.shefivan.aezaapp.domain.model.Notification
import com.shefivan.aezaapp.domain.model.Page

internal fun NotificationListResponseDto.toDomain(): Page<Notification> = Page(
    items = items.map { it.toDomain() },
    total = total,
)

internal fun NotificationResponseDto.toDomain(): Notification = Notification(
    id = id,
    text = text,
    isRead = isRead,
    createdAt = createdAt.toInstantOrEpoch(),
)
