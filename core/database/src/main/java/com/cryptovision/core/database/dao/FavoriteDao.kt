package com.cryptovision.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cryptovision.core.database.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for favorite operations.
 */
@Dao
interface FavoriteDao {
    
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE coinId = :coinId)")
    fun isFavorite(coinId: String): Flow<Boolean>
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE coinId = :coinId)")
    suspend fun isFavoriteSync(coinId: String): Boolean
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)
    
    @Query("DELETE FROM favorites WHERE coinId = :coinId")
    suspend fun deleteFavorite(coinId: String)
    
    @Query("DELETE FROM favorites")
    suspend fun deleteAllFavorites()
}
