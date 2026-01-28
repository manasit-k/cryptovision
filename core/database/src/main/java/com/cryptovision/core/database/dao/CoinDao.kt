package com.cryptovision.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cryptovision.core.database.entity.CoinEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for coin operations.
 */
@Dao
interface CoinDao {
    
    @Query("SELECT * FROM coins ORDER BY marketCapRank ASC")
    fun getAllCoins(): Flow<List<CoinEntity>>
    
    @Query("SELECT * FROM coins ORDER BY marketCapRank ASC")
    suspend fun getAllCoinsSync(): List<CoinEntity>
    
    @Query("SELECT * FROM coins WHERE id = :coinId")
    fun getCoinByIdFlow(coinId: String): Flow<CoinEntity?>
    
    @Query("SELECT * FROM coins WHERE id = :coinId")
    suspend fun getCoinById(coinId: String): CoinEntity?
    
    @Query("SELECT * FROM coins WHERE id IN (:coinIds)")
    suspend fun getCoinsByIds(coinIds: List<String>): List<CoinEntity>
    
    @Query("SELECT * FROM coins WHERE name LIKE :query OR symbol LIKE :query ORDER BY marketCapRank ASC")
    suspend fun searchCoins(query: String): List<CoinEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoins(coins: List<CoinEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoin(coin: CoinEntity)
    
    @Query("DELETE FROM coins")
    suspend fun deleteAllCoins()
}
