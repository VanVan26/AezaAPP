package com.shefivan.aezaapp.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shefivan.aezaapp.domain.usecase.notification.GetNotificationsUseCase
import com.shefivan.aezaapp.domain.usecase.notification.MarkAllNotificationsAsReadUseCase
import com.shefivan.aezaapp.domain.usecase.notification.MarkNotificationAsReadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getNotifications: GetNotificationsUseCase,
    private val markAsRead: MarkNotificationAsReadUseCase,
    private val markAllAsRead: MarkAllNotificationsAsReadUseCase,
) : ViewModel() {

    data class NotificationUiItem(
        val id: Long,
        val text: String,
        val date: String,
        val isRead: Boolean,
    )

    data class UiState(
        val isLoading: Boolean = true,
        val isRefreshing: Boolean = false,
        val notifications: List<NotificationUiItem> = emptyList(),
        val markingAllRead: Boolean = false,
    )

    sealed interface Command {
        data object Refresh : Command
        data class MarkRead(val id: Long) : Command
        data object MarkAllRead : Command
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { load() }
    }

    fun processCommand(command: Command) {
        when (command) {
            is Command.Refresh -> viewModelScope.launch {
                _uiState.update { it.copy(isRefreshing = true) }
                load()
            }
            is Command.MarkRead -> viewModelScope.launch {
                markAsRead(command.id)
                _uiState.update { state ->
                    state.copy(
                        notifications = state.notifications.map { n ->
                            if (n.id == command.id) n.copy(isRead = true) else n
                        },
                    )
                }
            }
            is Command.MarkAllRead -> viewModelScope.launch {
                _uiState.update { it.copy(markingAllRead = true) }
                markAllAsRead()
                _uiState.update { state ->
                    state.copy(
                        markingAllRead = false,
                        notifications = state.notifications.map { it.copy(isRead = true) },
                    )
                }
            }
        }
    }

    private suspend fun load() {
        val page = getNotifications()
        _uiState.update {
            it.copy(
                isLoading = false,
                isRefreshing = false,
                notifications = page?.items?.map { n ->
                    NotificationUiItem(
                        id = n.id,
                        text = n.text,
                        date = dateFormatter.format(n.createdAt),
                        isRead = n.isRead,
                    )
                } ?: emptyList(),
            )
        }
    }

    companion object {
        private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            .withZone(ZoneId.systemDefault())
    }
}
