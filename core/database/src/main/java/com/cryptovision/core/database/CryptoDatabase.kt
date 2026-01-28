package com.cryptovision.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cryptovision.core.database.dao.CoinDao
import com.cryptovision.core.database.dao.FavoriteDao
import com.cryptovision.core.database.entity.CoinEntity
import com.cryptovision.core.database.entity.FavoriteEntity

/**
 * Room database for CryptoVision app.
 * Implements Single Source of Truth pattern.
 */
@Database(
    entities = [
        CoinEntity::class,
        FavoriteEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class CryptoDatabase : RoomDatabase() {
    abstract fun coinDao(): CoinDao
    abstract fun favoriteDao(): FavoriteDao
}
