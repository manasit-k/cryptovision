package com.cryptovision.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

/**
 * DTO for cryptocurrency data from CoinGecko API.
 * Uses @SerialName to map API field names to Kotlin properties.
 */
@Serializable
data class CoinDto(
    @SerialName("id")
    val id: String,
    
    @SerialName("symbol")
    val symbol: String,
    
    @SerialName("name")
    val name: String,
    
    @SerialName("image")
    val image: String,
    
    @SerialName("current_price")
    val currentPrice: Double,
    
    @SerialName("market_cap")
    val marketCap: Double,
    
    @SerialName("market_cap_rank")
    val marketCapRank: Int? = 0,
    
    @SerialName("price_change_percentage_24h")
    val priceChangePercentage24h: Double? = 0.0,
    
    @SerialName("price_change_percentage_7d_in_currency")
    val priceChangePercentage7d: Double? = null,
    
    @SerialName("price_change_percentage_30d_in_currency")
    val priceChangePercentage30d: Double? = null,
    
    @SerialName("high_24h")
    val high24h: Double? = null,
    
    @SerialName("low_24h")
    val low24h: Double? = null,
    
    @SerialName("circulating_supply")
    val circulatingSupply: Double? = null,
    
    @SerialName("total_supply")
    val totalSupply: Double? = null,
    
    @SerialName("max_supply")
    val maxSupply: Double? = null,
    
    @SerialName("ath")
    val ath: Double? = null,
    
    @SerialName("ath_date")
    val athDate: String? = null,
    
    @SerialName("atl")
    val atl: Double? = null,
    
    @SerialName("atl_date")
    val atlDate: String? = null,
    
    @SerialName("last_updated")
    val lastUpdated: String
)

/**
 * Extension function to convert Double to BigDecimal safely.
 */
fun Double?.toBigDecimalOrZero(): BigDecimal {
    return this?.let { BigDecimal.valueOf(it) } ?: BigDecimal.ZERO
}
