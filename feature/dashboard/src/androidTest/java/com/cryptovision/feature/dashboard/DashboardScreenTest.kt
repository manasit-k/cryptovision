package com.cryptovision.feature.dashboard

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cryptovision.domain.model.Coin
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

/**
 * Instrumented UI tests for DashboardScreen.
 */
@RunWith(AndroidJUnit4::class)
class DashboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dashboardScreen_showsLoadingState() {
        // Given
        val state = DashboardState(
            coins = emptyList(),
            filteredCoins = emptyList(),
            isLoading = true
        )

        // When
        composeTestRule.setContent {
            MaterialTheme {
                DashboardScreen(
                    state = state,
                    onCoinClick = {},
                    onFavoritesClick = {},
                    onSearchQueryChange = {},
                    onClearSearch = {},
                    onRefresh = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithTag("loading_state").assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_showsTitle() {
        // Given
        val state = DashboardState(
            coins = testCoins,
            filteredCoins = testCoins,
            isLoading = false
        )

        // When
        composeTestRule.setContent {
            MaterialTheme {
                DashboardScreen(
                    state = state,
                    onCoinClick = {},
                    onFavoritesClick = {},
                    onSearchQueryChange = {},
                    onClearSearch = {},
                    onRefresh = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("CryptoVision").assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_showsCoinList() {
        // Given
        val state = DashboardState(
            coins = testCoins,
            filteredCoins = testCoins,
            isLoading = false
        )

        // When
        composeTestRule.setContent {
            MaterialTheme {
                DashboardScreen(
                    state = state,
                    onCoinClick = {},
                    onFavoritesClick = {},
                    onSearchQueryChange = {},
                    onClearSearch = {},
                    onRefresh = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithTag("success_state").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bitcoin").assertIsDisplayed()
        composeTestRule.onNodeWithText("BTC").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ethereum").assertIsDisplayed()
        composeTestRule.onNodeWithText("ETH").assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_showsMarketCapRank() {
        // Given
        val state = DashboardState(
            coins = testCoins,
            filteredCoins = testCoins,
            isLoading = false
        )

        // When
        composeTestRule.setContent {
            MaterialTheme {
                DashboardScreen(
                    state = state,
                    onCoinClick = {},
                    onFavoritesClick = {},
                    onSearchQueryChange = {},
                    onClearSearch = {},
                    onRefresh = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("#1").assertIsDisplayed()
        composeTestRule.onNodeWithText("#2").assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_showsEmptyState_whenNoCoins() {
        // Given
        val state = DashboardState(
            coins = emptyList(),
            filteredCoins = emptyList(),
            isLoading = false
        )

        // When
        composeTestRule.setContent {
            MaterialTheme {
                DashboardScreen(
                    state = state,
                    onCoinClick = {},
                    onFavoritesClick = {},
                    onSearchQueryChange = {},
                    onClearSearch = {},
                    onRefresh = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithTag("empty_state").assertIsDisplayed()
        composeTestRule.onNodeWithText("No coins available").assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_showsEmptySearchState_whenNoSearchResults() {
        // Given
        val state = DashboardState(
            coins = testCoins,
            filteredCoins = emptyList(),
            isLoading = false,
            searchQuery = "xyz"
        )

        // When
        composeTestRule.setContent {
            MaterialTheme {
                DashboardScreen(
                    state = state,
                    onCoinClick = {},
                    onFavoritesClick = {},
                    onSearchQueryChange = {},
                    onClearSearch = {},
                    onRefresh = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("No coins found for \"xyz\"").assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_showsErrorState() {
        // Given
        val state = DashboardState(
            coins = emptyList(),
            filteredCoins = emptyList(),
            isLoading = false,
            error = "Network Error"
        )

        // When
        composeTestRule.setContent {
            MaterialTheme {
                DashboardScreen(
                    state = state,
                    onCoinClick = {},
                    onFavoritesClick = {},
                    onSearchQueryChange = {},
                    onClearSearch = {},
                    onRefresh = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithTag("error_state").assertIsDisplayed()
        composeTestRule.onNodeWithText("Network Error").assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_clickCoin_callsOnCoinClick() {
        // Given
        var clickedCoinId = ""
        val state = DashboardState(
            coins = testCoins,
            filteredCoins = testCoins,
            isLoading = false
        )

        composeTestRule.setContent {
            MaterialTheme {
                DashboardScreen(
                    state = state,
                    onCoinClick = { clickedCoinId = it },
                    onFavoritesClick = {},
                    onSearchQueryChange = {},
                    onClearSearch = {},
                    onRefresh = {}
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("Bitcoin").performClick()

        // Then
        assertEquals("bitcoin", clickedCoinId)
    }

    @Test
    fun dashboardScreen_clickFavoritesFab_callsOnFavoritesClick() {
        // Given
        var favoritesClicked = false
        val state = DashboardState(
            coins = testCoins,
            filteredCoins = testCoins,
            isLoading = false
        )

        composeTestRule.setContent {
            MaterialTheme {
                DashboardScreen(
                    state = state,
                    onCoinClick = {},
                    onFavoritesClick = { favoritesClicked = true },
                    onSearchQueryChange = {},
                    onClearSearch = {},
                    onRefresh = {}
                )
            }
        }

        // When
        composeTestRule.onNodeWithContentDescription("Favorites").performClick()

        // Then
        assertEquals(true, favoritesClicked)
    }

    @Test
    fun dashboardScreen_clickSearchIcon_showsSearchBar() {
        // Given
        val state = DashboardState(
            coins = testCoins,
            filteredCoins = testCoins,
            isLoading = false
        )

        composeTestRule.setContent {
            MaterialTheme {
                DashboardScreen(
                    state = state,
                    onCoinClick = {},
                    onFavoritesClick = {},
                    onSearchQueryChange = {},
                    onClearSearch = {},
                    onRefresh = {}
                )
            }
        }

        // When
        composeTestRule.onNodeWithContentDescription("Search").performClick()

        // Then
        composeTestRule.onNodeWithText("Search coins...").assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_typeInSearchBar_callsOnSearchQueryChange() {
        // Given
        var searchQuery = ""
        val state = DashboardState(
            coins = testCoins,
            filteredCoins = testCoins,
            isLoading = false
        )

        composeTestRule.setContent {
            MaterialTheme {
                DashboardScreen(
                    state = state,
                    onCoinClick = {},
                    onFavoritesClick = {},
                    onSearchQueryChange = { searchQuery = it },
                    onClearSearch = {},
                    onRefresh = {}
                )
            }
        }

        // When
        composeTestRule.onNodeWithContentDescription("Search").performClick()
        composeTestRule.onNodeWithText("Search coins...").performTextInput("bit")

        // Then
        assertEquals("bit", searchQuery)
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
