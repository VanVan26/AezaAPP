package com.shefivan.aezaapp.data.mapper

import com.shefivan.aezaapp.data.remote.dto.SystemAlertResponseDto
import com.shefivan.aezaapp.domain.model.SystemAlert

internal fun SystemAlertResponseDto.toDomain(): SystemAlert = SystemAlert(
    id = id,
    slot = slot,
    title = title,
    body = body,
    metadata = metadata?.toDynamicMap(),
    createdAt = createdAt.toInstantOrEpoch(),
)
