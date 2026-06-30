package com.shefivan.aezaapp.data.local

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncStateStorage @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val preferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val ticketMapSerializer =
        MapSerializer(Long.serializer(), Int.serializer())

    var lastNotifiedNotificationId: Long
        get() = preferences.getLong(KEY_LAST_NOTIFICATION_ID, 0L)
        set(value) = preferences.edit { putLong(KEY_LAST_NOTIFICATION_ID, value) }

    fun ticketUnreadCounts(): Map<Long, Int> {
        val raw = preferences.getString(KEY_TICKET_COUNTS, null) ?: return emptyMap()
        return try {
            json.decodeFromString(ticketMapSerializer, raw)
        } catch (_: Exception) {
            emptyMap()
        }
    }

    fun saveTicketUnreadCounts(counts: Map<Long, Int>) {
        preferences.edit {
            putString(KEY_TICKET_COUNTS, json.encodeToString(ticketMapSerializer, counts))
        }
    }

    fun clear() {
        preferences.edit { clear() }
    }

    private companion object {
        const val PREFERENCES_NAME = "aeza_sync_state"
        const val KEY_LAST_NOTIFICATION_ID = "last_notification_id"
        const val KEY_TICKET_COUNTS = "ticket_unread_counts"

        val json = Json { ignoreUnknownKeys = true }
    }
}
