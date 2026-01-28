package com.cryptovision.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cryptovision.domain.model.Coin
import com.cryptovision.domain.model.Result
import com.cryptovision.domain.usecase.GetFavoriteCoinsUseCase
import com.cryptovision.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Favorites screen implementing MVI pattern.
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoriteCoinsUseCase: GetFavoriteCoinsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesState())
    val state: StateFlow<FavoritesState> = _state.asStateFlow()

    init {
        loadFavorites()
    }

    fun onIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.LoadFavorites -> loadFavorites()
            is FavoritesIntent.RemoveFavorite -> removeFavorite(intent.coinId)
            is FavoritesIntent.Refresh -> refresh()
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            getFavoriteCoinsUseCase().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Result.Success -> {
                        _state.update {
                            it.copy(
                                favoriteCoins = result.data,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    is Result.Error -> {
                        Timber.e(result.exception, "Error loading favorites")
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

    private fun removeFavorite(coinId: String) {
        viewModelScope.launch {
            when (val result = toggleFavoriteUseCase(coinId)) {
                is Result.Success -> {
                    Timber.d("Removed favorite: $coinId")
                    // Favorites will auto-update via Flow
                }
                is Result.Error -> {
                    Timber.e(result.exception, "Error removing favorite")
                    _state.update {
                        it.copy(error = result.exception.message ?: "Unknown error occurred")
                    }
                }
                is Result.Loading -> {
                    // No-op
                }
            }
        }
    }

    private fun refresh() {
        loadFavorites()
    }
}

/**
 * UI State for Favorites screen.
 */
data class FavoritesState(
    val favoriteCoins: List<Coin> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * User intents for Favorites screen.
 */
sealed class FavoritesIntent {
    object LoadFavorites : FavoritesIntent()
    data class RemoveFavorite(val coinId: String) : FavoritesIntent()
    object Refresh : FavoritesIntent()
}
