package com.shefivan.aezaapp.presentation.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shefivan.aezaapp.domain.model.CreateTicketInput
import com.shefivan.aezaapp.domain.model.SendTicketMessageInput
import com.shefivan.aezaapp.domain.usecase.support.ArchiveTicketUseCase
import com.shefivan.aezaapp.domain.usecase.support.CreateTicketUseCase
import com.shefivan.aezaapp.domain.usecase.support.GetSupportTicketsUseCase
import com.shefivan.aezaapp.domain.usecase.support.GetTicketMessagesUseCase
import com.shefivan.aezaapp.domain.usecase.support.MarkTicketAsReadUseCase
import com.shefivan.aezaapp.domain.usecase.support.SendTicketMessageUseCase
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
class SupportViewModel @Inject constructor(
    private val getTickets: GetSupportTicketsUseCase,
    private val createTicket: CreateTicketUseCase,
    private val getMessages: GetTicketMessagesUseCase,
    private val sendMessage: SendTicketMessageUseCase,
    private val markAsRead: MarkTicketAsReadUseCase,
    private val archiveTicket: ArchiveTicketUseCase,
) : ViewModel() {

    data class TicketUiItem(
        val id: Long,
        val name: String,
        val unreadCount: Int,
        val status: String,
    )

    data class MessageUiItem(
        val id: Long,
        val body: String,
        val date: String,
        val authorName: String,
        val isUser: Boolean,
    )

    data class UiState(
        val isLoading: Boolean = true,
        val isRefreshing: Boolean = false,
        val openTickets: List<TicketUiItem> = emptyList(),
        val closedTickets: List<TicketUiItem> = emptyList(),
        val totalUnread: Int = 0,
        val showCreateDialog: Boolean = false,
        val isCreating: Boolean = false,

        // Chat
        val selectedTicket: TicketUiItem? = null,
        val isMessagesLoading: Boolean = false,
        val messages: List<MessageUiItem> = emptyList(),
        val messageInput: String = "",
        val isSending: Boolean = false,
    )

    sealed interface Command {
        data object Refresh : Command
        data object OpenCreateDialog : Command
        data object DismissCreateDialog : Command
        data class ConfirmCreate(val name: String, val body: String) : Command
        data class SelectTicket(val ticket: TicketUiItem) : Command
        data object BackToList : Command
        data class MessageInputChanged(val text: String) : Command
        data object SendMessage : Command
        data class ArchiveTicket(val id: Long) : Command
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
            is Command.OpenCreateDialog ->
                _uiState.update { it.copy(showCreateDialog = true) }
            is Command.DismissCreateDialog ->
                _uiState.update { it.copy(showCreateDialog = false) }
            is Command.ConfirmCreate -> viewModelScope.launch {
                _uiState.update { it.copy(showCreateDialog = false, isCreating = true) }
                val ticket = createTicket(CreateTicketInput(name = command.name, body = command.body))
                _uiState.update { it.copy(isCreating = false) }
                if (ticket != null) {
                    load()
                }
            }
            is Command.SelectTicket -> {
                _uiState.update {
                    it.copy(
                        selectedTicket = command.ticket,
                        messages = emptyList(),
                        isMessagesLoading = true,
                        messageInput = "",
                    )
                }
                viewModelScope.launch {
                    markAsRead(command.ticket.id)
                    loadMessages(command.ticket.id)
                }
            }
            is Command.BackToList ->
                _uiState.update { it.copy(selectedTicket = null, messages = emptyList()) }
            is Command.MessageInputChanged ->
                _uiState.update { it.copy(messageInput = command.text) }
            is Command.SendMessage -> {
                val ticketId = _uiState.value.selectedTicket?.id ?: return
                val body = _uiState.value.messageInput.trim()
                if (body.isBlank()) return
                viewModelScope.launch {
                    _uiState.update { it.copy(isSending = true, messageInput = "") }
                    sendMessage(ticketId, SendTicketMessageInput(body = body))
                    _uiState.update { it.copy(isSending = false) }
                    loadMessages(ticketId)
                }
            }
            is Command.ArchiveTicket -> viewModelScope.launch {
                archiveTicket(command.id)
                _uiState.update { it.copy(selectedTicket = null, messages = emptyList()) }
                load()
            }
        }
    }

    private suspend fun load() {
        val tickets = getTickets() ?: run {
            _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
            return
        }

        fun List<com.shefivan.aezaapp.domain.model.TicketSummary>.toUiItems(status: String) =
            map { TicketUiItem(id = it.id, name = it.name, unreadCount = it.unreadCount, status = status) }

        _uiState.update {
            it.copy(
                isLoading = false,
                isRefreshing = false,
                openTickets = tickets.open.toUiItems("open") + tickets.solved.toUiItems("solved"),
                closedTickets = tickets.closed.toUiItems("closed"),
                totalUnread = tickets.totalUnread,
            )
        }
    }

    private suspend fun loadMessages(ticketId: Long) {
        val page = getMessages(ticketId)
        _uiState.update {
            it.copy(
                isMessagesLoading = false,
                messages = page?.items?.map { m ->
                    MessageUiItem(
                        id = m.id,
                        body = m.body,
                        date = dateFormatter.format(m.createdAt),
                        authorName = m.author.name,
                        isUser = m.role == "client",
                    )
                }?.reversed() ?: emptyList(),
            )
        }
    }

    companion object {
        private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            .withZone(ZoneId.systemDefault())
    }
}
