package com.shefivan.aezaapp.domain.usecase.notification

import com.shefivan.aezaapp.domain.repository.NotificationRepository
import com.shefivan.aezaapp.domain.error.AppErrorEmitter
import com.shefivan.aezaapp.domain.error.safeApiCall
import javax.inject.Inject

class GetNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository,
    private val errors: AppErrorEmitter,
) {
    suspend operator fun invoke(id: Long) = errors.safeApiCall { repository.getNotification(id) }
}
