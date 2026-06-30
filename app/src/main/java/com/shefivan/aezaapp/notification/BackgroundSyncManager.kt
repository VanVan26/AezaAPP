package com.shefivan.aezaapp.notification

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.shefivan.aezaapp.data.local.SyncStateStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackgroundSyncManager @Inject constructor(
    @ApplicationContext context: Context,
    private val state: SyncStateStorage,
) {
    private val workManager = WorkManager.getInstance(context)

    fun start() {
        val request = PeriodicWorkRequestBuilder<NotificationPollWorker>(
            POLL_INTERVAL_MINUTES, TimeUnit.MINUTES,
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build(),
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }

    fun stop() {
        workManager.cancelUniqueWork(WORK_NAME)
        state.clear()
    }

    private companion object {
        const val WORK_NAME = "aeza_notification_poll"
        const val POLL_INTERVAL_MINUTES = 15L
    }
}
