package com.cryptovision.feature.favorites

import app.cash.turbine.test
import com.cryptovision.domain.model.Coin
import com.cryptovision.domain.model.Result
import com.cryptovision.domain.usecase.GetFavoriteCoinsUseCase
import com.cryptovision.domain.usecase.ToggleFavoriteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
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
class FavoritesViewModelTest {

    private lateinit var getFavoriteCoinsUseCase: GetFavoriteCoinsUseCase
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase
    private lateinit var viewModel: FavoritesViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getFavoriteCoinsUseCase = mockk()
        toggleFavoriteUseCase = mockk()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should load favorite coins`() = runTest {
        // Given
        val favoriteCoins = listOf(
            createTestCoin("bitcoin"),
            createTestCoin("ethereum")
        )
        coEvery { getFavoriteCoinsUseCase() } returns flowOf(Result.Success(favoriteCoins))

        // When
        viewModel = FavoritesViewModel(getFavoriteCoinsUseCase, toggleFavoriteUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(favoriteCoins, state.favoriteCoins)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }
    }

    @Test
    fun `when loading favorites, state should show loading`() = runTest {
        // Given
        coEvery { getFavoriteCoinsUseCase() } returns flowOf(Result.Loading)

        // When
        viewModel = FavoritesViewModel(getFavoriteCoinsUseCase, toggleFavoriteUseCase)
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
        val errorMessage = "Database error"
        coEvery { getFavoriteCoinsUseCase() } returns flowOf(Result.Error(Exception(errorMessage)))

        // When
        viewModel = FavoritesViewModel(getFavoriteCoinsUseCase, toggleFavoriteUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(errorMessage, state.error)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `when favorites is empty, state should have empty list`() = runTest {
        // Given
        coEvery { getFavoriteCoinsUseCase() } returns flowOf(Result.Success(emptyList()))

        // When
        viewModel = FavoritesViewModel(getFavoriteCoinsUseCase, toggleFavoriteUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.favoriteCoins.isEmpty())
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `remove favorite should call toggle use case`() = runTest {
        // Given
        val favoriteCoins = listOf(createTestCoin("bitcoin"))
        coEvery { getFavoriteCoinsUseCase() } returns flowOf(Result.Success(favoriteCoins))
        coEvery { toggleFavoriteUseCase("bitcoin") } returns Result.Success(Unit)

        // When
        viewModel = FavoritesViewModel(getFavoriteCoinsUseCase, toggleFavoriteUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onIntent(FavoritesIntent.RemoveFavorite("bitcoin"))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { toggleFavoriteUseCase("bitcoin") }
    }

    @Test
    fun `remove favorite error should update state with error`() = runTest {
        // Given
        val errorMessage = "Failed to remove"
        val favoriteCoins = listOf(createTestCoin("bitcoin"))
        coEvery { getFavoriteCoinsUseCase() } returns flowOf(Result.Success(favoriteCoins))
        coEvery { toggleFavoriteUseCase("bitcoin") } returns Result.Error(Exception(errorMessage))

        // When
        viewModel = FavoritesViewModel(getFavoriteCoinsUseCase, toggleFavoriteUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onIntent(FavoritesIntent.RemoveFavorite("bitcoin"))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(errorMessage, state.error)
        }
    }

    @Test
    fun `refresh should reload favorites`() = runTest {
        // Given
        val favoriteCoins = listOf(createTestCoin("bitcoin"))
        coEvery { getFavoriteCoinsUseCase() } returns flowOf(Result.Success(favoriteCoins))

        // When
        viewModel = FavoritesViewModel(getFavoriteCoinsUseCase, toggleFavoriteUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onIntent(FavoritesIntent.Refresh)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(atLeast = 2) { getFavoriteCoinsUseCase() }
    }

    private fun createTestCoin(id: String) = Coin(
        id = id,
        symbol = id.take(3).uppercase(),
        name = id.replaceFirstChar { it.uppercase() },
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
