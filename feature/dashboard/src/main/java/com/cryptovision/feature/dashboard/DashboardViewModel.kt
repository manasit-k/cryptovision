package com.cryptovision.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cryptovision.domain.model.Coin
import com.cryptovision.domain.model.Result
import com.cryptovision.domain.usecase.GetCoinsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Dashboard screen implementing MVI pattern.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getCoinsUseCase: GetCoinsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadCoins()
    }

    fun onIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadCoins -> loadCoins(intent.forceRefresh)
            is DashboardIntent.SearchCoins -> searchCoins(intent.query)
            is DashboardIntent.ClearSearch -> clearSearch()
            is DashboardIntent.Refresh -> refresh()
        }
    }

    private fun loadCoins(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            getCoinsUseCase(forceRefresh).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Result.Success -> {
                        _state.update {
                            it.copy(
                                coins = result.data,
                                filteredCoins = result.data,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    is Result.Error -> {
                        Timber.e(result.exception, "Error loading coins")
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.exception.message ?: "Unknown error occurred"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun searchCoins(query: String) {
        _state.update { currentState ->
            val filtered = if (query.isBlank()) {
                currentState.coins
            } else {
                currentState.coins.filter { coin ->
                    coin.name.contains(query, ignoreCase = true) ||
                    coin.symbol.contains(query, ignoreCase = true)
                }
            }
            currentState.copy(
                searchQuery = query,
                filteredCoins = filtered
            )
        }
    }

    private fun clearSearch() {
        _state.update {
            it.copy(
                searchQuery = "",
                filteredCoins = it.coins
            )
        }
    }

    private fun refresh() {
        loadCoins(forceRefresh = true)
    }
}

/**
 * UI State for Dashboard screen.
 */
data class DashboardState(
    val coins: List<Coin> = emptyList(),
    val filteredCoins: List<Coin> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

/**
 * User intents for Dashboard screen.
 */
sealed class DashboardIntent {
    data class LoadCoins(val forceRefresh: Boolean = false) : DashboardIntent()
    data class SearchCoins(val query: String) : DashboardIntent()
    object ClearSearch : DashboardIntent()
    object Refresh : DashboardIntent()
}
