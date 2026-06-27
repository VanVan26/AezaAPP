package com.shefivan.aezaapp.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class TransactionListResponseDto(
    val items: List<TransactionDto> = emptyList(),
    val total: Int = 0,
)

@Serializable
data class TransactionDto(
    val id: Long = 0,
    val amount: Long = 0,
    val bonusAmount: Long = 0,
    val status: String = "",
    val performedAt: String? = null,
    val createdAt: String = "",
    val type: String = "",
    val serviceId: Long = 0,
    val payload: JsonObject = JsonObject(emptyMap()),
    val invoiceId: Long? = null,
    val invoice: InvoiceNestedDto? = null,
)

@Serializable
data class InvoiceNestedDto(
    val id: Long = 0,
    val flowType: String = "",
    val status: String = "",
)
