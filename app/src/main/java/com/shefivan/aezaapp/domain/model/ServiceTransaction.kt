package com.shefivan.aezaapp.domain.model

import java.time.Instant

data class ServiceTransaction(
    val id: Long,
    val amount: Long,
    val bonusAmount: Long,
    val status: String,
    val performedAt: Instant?,
    val createdAt: Instant,
    val type: String,
    val serviceId: Long,
)
