package com.shefivan.aezaapp.presentation.sshkeys

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shefivan.aezaapp.domain.model.CreateSshKeyInput
import com.shefivan.aezaapp.domain.model.EditSshKeyInput
import com.shefivan.aezaapp.domain.usecase.sshkey.CreateSshKeyUseCase
import com.shefivan.aezaapp.domain.usecase.sshkey.DeleteSshKeyUseCase
import com.shefivan.aezaapp.domain.usecase.sshkey.EditSshKeyUseCase
import com.shefivan.aezaapp.domain.usecase.sshkey.GetSshKeyUseCase
import com.shefivan.aezaapp.domain.usecase.sshkey.GetSshKeysUseCase
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
class SshKeysViewModel @Inject constructor(
    private val getSshKeys: GetSshKeysUseCase,
    private val getSshKey: GetSshKeyUseCase,
    private val createSshKey: CreateSshKeyUseCase,
    private val deleteSshKey: DeleteSshKeyUseCase,
    private val editSshKey: EditSshKeyUseCase,
) : ViewModel() {

    data class SshKeyUiItem(
        val id: Long,
        val name: String,
        val publicKeyPreview: String,
        val autoAssign: Boolean,
        val createdDate: String,
    )

    data class UiState(
        val isLoading: Boolean = true,
        val isRefreshing: Boolean = false,
        val keys: List<SshKeyUiItem> = emptyList(),
        val deletingIds: Set<Long> = emptySet(),
        val showAddDialog: Boolean = false,
        val isCreating: Boolean = false,
        val editingKey: SshKeyUiItem? = null,
        val isEditing: Boolean = false,
        val expandedIds: Set<Long> = emptySet(),
        val fullKeys: Map<Long, String> = emptyMap(),
        val loadingKeyIds: Set<Long> = emptySet(),
    )

    sealed interface Command {
        data object Refresh : Command
        data object OpenAddDialog : Command
        data object DismissAddDialog : Command
        data class ConfirmAdd(val name: String, val publicKey: String, val autoAssign: Boolean) : Command
        data class Delete(val id: Long) : Command
        data class OpenEditDialog(val key: SshKeyUiItem) : Command
        data object DismissEditDialog : Command
        data class ConfirmEdit(val name: String, val autoAssign: Boolean) : Command
        data class ToggleExpand(val id: Long) : Command
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
            is Command.OpenAddDialog ->
                _uiState.update { it.copy(showAddDialog = true) }
            is Command.DismissAddDialog ->
                _uiState.update { it.copy(showAddDialog = false) }
            is Command.ConfirmAdd -> viewModelScope.launch {
                _uiState.update { it.copy(showAddDialog = false, isCreating = true) }
                createSshKey(CreateSshKeyInput(
                    name = command.name,
                    publicKey = command.publicKey,
                    autoAssign = command.autoAssign,
                ))
                _uiState.update { it.copy(isCreating = false) }
                load()
            }
            is Command.Delete -> viewModelScope.launch {
                _uiState.update { it.copy(deletingIds = it.deletingIds + command.id) }
                deleteSshKey(command.id)
                _uiState.update { it.copy(deletingIds = it.deletingIds - command.id) }
                load()
            }
            is Command.OpenEditDialog ->
                _uiState.update { it.copy(editingKey = command.key) }
            is Command.DismissEditDialog ->
                _uiState.update { it.copy(editingKey = null) }
            is Command.ConfirmEdit -> {
                val id = _uiState.value.editingKey?.id ?: return
                _uiState.update { it.copy(editingKey = null, isEditing = true) }
                viewModelScope.launch {
                    editSshKey(id, EditSshKeyInput(name = command.name, autoAssign = command.autoAssign))
                    _uiState.update { it.copy(isEditing = false) }
                    load()
                }
            }
            is Command.ToggleExpand -> {
                val id = command.id
                val state = _uiState.value
                if (state.expandedIds.contains(id)) {
                    _uiState.update { it.copy(expandedIds = it.expandedIds - id) }
                } else {
                    _uiState.update { it.copy(expandedIds = it.expandedIds + id) }
                    if (!state.fullKeys.containsKey(id)) {
                        viewModelScope.launch {
                            _uiState.update { it.copy(loadingKeyIds = it.loadingKeyIds + id) }
                            val key = getSshKey(id)
                            _uiState.update { s ->
                                s.copy(
                                    loadingKeyIds = s.loadingKeyIds - id,
                                    fullKeys = if (key != null) s.fullKeys + (id to key.publicKey) else s.fullKeys,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun load() {
        val page = getSshKeys()
        _uiState.update {
            it.copy(
                isLoading = false,
                isRefreshing = false,
                keys = page?.items?.map { k ->
                    SshKeyUiItem(
                        id = k.id,
                        name = k.name,
                        publicKeyPreview = k.publicKey.take(48) + "…",
                        autoAssign = k.autoAssign,
                        createdDate = dateFormatter.format(k.createdAt),
                    )
                } ?: emptyList(),
            )
        }
    }

    companion object {
        private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            .withZone(ZoneId.systemDefault())
    }
}
