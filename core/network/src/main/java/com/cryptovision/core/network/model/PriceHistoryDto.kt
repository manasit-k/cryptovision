package com.cryptovision.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for price history data from CoinGecko API.
 */
@Serializable
data class PriceHistoryDto(
    @SerialName("prices")
    val prices: List<List<Double>>
)

/**
 * Data class representing a single price point.
 */
data class PricePoint(
    val timestamp: Long,
    val price: Double
)
