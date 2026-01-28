package com.cryptovision.domain.usecase

import com.cryptovision.domain.model.Coin
import com.cryptovision.domain.model.Result
import com.cryptovision.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting detailed information about a specific cryptocurrency.
 */
class GetCoinDetailsUseCase @Inject constructor(
    private val repository: CoinRepository
) {
    /**
     * Execute the use case.
     * @param coinId The ID of the coin to fetch.
     */
    operator fun invoke(coinId: String): Flow<Result<Coin>> {
        return repository.getCoinById(coinId)
    }
}
