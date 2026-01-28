package com.cryptovision.feature.dashboard

import app.cash.turbine.test
import com.cryptovision.domain.model.Coin
import com.cryptovision.domain.model.Result
import com.cryptovision.domain.usecase.GetCoinsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private lateinit var getCoinsUseCase: GetCoinsUseCase
    private lateinit var viewModel: DashboardViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getCoinsUseCase = mockk()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should load coins`() = runTest {
        // Given
        val coins = listOf(createTestCoin("bitcoin"), createTestCoin("ethereum"))
        coEvery { getCoinsUseCase(any()) } returns flowOf(Result.Success(coins))

        // When
        viewModel = DashboardViewModel(getCoinsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(coins, state.coins)
            assertEquals(coins, state.filteredCoins)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }
    }

    @Test
    fun `when loading coins, state should show loading`() = runTest {
        // Given
        coEvery { getCoinsUseCase(any()) } returns flowOf(Result.Loading)

        // When
        viewModel = DashboardViewModel(getCoinsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isLoading)
        }
    }

    @Test
    fun `when error occurs, state should show error`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { getCoinsUseCase(any()) } returns flowOf(Result.Error(Exception(errorMessage)))

        // When
        viewModel = DashboardViewModel(getCoinsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(errorMessage, state.error)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `search should filter coins by name`() = runTest {
        // Given
        val coins = listOf(
            createTestCoin("bitcoin", "Bitcoin"),
            createTestCoin("ethereum", "Ethereum"),
            createTestCoin("solana", "Solana")
        )
        coEvery { getCoinsUseCase(any()) } returns flowOf(Result.Success(coins))

        // When
        viewModel = DashboardViewModel(getCoinsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onIntent(DashboardIntent.SearchCoins("bit"))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(1, state.filteredCoins.size)
            assertEquals("bitcoin", state.filteredCoins[0].id)
            assertEquals("bit", state.searchQuery)
        }
    }

    @Test
    fun `search should filter coins by symbol`() = runTest {
        // Given
        val coins = listOf(
            createTestCoin("bitcoin", "Bitcoin", "BTC"),
            createTestCoin("ethereum", "Ethereum", "ETH")
        )
        coEvery { getCoinsUseCase(any()) } returns flowOf(Result.Success(coins))

        // When
        viewModel = DashboardViewModel(getCoinsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onIntent(DashboardIntent.SearchCoins("ETH"))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(1, state.filteredCoins.size)
            assertEquals("ethereum", state.filteredCoins[0].id)
        }
    }

    @Test
    fun `clear search should reset filtered coins`() = runTest {
        // Given
        val coins = listOf(
            createTestCoin("bitcoin"),
            createTestCoin("ethereum")
        )
        coEvery { getCoinsUseCase(any()) } returns flowOf(Result.Success(coins))

        // When
        viewModel = DashboardViewModel(getCoinsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onIntent(DashboardIntent.SearchCoins("bit"))
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onIntent(DashboardIntent.ClearSearch)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(coins, state.filteredCoins)
            assertEquals("", state.searchQuery)
        }
    }

    @Test
    fun `refresh should reload coins with force refresh`() = runTest {
        // Given
        val coins = listOf(createTestCoin("bitcoin"))
        coEvery { getCoinsUseCase(forceRefresh = true) } returns flowOf(Result.Success(coins))
        coEvery { getCoinsUseCase(forceRefresh = false) } returns flowOf(Result.Success(emptyList()))

        // When
        viewModel = DashboardViewModel(getCoinsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onIntent(DashboardIntent.Refresh)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(coins, state.coins)
        }
    }

    private fun createTestCoin(
        id: String,
        name: String = id.replaceFirstChar { it.uppercase() },
        symbol: String = id.take(3).uppercase()
    ) = Coin(
        id = id,
        symbol = symbol,
        name = name,
        image = "https://example.com/$id.png",
        currentPrice = BigDecimal("50000.00"),
        marketCap = BigDecimal("1000000000"),
        marketCapRank = 1,
        priceChangePercentage24h = BigDecimal("2.5"),
        priceChangePercentage7d = BigDecimal("5.0"),
        priceChangePercentage30d = BigDecimal("10.0"),
        high24h = BigDecimal("51000.00"),
        low24h = BigDecimal("49000.00"),
        circulatingSupply = BigDecimal("19000000"),
        totalSupply = BigDecimal("21000000"),
        maxSupply = BigDecimal("21000000"),
        ath = BigDecimal("69000.00"),
        athDate = "2021-11-10T14:24:11.849Z",
        atl = BigDecimal("67.81"),
        atlDate = "2013-07-06T00:00:00.000Z",
        lastUpdated = "2024-01-01T00:00:00.000Z"
    )
}
