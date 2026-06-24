package com.shefivan.aezaapp.data.mapper

import com.shefivan.aezaapp.data.remote.dto.CreateDomainRecordRequestDto
import com.shefivan.aezaapp.data.remote.dto.DomainListResponseDto
import com.shefivan.aezaapp.data.remote.dto.DomainRecordListResponseDto
import com.shefivan.aezaapp.data.remote.dto.DomainRecordResponseDto
import com.shefivan.aezaapp.data.remote.dto.DomainRecordTypePartResponseDto
import com.shefivan.aezaapp.data.remote.dto.DomainRecordTypeResponseDto
import com.shefivan.aezaapp.data.remote.dto.DomainResponseDto
import com.shefivan.aezaapp.data.remote.dto.EditDomainRecordRequestDto
import com.shefivan.aezaapp.domain.model.CreateDomainRecordInput
import com.shefivan.aezaapp.domain.model.Domain
import com.shefivan.aezaapp.domain.model.DomainRecord
import com.shefivan.aezaapp.domain.model.DomainRecordType
import com.shefivan.aezaapp.domain.model.DomainRecordTypePart
import com.shefivan.aezaapp.domain.model.EditDomainRecordInput
import com.shefivan.aezaapp.domain.model.Page

internal fun DomainListResponseDto.toDomain(): Page<Domain> = Page(
    items = items.map { it.toDomain() },
    total = total,
)

internal fun DomainResponseDto.toDomain(): Domain = Domain(
    id = id,
    name = name,
    status = status,
    statusReason = statusReason,
    observedNameservers = observedNameservers,
    nsCheckedAt = nsCheckedAt?.toInstantOrEpoch(),
    createdAt = createdAt.toInstantOrEpoch(),
    updatedAt = updatedAt.toInstantOrEpoch(),
)

internal fun DomainRecordListResponseDto.toDomain(): Page<DomainRecord> = Page(
    items = items.map { it.toDomain() },
    total = total,
)

internal fun DomainRecordResponseDto.toDomain(): DomainRecord = DomainRecord(
    id = id,
    type = type,
    name = name,
    content = content,
    ttl = ttl,
    priority = priority,
    weight = weight,
    port = port,
    isEnabled = isEnabled,
    note = note,
    createdAt = createdAt.toInstantOrEpoch(),
    updatedAt = updatedAt.toInstantOrEpoch(),
)

internal fun DomainRecordTypeResponseDto.toDomain(): DomainRecordType = DomainRecordType(
    name = name,
    description = description,
    pattern = pattern,
    parts = parts.map { it.toDomain() },
)

private fun DomainRecordTypePartResponseDto.toDomain(): DomainRecordTypePart = DomainRecordTypePart(
    slug = slug,
    pattern = pattern,
    type = type,
)

internal fun CreateDomainRecordInput.toDto(): CreateDomainRecordRequestDto = CreateDomainRecordRequestDto(
    type = type,
    name = name,
    content = content,
    ttl = ttl,
    priority = priority,
    weight = weight,
    port = port,
    isEnabled = isEnabled,
    note = note,
)

internal fun EditDomainRecordInput.toDto(): EditDomainRecordRequestDto = EditDomainRecordRequestDto(
    type = type,
    name = name,
    content = content,
    ttl = ttl,
    priority = priority,
    weight = weight,
    port = port,
    isEnabled = isEnabled,
    note = note,
)
