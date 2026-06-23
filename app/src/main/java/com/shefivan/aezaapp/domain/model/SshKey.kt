package com.shefivan.aezaapp.domain.model

import java.time.Instant

data class SshKey(
    val id: Long,
    val ownerId: Long,
    val name: String,
    val publicKey: String,
    val autoAssign: Boolean,
    val createdAt: Instant,
)

data class CreateSshKeyInput(
    val name: String,
    val publicKey: String,
    val autoAssign: Boolean,
)

data class EditSshKeyInput(
    val name: String? = null,
    val publicKey: String? = null,
    val autoAssign: Boolean? = null,
)
