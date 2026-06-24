package com.shefivan.aezaapp.domain.usecase.support

import com.shefivan.aezaapp.domain.repository.SupportRepository
import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.error.safeApiCall
import javax.inject.Inject

class GetTicketMessagesUseCase @Inject constructor(
    private val repository: SupportRepository,
    private val errors: AppErrorEmitter,
) {
    suspend operator fun invoke(ticketId: Long) = errors.safeApiCall { repository.getMessages(ticketId) }
}
