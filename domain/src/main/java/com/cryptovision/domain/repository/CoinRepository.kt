package com.cryptovision.domain.repository

import com.cryptovision.domain.model.Coin
import com.cryptovision.domain.model.PriceHistory
import com.cryptovision.domain.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for cryptocurrency data.
 * This is the contract that the data layer must implement.
 */
interface CoinRepository {
    
    /**
     * Get a flow of all coins.
     * Implements Single Source of Truth pattern - always emits from local database.
     */
    fun getCoins(forceRefresh: Boolean = false): Flow<Result<List<Coin>>>
    
    /**
     * Get a specific coin by ID.
     */
    fun getCoinById(coinId: String): Flow<Result<Coin>>
    
    /**
     * Get price history for a coin.
     */
    fun getPriceHistory(coinId: String, days: Int): Flow<Result<PriceHistory>>
    
    /**
     * Search coins by name or symbol.
     */
    fun searchCoins(query: String): Flow<Result<List<Coin>>>
    
    /**
     * Get favorite coins.
     */
    fun getFavoriteCoins(): Flow<Result<List<Coin>>>
    
    /**
     * Toggle favorite status for a coin.
     */
    suspend fun toggleFavorite(coinId: String): Result<Unit>
    
    /**
     * Check if a coin is favorited.
     */
    fun isFavorite(coinId: String): Flow<Boolean>
}
