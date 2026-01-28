package com.cryptovision.domain.usecase

import com.cryptovision.domain.model.Result
import com.cryptovision.domain.repository.CoinRepository
import javax.inject.Inject

/**
 * Use case for toggling the favorite status of a cryptocurrency.
 */
class ToggleFavoriteUseCase @Inject constructor(
    private val repository: CoinRepository
) {
    /**
     * Execute the use case.
     * @param coinId The ID of the coin to toggle favorite status.
     */
    suspend operator fun invoke(coinId: String): Result<Unit> {
        return repository.toggleFavorite(coinId)
    }
}
