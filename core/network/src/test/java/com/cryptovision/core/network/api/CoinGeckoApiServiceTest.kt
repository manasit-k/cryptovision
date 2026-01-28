package com.cryptovision.core.network.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import retrofit2.HttpException
import retrofit2.Retrofit

/**
 * Unit tests for CoinGeckoApiService using MockWebServer.
 */
class CoinGeckoApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: CoinGeckoApiService

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        apiService = retrofit.create(CoinGeckoApiService::class.java)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Nested
    @DisplayName("getCoins()")
    inner class GetCoinsTests {

        @Test
        fun `should return list of coins on success`() = runTest {
            // Given
            val responseBody = """
                [
                    {
                        "id": "bitcoin",
                        "symbol": "btc",
                        "name": "Bitcoin",
                        "image": "https://example.com/bitcoin.png",
                        "current_price": 50000.0,
                        "market_cap": 1000000000000.0,
                        "market_cap_rank": 1,
                        "price_change_percentage_24h": 2.5,
                        "last_updated": "2024-01-01T00:00:00.000Z"
                    },
                    {
                        "id": "ethereum",
                        "symbol": "eth",
                        "name": "Ethereum",
                        "image": "https://example.com/ethereum.png",
                        "current_price": 3000.0,
                        "market_cap": 500000000000.0,
                        "market_cap_rank": 2,
                        "price_change_percentage_24h": -1.5,
                        "last_updated": "2024-01-01T00:00:00.000Z"
                    }
                ]
            """.trimIndent()
            mockWebServer.enqueue(MockResponse().setBody(responseBody).setResponseCode(200))

            // When
            val result = apiService.getCoins()

            // Then
            assertEquals(2, result.size)
            assertEquals("bitcoin", result[0].id)
            assertEquals("BTC", result[0].symbol.uppercase())
            assertEquals(50000.0, result[0].currentPrice)
            assertEquals("ethereum", result[1].id)
        }

        @Test
        fun `should send correct query parameters`() = runTest {
            // Given
            val responseBody = "[]"
            mockWebServer.enqueue(MockResponse().setBody(responseBody).setResponseCode(200))

            // When
            apiService.getCoins(
                vsCurrency = "eur",
                order = "market_cap_asc",
                perPage = 50,
                page = 2
            )

            // Then
            val request = mockWebServer.takeRequest()
            assertTrue(request.path!!.contains("vs_currency=eur"))
            assertTrue(request.path!!.contains("order=market_cap_asc"))
            assertTrue(request.path!!.contains("per_page=50"))
            assertTrue(request.path!!.contains("page=2"))
        }

        @Test
        fun `should return empty list when no coins`() = runTest {
            // Given
            mockWebServer.enqueue(MockResponse().setBody("[]").setResponseCode(200))

            // When
            val result = apiService.getCoins()

            // Then
            assertTrue(result.isEmpty())
        }

        @Test
        fun `should throw exception on server error`() = runTest {
            // Given
            mockWebServer.enqueue(MockResponse().setResponseCode(500))

            // When/Then
            assertThrows<HttpException> {
                apiService.getCoins()
            }
        }
    }

    @Nested
    @DisplayName("getCoinById()")
    inner class GetCoinByIdTests {

        @Test
        fun `should return coin details on success`() = runTest {
            // Given
            val responseBody = """
                {
                    "id": "bitcoin",
                    "symbol": "btc",
                    "name": "Bitcoin",
                    "image": "https://example.com/bitcoin.png",
                    "current_price": 50000.0,
                    "market_cap": 1000000000000.0,
                    "market_cap_rank": 1,
                    "price_change_percentage_24h": 2.5,
                    "high_24h": 51000.0,
                    "low_24h": 49000.0,
                    "circulating_supply": 19000000.0,
                    "total_supply": 21000000.0,
                    "max_supply": 21000000.0,
                    "ath": 69000.0,
                    "ath_date": "2021-11-10T14:24:11.849Z",
                    "atl": 67.81,
                    "atl_date": "2013-07-06T00:00:00.000Z",
                    "last_updated": "2024-01-01T00:00:00.000Z"
                }
            """.trimIndent()
            mockWebServer.enqueue(MockResponse().setBody(responseBody).setResponseCode(200))

            // When
            val result = apiService.getCoinById("bitcoin")

            // Then
            assertEquals("bitcoin", result.id)
            assertEquals("btc", result.symbol)
            assertEquals(50000.0, result.currentPrice)
            assertEquals(51000.0, result.high24h)
            assertEquals(49000.0, result.low24h)
        }

        @Test
        fun `should include coin id in path`() = runTest {
            // Given
            val responseBody = """
                {
                    "id": "bitcoin",
                    "symbol": "btc",
                    "name": "Bitcoin",
                    "image": "https://example.com/bitcoin.png",
                    "current_price": 50000.0,
                    "market_cap": 1000000000000.0,
                    "last_updated": "2024-01-01T00:00:00.000Z"
                }
            """.trimIndent()
            mockWebServer.enqueue(MockResponse().setBody(responseBody).setResponseCode(200))

            // When
            apiService.getCoinById("bitcoin")

            // Then
            val request = mockWebServer.takeRequest()
            assertTrue(request.path!!.contains("/coins/bitcoin"))
        }

        @Test
        fun `should throw exception when coin not found`() = runTest {
            // Given
            mockWebServer.enqueue(MockResponse().setResponseCode(404))

            // When/Then
            assertThrows<HttpException> {
                apiService.getCoinById("nonexistent")
            }
        }
    }

    @Nested
    @DisplayName("getPriceHistory()")
    inner class GetPriceHistoryTests {

        @Test
        fun `should return price history on success`() = runTest {
            // Given
            val responseBody = """
                {
                    "prices": [
                        [1704067200000, 42000.0],
                        [1704153600000, 43000.0],
                        [1704240000000, 44000.0]
                    ]
                }
            """.trimIndent()
            mockWebServer.enqueue(MockResponse().setBody(responseBody).setResponseCode(200))

            // When
            val result = apiService.getPriceHistory("bitcoin")

            // Then
            assertEquals(3, result.prices.size)
            assertEquals(1704067200000.0, result.prices[0][0])
            assertEquals(42000.0, result.prices[0][1])
        }

        @Test
        fun `should send correct query parameters`() = runTest {
            // Given
            val responseBody = """{"prices": []}"""
            mockWebServer.enqueue(MockResponse().setBody(responseBody).setResponseCode(200))

            // When
            apiService.getPriceHistory(
                id = "bitcoin",
                vsCurrency = "eur",
                days = 30
            )

            // Then
            val request = mockWebServer.takeRequest()
            assertTrue(request.path!!.contains("/coins/bitcoin/market_chart"))
            assertTrue(request.path!!.contains("vs_currency=eur"))
            assertTrue(request.path!!.contains("days=30"))
        }

        @Test
        fun `should return empty prices when no data`() = runTest {
            // Given
            val responseBody = """{"prices": []}"""
            mockWebServer.enqueue(MockResponse().setBody(responseBody).setResponseCode(200))

            // When
            val result = apiService.getPriceHistory("bitcoin")

            // Then
            assertTrue(result.prices.isEmpty())
        }
    }

    @Nested
    @DisplayName("searchCoins()")
    inner class SearchCoinsTests {

        @Test
        fun `should return search results on success`() = runTest {
            // Given
            val responseBody = """
                {
                    "coins": [
                        {
                            "id": "bitcoin",
                            "name": "Bitcoin",
                            "symbol": "BTC",
                            "market_cap_rank": 1,
                            "thumb": "https://example.com/thumb.png",
                            "large": "https://example.com/large.png"
                        },
                        {
                            "id": "bitcoin-cash",
                            "name": "Bitcoin Cash",
                            "symbol": "BCH",
                            "market_cap_rank": 20,
                            "thumb": "https://example.com/bch_thumb.png",
                            "large": "https://example.com/bch_large.png"
                        }
                    ]
                }
            """.trimIndent()
            mockWebServer.enqueue(MockResponse().setBody(responseBody).setResponseCode(200))

            // When
            val result = apiService.searchCoins("bitcoin")

            // Then
            assertEquals(2, result.coins.size)
            assertEquals("bitcoin", result.coins[0].id)
            assertEquals("Bitcoin", result.coins[0].name)
            assertEquals("bitcoin-cash", result.coins[1].id)
        }

        @Test
        fun `should send query parameter`() = runTest {
            // Given
            val responseBody = """{"coins": []}"""
            mockWebServer.enqueue(MockResponse().setBody(responseBody).setResponseCode(200))

            // When
            apiService.searchCoins("ethereum")

            // Then
            val request = mockWebServer.takeRequest()
            assertTrue(request.path!!.contains("query=ethereum"))
        }

        @Test
        fun `should return empty results when no match`() = runTest {
            // Given
            val responseBody = """{"coins": []}"""
            mockWebServer.enqueue(MockResponse().setBody(responseBody).setResponseCode(200))

            // When
            val result = apiService.searchCoins("xyz123")

            // Then
            assertTrue(result.coins.isEmpty())
        }

        @Test
        fun `should handle coins with null optional fields`() = runTest {
            // Given
            val responseBody = """
                {
                    "coins": [
                        {
                            "id": "new-coin",
                            "name": "New Coin",
                            "symbol": "NEW"
                        }
                    ]
                }
            """.trimIndent()
            mockWebServer.enqueue(MockResponse().setBody(responseBody).setResponseCode(200))

            // When
            val result = apiService.searchCoins("new")

            // Then
            assertEquals(1, result.coins.size)
            assertNotNull(result.coins[0].id)
            assertEquals(null, result.coins[0].marketCapRank)
            assertEquals(null, result.coins[0].thumb)
        }
    }

    @Nested
    @DisplayName("Error handling")
    inner class ErrorHandlingTests {

        @Test
        fun `should throw HttpException on 401 unauthorized`() = runTest {
            // Given
            mockWebServer.enqueue(MockResponse().setResponseCode(401))

            // When/Then
            val exception = assertThrows<HttpException> {
                apiService.getCoins()
            }
            assertEquals(401, exception.code())
        }

        @Test
        fun `should throw HttpException on 429 rate limit`() = runTest {
            // Given
            mockWebServer.enqueue(MockResponse().setResponseCode(429))

            // When/Then
            val exception = assertThrows<HttpException> {
                apiService.getCoins()
            }
            assertEquals(429, exception.code())
        }

        @Test
        fun `should throw HttpException on 503 service unavailable`() = runTest {
            // Given
            mockWebServer.enqueue(MockResponse().setResponseCode(503))

            // When/Then
            val exception = assertThrows<HttpException> {
                apiService.getCoins()
            }
            assertEquals(503, exception.code())
        }
    }
}
