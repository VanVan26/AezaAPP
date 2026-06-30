package com.shefivan.aezaapp.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.shefivan.aezaapp.MainActivity
import com.shefivan.aezaapp.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AezaNotificationManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val manager = NotificationManagerCompat.from(context)

    fun ensureChannels() {
        manager.createNotificationChannelsCompat(
            listOf(
                NotificationChannelCompat.Builder(
                    CHANNEL_GENERAL,
                    NotificationManagerCompat.IMPORTANCE_DEFAULT,
                ).setName("Уведомления").build(),
                NotificationChannelCompat.Builder(
                    CHANNEL_SUPPORT,
                    NotificationManagerCompat.IMPORTANCE_HIGH,
                ).setName("Поддержка").build(),
                NotificationChannelCompat.Builder(
                    CHANNEL_STOCK,
                    NotificationManagerCompat.IMPORTANCE_HIGH,
                ).setName("Наличие услуг").build(),
            ),
        )
    }

    fun notifyGeneral(notificationId: Long, text: String) {
        post(
            channelId = CHANNEL_GENERAL,
            systemId = GENERAL_ID_OFFSET + notificationId.toInt(),
            title = "Aeza",
            text = text,
            navTarget = TARGET_NOTIFICATIONS,
        )
    }

    fun notifySupport(ticketId: Long, ticketName: String) {
        post(
            channelId = CHANNEL_SUPPORT,
            systemId = SUPPORT_ID_OFFSET + ticketId.toInt(),
            title = "Новый ответ в поддержке",
            text = ticketName,
            navTarget = TARGET_SUPPORT,
        )
    }

    fun notifyStock(productId: Long, productName: String) {
        post(
            channelId = CHANNEL_STOCK,
            systemId = STOCK_ID_OFFSET + productId.toInt(),
            title = "Услуга снова в продаже",
            text = productName,
            navTarget = TARGET_STOCK,
        )
    }

    private fun post(
        channelId: String,
        systemId: Int,
        title: String,
        text: String,
        navTarget: String,
    ) {
        if (!areNotificationsEnabled()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_NAV_TARGET, navTarget)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            systemId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_stat_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(systemId, notification)
    }

    fun areNotificationsEnabled(): Boolean = manager.areNotificationsEnabled()

    companion object {
        const val CHANNEL_GENERAL = "aeza_general"
        const val CHANNEL_SUPPORT = "aeza_support"
        const val CHANNEL_STOCK = "aeza_stock"

        const val EXTRA_NAV_TARGET = "nav_target"
        const val TARGET_NOTIFICATIONS = "notifications"
        const val TARGET_SUPPORT = "support"
        const val TARGET_STOCK = "stock"

        private const val GENERAL_ID_OFFSET = 1_000_000
        private const val SUPPORT_ID_OFFSET = 2_000_000
        private const val STOCK_ID_OFFSET = 3_000_000
    }
}
