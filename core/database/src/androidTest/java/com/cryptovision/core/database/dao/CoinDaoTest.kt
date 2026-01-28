package com.cryptovision.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cryptovision.core.database.CryptoDatabase
import com.cryptovision.core.database.entity.CoinEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for CoinDao.
 * Tests run on Android device/emulator using in-memory database.
 */
@RunWith(AndroidJUnit4::class)
class CoinDaoTest {

    private lateinit var database: CryptoDatabase
    private lateinit var coinDao: CoinDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, CryptoDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        coinDao = database.coinDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertCoins_and_getAllCoins_returnsCoinsOrderedByRank() = runTest {
        // Given
        val coins = listOf(
            createCoinEntity(id = "ethereum", name = "Ethereum", marketCapRank = 2),
            createCoinEntity(id = "bitcoin", name = "Bitcoin", marketCapRank = 1),
            createCoinEntity(id = "cardano", name = "Cardano", marketCapRank = 3)
        )
        coinDao.insertCoins(coins)

        // When
        val result = coinDao.getAllCoins().first()

        // Then
        assertEquals(3, result.size)
        assertEquals("bitcoin", result[0].id)
        assertEquals("ethereum", result[1].id)
        assertEquals("cardano", result[2].id)
    }

    @Test
    fun getAllCoinsSync_returnsAllCoinsOrderedByRank() = runTest {
        // Given
        val coins = listOf(
            createCoinEntity(id = "ethereum", marketCapRank = 2),
            createCoinEntity(id = "bitcoin", marketCapRank = 1)
        )
        coinDao.insertCoins(coins)

        // When
        val result = coinDao.getAllCoinsSync()

        // Then
        assertEquals(2, result.size)
        assertEquals("bitcoin", result[0].id)
        assertEquals("ethereum", result[1].id)
    }

    @Test
    fun getCoinById_existingCoin_returnsCoin() = runTest {
        // Given
        val coin = createCoinEntity(id = "bitcoin", name = "Bitcoin")
        coinDao.insertCoin(coin)

        // When
        val result = coinDao.getCoinById("bitcoin")

        // Then
        assertEquals("bitcoin", result?.id)
        assertEquals("Bitcoin", result?.name)
    }

    @Test
    fun getCoinById_nonExistingCoin_returnsNull() = runTest {
        // When
        val result = coinDao.getCoinById("nonexistent")

        // Then
        assertNull(result)
    }

    @Test
    fun getCoinByIdFlow_existingCoin_emitsCoin() = runTest {
        // Given
        val coin = createCoinEntity(id = "bitcoin", name = "Bitcoin")
        coinDao.insertCoin(coin)

        // When
        val result = coinDao.getCoinByIdFlow("bitcoin").first()

        // Then
        assertEquals("bitcoin", result?.id)
        assertEquals("Bitcoin", result?.name)
    }

    @Test
    fun getCoinsByIds_returnsMatchingCoins() = runTest {
        // Given
        val coins = listOf(
            createCoinEntity(id = "bitcoin"),
            createCoinEntity(id = "ethereum"),
            createCoinEntity(id = "cardano")
        )
        coinDao.insertCoins(coins)

        // When
        val result = coinDao.getCoinsByIds(listOf("bitcoin", "cardano"))

        // Then
        assertEquals(2, result.size)
        assertTrue(result.any { it.id == "bitcoin" })
        assertTrue(result.any { it.id == "cardano" })
    }

    @Test
    fun searchCoins_byName_returnsMatchingCoins() = runTest {
        // Given
        val coins = listOf(
            createCoinEntity(id = "bitcoin", name = "Bitcoin", symbol = "BTC"),
            createCoinEntity(id = "ethereum", name = "Ethereum", symbol = "ETH"),
            createCoinEntity(id = "bitcoin-cash", name = "Bitcoin Cash", symbol = "BCH")
        )
        coinDao.insertCoins(coins)

        // When
        val result = coinDao.searchCoins("%Bitcoin%")

        // Then
        assertEquals(2, result.size)
        assertTrue(result.any { it.id == "bitcoin" })
        assertTrue(result.any { it.id == "bitcoin-cash" })
    }

    @Test
    fun searchCoins_bySymbol_returnsMatchingCoins() = runTest {
        // Given
        val coins = listOf(
            createCoinEntity(id = "bitcoin", name = "Bitcoin", symbol = "BTC"),
            createCoinEntity(id = "ethereum", name = "Ethereum", symbol = "ETH")
        )
        coinDao.insertCoins(coins)

        // When
        val result = coinDao.searchCoins("%BTC%")

        // Then
        assertEquals(1, result.size)
        assertEquals("bitcoin", result[0].id)
    }

    @Test
    fun searchCoins_noMatch_returnsEmptyList() = runTest {
        // Given
        val coin = createCoinEntity(id = "bitcoin", name = "Bitcoin", symbol = "BTC")
        coinDao.insertCoin(coin)

        // When
        val result = coinDao.searchCoins("%XYZ%")

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun insertCoins_withConflict_replacesExisting() = runTest {
        // Given
        val originalCoin = createCoinEntity(id = "bitcoin", name = "Bitcoin", currentPrice = 50000.0)
        coinDao.insertCoin(originalCoin)

        val updatedCoin = createCoinEntity(id = "bitcoin", name = "Bitcoin", currentPrice = 55000.0)

        // When
        coinDao.insertCoins(listOf(updatedCoin))
        val result = coinDao.getCoinById("bitcoin")

        // Then
        assertEquals(55000.0, result?.currentPrice)
    }

    @Test
    fun deleteAllCoins_removesAllCoins() = runTest {
        // Given
        val coins = listOf(
            createCoinEntity(id = "bitcoin"),
            createCoinEntity(id = "ethereum")
        )
        coinDao.insertCoins(coins)

        // When
        coinDao.deleteAllCoins()
        val result = coinDao.getAllCoinsSync()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun getAllCoins_emptyDatabase_returnsEmptyList() = runTest {
        // When
        val result = coinDao.getAllCoins().first()

        // Then
        assertTrue(result.isEmpty())
    }

    private fun createCoinEntity(
        id: String = "bitcoin",
        symbol: String = "BTC",
        name: String = "Bitcoin",
        image: String = "https://example.com/bitcoin.png",
        currentPrice: Double = 50000.0,
        marketCap: Double = 1000000000.0,
        marketCapRank: Int = 1,
        priceChangePercentage24h: Double = 2.5,
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
        lastUpdated: String = "2024-01-01T00:00:00.000Z",
        cachedAt: Long = System.currentTimeMillis()
    ) = CoinEntity(
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
        lastUpdated = lastUpdated,
        cachedAt = cachedAt
    )
}
