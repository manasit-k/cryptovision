package com.cryptovision.feature.details

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.cryptovision.domain.model.Coin
import com.cryptovision.domain.model.PriceHistory
import com.cryptovision.domain.model.Result
import com.cryptovision.domain.usecase.CheckCoinFavoriteUseCase
import com.cryptovision.domain.usecase.GetCoinDetailsUseCase
import com.cryptovision.domain.usecase.GetPriceHistoryUseCase
import com.cryptovision.domain.usecase.ToggleFavoriteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {

    private lateinit var getCoinDetailsUseCase: GetCoinDetailsUseCase
    private lateinit var getPriceHistoryUseCase: GetPriceHistoryUseCase
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase
    private lateinit var checkCoinFavoriteUseCase: CheckCoinFavoriteUseCase
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: DetailsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getCoinDetailsUseCase = mockk()
        getPriceHistoryUseCase = mockk()
        toggleFavoriteUseCase = mockk()
        checkCoinFavoriteUseCase = mockk()
        savedStateHandle = SavedStateHandle(mapOf("coinId" to "bitcoin"))
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should load coin details and price history`() = runTest {
        // Given
        val coin = createTestCoin("bitcoin")
        val priceHistory = createTestPriceHistory("bitcoin")
        coEvery { getCoinDetailsUseCase("bitcoin") } returns flowOf(Result.Success(coin))
        coEvery { getPriceHistoryUseCase("bitcoin", 7) } returns flowOf(Result.Success(priceHistory))
        every { checkCoinFavoriteUseCase("bitcoin") } returns flowOf(false)

        // When
        viewModel = DetailsViewModel(
            getCoinDetailsUseCase,
            getPriceHistoryUseCase,
            toggleFavoriteUseCase,
            checkCoinFavoriteUseCase,
            savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(coin, state.coin)
            assertEquals(priceHistory, state.priceHistory)
            assertFalse(state.isLoadingDetails)
            assertFalse(state.isLoadingChart)
        }
    }

    @Test
    fun `when loading details, state should show loading`() = runTest {
        // Given
        coEvery { getCoinDetailsUseCase("bitcoin") } returns flowOf(Result.Loading)
        coEvery { getPriceHistoryUseCase("bitcoin", 7) } returns flowOf(Result.Loading)
        every { checkCoinFavoriteUseCase("bitcoin") } returns flowOf(false)

        // When
        viewModel = DetailsViewModel(
            getCoinDetailsUseCase,
            getPriceHistoryUseCase,
            toggleFavoriteUseCase,
            checkCoinFavoriteUseCase,
            savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isLoadingDetails || state.isLoadingChart)
        }
    }

    @Test
    fun `when error occurs, state should show error`() = runTest {
        // Given
        val errorMessage = "Coin not found"
        coEvery { getCoinDetailsUseCase("bitcoin") } returns flowOf(Result.Error(Exception(errorMessage)))
        coEvery { getPriceHistoryUseCase("bitcoin", 7) } returns flowOf(Result.Loading)
        every { checkCoinFavoriteUseCase("bitcoin") } returns flowOf(false)

        // When
        viewModel = DetailsViewModel(
            getCoinDetailsUseCase,
            getPriceHistoryUseCase,
            toggleFavoriteUseCase,
            checkCoinFavoriteUseCase,
            savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(errorMessage, state.error)
        }
    }

    @Test
    fun `toggle favorite should call use case`() = runTest {
        // Given
        val coin = createTestCoin("bitcoin")
        coEvery { getCoinDetailsUseCase("bitcoin") } returns flowOf(Result.Success(coin))
        coEvery { getPriceHistoryUseCase("bitcoin", 7) } returns flowOf(Result.Success(createTestPriceHistory("bitcoin")))
        every { checkCoinFavoriteUseCase("bitcoin") } returns flowOf(false)
        coEvery { toggleFavoriteUseCase("bitcoin") } returns Result.Success(Unit)

        // When
        viewModel = DetailsViewModel(
            getCoinDetailsUseCase,
            getPriceHistoryUseCase,
            toggleFavoriteUseCase,
            checkCoinFavoriteUseCase,
            savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onIntent(DetailsIntent.ToggleFavorite)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { toggleFavoriteUseCase("bitcoin") }
    }

    @Test
    fun `select time range should reload price history`() = runTest {
        // Given
        val coin = createTestCoin("bitcoin")
        val priceHistory7d = createTestPriceHistory("bitcoin")
        val priceHistory30d = createTestPriceHistory("bitcoin")
        coEvery { getCoinDetailsUseCase("bitcoin") } returns flowOf(Result.Success(coin))
        coEvery { getPriceHistoryUseCase("bitcoin", 7) } returns flowOf(Result.Success(priceHistory7d))
        coEvery { getPriceHistoryUseCase("bitcoin", 30) } returns flowOf(Result.Success(priceHistory30d))
        every { checkCoinFavoriteUseCase("bitcoin") } returns flowOf(false)

        // When
        viewModel = DetailsViewModel(
            getCoinDetailsUseCase,
            getPriceHistoryUseCase,
            toggleFavoriteUseCase,
            checkCoinFavoriteUseCase,
            savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onIntent(DetailsIntent.SelectTimeRange(30))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(30, state.selectedTimeRange)
        }
    }

    @Test
    fun `favorite status should update from flow`() = runTest {
        // Given
        val coin = createTestCoin("bitcoin")
        coEvery { getCoinDetailsUseCase("bitcoin") } returns flowOf(Result.Success(coin))
        coEvery { getPriceHistoryUseCase("bitcoin", 7) } returns flowOf(Result.Success(createTestPriceHistory("bitcoin")))
        every { checkCoinFavoriteUseCase("bitcoin") } returns flowOf(true)

        // When
        viewModel = DetailsViewModel(
            getCoinDetailsUseCase,
            getPriceHistoryUseCase,
            toggleFavoriteUseCase,
            checkCoinFavoriteUseCase,
            savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isFavorite)
        }
    }

    private fun createTestCoin(id: String) = Coin(
        id = id,
        symbol = "BTC",
        name = "Bitcoin",
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

    private fun createTestPriceHistory(coinId: String) = PriceHistory(
        coinId = coinId,
        prices = listOf(
            PriceHistory.PricePoint(1609459200000, BigDecimal("29000.00")),
            PriceHistory.PricePoint(1609545600000, BigDecimal("32000.00")),
            PriceHistory.PricePoint(1609632000000, BigDecimal("33000.00"))
        )
    )
}
