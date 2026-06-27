package com.shefivan.aezaapp.presentation.domains

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shefivan.aezaapp.domain.model.CreateDomainRecordInput
import com.shefivan.aezaapp.domain.usecase.domain.CreateDomainRecordUseCase
import com.shefivan.aezaapp.domain.usecase.domain.CreateDomainUseCase
import com.shefivan.aezaapp.domain.usecase.domain.DeleteDomainRecordUseCase
import com.shefivan.aezaapp.domain.usecase.domain.GetDomainRecordsUseCase
import com.shefivan.aezaapp.domain.usecase.domain.GetDomainsUseCase
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
class DomainsViewModel @Inject constructor(
    private val getDomains: GetDomainsUseCase,
    private val createDomain: CreateDomainUseCase,
    private val getRecords: GetDomainRecordsUseCase,
    private val deleteRecord: DeleteDomainRecordUseCase,
    private val createRecord: CreateDomainRecordUseCase,
) : ViewModel() {

    data class DomainUiItem(
        val id: Long,
        val name: String,
        val status: String,
        val createdDate: String,
    )

    data class RecordUiItem(
        val id: Long,
        val type: String,
        val name: String,
        val content: String,
        val ttl: Int,
        val isEnabled: Boolean,
    )

    data class UiState(
        val isLoading: Boolean = true,
        val isRefreshing: Boolean = false,
        val domains: List<DomainUiItem> = emptyList(),
        val showAddDomainDialog: Boolean = false,
        val isCreatingDomain: Boolean = false,

        // Detail
        val selectedDomain: DomainUiItem? = null,
        val isRecordsLoading: Boolean = false,
        val isRecordsRefreshing: Boolean = false,
        val records: List<RecordUiItem> = emptyList(),
        val deletingRecordIds: Set<Long> = emptySet(),
        val showAddRecordDialog: Boolean = false,
        val isCreatingRecord: Boolean = false,
    )

    sealed interface Command {
        data object Refresh : Command
        data object OpenAddDomainDialog : Command
        data object DismissAddDomainDialog : Command
        data class ConfirmAddDomain(val name: String) : Command
        data class SelectDomain(val domain: DomainUiItem) : Command
        data object BackToList : Command
        data object RefreshRecords : Command
        data object OpenAddRecordDialog : Command
        data object DismissAddRecordDialog : Command
        data class ConfirmAddRecord(val type: String, val name: String, val content: String, val ttl: Int) : Command
        data class DeleteRecord(val recordId: Long) : Command
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { loadDomains() }
    }

    fun processCommand(command: Command) {
        when (command) {
            is Command.Refresh -> viewModelScope.launch {
                _uiState.update { it.copy(isRefreshing = true) }
                loadDomains()
            }
            is Command.OpenAddDomainDialog ->
                _uiState.update { it.copy(showAddDomainDialog = true) }
            is Command.DismissAddDomainDialog ->
                _uiState.update { it.copy(showAddDomainDialog = false) }
            is Command.ConfirmAddDomain -> viewModelScope.launch {
                _uiState.update { it.copy(showAddDomainDialog = false, isCreatingDomain = true) }
                createDomain(command.name)
                _uiState.update { it.copy(isCreatingDomain = false) }
                loadDomains()
            }
            is Command.SelectDomain -> {
                _uiState.update {
                    it.copy(
                        selectedDomain = command.domain,
                        records = emptyList(),
                        isRecordsLoading = true,
                    )
                }
                viewModelScope.launch { loadRecords(command.domain.id) }
            }
            is Command.BackToList ->
                _uiState.update { it.copy(selectedDomain = null, records = emptyList()) }
            is Command.RefreshRecords -> {
                val id = _uiState.value.selectedDomain?.id ?: return
                viewModelScope.launch {
                    _uiState.update { it.copy(isRecordsRefreshing = true) }
                    loadRecords(id)
                }
            }
            is Command.OpenAddRecordDialog ->
                _uiState.update { it.copy(showAddRecordDialog = true) }
            is Command.DismissAddRecordDialog ->
                _uiState.update { it.copy(showAddRecordDialog = false) }
            is Command.ConfirmAddRecord -> {
                val domainId = _uiState.value.selectedDomain?.id ?: return
                viewModelScope.launch {
                    _uiState.update { it.copy(showAddRecordDialog = false, isCreatingRecord = true) }
                    createRecord(domainId, CreateDomainRecordInput(
                        type = command.type,
                        name = command.name,
                        content = command.content,
                        ttl = command.ttl,
                    ))
                    _uiState.update { it.copy(isCreatingRecord = false) }
                    loadRecords(domainId)
                }
            }
            is Command.DeleteRecord -> {
                val domainId = _uiState.value.selectedDomain?.id ?: return
                viewModelScope.launch {
                    _uiState.update { it.copy(deletingRecordIds = it.deletingRecordIds + command.recordId) }
                    deleteRecord(domainId, command.recordId)
                    _uiState.update { it.copy(deletingRecordIds = it.deletingRecordIds - command.recordId) }
                    loadRecords(domainId)
                }
            }
        }
    }

    private suspend fun loadDomains() {
        val page = getDomains()
        _uiState.update {
            it.copy(
                isLoading = false,
                isRefreshing = false,
                domains = page?.items?.map { d ->
                    DomainUiItem(
                        id = d.id,
                        name = d.name,
                        status = d.status,
                        createdDate = dateFormatter.format(d.createdAt),
                    )
                } ?: emptyList(),
            )
        }
    }

    private suspend fun loadRecords(domainId: Long) {
        val page = getRecords(domainId)
        _uiState.update {
            it.copy(
                isRecordsLoading = false,
                isRecordsRefreshing = false,
                records = page?.items?.map { r ->
                    RecordUiItem(
                        id = r.id,
                        type = r.type,
                        name = r.name,
                        content = r.content,
                        ttl = r.ttl,
                        isEnabled = r.isEnabled,
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
