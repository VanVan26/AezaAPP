package com.shefivan.aezaapp.presentation.support

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shefivan.aezaapp.domain.model.CreateTicketInput
import com.shefivan.aezaapp.domain.model.SendTicketMessageInput
import com.shefivan.aezaapp.domain.model.TicketRate
import com.shefivan.aezaapp.domain.model.TicketRateInput
import com.shefivan.aezaapp.domain.usecase.file.UploadFileUseCase
import com.shefivan.aezaapp.domain.usecase.service.GetServicesUseCase
import com.shefivan.aezaapp.domain.usecase.support.ArchiveTicketUseCase
import com.shefivan.aezaapp.domain.usecase.support.CreateTicketUseCase
import com.shefivan.aezaapp.domain.usecase.support.GetSupportTicketsUseCase
import com.shefivan.aezaapp.domain.usecase.support.GetTicketMessagesUseCase
import com.shefivan.aezaapp.domain.usecase.support.GetTicketRateUseCase
import com.shefivan.aezaapp.domain.usecase.support.GetTicketUseCase
import com.shefivan.aezaapp.domain.usecase.support.MarkTicketAsReadUseCase
import com.shefivan.aezaapp.domain.usecase.support.RateTicketUseCase
import com.shefivan.aezaapp.domain.usecase.support.SendTicketMessageUseCase
import com.shefivan.aezaapp.domain.usecase.support.SetTicketMessageReactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SupportViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val getTickets: GetSupportTicketsUseCase,
    private val createTicket: CreateTicketUseCase,
    private val getMessages: GetTicketMessagesUseCase,
    private val sendMessage: SendTicketMessageUseCase,
    private val markAsRead: MarkTicketAsReadUseCase,
    private val archiveTicket: ArchiveTicketUseCase,
    private val getTicket: GetTicketUseCase,
    private val getTicketRate: GetTicketRateUseCase,
    private val rateTicket: RateTicketUseCase,
    private val setMessageReaction: SetTicketMessageReactionUseCase,
    private val uploadFile: UploadFileUseCase,
    private val getServices: GetServicesUseCase,
) : ViewModel() {

    data class TicketUiItem(
        val id: Long,
        val name: String,
        val unreadCount: Int,
        val status: String,
    )

    data class ServiceUiItem(
        val id: Long,
        val name: String,
    )

    data class PendingFileUiItem(
        val fileId: Long,
        val name: String,
    )

    data class MessageUiItem(
        val id: Long,
        val body: String,
        val date: String,
        val authorName: String,
        val isUser: Boolean,
        val reaction: String? = null,
    )

    data class UiState(
        val isLoading: Boolean = true,
        val isRefreshing: Boolean = false,
        val openTickets: List<TicketUiItem> = emptyList(),
        val solvedTickets: List<TicketUiItem> = emptyList(),
        val closedTickets: List<TicketUiItem> = emptyList(),
        val totalUnread: Int = 0,
        val showCreateDialog: Boolean = false,
        val isCreating: Boolean = false,
        val services: List<ServiceUiItem> = emptyList(),
        val isLoadingServices: Boolean = false,

        val selectedTicket: TicketUiItem? = null,
        val isMessagesLoading: Boolean = false,
        val messages: List<MessageUiItem> = emptyList(),
        val messageInput: String = "",
        val isSending: Boolean = false,
        val pendingFiles: List<PendingFileUiItem> = emptyList(),
        val isUploading: Boolean = false,

        val ticketRate: TicketRate? = null,
        val showRateDialog: Boolean = false,
        val isRating: Boolean = false,

        val ticketServiceName: String? = null,
    )

    sealed interface Command {
        data object Refresh : Command
        data object OpenCreateDialog : Command
        data object DismissCreateDialog : Command
        data class ConfirmCreate(val name: String, val body: String, val serviceId: Long?) : Command
        data class SelectTicket(val ticket: TicketUiItem) : Command
        data object BackToList : Command
        data class MessageInputChanged(val text: String) : Command
        data object SendMessage : Command
        data class ArchiveTicket(val id: Long) : Command
        data object ShowRateDialog : Command
        data object DismissRateDialog : Command
        data class ConfirmRate(val value: Int, val comment: String?) : Command
        data class SetReaction(val messageId: Long, val reaction: String) : Command
        data class AttachFile(val uri: Uri, val mimeType: String) : Command
        data class RemovePendingFile(val fileId: Long) : Command
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
            is Command.OpenCreateDialog -> {
                _uiState.update { it.copy(showCreateDialog = true, isLoadingServices = true, services = emptyList()) }
                viewModelScope.launch {
                    val page = getServices()
                    _uiState.update { state ->
                        state.copy(
                            isLoadingServices = false,
                            services = page?.items?.map { ServiceUiItem(it.id, it.name) } ?: emptyList(),
                        )
                    }
                }
            }
            is Command.DismissCreateDialog ->
                _uiState.update { it.copy(showCreateDialog = false) }
            is Command.ConfirmCreate -> viewModelScope.launch {
                _uiState.update { it.copy(showCreateDialog = false, isCreating = true) }
                try {
                    val ticket = createTicket(CreateTicketInput(name = command.name, body = command.body, serviceId = command.serviceId))
                    if (ticket != null) load()
                } finally {
                    _uiState.update { it.copy(isCreating = false) }
                }
            }
            is Command.SelectTicket -> {
                _uiState.update {
                    it.copy(
                        selectedTicket = command.ticket,
                        messages = emptyList(),
                        isMessagesLoading = true,
                        messageInput = "",
                        ticketRate = null,
                        ticketServiceName = null,
                        pendingFiles = emptyList(),
                    )
                }
                viewModelScope.launch {
                    markAsRead(command.ticket.id)
                    loadMessages(command.ticket.id)
                    if (command.ticket.status == "solved") loadTicketRate(command.ticket.id)
                    loadTicketDetail(command.ticket.id)
                }
            }
            is Command.BackToList ->
                _uiState.update { it.copy(selectedTicket = null, messages = emptyList(), ticketRate = null, ticketServiceName = null, pendingFiles = emptyList()) }
            is Command.MessageInputChanged ->
                _uiState.update { it.copy(messageInput = command.text) }
            is Command.SendMessage -> {
                val ticketId = _uiState.value.selectedTicket?.id ?: return
                val body = _uiState.value.messageInput.trim()
                if (body.isBlank() && _uiState.value.pendingFiles.isEmpty()) return
                viewModelScope.launch {
                    val fileIds = _uiState.value.pendingFiles.map { it.fileId }
                    _uiState.update { it.copy(isSending = true, messageInput = "", pendingFiles = emptyList()) }
                    try {
                        sendMessage(ticketId, SendTicketMessageInput(body = body.ifBlank { "📎" }, fileIds = fileIds))
                        loadMessages(ticketId)
                    } catch (_: Exception) {
                        _uiState.update { it.copy(messageInput = body) }
                    } finally {
                        _uiState.update { it.copy(isSending = false) }
                    }
                }
            }
            is Command.ArchiveTicket -> viewModelScope.launch {
                archiveTicket(command.id)
                _uiState.update { it.copy(selectedTicket = null, messages = emptyList(), ticketRate = null, ticketServiceName = null) }
                load()
            }
            is Command.ShowRateDialog ->
                _uiState.update { it.copy(showRateDialog = true) }
            is Command.DismissRateDialog ->
                _uiState.update { it.copy(showRateDialog = false) }
            is Command.ConfirmRate -> {
                val ticketId = _uiState.value.selectedTicket?.id ?: return
                viewModelScope.launch {
                    _uiState.update { it.copy(showRateDialog = false, isRating = true) }
                    try {
                        rateTicket(ticketId, TicketRateInput(value = command.value, comment = command.comment))
                        loadTicketRate(ticketId)
                    } finally {
                        _uiState.update { it.copy(isRating = false) }
                    }
                }
            }
            is Command.SetReaction -> {
                val ticketId = _uiState.value.selectedTicket?.id ?: return
                viewModelScope.launch {
                    setMessageReaction(ticketId, command.messageId, command.reaction)
                    loadMessages(ticketId)
                }
            }
            is Command.AttachFile -> viewModelScope.launch {
                _uiState.update { it.copy(isUploading = true) }
                try {
                    val (fileName, bytes) = withContext(Dispatchers.IO) {
                        val name = resolveFileName(command.uri) ?: "file"
                        val data = context.contentResolver.openInputStream(command.uri)?.use { it.readBytes() }
                        name to data
                    }
                    if (bytes == null) return@launch
                    val asset = uploadFile(bytes = bytes, fileName = fileName, contentType = command.mimeType)
                    _uiState.update { state ->
                        state.copy(
                            pendingFiles = if (asset != null) state.pendingFiles + PendingFileUiItem(asset.id, asset.name) else state.pendingFiles,
                        )
                    }
                } finally {
                    _uiState.update { it.copy(isUploading = false) }
                }
            }
            is Command.RemovePendingFile ->
                _uiState.update { it.copy(pendingFiles = it.pendingFiles.filter { f -> f.fileId != command.fileId }) }
        }
    }

    private fun resolveFileName(uri: Uri): String? {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) return cursor.getString(0)
        }
        return uri.lastPathSegment
    }

    private suspend fun load() {
        try {
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
                openTickets = tickets.open.toUiItems("open"),
                solvedTickets = tickets.solved.toUiItems("solved"),
                closedTickets = tickets.closed.toUiItems("closed"),
                totalUnread = tickets.totalUnread,
            )
        }
        } catch (_: Exception) {
            _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
        }
    }

    private suspend fun loadTicketDetail(ticketId: Long) {
        val ticket = getTicket(ticketId) ?: return
        _uiState.update { it.copy(ticketServiceName = ticket.service?.name) }
    }

    private suspend fun loadTicketRate(ticketId: Long) {
        val rate = getTicketRate(ticketId)
        _uiState.update { it.copy(ticketRate = rate) }
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
                        reaction = when (m.reaction) {
                            "like" -> "👍"
                            "dislike" -> "👎"
                            else -> null
                        },
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
