package com.shefivan.aezaapp.data.repository

import com.shefivan.aezaapp.data.mapper.toDomain
import com.shefivan.aezaapp.data.mapper.toDto
import com.shefivan.aezaapp.data.remote.api.AezaApiService
import com.shefivan.aezaapp.data.remote.dto.SetTicketReactionRequestDto
import com.shefivan.aezaapp.domain.model.CreateTicketInput
import com.shefivan.aezaapp.domain.model.Page
import com.shefivan.aezaapp.domain.model.SendTicketMessageInput
import com.shefivan.aezaapp.domain.model.SupportTickets
import com.shefivan.aezaapp.domain.model.Ticket
import com.shefivan.aezaapp.domain.model.TicketMessage
import com.shefivan.aezaapp.domain.model.TicketRate
import com.shefivan.aezaapp.domain.model.TicketRateInput
import com.shefivan.aezaapp.domain.repository.SupportRepository
import javax.inject.Inject

class SupportRepositoryImpl @Inject constructor(
    private val api: AezaApiService,
) : SupportRepository {
    override suspend fun getTickets(): SupportTickets = api.getSupportTickets().toDomain()

    override suspend fun createTicket(input: CreateTicketInput): Ticket =
        api.createSupportTicket(input.toDto()).toDomain()

    override suspend fun getTicket(id: Long): Ticket = api.getSupportTicket(id).toDomain()

    override suspend fun archiveTicket(id: Long) {
        api.archiveSupportTicket(id)
    }

    override suspend fun getMessages(ticketId: Long): Page<TicketMessage> =
        api.getSupportTicketMessages(ticketId = ticketId, offset = null, limit = null).toDomain()

    override suspend fun sendMessage(ticketId: Long, input: SendTicketMessageInput): TicketMessage =
        api.sendSupportTicketMessage(ticketId, input.toDto()).toDomain()

    override suspend fun markTicketAsRead(ticketId: Long) {
        api.markSupportTicketAsRead(ticketId)
    }

    override suspend fun setMessageReaction(ticketId: Long, messageId: Long, reaction: String): TicketMessage =
        api.setTicketMessageReaction(ticketId, messageId, SetTicketReactionRequestDto(reaction)).toDomain()

    override suspend fun getRate(ticketId: Long): TicketRate =
        api.getSupportTicketRate(ticketId).toDomain()

    override suspend fun rateTicket(ticketId: Long, input: TicketRateInput): TicketRate =
        api.rateSupportTicket(ticketId, input.toDto()).toDomain()
}
