package com.shefivan.aezaapp.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class ServicesListResponseDto(
    val items: List<ServiceSmallResponseDto> = emptyList(),
    val total: Int = 0,
)

@Serializable
data class ServiceResponseDto(
    val id: Long = 0L,
    val name: String = "",
    val ip: String = "",
    val payload: JsonObject = JsonObject(emptyMap()),
    val price: Double = 0.0,
    val paymentTerm: String = "",
    val autoProlong: Boolean = false,
    val createdAt: String = "",
    val expiresAt: String = "",
    val status: String = "",
    val typeSlug: String = "",
    val productName: String = "",
    val product: ProductNestedResponseDto = ProductNestedResponseDto(),
    val locationCode: String? = null,
    val currentTask: ServiceTaskResponseDto? = null,
    val capabilities: JsonElement? = null,
    val ownerId: Long? = null,
    val productId: Long? = null,
    val backups: Boolean? = null,
    val schedule: JsonObject = JsonObject(emptyMap()),
    val parameters: JsonObject = JsonObject(emptyMap()),
    val secureParameters: JsonObject = JsonObject(emptyMap()),
)

@Serializable
data class ServiceSmallResponseDto(
    val id: Long = 0L,
    val name: String = "",
    val ip: String = "",
    val payload: JsonObject = JsonObject(emptyMap()),
    val price: Double = 0.0,
    val paymentTerm: String = "",
    val autoProlong: Boolean = false,
    val createdAt: String = "",
    val expiresAt: String = "",
    val status: String = "",
    val typeSlug: String = "",
    val productName: String = "",
    val product: ProductNestedResponseDto = ProductNestedResponseDto(),
    val locationCode: String? = null,
    val currentTask: ServiceTaskResponseDto? = null,
    val capabilities: JsonElement? = null,
)

@Serializable
data class ProductNestedResponseDto(
    val id: Long = 0L,
    val name: String = "",
    val typeSlug: String = "",
    val typeName: String = "",
    val groupId: Long = 0L,
    val payload: JsonObject = JsonObject(emptyMap()),
    val localedPayload: JsonObject = JsonObject(emptyMap()),
)

@Serializable
data class ServiceTaskResponseDto(
    val id: String = "",
    val slug: String = "",
    val name: String = "",
    val createdAt: String = "",
    val status: String = "",
)

@Serializable
data class RemoteVncResponseDto(
    val address: String = "",
    val password: String = "",
)

@Serializable
data class ServiceStatsResponseDto(
    val data: List<String> = emptyList(),
)

@Serializable
data class ServiceTaskListResponseDto(
    val items: List<ServiceTaskResponseDto> = emptyList(),
    val total: Int = 0,
)

@Serializable
data class ServiceSuspendRequestDto(
    val force: Boolean = false,
)

@Serializable
data class ChangePasswordRequestDto(
    val password: String,
)

@Serializable
data class ReinstallServiceRequestDto(
    val os: String,
    val recipe: String,
    val password: String,
    val sshKeyIds: List<Long> = emptyList(),
)
