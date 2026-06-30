package com.shefivan.aezaapp.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.shefivan.aezaapp.data.local.ApiKeyProvider
import com.shefivan.aezaapp.data.local.StockWatchStorage
import com.shefivan.aezaapp.data.local.SyncStateStorage
import com.shefivan.aezaapp.domain.usecase.notification.GetNotificationsUseCase
import com.shefivan.aezaapp.domain.usecase.product.GetProductsUseCase
import com.shefivan.aezaapp.domain.usecase.support.GetSupportTicketsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NotificationPollWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val apiKeyProvider: ApiKeyProvider,
    private val getNotifications: GetNotificationsUseCase,
    private val getTickets: GetSupportTicketsUseCase,
    private val getProducts: GetProductsUseCase,
    private val notifier: AezaNotificationManager,
    private val state: SyncStateStorage,
    private val stockWatch: StockWatchStorage,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (apiKeyProvider.get() == null) return Result.success()

        notifier.ensureChannels()

        val notificationsPage = getNotifications()
        val tickets = getTickets()

        if (notificationsPage == null && tickets == null) return Result.retry()

        val unread = notificationsPage?.items?.filter { !it.isRead }.orEmpty()
        val allTickets = tickets?.let { it.open + it.solved + it.closed + listOfNotNull(it.extra) }.orEmpty()
        val currentTicketCounts = allTickets.associate { it.id to it.unreadCount }

        if (notificationsPage != null) {
            val watermark = state.lastNotifiedNotificationId
            val fresh = unread.filter { it.id > watermark }.sortedBy { it.id }
            fresh.forEach { notifier.notifyGeneral(it.id, it.text) }
            if (fresh.isNotEmpty()) {
                state.lastNotifiedNotificationId = fresh.maxOf { it.id }
            }
        }

        if (tickets != null) {
            val previous = state.ticketUnreadCounts()
            allTickets.forEach { ticket ->
                val before = previous[ticket.id] ?: 0
                if (ticket.unreadCount > before) {
                    notifier.notifySupport(ticket.id, ticket.name)
                }
            }
            state.saveTicketUnreadCounts(currentTicketCounts)
        }

        val watchedIds = stockWatch.watchedIds()
        if (watchedIds.isNotEmpty()) {
            val products = getProducts()
            if (products != null) {
                val nowAvailable = products.filter { it.id in watchedIds && it.isAvailable }
                nowAvailable.forEach { product ->
                    val typeAndName = listOf(product.typeName, product.name)
                        .filter { it.isNotBlank() }
                        .joinToString(" ")
                        .ifBlank { product.name }
                    val label = if (product.groupName.isNotBlank()) {
                        "$typeAndName — ${product.groupName}"
                    } else {
                        typeAndName
                    }
                    notifier.notifyStock(product.id, label)
                }
                stockWatch.unwatch(nowAvailable.map { it.id })
            }
        }

        return Result.success()
    }
}
