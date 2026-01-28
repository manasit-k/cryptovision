package com.cryptovision.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cryptovision.domain.model.Coin
import com.cryptovision.domain.model.PriceHistory
import com.cryptovision.domain.model.Result
import com.cryptovision.domain.usecase.GetCoinDetailsUseCase
import com.cryptovision.domain.usecase.GetPriceHistoryUseCase
import com.cryptovision.domain.usecase.ToggleFavoriteUseCase
import com.cryptovision.domain.usecase.CheckCoinFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Details screen implementing MVI pattern.
 */
@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getCoinDetailsUseCase: GetCoinDetailsUseCase,
    private val getPriceHistoryUseCase: GetPriceHistoryUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val checkCoinFavoriteUseCase: CheckCoinFavoriteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val coinId: String = checkNotNull(savedStateHandle["coinId"])

    private val _state = MutableStateFlow(DetailsState())
    val state: StateFlow<DetailsState> = _state.asStateFlow()

    init {
        loadCoinDetails()
        loadPriceHistory(7) // Default to 7 days
        observeFavoriteStatus()
    }

    fun onIntent(intent: DetailsIntent) {
        when (intent) {
            is DetailsIntent.LoadDetails -> loadCoinDetails()
            is DetailsIntent.LoadPriceHistory -> loadPriceHistory(intent.days)
            is DetailsIntent.ToggleFavorite -> toggleFavorite()
            is DetailsIntent.SelectTimeRange -> selectTimeRange(intent.days)
        }
    }

    private fun loadCoinDetails() {
        viewModelScope.launch {
            getCoinDetailsUseCase(coinId).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update { it.copy(isLoadingDetails = true, error = null) }
                    }
                    is Result.Success -> {
                        _state.update {
                            it.copy(
                                coin = result.data,
                                isLoadingDetails = false,
                                error = null
                            )
                        }
                    }
                    is Result.Error -> {
                        Timber.e(result.exception, "Error loading coin details")
                        _state.update {
                            it.copy(
                                isLoadingDetails = false,
                                error = result.exception.message ?: "Unknown error occurred"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadPriceHistory(days: Int) {
        viewModelScope.launch {
            getPriceHistoryUseCase(coinId, days).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update { it.copy(isLoadingChart = true) }
                    }
                    is Result.Success -> {
                        _state.update {
                            it.copy(
                                priceHistory = result.data,
                                isLoadingChart = false,
                                selectedTimeRange = days
                            )
                        }
                    }
                    is Result.Error -> {
                        Timber.e(result.exception, "Error loading price history")
                        _state.update {
                            it.copy(
                                isLoadingChart = false,
                                error = result.exception.message ?: "Unknown error occurred"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun toggleFavorite() {
        viewModelScope.launch {
            when (val result = toggleFavoriteUseCase(coinId)) {
                is Result.Success -> {
                    Timber.d("Toggled favorite for $coinId")
                    // Favorite status will be updated via flow
                }
                is Result.Error -> {
                    Timber.e(result.exception, "Error toggling favorite")
                }
                is Result.Loading -> {
                    // No-op
                }
            }
        }
    }

    private fun selectTimeRange(days: Int) {
        loadPriceHistory(days)
    }

    private fun observeFavoriteStatus() {
        viewModelScope.launch {
            checkCoinFavoriteUseCase(coinId).collect { isFavorite ->
                _state.update { it.copy(isFavorite = isFavorite) }
            }
        }
    }
}

/**
 * UI State for Details screen.
 */
data class DetailsState(
    val coin: Coin? = null,
    val priceHistory: PriceHistory? = null,
    val isFavorite: Boolean = false,
    val isLoadingDetails: Boolean = false,
    val isLoadingChart: Boolean = false,
    val error: String? = null,
    val selectedTimeRange: Int = 7
)

/**
 * User intents for Details screen.
 */
sealed class DetailsIntent {
    object LoadDetails : DetailsIntent()
    data class LoadPriceHistory(val days: Int) : DetailsIntent()
    object ToggleFavorite : DetailsIntent()
    data class SelectTimeRange(val days: Int) : DetailsIntent()
}
