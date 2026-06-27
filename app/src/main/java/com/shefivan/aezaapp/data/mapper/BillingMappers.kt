package com.shefivan.aezaapp.data.mapper

import com.shefivan.aezaapp.data.remote.dto.TransactionDto
import com.shefivan.aezaapp.data.remote.dto.TransactionListResponseDto
import com.shefivan.aezaapp.domain.model.Page
import com.shefivan.aezaapp.domain.model.ServiceTransaction

internal fun TransactionListResponseDto.toDomain(): Page<ServiceTransaction> = Page(
    items = items.map { it.toDomain() },
    total = total,
)

private fun TransactionDto.toDomain(): ServiceTransaction = ServiceTransaction(
    id = id,
    amount = amount,
    bonusAmount = bonusAmount,
    status = status,
    performedAt = performedAt?.toInstantOrEpoch()?.takeIf { it.epochSecond > 0 },
    createdAt = createdAt.toInstantOrEpoch(),
    type = type,
    serviceId = serviceId,
)
