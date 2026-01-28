package com.cryptovision.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for cryptocurrency data.
 * Stores coin information in local database.
 */
@Entity(tableName = "coins")
data class CoinEntity(
    @PrimaryKey
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val currentPrice: Double,
    val marketCap: Double,
    val marketCapRank: Int,
    val priceChangePercentage24h: Double,
    val priceChangePercentage7d: Double?,
    val priceChangePercentage30d: Double?,
    val high24h: Double?,
    val low24h: Double?,
    val circulatingSupply: Double?,
    val totalSupply: Double?,
    val maxSupply: Double?,
    val ath: Double?,
    val athDate: String?,
    val atl: Double?,
    val atlDate: String?,
    val lastUpdated: String,
    val cachedAt: Long = System.currentTimeMillis()
)
