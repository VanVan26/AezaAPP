package com.shefivan.aezaapp.domain.model

import java.math.BigDecimal
import java.time.Instant

data class SupportTickets(
    val open: List<TicketSummary>,
    val closed: List<TicketSummary>,
    val solved: List<TicketSummary>,
    val extra: TicketSummary?,
    val totalUnread: Int,
)

data class TicketSummary(
    val id: Long,
    val name: String,
    val unreadCount: Int,
)

data class Ticket(
    val id: Long,
    val name: String,
    val status: String,
    val service: Service?,
    val abuse: TicketAbuse?,
    val rate: Int?,
    val rateComment: String?,
    val totalTips: BigDecimal?,
)

data class TicketAbuse(
    val status: String?,
    val restriction: String?,
    val startedAt: Instant?,
    val expiresAt: Instant?,
)

data class TicketMessage(
    val id: Long,
    val body: String,
    val createdAt: Instant,
    val role: String,
    val reaction: String?,
    val files: List<FileAsset>,
    val author: TicketMessageAuthor,
    val type: String?,
)

data class TicketMessageAuthor(
    val name: String,
    val photo: String?,
    val isIntern: Boolean?,
    val id: Long?,
)

data class CreateTicketInput(
    val name: String,
    val body: String,
    val fileIds: List<Long> = emptyList(),
    val serviceId: Long? = null,
)

data class SendTicketMessageInput(
    val body: String,
    val fileIds: List<Long> = emptyList(),
)

data class TicketRate(
    val value: Int?,
    val comment: String?,
)

data class TicketRateInput(
    val value: Int,
    val comment: String? = null,
)
