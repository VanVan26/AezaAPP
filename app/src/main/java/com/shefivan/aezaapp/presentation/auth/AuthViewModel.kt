package com.shefivan.aezaapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shefivan.aezaapp.domain.usecase.account.GetAccountUseCase
import com.shefivan.aezaapp.domain.usecase.auth.ClearApiKeyUseCase
import com.shefivan.aezaapp.domain.usecase.auth.SaveApiKeyUseCase
import com.shefivan.aezaapp.notification.BackgroundSyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val saveApiKey: SaveApiKeyUseCase,
    private val clearApiKey: ClearApiKeyUseCase,
    private val getAccount: GetAccountUseCase,
    private val backgroundSync: BackgroundSyncManager,
) : ViewModel() {

    data class UiState(
        val apiKey: String = "",
        val isLoading: Boolean = false,
        val showApiKey: Boolean = false,
    )

    sealed interface UiEvent {
        data object NavigateToHome : UiEvent
    }

    sealed interface Command {
        data class ApiKeyChanged(val value: String) : Command
        data object ToggleShowApiKey : Command
        data object Submit : Command
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun processCommand(command: Command) {
        when (command) {
            is Command.ApiKeyChanged -> _uiState.update { it.copy(apiKey = command.value) }
            is Command.ToggleShowApiKey -> _uiState.update { it.copy(showApiKey = !it.showApiKey) }
            is Command.Submit -> submit()
        }
    }

    private fun submit() {
        val key = _uiState.value.apiKey.trim()
        if (key.isBlank() || _uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                saveApiKey(key)
                val account = getAccount()
                if (account != null) {
                    backgroundSync.start()
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(UiEvent.NavigateToHome)
                } else {
                    clearApiKey()
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                clearApiKey()
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
