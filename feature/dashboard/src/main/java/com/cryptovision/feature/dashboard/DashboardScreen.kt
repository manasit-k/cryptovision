package com.cryptovision.feature.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.cryptovision.core.ui.components.CoinListShimmer
import com.cryptovision.core.ui.components.EmptyState
import com.cryptovision.core.ui.components.ErrorState
import com.cryptovision.core.ui.components.LoadingIndicator
import com.cryptovision.domain.model.Coin
import java.text.NumberFormat
import java.util.Locale
import java.math.BigDecimal
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * Dashboard screen showing list of cryptocurrencies.
 * Implements MVI architecture pattern.
 */
@Composable
fun DashboardScreen(
    onCoinClick: (String) -> Unit,
    onFavoritesClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DashboardScreen(
        state = state,
        onCoinClick = onCoinClick,
        onFavoritesClick = onFavoritesClick,
        onSearchQueryChange = { viewModel.onIntent(DashboardIntent.SearchCoins(it)) },
        onClearSearch = { viewModel.onIntent(DashboardIntent.ClearSearch) },
        onRefresh = { viewModel.onIntent(DashboardIntent.Refresh) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    state: DashboardState,
    onCoinClick: (String) -> Unit,
    onFavoritesClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSearch by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (showSearch) {
                SearchBar(
                    query = state.searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onClose = {
                        showSearch = false
                        onClearSearch()
                    }
                )
            } else {
                TopAppBar(
                    title = { Text("CryptoVision") },
                    actions = {
                        IconButton(onClick = { showSearch = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onFavoritesClick) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorites"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading && state.coins.isEmpty() -> {
                    CoinListShimmer(modifier = Modifier.testTag("loading_state"))
                }
                state.error != null && state.coins.isEmpty() -> {
                    ErrorState(
                        message = state.error ?: "Unknown error",
                        onRetry = onRefresh,
                        modifier = Modifier.testTag("error_state")
                    )
                }
                state.filteredCoins.isEmpty() -> {
                    EmptyState(
                        message = if (state.searchQuery.isNotEmpty()) {
                            "No coins found for \"${state.searchQuery}\""
                        } else {
                            "No coins available"
                        },
                        modifier = Modifier.testTag("empty_state")
                    )
                }
                else -> {
                    CoinList(
                        coins = state.filteredCoins,
                        onCoinClick = onCoinClick,
                        onRefresh = onRefresh,
                        isRefreshing = state.isLoading,
                        modifier = Modifier.testTag("success_state")
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Search coins...") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close search")
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoinList(
    coins: List<Coin>,
    onCoinClick: (String) -> Unit,
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier
) {
    val pullRefreshState = rememberPullToRefreshState()
    
    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            onRefresh()
        }
    }
    
    // Sync external refreshing state with internal state
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            pullRefreshState.startRefresh()
        } else {
            pullRefreshState.endRefresh()
        }
    }

    Box(modifier = modifier.nestedScroll(pullRefreshState.nestedScrollConnection)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(
                items = coins,
                key = { it.id }
            ) { coin ->
                CoinListItem(
                    coin = coin,
                    onClick = { onCoinClick(coin.id) }
                )
                HorizontalDivider()
            }
        }
        
        PullToRefreshContainer(
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun CoinListItem(
    coin: Coin,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val numberFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }
    val percentFormat = remember { NumberFormat.getPercentInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }}
    
    val priceChange = coin.priceChangePercentage24h.toDouble() / 100.0
    val priceChangeColor = if (priceChange >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Coin Image
            AsyncImage(
                model = coin.image,
                contentDescription = coin.name,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Coin Info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = coin.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "#${coin.marketCapRank}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = coin.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Price Info
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = numberFormat.format(coin.currentPrice.toDouble()),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = percentFormat.format(priceChange),
                    style = MaterialTheme.typography.bodyMedium,
                    color = priceChangeColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview(
    @PreviewParameter(DashboardStateProvider::class) state: DashboardState
) {
    MaterialTheme {
        DashboardScreen(
            state = state,
            onCoinClick = {},
            onFavoritesClick = {},
            onSearchQueryChange = {},
            onClearSearch = {},
            onRefresh = {},
        )
    }
}

private class DashboardStateProvider : PreviewParameterProvider<DashboardState> {
    override val values = sequenceOf(
        DashboardState(
            coins = sampleCoins,
            filteredCoins = sampleCoins,
            isLoading = false
        ),
        DashboardState(
            coins = emptyList(),
            filteredCoins = emptyList(),
            isLoading = true
        ),
        DashboardState(
            coins = emptyList(),
            filteredCoins = emptyList(),
            isLoading = false,
            error = "Network Error"
        )
    )
}

private val sampleCoins = listOf(
    Coin(
        id = "bitcoin",
        symbol = "BTC",
        name = "Bitcoin",
        image = "",
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
        athDate = "2021-11-10",
        atl = BigDecimal("67.00"),
        atlDate = "2013-07-05",
        lastUpdated = "2023-01-01T12:00:00Z"
    ),
    Coin(
        id = "ethereum",
        symbol = "ETH",
        name = "Ethereum",
        image = "",
        currentPrice = BigDecimal("3000.00"),
        marketCap = BigDecimal("500000000"),
        marketCapRank = 2,
        priceChangePercentage24h = BigDecimal("-1.2"),
        priceChangePercentage7d = BigDecimal("2.0"),
        priceChangePercentage30d = BigDecimal("5.0"),
        high24h = BigDecimal("3100.00"),
        low24h = BigDecimal("2900.00"),
        circulatingSupply = BigDecimal("120000000"),
        totalSupply = BigDecimal("120000000"),
        maxSupply = BigDecimal("120000000"),
        ath = BigDecimal("4800.00"),
        athDate = "2021-11-10",
        atl = BigDecimal("0.42"),
        atlDate = "2015-10-21",
        lastUpdated = "2023-01-01T12:00:00Z"
    )
)
