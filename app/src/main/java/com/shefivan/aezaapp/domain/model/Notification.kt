package com.shefivan.aezaapp.domain.model

import java.time.Instant

data class Notification(
    val id: Long,
    val text: String,
    val isRead: Boolean,
    val createdAt: Instant,
)
