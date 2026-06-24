package com.shefivan.aezaapp.domain.usecase.support

import com.shefivan.aezaapp.domain.repository.SupportRepository
import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.error.safeApiCall
import javax.inject.Inject

class SetTicketMessageReactionUseCase @Inject constructor(
    private val repository: SupportRepository,
    private val errors: AppErrorEmitter,
) {
    suspend operator fun invoke(ticketId: Long, messageId: Long, reaction: String) = errors.safeApiCall { repository.setMessageReaction(ticketId, messageId, reaction) }
}
