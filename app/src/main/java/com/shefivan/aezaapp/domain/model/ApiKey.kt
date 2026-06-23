package com.shefivan.aezaapp.domain.model

import java.time.Instant

data class ApiKey(
    val id: Long,
    val ownerId: Long,
    val name: String,
    val ips: List<String>,
    val isActive: Boolean,
    val lastUsedAt: Instant?,
    val lastIp: String?,
    val createdAt: Instant,
)

data class ApiKeyInput(
    val name: String,
    val ips: List<String> = emptyList(),
    val isActive: Boolean = true,
)

