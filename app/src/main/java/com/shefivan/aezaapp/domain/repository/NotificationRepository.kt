package com.shefivan.aezaapp.domain.repository

import com.shefivan.aezaapp.domain.model.Notification
import com.shefivan.aezaapp.domain.model.Page
import com.shefivan.aezaapp.domain.model.PageQuery

interface NotificationRepository {
    suspend fun getNotifications(query: PageQuery = PageQuery()): Page<Notification>

    suspend fun getNotification(id: Long): Notification

    suspend fun markAllAsRead()

    suspend fun markAsRead(id: Long)
}

