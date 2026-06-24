package com.shefivan.aezaapp.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class SystemAlertResponseDto(
    val id: Long = 0L,
    val slot: String = "",
    val title: String? = null,
    val body: String = "",
    val metadata: JsonObject? = null,
    val createdAt: String = "",
)
