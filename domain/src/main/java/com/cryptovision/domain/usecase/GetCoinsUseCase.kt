package com.cryptovision.domain.usecase

import com.cryptovision.domain.model.Coin
import com.cryptovision.domain.model.Result
import com.cryptovision.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting the list of cryptocurrencies.
 * Encapsulates business logic for fetching coins.
 */
class GetCoinsUseCase @Inject constructor(
    private val repository: CoinRepository
) {
    /**
     * Execute the use case.
     * @param forceRefresh Whether to force a refresh from the network.
     */
    operator fun invoke(forceRefresh: Boolean = false): Flow<Result<List<Coin>>> {
        return repository.getCoins(forceRefresh)
    }
}
