package com.shefivan.aezaapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DomainListResponseDto(
    val items: List<DomainResponseDto> = emptyList(),
    val total: Int = 0,
)

@Serializable
data class DomainResponseDto(
    val id: Long = 0L,
    val name: String = "",
    val status: String = "",
    val statusReason: String? = null,
    val observedNameservers: List<String>? = null,
    val nsCheckedAt: String? = null,
    val createdAt: String = "",
    val updatedAt: String = "",
)

@Serializable
data class DomainRecordListResponseDto(
    val items: List<DomainRecordResponseDto> = emptyList(),
    val total: Int = 0,
)

@Serializable
data class DomainRecordResponseDto(
    val id: Long = 0L,
    val type: String = "",
    val name: String = "",
    val content: String = "",
    val ttl: Int = 3600,
    val priority: Int? = null,
    val weight: Int? = null,
    val port: Int? = null,
    val isEnabled: Boolean = true,
    val note: String? = null,
    val createdAt: String = "",
    val updatedAt: String = "",
)

@Serializable
data class DomainRecordTypeListResponseDto(
    val items: List<DomainRecordTypeResponseDto> = emptyList(),
    val total: Int = 0,
)

@Serializable
data class DomainRecordTypeResponseDto(
    val name: String = "",
    val description: String? = null,
    val pattern: String? = null,
    val parts: List<DomainRecordTypePartResponseDto> = emptyList(),
)

@Serializable
data class DomainRecordTypePartResponseDto(
    val slug: String = "",
    val pattern: String? = null,
    val type: String = "",
)

@Serializable
data class DomainExpectedNameserversResponseDto(
    val items: List<String> = emptyList(),
)

@Serializable
data class CreateDomainRequestDto(
    val name: String,
)

@Serializable
data class CreateDomainRecordRequestDto(
    val type: String,
    val name: String,
    val content: String,
    val ttl: Int? = null,
    val priority: Int? = null,
    val weight: Int? = null,
    val port: Int? = null,
    val isEnabled: Boolean? = null,
    val note: String? = null,
)

@Serializable
data class EditDomainRecordRequestDto(
    val type: String? = null,
    val name: String? = null,
    val content: String? = null,
    val ttl: Int? = null,
    val priority: Int? = null,
    val weight: Int? = null,
    val port: Int? = null,
    val isEnabled: Boolean? = null,
    val note: String? = null,
)
