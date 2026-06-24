package com.shefivan.aezaapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupportTicketsResponseDto(
    val open: List<TicketSummaryResponseDto> = emptyList(),
    val closed: List<TicketSummaryResponseDto> = emptyList(),
    val solved: List<TicketSummaryResponseDto> = emptyList(),
    @SerialName("\u0067\u0070\u0074")
    val extra: TicketSummaryResponseDto? = null,
    val totalUnread: Int = 0,
)

@Serializable
data class TicketSummaryResponseDto(
    val id: Long = 0L,
    val name: String = "",
    val unreadCount: Int = 0,
)

@Serializable
data class TicketResponseDto(
    val id: Long = 0L,
    val name: String = "",
    val status: String = "",
    val service: TicketServiceDto? = null,
    val abuse: TicketAbuseDto? = null,
    val rate: Int? = null,
    val rateComment: String? = null,
    val totalTips: Double? = null,
)

@Serializable
data class TicketServiceDto(
    val id: Long = 0L,
    val name: String = "",
)

@Serializable
data class TicketAbuseDto(
    val status: String? = null,
    val restriction: String? = null,
    val startedAt: String? = null,
    val expiresAt: String? = null,
)

@Serializable
data class TicketMessageListResponseDto(
    val items: List<TicketMessageResponseDto> = emptyList(),
    val total: Int = 0,
)

@Serializable
data class TicketMessageResponseDto(
    val id: Long = 0L,
    val body: String = "",
    val createdAt: String = "",
    val role: String = "",
    val reaction: String? = null,
    val files: List<FileAssetResponseDto> = emptyList(),
    val author: TicketMessageAuthorDto = TicketMessageAuthorDto(),
    val type: String? = null,
)

@Serializable
data class TicketMessageAuthorDto(
    val name: String = "",
    val photo: String? = null,
    val isIntern: Boolean? = null,
    val id: Long? = null,
)

@Serializable
data class CreateTicketRequestDto(
    val name: String,
    val body: String,
    val fileIds: List<Long> = emptyList(),
    val serviceId: Long? = null,
)

@Serializable
data class SendTicketMessageRequestDto(
    val body: String,
    val fileIds: List<Long> = emptyList(),
)

@Serializable
data class SetTicketReactionRequestDto(
    val reaction: String,
)

@Serializable
data class TicketRateResponseDto(
    val value: Int? = null,
    val comment: String? = null,
)

@Serializable
data class TicketRateRequestDto(
    val value: Int,
    val comment: String? = null,
)
