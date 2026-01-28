package com.cryptovision.feature.favorites

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cryptovision.domain.model.Coin
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

/**
 * Instrumented UI tests for FavoritesScreen.
 */
@RunWith(AndroidJUnit4::class)
class FavoritesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun favoritesScreen_showsTitle() {
        // Given
        val state = FavoritesState(
            favoriteCoins = testCoins,
            isLoading = false
        )

        // When
        composeTestRule.setContent {
            MaterialTheme {
                FavoritesScreenContent(
                    state = state,
                    onNavigateBack = {},
                    onCoinClick = {},
                    onRemoveFavorite = {},
                    onRetry = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Favorites").assertIsDisplayed()
    }

    @Test
    fun favoritesScreen_showsFavoriteCoins() {
        // Given
        val state = FavoritesState(
            favoriteCoins = testCoins,
            isLoading = false
        )

        // When
        composeTestRule.setContent {
            MaterialTheme {
                FavoritesScreenContent(
                    state = state,
                    onNavigateBack = {},
                    onCoinClick = {},
                    onRemoveFavorite = {},
                    onRetry = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Bitcoin").assertIsDisplayed()
        composeTestRule.onNodeWithText("BTC").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ethereum").assertIsDisplayed()
        composeTestRule.onNodeWithText("ETH").assertIsDisplayed()
    }

    @Test
    fun favoritesScreen_showsEmptyState_whenNoFavorites() {
        // Given
        val state = FavoritesState(
            favoriteCoins = emptyList(),
            isLoading = false
        )

        // When
        composeTestRule.setContent {
            MaterialTheme {
                FavoritesScreenContent(
                    state = state,
                    onNavigateBack = {},
                    onCoinClick = {},
                    onRemoveFavorite = {},
                    onRetry = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("No favorite coins yet.\nAdd some from the dashboard!")
            .assertIsDisplayed()
    }

    @Test
    fun favoritesScreen_showsErrorState_whenError() {
        // Given
        val state = FavoritesState(
            favoriteCoins = emptyList(),
            isLoading = false,
            error = "Failed to load favorites"
        )

        // When
        composeTestRule.setContent {
            MaterialTheme {
                FavoritesScreenContent(
                    state = state,
                    onNavigateBack = {},
                    onCoinClick = {},
                    onRemoveFavorite = {},
                    onRetry = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Failed to load favorites").assertIsDisplayed()
    }

    @Test
    fun favoritesScreen_clickBack_callsOnNavigateBack() {
        // Given
        var backClicked = false
        val state = FavoritesState(
            favoriteCoins = testCoins,
            isLoading = false
        )

        composeTestRule.setContent {
            MaterialTheme {
                FavoritesScreenContent(
                    state = state,
                    onNavigateBack = { backClicked = true },
                    onCoinClick = {},
                    onRemoveFavorite = {},
                    onRetry = {}
                )
            }
        }

        // When
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // Then
        assertTrue(backClicked)
    }

    @Test
    fun favoritesScreen_clickCoin_callsOnCoinClick() {
        // Given
        var clickedCoinId = ""
        val state = FavoritesState(
            favoriteCoins = testCoins,
            isLoading = false
        )

        composeTestRule.setContent {
            MaterialTheme {
                FavoritesScreenContent(
                    state = state,
                    onNavigateBack = {},
                    onCoinClick = { clickedCoinId = it },
                    onRemoveFavorite = {},
                    onRetry = {}
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("Bitcoin").performClick()

        // Then
        assertEquals("bitcoin", clickedCoinId)
    }

    @Test
    fun favoritesScreen_showsDeleteButton() {
        // Given
        val state = FavoritesState(
            favoriteCoins = testCoins,
            isLoading = false
        )

        // When
        composeTestRule.setContent {
            MaterialTheme {
                FavoritesScreenContent(
                    state = state,
                    onNavigateBack = {},
                    onCoinClick = {},
                    onRemoveFavorite = {},
                    onRetry = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Remove from favorites").assertIsDisplayed()
    }

    @Test
    fun favoritesScreen_showsMarketCapRank() {
        // Given
        val state = FavoritesState(
            favoriteCoins = testCoins,
            isLoading = false
        )

        // When
        composeTestRule.setContent {
            MaterialTheme {
                FavoritesScreenContent(
                    state = state,
                    onNavigateBack = {},
                    onCoinClick = {},
                    onRemoveFavorite = {},
                    onRetry = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("#1").assertIsDisplayed()
        composeTestRule.onNodeWithText("#2").assertIsDisplayed()
    }

    companion object {
        private val testCoins = listOf(
            Coin(
                id = "bitcoin",
                symbol = "BTC",
                name = "Bitcoin",
                image = "https://example.com/bitcoin.png",
                currentPrice = BigDecimal("50000.00"),
                marketCap = BigDecimal("1000000000000"),
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
            ),
            Coin(
                id = "ethereum",
                symbol = "ETH",
                name = "Ethereum",
                image = "https://example.com/ethereum.png",
                currentPrice = BigDecimal("3000.00"),
                marketCap = BigDecimal("500000000000"),
                marketCapRank = 2,
                priceChangePercentage24h = BigDecimal("-1.5"),
                priceChangePercentage7d = BigDecimal("2.0"),
                priceChangePercentage30d = BigDecimal("5.0"),
                high24h = BigDecimal("3100.00"),
                low24h = BigDecimal("2900.00"),
                circulatingSupply = BigDecimal("120000000"),
                totalSupply = null,
                maxSupply = null,
                ath = BigDecimal("4800.00"),
                athDate = "2021-11-10T14:24:11.849Z",
                atl = BigDecimal("0.42"),
                atlDate = "2015-10-21T00:00:00.000Z",
                lastUpdated = "2024-01-01T00:00:00.000Z"
            )
        )
    }
}
