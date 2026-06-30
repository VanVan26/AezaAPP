package com.shefivan.aezaapp.presentation.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shefivan.aezaapp.data.local.StockWatchStorage
import com.shefivan.aezaapp.domain.usecase.product.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockWatchViewModel @Inject constructor(
    private val getProducts: GetProductsUseCase,
    private val stockWatch: StockWatchStorage,
) : ViewModel() {

    data class ProductUiItem(
        val id: Long,
        val title: String,
        val subtitle: String,
    )

    data class UiState(
        val isLoading: Boolean = true,
        val isRefreshing: Boolean = false,
        val isError: Boolean = false,
        val products: List<ProductUiItem> = emptyList(),
        val watched: Set<Long> = emptySet(),
    )

    sealed interface Command {
        data object Refresh : Command
        data class ToggleWatch(val id: Long, val watched: Boolean) : Command
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
            is Command.ToggleWatch -> {
                stockWatch.setWatched(command.id, command.watched)
                _uiState.update { state ->
                    val updated = if (command.watched) state.watched + command.id else state.watched - command.id
                    state.copy(watched = updated)
                }
            }
        }
    }

    private fun buildProductTitle(typeName: String, name: String): String {
        val type = typeName.trim()
        val productName = name.trim()
        return when {
            type.isEmpty() -> productName
            productName.isEmpty() -> type
            productName.contains(type, ignoreCase = true) -> productName
            else -> "$type $productName"
        }
    }

    private suspend fun load() {
        val products = getProducts()
        if (products == null) {
            _uiState.update { it.copy(isLoading = false, isRefreshing = false, isError = true) }
            return
        }
        val unavailable = products
            .filter { !it.isAvailable }
            .sortedWith(compareBy({ it.typeName }, { it.groupName }, { it.name }))
            .map { product ->
                ProductUiItem(
                    id = product.id,
                    title = buildProductTitle(product.typeName, product.name),
                    subtitle = product.groupName,
                )
            }
        _uiState.update {
            it.copy(
                isLoading = false,
                isRefreshing = false,
                isError = false,
                products = unavailable,
                watched = stockWatch.watchedIds(),
            )
        }
    }
}
