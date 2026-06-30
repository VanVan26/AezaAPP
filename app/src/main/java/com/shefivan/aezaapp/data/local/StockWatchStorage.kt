package com.shefivan.aezaapp.data.local

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockWatchStorage @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val preferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun watchedIds(): Set<Long> =
        preferences.getStringSet(KEY_WATCHED, emptySet())
            .orEmpty()
            .mapNotNull { it.toLongOrNull() }
            .toSet()

    fun isWatched(id: Long): Boolean = watchedIds().contains(id)

    fun setWatched(id: Long, watched: Boolean) {
        val current = watchedIds().toMutableSet()
        if (watched) current.add(id) else current.remove(id)
        save(current)
    }

    fun unwatch(ids: Collection<Long>) {
        if (ids.isEmpty()) return
        val current = watchedIds().toMutableSet()
        if (current.removeAll(ids.toSet())) save(current)
    }

    private fun save(ids: Set<Long>) {
        preferences.edit { putStringSet(KEY_WATCHED, ids.map { it.toString() }.toSet()) }
    }

    private companion object {
        const val PREFERENCES_NAME = "aeza_stock_watch"
        const val KEY_WATCHED = "watched_product_ids"
    }
}
