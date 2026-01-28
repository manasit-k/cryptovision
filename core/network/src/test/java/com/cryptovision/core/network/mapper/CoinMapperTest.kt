package com.cryptovision.core.network.mapper

import com.cryptovision.core.network.model.CoinDto
import com.cryptovision.core.network.model.PriceHistoryDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

/**
 * Unit tests for CoinMapper extension functions.
 */
class CoinMapperTest {

    @Nested
    @DisplayName("CoinDto.toDomain()")
    inner class CoinDtoToDomainTests {

        @Test
        fun `should map all fields correctly`() {
            // Given
            val dto = createCoinDto()

            // When
            val result = dto.toDomain()

            // Then
            assertEquals("bitcoin", result.id)
            assertEquals("BTC", result.symbol) // Should be uppercase
            assertEquals("Bitcoin", result.name)
            assertEquals("https://example.com/bitcoin.png", result.image)
            assertEquals(BigDecimal.valueOf(50000.0), result.currentPrice)
            assertEquals(BigDecimal.valueOf(1000000000.0), result.marketCap)
            assertEquals(1, result.marketCapRank)
            assertEquals(BigDecimal.valueOf(2.5), result.priceChangePercentage24h)
            assertEquals(BigDecimal.valueOf(5.0), result.priceChangePercentage7d)
            assertEquals(BigDecimal.valueOf(10.0), result.priceChangePercentage30d)
            assertEquals(BigDecimal.valueOf(51000.0), result.high24h)
            assertEquals(BigDecimal.valueOf(49000.0), result.low24h)
            assertEquals(BigDecimal.valueOf(19000000.0), result.circulatingSupply)
            assertEquals(BigDecimal.valueOf(21000000.0), result.totalSupply)
            assertEquals(BigDecimal.valueOf(21000000.0), result.maxSupply)
            assertEquals(BigDecimal.valueOf(69000.0), result.ath)
            assertEquals("2021-11-10T14:24:11.849Z", result.athDate)
            assertEquals(BigDecimal.valueOf(67.81), result.atl)
            assertEquals("2013-07-06T00:00:00.000Z", result.atlDate)
            assertEquals("2024-01-01T00:00:00.000Z", result.lastUpdated)
        }

        @Test
        fun `should convert symbol to uppercase`() {
            // Given
            val dto = createCoinDto(symbol = "btc")

            // When
            val result = dto.toDomain()

            // Then
            assertEquals("BTC", result.symbol)
        }

        @Test
        fun `should handle null marketCapRank with default 0`() {
            // Given
            val dto = createCoinDto(marketCapRank = null)

            // When
            val result = dto.toDomain()

            // Then
            assertEquals(0, result.marketCapRank)
        }

        @Test
        fun `should handle null priceChangePercentage24h with default 0`() {
            // Given
            val dto = createCoinDto(priceChangePercentage24h = null)

            // When
            val result = dto.toDomain()

            // Then
            assertEquals(BigDecimal.ZERO, result.priceChangePercentage24h)
        }

        @Test
        fun `should handle null optional fields`() {
            // Given
            val dto = createCoinDto(
                priceChangePercentage7d = null,
                priceChangePercentage30d = null,
                high24h = null,
                low24h = null,
                circulatingSupply = null,
                totalSupply = null,
                maxSupply = null,
                ath = null,
                athDate = null,
                atl = null,
                atlDate = null
            )

            // When
            val result = dto.toDomain()

            // Then
            assertNull(result.priceChangePercentage7d)
            assertNull(result.priceChangePercentage30d)
            assertNull(result.high24h)
            assertNull(result.low24h)
            assertNull(result.circulatingSupply)
            assertNull(result.totalSupply)
            assertNull(result.maxSupply)
            assertNull(result.ath)
            assertNull(result.athDate)
            assertNull(result.atl)
            assertNull(result.atlDate)
        }

        @Test
        fun `should handle negative price changes`() {
            // Given
            val dto = createCoinDto(
                priceChangePercentage24h = -5.5,
                priceChangePercentage7d = -10.0,
                priceChangePercentage30d = -15.0
            )

            // When
            val result = dto.toDomain()

            // Then
            assertEquals(BigDecimal.valueOf(-5.5), result.priceChangePercentage24h)
            assertEquals(BigDecimal.valueOf(-10.0), result.priceChangePercentage7d)
            assertEquals(BigDecimal.valueOf(-15.0), result.priceChangePercentage30d)
        }

        @Test
        fun `should handle zero values`() {
            // Given
            val dto = createCoinDto(
                currentPrice = 0.0,
                marketCap = 0.0,
                priceChangePercentage24h = 0.0
            )

            // When
            val result = dto.toDomain()

            // Then
            assertEquals(BigDecimal.valueOf(0.0), result.currentPrice)
            assertEquals(BigDecimal.valueOf(0.0), result.marketCap)
            assertEquals(BigDecimal.valueOf(0.0), result.priceChangePercentage24h)
        }
    }

