package com.shefivan.aezaapp.data.repository

import com.shefivan.aezaapp.data.mapper.toDomain
import com.shefivan.aezaapp.data.remote.api.AezaApiService
import com.shefivan.aezaapp.domain.model.Notification
import com.shefivan.aezaapp.domain.model.Page
import com.shefivan.aezaapp.domain.model.PageQuery
import com.shefivan.aezaapp.domain.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val api: AezaApiService,
) : NotificationRepository {
    override suspend fun getNotifications(query: PageQuery): Page<Notification> = api.getNotifications(
        offset = query.offset,
        limit = query.limit,
        sort = query.sort,
        filter = query.filter,
    ).toDomain()

    override suspend fun getNotification(id: Long): Notification = api.getNotification(id).toDomain()

    override suspend fun markAllAsRead() {
        api.markAllNotificationsAsRead()
    }

    override suspend fun markAsRead(id: Long) {
        api.markNotificationAsRead(id)
    }
}
