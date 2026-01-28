package com.cryptovision.core.network.mapper

import com.cryptovision.core.network.model.CoinDto
import com.cryptovision.core.network.model.PriceHistoryDto
import com.cryptovision.core.network.model.toBigDecimalOrZero
import com.cryptovision.domain.model.Coin
import com.cryptovision.domain.model.PriceHistory
import java.math.BigDecimal

/**
 * Mapper functions to convert network DTOs to domain models.
 */

/**
 * Convert CoinDto to Coin domain model.
 */
fun CoinDto.toDomain(): Coin {
    return Coin(
        id = id,
        symbol = symbol.uppercase(),
        name = name,
        image = image,
        currentPrice = currentPrice.toBigDecimalOrZero(),
        marketCap = marketCap.toBigDecimalOrZero(),
        marketCapRank = marketCapRank ?: 0,
        priceChangePercentage24h = priceChangePercentage24h.toBigDecimalOrZero(),
        priceChangePercentage7d = priceChangePercentage7d?.toBigDecimalOrZero(),
        priceChangePercentage30d = priceChangePercentage30d?.toBigDecimalOrZero(),
        high24h = high24h?.toBigDecimalOrZero(),
        low24h = low24h?.toBigDecimalOrZero(),
        circulatingSupply = circulatingSupply?.toBigDecimalOrZero(),
        totalSupply = totalSupply?.toBigDecimalOrZero(),
        maxSupply = maxSupply?.toBigDecimalOrZero(),
        ath = ath?.toBigDecimalOrZero(),
        athDate = athDate,
        atl = atl?.toBigDecimalOrZero(),
        atlDate = atlDate,
        lastUpdated = lastUpdated
    )
}

/**
 * Convert list of CoinDto to list of Coin domain models.
 */
fun List<CoinDto>.toDomain(): List<Coin> {
    return map { it.toDomain() }
}

/**
 * Convert PriceHistoryDto to PriceHistory domain model.
 */
fun PriceHistoryDto.toDomain(): PriceHistory {
    val pricePoints = prices.map { priceData ->
        PriceHistory.PricePoint(
            timestamp = priceData[0].toLong(),
            price = BigDecimal.valueOf(priceData[1])
        )
    }
    
    return PriceHistory(
        prices = pricePoints
    )
}
