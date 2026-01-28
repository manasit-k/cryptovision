package com.cryptovision.domain.usecase

import com.cryptovision.domain.model.PriceHistory
import com.cryptovision.domain.model.Result
import com.cryptovision.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting price history for a specific coin.
 */
class GetPriceHistoryUseCase @Inject constructor(
    private val repository: CoinRepository
) {
    operator fun invoke(coinId: String, days: Int = 7): Flow<Result<PriceHistory>> {
        return repository.getPriceHistory(coinId, days)
    }
}
