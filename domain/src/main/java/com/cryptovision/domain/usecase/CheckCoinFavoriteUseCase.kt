package com.cryptovision.domain.usecase

import com.cryptovision.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing if a coin is marked as favorite.
 */
class CheckCoinFavoriteUseCase @Inject constructor(
    private val repository: CoinRepository
) {
    /**
     * Execute the use case.
     * @param coinId The ID of the coin to check.
     */
    operator fun invoke(coinId: String): Flow<Boolean> {
        return repository.isFavorite(coinId)
    }
}
