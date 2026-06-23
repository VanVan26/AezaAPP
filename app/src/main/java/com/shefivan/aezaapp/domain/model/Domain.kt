package com.shefivan.aezaapp.domain.model

import java.time.Instant

data class Domain(
    val id: Long,
    val name: String,
    val status: String,
    val statusReason: String?,
    val observedNameservers: List<String>?,
    val nsCheckedAt: Instant?,
    val createdAt: Instant,
    val updatedAt: Instant,
)

data class DomainRecord(
    val id: Long,
    val type: String,
    val name: String,
    val content: String,
    val ttl: Int,
    val priority: Int?,
    val weight: Int?,
    val port: Int?,
    val isEnabled: Boolean,
    val note: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
)

data class CreateDomainRecordInput(
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

data class EditDomainRecordInput(
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

data class DomainRecordType(
    val name: String,
    val description: String?,
    val pattern: String?,
    val parts: List<DomainRecordTypePart>,
)

data class DomainRecordTypePart(
    val slug: String,
    val pattern: String?,
    val type: String,
)
