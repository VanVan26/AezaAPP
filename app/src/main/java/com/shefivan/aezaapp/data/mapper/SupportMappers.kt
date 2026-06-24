package com.shefivan.aezaapp.data.mapper

import com.shefivan.aezaapp.data.remote.dto.CreateTicketRequestDto
import com.shefivan.aezaapp.data.remote.dto.SendTicketMessageRequestDto
import com.shefivan.aezaapp.data.remote.dto.SupportTicketsResponseDto
import com.shefivan.aezaapp.data.remote.dto.TicketAbuseDto
import com.shefivan.aezaapp.data.remote.dto.TicketMessageAuthorDto
import com.shefivan.aezaapp.data.remote.dto.TicketMessageListResponseDto
import com.shefivan.aezaapp.data.remote.dto.TicketMessageResponseDto
import com.shefivan.aezaapp.data.remote.dto.TicketRateRequestDto
import com.shefivan.aezaapp.data.remote.dto.TicketRateResponseDto
import com.shefivan.aezaapp.data.remote.dto.TicketResponseDto
import com.shefivan.aezaapp.data.remote.dto.TicketServiceDto
import com.shefivan.aezaapp.data.remote.dto.TicketSummaryResponseDto
import com.shefivan.aezaapp.domain.model.CreateTicketInput
import com.shefivan.aezaapp.domain.model.Page
import com.shefivan.aezaapp.domain.model.SendTicketMessageInput
import com.shefivan.aezaapp.domain.model.Service
import com.shefivan.aezaapp.domain.model.ServiceProduct
import com.shefivan.aezaapp.domain.model.ServiceStatus
import com.shefivan.aezaapp.domain.model.ServiceTerm
import com.shefivan.aezaapp.domain.model.SupportTickets
import com.shefivan.aezaapp.domain.model.Ticket
import com.shefivan.aezaapp.domain.model.TicketAbuse
import com.shefivan.aezaapp.domain.model.TicketMessage
import com.shefivan.aezaapp.domain.model.TicketMessageAuthor
import com.shefivan.aezaapp.domain.model.TicketRate
import com.shefivan.aezaapp.domain.model.TicketRateInput
import com.shefivan.aezaapp.domain.model.TicketSummary
import java.math.BigDecimal
import java.time.Instant

internal fun SupportTicketsResponseDto.toDomain(): SupportTickets = SupportTickets(
    open = open.map { it.toDomain() },
    closed = closed.map { it.toDomain() },
    solved = solved.map { it.toDomain() },
    extra = extra?.toDomain(),
    totalUnread = totalUnread,
)

private fun TicketSummaryResponseDto.toDomain(): TicketSummary = TicketSummary(
    id = id,
    name = name,
    unreadCount = unreadCount,
)

internal fun TicketResponseDto.toDomain(): Ticket = Ticket(
    id = id,
    name = name,
    status = status,
    service = service?.toDomain(),
    abuse = abuse?.toDomain(),
    rate = rate,
    rateComment = rateComment,
    totalTips = totalTips?.toBigDecimalValue(),
)

private fun TicketServiceDto.toDomain(): Service = Service(
    id = id,
    name = name,
    ip = "",
    payload = emptyMap(),
    price = BigDecimal.ZERO,
    paymentTerm = ServiceTerm.UNKNOWN,
    autoProlong = false,
    createdAt = Instant.EPOCH,
    expiresAt = Instant.EPOCH,
    status = ServiceStatus.UNKNOWN,
    typeSlug = "",
    productName = "",
    product = ServiceProduct(
        id = 0L,
        name = "",
        typeSlug = "",
        typeName = "",
        groupId = 0L,
        payload = emptyMap(),
        localizedPayload = emptyMap(),
    ),
    locationCode = null,
    currentTask = null,
    capabilities = emptySet(),
    ownerId = null,
    productId = null,
    backupsEnabled = null,
    schedule = emptyMap(),
    parameters = emptyMap(),
    secureParameters = emptyMap(),
)

private fun TicketAbuseDto.toDomain(): TicketAbuse = TicketAbuse(
    status = status,
    restriction = restriction,
    startedAt = startedAt?.toInstantOrEpoch(),
    expiresAt = expiresAt?.toInstantOrEpoch(),
)

internal fun TicketMessageListResponseDto.toDomain(): Page<TicketMessage> = Page(
    items = items.map { it.toDomain() },
    total = total,
)

internal fun TicketMessageResponseDto.toDomain(): TicketMessage = TicketMessage(
    id = id,
    body = body,
    createdAt = createdAt.toInstantOrEpoch(),
    role = role,
    reaction = reaction,
    files = files.map { it.toDomain() },
    author = author.toDomain(),
    type = type,
)

private fun TicketMessageAuthorDto.toDomain(): TicketMessageAuthor = TicketMessageAuthor(
    name = name,
    photo = photo,
    isIntern = isIntern,
    id = id,
)

internal fun TicketRateResponseDto.toDomain(): TicketRate = TicketRate(
    value = value,
    comment = comment,
)

internal fun CreateTicketInput.toDto(): CreateTicketRequestDto = CreateTicketRequestDto(
    name = name,
    body = body,
    fileIds = fileIds,
    serviceId = serviceId,
)

internal fun SendTicketMessageInput.toDto(): SendTicketMessageRequestDto = SendTicketMessageRequestDto(
    body = body,
    fileIds = fileIds,
)

internal fun TicketRateInput.toDto(): TicketRateRequestDto = TicketRateRequestDto(
    value = value,
    comment = comment,
)
