package com.shefivan.aezaapp.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shefivan.aezaapp.domain.model.AccountBonusState
import com.shefivan.aezaapp.domain.model.AccountProfileType
import com.shefivan.aezaapp.domain.model.AccountRegion
import com.shefivan.aezaapp.domain.usecase.account.GetAccountUseCase
import com.shefivan.aezaapp.domain.usecase.auth.ClearApiKeyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val getAccount: GetAccountUseCase,
    private val clearApiKey: ClearApiKeyUseCase,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val isRefreshing: Boolean = false,
        val initials: String = "",
        val email: String = "",
        val balance: String = "",
        val bonusBalance: String = "",
        val profileName: String = "",
        val phone: String = "",
        val tfaEnabled: Boolean = false,
        val region: String = "",
        val lang: String = "",
        val currency: String = "",
        val theme: String = "",
        val roles: List<String> = emptyList(),
        val bonusState: String = "",
        val profileType: String = "",
    )

    sealed interface Command {
        data object Refresh : Command
        data object Logout : Command
    }

    sealed interface UiEvent {
        data object NavigateToAuth : UiEvent
    }

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

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
            is Command.Logout -> viewModelScope.launch {
                clearApiKey()
                _events.send(UiEvent.NavigateToAuth)
            }
        }
    }

    private suspend fun load() {
        val account = getAccount() ?: run {
            _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
            return
        }

        val symbol = currencySymbols[account.currency.uppercase()] ?: account.currency.uppercase()
        val totalBalance = account.balance.divide(BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)
        val bonus = account.bonusBalance.divide(BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)

        _uiState.update {
            it.copy(
                isLoading = false,
                isRefreshing = false,
                initials = account.email.substringBefore("@").take(2).uppercase(),
                email = account.email,
                balance = "$symbol $totalBalance",
                bonusBalance = "$symbol $bonus",
                profileName = account.profile.name
                    ?: account.profile.names.filter { n -> n.isNotBlank() }.joinToString(" "),
                phone = account.profile.phone ?: "",
                tfaEnabled = account.tfaEnabled,
                region = account.region.toLabel(),
                lang = account.interfaceSettings.lang.uppercase(),
                currency = account.currency.uppercase(),
                theme = account.interfaceSettings.theme,
                roles = account.roles,
                bonusState = account.bonusState.toLabel(),
                profileType = account.profile.type.toLabel(),
            )
        }
    }

    companion object {
        private val currencySymbols = mapOf("EUR" to "€", "USD" to "$", "RUB" to "₽", "GBP" to "£")

        private fun AccountRegion?.toLabel() = when (this) {
            AccountRegion.GLOBAL -> "Global"
            AccountRegion.RU -> "Россия"
            else -> "—"
        }

        private fun AccountBonusState?.toLabel() = when (this) {
            AccountBonusState.NOT_USED -> "не активированы"
            AccountBonusState.LOCKED -> "заблокированы"
            AccountBonusState.UNLOCKED -> "активны"
            else -> "—"
        }

        private fun AccountProfileType?.toLabel() = when (this) {
            AccountProfileType.LEGAL -> "Юридическое лицо"
            AccountProfileType.PERSON -> "Физическое лицо"
            else -> "—"
        }
    }
}
