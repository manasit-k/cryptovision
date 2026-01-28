package com.cryptovision.domain.usecase

import com.cryptovision.domain.model.Coin
import com.cryptovision.domain.model.Result
import com.cryptovision.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting favorite cryptocurrencies.
 */
class GetFavoriteCoinsUseCase @Inject constructor(
    private val repository: CoinRepository
) {
    /**
     * Execute the use case.
     */
    operator fun invoke(): Flow<Result<List<Coin>>> {
        return repository.getFavoriteCoins()
    }
}
