package com.shefivan.aezaapp.data.mapper

import com.shefivan.aezaapp.data.remote.dto.CreateSshKeyRequestDto
import com.shefivan.aezaapp.data.remote.dto.EditSshKeyRequestDto
import com.shefivan.aezaapp.data.remote.dto.SshKeyListResponseDto
import com.shefivan.aezaapp.data.remote.dto.SshKeyResponseDto
import com.shefivan.aezaapp.domain.model.CreateSshKeyInput
import com.shefivan.aezaapp.domain.model.EditSshKeyInput
import com.shefivan.aezaapp.domain.model.Page
import com.shefivan.aezaapp.domain.model.SshKey

internal fun SshKeyListResponseDto.toDomain(): Page<SshKey> = Page(
    items = items.map { it.toDomain() },
    total = total,
)

internal fun SshKeyResponseDto.toDomain(): SshKey = SshKey(
    id = id,
    ownerId = ownerId,
    name = name,
    publicKey = publicKey,
    autoAssign = autoAssign,
    createdAt = createdAt.toInstantOrEpoch(),
)

internal fun CreateSshKeyInput.toDto(): CreateSshKeyRequestDto = CreateSshKeyRequestDto(
    name = name,
    publicKey = publicKey,
    autoAssign = autoAssign,
)

internal fun EditSshKeyInput.toDto(): EditSshKeyRequestDto = EditSshKeyRequestDto(
    name = name,
    publicKey = publicKey,
    autoAssign = autoAssign,
)
