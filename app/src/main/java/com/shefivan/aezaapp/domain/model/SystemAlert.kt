package com.shefivan.aezaapp.domain.model

import java.time.Instant

data class SystemAlert(
    val id: Long,
    val slot: String,
    val title: String?,
    val body: String,
    val metadata: DynamicMap?,
    val createdAt: Instant,
)

