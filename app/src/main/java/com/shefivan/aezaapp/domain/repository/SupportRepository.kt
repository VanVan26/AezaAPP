package com.shefivan.aezaapp.domain.repository

import com.shefivan.aezaapp.domain.model.CreateTicketInput
import com.shefivan.aezaapp.domain.model.Page
import com.shefivan.aezaapp.domain.model.SendTicketMessageInput
import com.shefivan.aezaapp.domain.model.SupportTickets
import com.shefivan.aezaapp.domain.model.Ticket
import com.shefivan.aezaapp.domain.model.TicketMessage
import com.shefivan.aezaapp.domain.model.TicketRate
import com.shefivan.aezaapp.domain.model.TicketRateInput

interface SupportRepository {
    suspend fun getTickets(): SupportTickets

    suspend fun createTicket(input: CreateTicketInput): Ticket

    suspend fun getTicket(id: Long): Ticket

    suspend fun archiveTicket(id: Long)

    suspend fun getMessages(ticketId: Long): Page<TicketMessage>

    suspend fun sendMessage(ticketId: Long, input: SendTicketMessageInput): TicketMessage

    suspend fun markTicketAsRead(ticketId: Long)

    suspend fun setMessageReaction(ticketId: Long, messageId: Long, reaction: String): TicketMessage

    suspend fun getRate(ticketId: Long): TicketRate

    suspend fun rateTicket(ticketId: Long, input: TicketRateInput): TicketRate
}
