package com.cryptovision.data.repository

import com.cryptovision.core.database.dao.CoinDao
import com.cryptovision.core.database.dao.FavoriteDao
import com.cryptovision.core.database.entity.CoinEntity
import com.cryptovision.core.database.entity.FavoriteEntity
import com.cryptovision.core.network.api.CoinGeckoApiService
import com.cryptovision.core.network.mapper.toDomain
import com.cryptovision.domain.model.Coin
import com.cryptovision.domain.model.PriceHistory
import com.cryptovision.domain.model.Result
import com.cryptovision.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CoinRepository.
 * Implements Network Bound Resource pattern for offline-first architecture.
 */
@Singleton
class CoinRepositoryImpl @Inject constructor(
    private val coinDao: CoinDao,
    private val favoriteDao: FavoriteDao,
    private val apiService: CoinGeckoApiService
) : CoinRepository {

    override fun getCoins(forceRefresh: Boolean): Flow<Result<List<Coin>>> = flow {
        emit(Result.Loading)
        
        try {
            // 1. Emit cached data first (offline-first)
            if (!forceRefresh) {
                val cachedCoins = coinDao.getAllCoinsSync().map { it.toDomain() }
                
                if (cachedCoins.isNotEmpty()) {
                    emit(Result.Success(cachedCoins))
                }
            }
            
            // 2. Fetch from network
            Timber.d("Fetching coins from network, forceRefresh: $forceRefresh")
            val coinDtos = apiService.getCoins()
            val coins = coinDtos.toDomain()
            
            // 3. Update cache
            val entities = coins.map { it.toEntity() }
            coinDao.insertCoins(entities)
            
            // 4. Emit updated data
            emit(Result.Success(coins))
            
        } catch (e: Exception) {
            Timber.e(e, "Error getting coins")
            
            // Try to emit cached data on error
            try {
                val cachedCoins = coinDao.getAllCoinsSync().map { it.toDomain() }
                if (cachedCoins.isNotEmpty()) {
                    emit(Result.Success(cachedCoins))
                } else {
                    emit(Result.Error(e))
                }
            } catch (cacheError: Exception) {
                emit(Result.Error(e))
            }
        }
    }

    override fun getCoinById(coinId: String): Flow<Result<Coin>> = flow {
        emit(Result.Loading)
        
        try {
            // Try cache first
            val cachedCoin = coinDao.getCoinById(coinId)
            if (cachedCoin != null) {
                emit(Result.Success(cachedCoin.toDomain()))
            }
            
            // Fetch from network
            Timber.d("Getting coin by ID: $coinId")
            val coinDto = apiService.getCoinById(coinId)
            val coin = coinDto.toDomain()
            
            // Update cache
            coinDao.insertCoin(coin.toEntity())
            
            emit(Result.Success(coin))
        } catch (e: Exception) {
            Timber.e(e, "Error getting coin by ID: $coinId")
            
            // Try cache on error
            try {
                val cachedCoin = coinDao.getCoinById(coinId)
                if (cachedCoin != null) {
                    emit(Result.Success(cachedCoin.toDomain()))
                } else {
                    emit(Result.Error(e))
                }
            } catch (cacheError: Exception) {
                emit(Result.Error(e))
            }
        }
    }

    override fun getPriceHistory(coinId: String, days: Int): Flow<Result<PriceHistory>> = flow {
        emit(Result.Loading)
        
        try {
            Timber.d("Getting price history for $coinId, days: $days")
            val priceHistoryDto = apiService.getPriceHistory(id = coinId, days = days)
            val priceHistory = priceHistoryDto.toDomain().copy(coinId = coinId)
            
            emit(Result.Success(priceHistory))
        } catch (e: Exception) {
            Timber.e(e, "Error getting price history")
            emit(Result.Error(e))
        }
    }

    override fun searchCoins(query: String): Flow<Result<List<Coin>>> = flow {
        emit(Result.Loading)
        
        try {
            Timber.d("Searching coins: $query")
            
            // Search in local database first
            val localResults = coinDao.searchCoins("%$query%").map { it.toDomain() }
            
            if (localResults.isNotEmpty()) {
                emit(Result.Success(localResults))
            }
            
            // Search from network
            val searchResponse = apiService.searchCoins(query)
            val searchIds = searchResponse.coins.map { it.id }
            
            if (searchIds.isNotEmpty()) {
                // Fetch full coin details for the found IDs
                // Join IDs with comma
                val idsString = searchIds.joinToString(",")
                val coinDtos = apiService.getCoins(ids = idsString)
                val coins = coinDtos.toDomain()
                
                emit(Result.Success(coins))
            } else if (localResults.isEmpty()) {
                emit(Result.Success(emptyList()))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error searching coins")
            // If we already emitted local results, we might not want to emit Error?
            // But strict error handling is usually better.
            emit(Result.Error(e))
        }
    }

    override fun getFavoriteCoins(): Flow<Result<List<Coin>>> = flow {
        emit(Result.Loading)
        
        try {
            Timber.d("Getting favorite coins")
            
            favoriteDao.getAllFavorites().collect { favoriteEntities ->
                val coinIds = favoriteEntities.map { it.coinId }
                
                if (coinIds.isEmpty()) {
                    emit(Result.Success(emptyList()))
                } else {
                    val favoriteCoins = coinDao.getCoinsByIds(coinIds).map { it.toDomain() }
                    emit(Result.Success(favoriteCoins))
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting favorite coins")
            emit(Result.Error(e))
        }
    }

    override suspend fun toggleFavorite(coinId: String): Result<Unit> {
        return try {
            Timber.d("Toggling favorite for: $coinId")
            
            val isFavorite = favoriteDao.isFavoriteSync(coinId)
            
            if (isFavorite) {
                favoriteDao.deleteFavorite(coinId)
            } else {
                val favorite = FavoriteEntity(
                    coinId = coinId,
                    addedAt = System.currentTimeMillis()
                )
                favoriteDao.insertFavorite(favorite)
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error toggling favorite")
            Result.Error(e)
        }
    }

    override fun isFavorite(coinId: String): Flow<Boolean> {
        return favoriteDao.isFavorite(coinId)
    }
}

/**
 * Extension function to convert Coin domain model to CoinEntity.
 */
private fun Coin.toEntity(): CoinEntity {
    return CoinEntity(
        id = id,
        symbol = symbol,
        name = name,
        image = image,
        currentPrice = currentPrice.toDouble(),
        marketCap = marketCap.toDouble(),
        marketCapRank = marketCapRank,
        priceChangePercentage24h = priceChangePercentage24h.toDouble(),
        priceChangePercentage7d = priceChangePercentage7d?.toDouble(),
        priceChangePercentage30d = priceChangePercentage30d?.toDouble(),
        high24h = high24h?.toDouble(),
        low24h = low24h?.toDouble(),
        circulatingSupply = circulatingSupply?.toDouble(),
        totalSupply = totalSupply?.toDouble(),
        maxSupply = maxSupply?.toDouble(),
        ath = ath?.toDouble(),
        athDate = athDate,
        atl = atl?.toDouble(),
        atlDate = atlDate,
        lastUpdated = lastUpdated
    )
}

/**
 * Extension function to convert CoinEntity to Coin domain model.
 */
private fun CoinEntity.toDomain(): Coin {
    return Coin(
        id = id,
        symbol = symbol,
        name = name,
        image = image,
        currentPrice = BigDecimal.valueOf(currentPrice),
        marketCap = BigDecimal.valueOf(marketCap),
        marketCapRank = marketCapRank,
        priceChangePercentage24h = BigDecimal.valueOf(priceChangePercentage24h),
        priceChangePercentage7d = priceChangePercentage7d?.let { BigDecimal.valueOf(it) },
        priceChangePercentage30d = priceChangePercentage30d?.let { BigDecimal.valueOf(it) },
        high24h = high24h?.let { BigDecimal.valueOf(it) },
        low24h = low24h?.let { BigDecimal.valueOf(it) },
        circulatingSupply = circulatingSupply?.let { BigDecimal.valueOf(it) },
        totalSupply = totalSupply?.let { BigDecimal.valueOf(it) },
        maxSupply = maxSupply?.let { BigDecimal.valueOf(it) },
        ath = ath?.let { BigDecimal.valueOf(it) },
        athDate = athDate,
        atl = atl?.let { BigDecimal.valueOf(it) },
        atlDate = atlDate,
        lastUpdated = lastUpdated
    )
}
