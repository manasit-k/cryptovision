package com.cryptovision.domain.model

import java.math.BigDecimal

/**
 * Domain model representing historical price data for charting.
 */
data class PriceHistory(
    val prices: List<PricePoint>,
    val coinId: String? = null
) {
    /**
     * Single price point with timestamp.
     */
    data class PricePoint(
        val timestamp: Long,
        val price: BigDecimal
    )
}
