package com.shefivan.aezaapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SshKeyListResponseDto(
    val items: List<SshKeyResponseDto> = emptyList(),
    val total: Int = 0,
)

@Serializable
data class SshKeyResponseDto(
    val id: Long = 0L,
    val ownerId: Long = 0L,
    val name: String = "",
    val publicKey: String = "",
    val autoAssign: Boolean = false,
    val createdAt: String = "",
)

@Serializable
data class CreateSshKeyRequestDto(
    val name: String,
    val publicKey: String,
    val autoAssign: Boolean,
)

@Serializable
data class EditSshKeyRequestDto(
    val name: String? = null,
    val publicKey: String? = null,
    val autoAssign: Boolean? = null,
)

