package com.cryptovision.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for favorite cryptocurrencies.
 */
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val coinId: String,
    val addedAt: Long = System.currentTimeMillis()
)
