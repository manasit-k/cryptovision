package com.cryptovision.data.repository

import app.cash.turbine.test
import com.cryptovision.core.database.dao.CoinDao
import com.cryptovision.core.database.dao.FavoriteDao
import com.cryptovision.core.database.entity.CoinEntity
import com.cryptovision.core.database.entity.FavoriteEntity
import com.cryptovision.core.network.api.CoinGeckoApiService
import com.cryptovision.core.network.model.CoinDto
import com.cryptovision.core.network.model.PriceHistoryDto
import com.cryptovision.domain.model.Result
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CoinRepositoryImplTest {

    private lateinit var coinDao: CoinDao
    private lateinit var favoriteDao: FavoriteDao
    private lateinit var apiService: CoinGeckoApiService
    private lateinit var repository: CoinRepositoryImpl

    @BeforeEach
    fun setup() {
        coinDao = mockk(relaxed = true)
        favoriteDao = mockk(relaxed = true)
        apiService = mockk()
        repository = CoinRepositoryImpl(coinDao, favoriteDao, apiService)
    }

    @Test
    fun `getCoins should emit loading then success from network`() = runTest {
        // Given
        val coinDtos = listOf(createTestCoinDto("bitcoin"), createTestCoinDto("ethereum"))
        coEvery { apiService.getCoins() } returns coinDtos
        coEvery { coinDao.getAllCoins() } returns flowOf(emptyList())
        coEvery { coinDao.insertCoins(any()) } just Runs

        // When & Then
        repository.getCoins(forceRefresh = true).test {
            val loading = awaitItem()
            assertTrue(loading is Result.Loading)

            val success = awaitItem()
            assertTrue(success is Result.Success)
            assertEquals(2, (success as Result.Success).data.size)

            awaitComplete()
        }
    }

    @Test
    fun `getCoins should return cached data on network error`() = runTest {
        // Given
        val cachedEntities = listOf(createTestCoinEntity("bitcoin"))
        coEvery { apiService.getCoins() } throws Exception("Network error")
        coEvery { coinDao.getAllCoinsSync() } returns cachedEntities

        // When & Then
        repository.getCoins(forceRefresh = true).test {
            val loading = awaitItem()
            assertTrue(loading is Result.Loading)

            val success = awaitItem()
            assertTrue(success is Result.Success)
            assertEquals(1, (success as Result.Success).data.size)

            awaitComplete()
        }
    }

    @Test
    fun `getCoinById should emit coin details from network`() = runTest {
        // Given
        val coinDto = createTestCoinDto("bitcoin")
        coEvery { apiService.getCoinById("bitcoin") } returns coinDto
        coEvery { coinDao.getCoinById("bitcoin") } returns null
        coEvery { coinDao.insertCoin(any()) } just Runs

        // When & Then
        repository.getCoinById("bitcoin").test {
            val loading = awaitItem()
            assertTrue(loading is Result.Loading)

            val success = awaitItem()
            assertTrue(success is Result.Success)
            assertEquals("bitcoin", (success as Result.Success).data.id)

            awaitComplete()
        }
    }

    @Test
    fun `getPriceHistory should emit price history from network`() = runTest {
        // Given
        val priceHistoryDto = PriceHistoryDto(
            prices = listOf(
                listOf(1609459200000.0, 29000.0),
                listOf(1609545600000.0, 32000.0)
            )
        )
        coEvery { apiService.getPriceHistory("bitcoin", days = 7) } returns priceHistoryDto

        // When & Then
        repository.getPriceHistory("bitcoin", 7).test {
            val loading = awaitItem()
            assertTrue(loading is Result.Loading)

            val success = awaitItem()
            assertTrue(success is Result.Success)
            assertEquals(2, (success as Result.Success).data.prices.size)

            awaitComplete()
        }
    }

    @Test
    fun `toggleFavorite should add favorite when not exists`() = runTest {
        // Given
        coEvery { favoriteDao.isFavoriteSync("bitcoin") } returns false
        coEvery { favoriteDao.insertFavorite(any()) } just Runs

        // When
        val result = repository.toggleFavorite("bitcoin")

        // Then
        assertTrue(result is Result.Success)
        coVerify { favoriteDao.insertFavorite(any()) }
    }

    @Test
    fun `toggleFavorite should remove favorite when exists`() = runTest {
        // Given
        coEvery { favoriteDao.isFavoriteSync("bitcoin") } returns true
        coEvery { favoriteDao.deleteFavorite("bitcoin") } just Runs

        // When
        val result = repository.toggleFavorite("bitcoin")

        // Then
        assertTrue(result is Result.Success)
        coVerify { favoriteDao.deleteFavorite("bitcoin") }
    }

    @Test
    fun `isFavorite should return flow from dao`() = runTest {
        // Given
        every { favoriteDao.isFavorite("bitcoin") } returns flowOf(true)

        // When & Then
        repository.isFavorite("bitcoin").test {
            assertTrue(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `getFavoriteCoins should return coins for favorite ids`() = runTest {
        // Given
        val favoriteEntities = listOf(
            FavoriteEntity(coinId = "bitcoin", addedAt = System.currentTimeMillis())
        )
        val coinEntities = listOf(createTestCoinEntity("bitcoin"))

        every { favoriteDao.getAllFavorites() } returns flowOf(favoriteEntities)
        coEvery { coinDao.getCoinsByIds(listOf("bitcoin")) } returns coinEntities

        // When & Then
        repository.getFavoriteCoins().test {
            val loading = awaitItem()
            assertTrue(loading is Result.Loading)

            val success = awaitItem()
            assertTrue(success is Result.Success)
            assertEquals(1, (success as Result.Success).data.size)
            assertEquals("bitcoin", success.data[0].id)

            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createTestCoinDto(id: String) = CoinDto(
        id = id,
        symbol = "btc",
        name = "Bitcoin",
        image = "https://example.com/image.png",
        currentPrice = 50000.0,
        marketCap = 1000000000.0,
        marketCapRank = 1,
        priceChangePercentage24h = 2.5,
        priceChangePercentage7d = 5.0,
        priceChangePercentage30d = 10.0,
        high24h = 51000.0,
        low24h = 49000.0,
        circulatingSupply = 19000000.0,
        totalSupply = 21000000.0,
        maxSupply = 21000000.0,
        ath = 69000.0,
        athDate = "2021-11-10",
        atl = 67.0,
        atlDate = "2013-07-05",
        lastUpdated = "2024-01-01T00:00:00Z"
    )

    private fun createTestCoinEntity(id: String) = CoinEntity(
        id = id,
        symbol = "BTC",
        name = "Bitcoin",
        image = "https://example.com/image.png",
        currentPrice = 50000.0,
        marketCap = 1000000000.0,
        marketCapRank = 1,
        priceChangePercentage24h = 2.5,
        priceChangePercentage7d = 5.0,
        priceChangePercentage30d = 10.0,
        high24h = 51000.0,
        low24h = 49000.0,
        circulatingSupply = 19000000.0,
        totalSupply = 21000000.0,
        maxSupply = 21000000.0,
        ath = 69000.0,
        athDate = "2021-11-10",
        atl = 67.0,
        atlDate = "2013-07-05",
        lastUpdated = "2024-01-01T00:00:00Z"
    )
}
