package com.cryptovision.core.network.api

import com.cryptovision.core.network.model.CoinDto
import com.cryptovision.core.network.model.PriceHistoryDto
import com.cryptovision.core.network.model.SearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * CoinGecko API service interface.
 * Defines endpoints for cryptocurrency data.
 */
interface CoinGeckoApiService {
    
    /**
     * Get list of coins with market data.
     * 
     * @param vsCurrency The target currency (default: usd)
     * @param order Sort order (market_cap_desc, market_cap_asc, etc.)
     * @param perPage Number of results per page (max 250)
     * @param page Page number
     * @param sparkline Include sparkline 7d data
     * @param priceChangePercentage Price change percentage intervals
     */
    @GET("coins/markets")
    suspend fun getCoins(
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 100,
        @Query("page") page: Int = 1,
        @Query("ids") ids: String? = null,
        @Query("sparkline") sparkline: Boolean = false,
        @Query("price_change_percentage") priceChangePercentage: String = "24h,7d,30d"
    ): List<CoinDto>
    
    /**
     * Get coin details by ID.
     * 
     * @param id Coin ID (e.g., "bitcoin")
     * @param localization Include localized languages
     * @param tickers Include tickers data
     * @param marketData Include market data
     * @param communityData Include community data
     * @param developerData Include developer data
     * @param sparkline Include sparkline data
     */
    @GET("coins/{id}")
    suspend fun getCoinById(
        @Path("id") id: String,
        @Query("localization") localization: Boolean = false,
        @Query("tickers") tickers: Boolean = false,
        @Query("market_data") marketData: Boolean = true,
        @Query("community_data") communityData: Boolean = false,
        @Query("developer_data") developerData: Boolean = false,
        @Query("sparkline") sparkline: Boolean = false
    ): CoinDto
    
    /**
     * Get historical market data for a coin.
     * 
     * @param id Coin ID
     * @param vsCurrency Target currency
     * @param days Number of days (1, 7, 14, 30, 90, 180, 365, max)
     * @param interval Data interval (empty for auto)
     */
    @GET("coins/{id}/market_chart")
    suspend fun getPriceHistory(
        @Path("id") id: String,
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("days") days: Int = 7,
        @Query("interval") interval: String? = null
    ): PriceHistoryDto
    
    /**
     * Search for coins.
     * 
     * @param query Search query
     */
    @GET("search")
    suspend fun searchCoins(
        @Query("query") query: String
    ): SearchResponseDto
}
