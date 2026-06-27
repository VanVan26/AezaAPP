package com.shefivan.aezaapp.presentation.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shefivan.aezaapp.domain.model.Service
import com.shefivan.aezaapp.domain.model.ServiceStatus
import com.shefivan.aezaapp.domain.model.ServiceTerm
import com.shefivan.aezaapp.domain.usecase.account.GetAccountUseCase
import com.shefivan.aezaapp.domain.usecase.service.GetServicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ServicesViewModel @Inject constructor(
    private val getAccount: GetAccountUseCase,
    private val getServices: GetServicesUseCase,
) : ViewModel() {

    data class ServiceUiItem(
        val id: Long,
        val flag: String,
        val name: String,
        val typeLabel: String,
        val planName: String,
        val ip: String,
        val price: String,
        val priceTerm: String,
        val statusLabel: String,
        val isActive: Boolean,
        val expiresDate: String,
    )

    data class UiState(
        val isLoading: Boolean = true,
        val isRefreshing: Boolean = false,
        val services: List<ServiceUiItem> = emptyList(),
    )

    sealed interface Command {
        data object Refresh : Command
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { loadData() }
    }

    fun processCommand(command: Command) {
        when (command) {
            is Command.Refresh -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isRefreshing = true) }
                    loadData()
                }
            }
        }
    }

    private suspend fun loadData() {
        val accountDeferred = viewModelScope.async { getAccount() }
        val servicesDeferred = viewModelScope.async { getServices() }

        var currencySymbol = "€"
        val account = accountDeferred.await()
        if (account != null) {
            currencySymbol = currencySymbols[account.currency.uppercase()] ?: account.currency.uppercase()
        }

        val page = servicesDeferred.await()
        _uiState.update {
            it.copy(
                isLoading = false,
                isRefreshing = false,
                services = page?.items?.map { s -> s.toUiItem(currencySymbol) } ?: emptyList(),
            )
        }
    }

    private fun Service.toUiItem(symbol: String) = ServiceUiItem(
        id = id,
        flag = locationCodeToFlag(locationCode),
        name = name,
        typeLabel = product.typeName,
        planName = productName,
        ip = ip,
        price = "$symbol ${price.divide(BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)}",
        priceTerm = paymentTerm.toTermLabel(),
        statusLabel = status.toLabel(),
        isActive = status == ServiceStatus.ACTIVE,
        expiresDate = expiresAt.toDisplayDate(),
    )

    companion object {
        private val currencySymbols = mapOf(
            "EUR" to "€", "USD" to "$", "RUB" to "₽", "GBP" to "£",
        )
        private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            .withZone(ZoneId.systemDefault())

        fun locationCodeToFlag(code: String?): String {
            if (code.isNullOrBlank() || code.length < 2) return "🌍"
            return code.take(2).uppercase().map { c ->
                String(Character.toChars(0x1F1E6 + (c - 'A')))
            }.joinToString("")
        }

        private fun Instant.toDisplayDate(): String = dateFormatter.format(this)

        private fun ServiceTerm.toTermLabel() = when (this) {
            ServiceTerm.HOUR -> "/ час"
            ServiceTerm.HALF_DAY -> "/ полдня"
            ServiceTerm.DAY -> "/ день"
            ServiceTerm.WEEK -> "/ неделю"
            ServiceTerm.MONTH -> "/ месяц"
            ServiceTerm.QUARTER_YEAR -> "/ квартал"
            ServiceTerm.HALF_YEAR -> "/ полгода"
            ServiceTerm.YEAR -> "/ год"
            ServiceTerm.ETERNAL, ServiceTerm.UNKNOWN -> ""
        }

        private fun ServiceStatus.toLabel() = when (this) {
            ServiceStatus.ACTIVE -> "работает"
            ServiceStatus.ACTIVATION_WAIT -> "активация..."
            ServiceStatus.SUSPENDED -> "приостановлен"
            ServiceStatus.PROLONG_WAIT -> "продление..."
            ServiceStatus.DELETED -> "удалён"
            ServiceStatus.BLOCKED -> "заблокирован"
            ServiceStatus.RESCUE -> "rescue"
            ServiceStatus.UNKNOWN -> "неизвестно"
        }
    }
}