    @Nested
    @DisplayName("List<CoinDto>.toDomain()")
    inner class CoinDtoListToDomainTests {

        @Test
        fun `should map list of CoinDto to list of Coin`() {
            // Given
            val dtoList = listOf(
                createCoinDto(id = "bitcoin", name = "Bitcoin", symbol = "btc"),
                createCoinDto(id = "ethereum", name = "Ethereum", symbol = "eth")
            )

            // When
            val result = dtoList.toDomain()

            // Then
            assertEquals(2, result.size)
            assertEquals("bitcoin", result[0].id)
            assertEquals("Bitcoin", result[0].name)
            assertEquals("BTC", result[0].symbol)
            assertEquals("ethereum", result[1].id)
            assertEquals("Ethereum", result[1].name)
            assertEquals("ETH", result[1].symbol)
        }

        @Test
        fun `should return empty list when input is empty`() {
            // Given
            val dtoList = emptyList<CoinDto>()

            // When
            val result = dtoList.toDomain()

            // Then
            assertEquals(0, result.size)
        }
    }

    @Nested
    @DisplayName("PriceHistoryDto.toDomain()")
    inner class PriceHistoryDtoToDomainTests {

        @Test
        fun `should map price history correctly`() {
            // Given
            val dto = PriceHistoryDto(
                prices = listOf(
                    listOf(1704067200000.0, 42000.0),
                    listOf(1704153600000.0, 43000.0),
                    listOf(1704240000000.0, 44000.0)
                )
            )

            // When
            val result = dto.toDomain()

            // Then
            assertEquals(3, result.prices.size)
            assertEquals(1704067200000L, result.prices[0].timestamp)
            assertEquals(BigDecimal.valueOf(42000.0), result.prices[0].price)
            assertEquals(1704153600000L, result.prices[1].timestamp)
            assertEquals(BigDecimal.valueOf(43000.0), result.prices[1].price)
            assertEquals(1704240000000L, result.prices[2].timestamp)
            assertEquals(BigDecimal.valueOf(44000.0), result.prices[2].price)
        }

        @Test
        fun `should return empty prices list when input is empty`() {
            // Given
            val dto = PriceHistoryDto(prices = emptyList())

            // When
            val result = dto.toDomain()

            // Then
            assertEquals(0, result.prices.size)
        }

        @Test
        fun `should handle decimal prices`() {
            // Given
            val dto = PriceHistoryDto(
                prices = listOf(
                    listOf(1704067200000.0, 0.00001234)
                )
            )

            // When
            val result = dto.toDomain()

            // Then
            assertEquals(BigDecimal.valueOf(0.00001234), result.prices[0].price)
        }
    }

    // Helper function to create test CoinDto
    private fun createCoinDto(
        id: String = "bitcoin",
        symbol: String = "btc",
        name: String = "Bitcoin",
        image: String = "https://example.com/bitcoin.png",
        currentPrice: Double = 50000.0,
        marketCap: Double = 1000000000.0,
        marketCapRank: Int? = 1,
        priceChangePercentage24h: Double? = 2.5,
        priceChangePercentage7d: Double? = 5.0,
        priceChangePercentage30d: Double? = 10.0,
        high24h: Double? = 51000.0,
        low24h: Double? = 49000.0,
        circulatingSupply: Double? = 19000000.0,
        totalSupply: Double? = 21000000.0,
        maxSupply: Double? = 21000000.0,
        ath: Double? = 69000.0,
        athDate: String? = "2021-11-10T14:24:11.849Z",
        atl: Double? = 67.81,
        atlDate: String? = "2013-07-06T00:00:00.000Z",
        lastUpdated: String = "2024-01-01T00:00:00.000Z"
    ) = CoinDto(
        id = id,
        symbol = symbol,
        name = name,
        image = image,
        currentPrice = currentPrice,
        marketCap = marketCap,
        marketCapRank = marketCapRank,
        priceChangePercentage24h = priceChangePercentage24h,
        priceChangePercentage7d = priceChangePercentage7d,
        priceChangePercentage30d = priceChangePercentage30d,
        high24h = high24h,
        low24h = low24h,
        circulatingSupply = circulatingSupply,
        totalSupply = totalSupply,
        maxSupply = maxSupply,
        ath = ath,
        athDate = athDate,
        atl = atl,
        atlDate = atlDate,
        lastUpdated = lastUpdated
    )
}
