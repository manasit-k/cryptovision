package com.cryptovision.feature.details

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cryptovision.domain.model.Coin
import com.cryptovision.domain.model.PriceHistory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

/**
 * Instrumented UI tests for DetailsScreen.
 */
@RunWith(AndroidJUnit4::class)
class DetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun detailsScreen_showsCoinName_inTitle() {
        // Given
        val state = DetailsState(
            coin = testCoin,
            isLoadingDetails = false
        )

        // When
        composeTestRule.setContent {
            MaterialTheme {
                DetailsScreenContent(
                    state = state,
                    onNavigateBack = {},
                    onToggleFavorite = {},
                    onTimeRangeSelected = {},
                    onRetry = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Bitcoin").assertIsDisplayed()
    }

    @Test
    fun detailsScreen_showsCoinDetails() {
        // Given
        val state = DetailsState(
            coin = testCoin,
            isLoadingDetails = false
        )

        // When
        composeTestRule.setContent {
            MaterialTheme {
                DetailsScreenContent(
                    state = state,
                    onNavigateBack = {},
                    onToggleFavorite = {},
                    onTimeRangeSelected = {},
                    onRetry = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Current Price").assertIsDisplayed()
        composeTestRule.onNodeWithText("Market Stats").assertIsDisplayed()
        composeTestRule.onNodeWithText("Price Chart").assertIsDisplayed()
    }

    @Test
    fun detailsScreen_showsErrorState_whenError() {
        // Given
        val state = DetailsState(
            coin = null,
            isLoadingDetails = false,
            error = "Network Error"
        )

        // When
        composeTestRule.setContent {
            MaterialTheme {
                DetailsScreenContent(
                    state = state,
                    onNavigateBack = {},
                    onToggleFavorite = {},
                    onTimeRangeSelected = {},
                    onRetry = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Network Error").assertIsDisplayed()
    }

    @Test
    fun detailsScreen_clickBack_callsOnNavigateBack() {
        // Given
        var backClicked = false
        val state = DetailsState(
            coin = testCoin,
            isLoadingDetails = false
        )

        composeTestRule.setContent {
            MaterialTheme {
                DetailsScreenContent(
                    state = state,
                    onNavigateBack = { backClicked = true },
                    onToggleFavorite = {},
                    onTimeRangeSelected = {},
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
    fun detailsScreen_showsFavoriteIcon_whenFavorite() {
        // Given
        val state = DetailsState(
            coin = testCoin,
            isFavorite = true,
            isLoadingDetails = false
        )

        // When
        composeTestRule.setContent {
            MaterialTheme {
                DetailsScreenContent(
                    state = state,
                    onNavigateBack = {},
                    onToggleFavorite = {},
                    onTimeRangeSelected = {},
                    onRetry = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Remove from favorites").assertIsDisplayed()
    }

    @Test
    fun detailsScreen_showsNotFavoriteIcon_whenNotFavorite() {
        // Given
        val state = DetailsState(
            coin = testCoin,
            isFavorite = false,
            isLoadingDetails = false
        )

        // When
        composeTestRule.setContent {
            MaterialTheme {
                DetailsScreenContent(
                    state = state,
                    onNavigateBack = {},
                    onToggleFavorite = {},
                    onTimeRangeSelected = {},
                    onRetry = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Add to favorites").assertIsDisplayed()
    }

    @Test
    fun detailsScreen_clickFavorite_callsOnToggleFavorite() {
        // Given
        var favoriteToggled = false
        val state = DetailsState(
            coin = testCoin,
            isFavorite = false,
            isLoadingDetails = false
        )

        composeTestRule.setContent {
            MaterialTheme {
                DetailsScreenContent(
                    state = state,
                    onNavigateBack = {},
                    onToggleFavorite = { favoriteToggled = true },
                    onTimeRangeSelected = {},
                    onRetry = {}
                )
            }
        }

        // When
        composeTestRule.onNodeWithContentDescription("Add to favorites").performClick()

        // Then
        assertTrue(favoriteToggled)
    }

    @Test
    fun detailsScreen_showsTimeRangeChips() {
        // Given
        val state = DetailsState(
            coin = testCoin,
            isLoadingDetails = false
        )

        // When
        composeTestRule.setContent {
            MaterialTheme {
                DetailsScreenContent(
                    state = state,
                    onNavigateBack = {},
                    onToggleFavorite = {},
                    onTimeRangeSelected = {},
                    onRetry = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("7D").assertIsDisplayed()
        composeTestRule.onNodeWithText("30D").assertIsDisplayed()
        composeTestRule.onNodeWithText("90D").assertIsDisplayed()
        composeTestRule.onNodeWithText("1Y").assertIsDisplayed()
    }

    @Test
    fun detailsScreen_clickTimeRange_callsOnTimeRangeSelected() {
        // Given
        var selectedDays = 0
        val state = DetailsState(
            coin = testCoin,
            isLoadingDetails = false
        )

        composeTestRule.setContent {
            MaterialTheme {
                DetailsScreenContent(
                    state = state,
                    onNavigateBack = {},
                    onToggleFavorite = {},
                    onTimeRangeSelected = { selectedDays = it },
                    onRetry = {}
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("30D").performClick()

        // Then
        assertEquals(30, selectedDays)
    }

    @Test
    fun detailsScreen_showsMarketStats() {
        // Given
        val state = DetailsState(
            coin = testCoin,
            isLoadingDetails = false
        )

        // When
        composeTestRule.setContent {
            MaterialTheme {
                DetailsScreenContent(
                    state = state,
                    onNavigateBack = {},
                    onToggleFavorite = {},
                    onTimeRangeSelected = {},
                    onRetry = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Market Cap").assertIsDisplayed()
        composeTestRule.onNodeWithText("Market Cap Rank").assertIsDisplayed()
        composeTestRule.onNodeWithText("#1").assertIsDisplayed()
    }

    @Test
    fun detailsScreen_showsAllTimeStats() {
        // Given
        val state = DetailsState(
            coin = testCoin,
            isLoadingDetails = false
        )

        // When
        composeTestRule.setContent {
            MaterialTheme {
                DetailsScreenContent(
                    state = state,
                    onNavigateBack = {},
                    onToggleFavorite = {},
                    onTimeRangeSelected = {},
                    onRetry = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("All-Time Stats").assertIsDisplayed()
        composeTestRule.onNodeWithText("All-Time High").assertIsDisplayed()
        composeTestRule.onNodeWithText("All-Time Low").assertIsDisplayed()
    }

    companion object {
        private val testCoin = Coin(
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
        )
    }
}
