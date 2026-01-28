package com.cryptovision.domain.model

import java.math.BigDecimal

/**
 * Domain model representing a cryptocurrency.
 * Uses BigDecimal for financial precision.
 */
data class Coin(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val currentPrice: BigDecimal,
    val marketCap: BigDecimal,
    val marketCapRank: Int,
    val priceChangePercentage24h: BigDecimal,
    val priceChangePercentage7d: BigDecimal?,
    val priceChangePercentage30d: BigDecimal?,
    val high24h: BigDecimal?,
    val low24h: BigDecimal?,
    val circulatingSupply: BigDecimal?,
    val totalSupply: BigDecimal?,
    val maxSupply: BigDecimal?,
    val ath: BigDecimal?,
    val athDate: String?,
    val atl: BigDecimal?,
    val atlDate: String?,
    val lastUpdated: String
)
