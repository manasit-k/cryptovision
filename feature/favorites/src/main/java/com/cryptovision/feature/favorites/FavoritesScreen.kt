package com.cryptovision.feature.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.cryptovision.core.ui.components.CoinListShimmer
import com.cryptovision.core.ui.components.EmptyState
import com.cryptovision.core.ui.components.ErrorState
import com.cryptovision.core.ui.components.LoadingIndicator
import com.cryptovision.domain.model.Coin
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

/**
 * Favorites screen showing user's favorite cryptocurrencies.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onNavigateBack: () -> Unit,
    onCoinClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading && state.favoriteCoins.isEmpty() -> {
                    CoinListShimmer(itemCount = 5)
                }
                state.error != null && state.favoriteCoins.isEmpty() -> {
                    ErrorState(
                        message = state.error ?: "Unknown error",
                        onRetry = { viewModel.onIntent(FavoritesIntent.Refresh) }
                    )
                }
                state.favoriteCoins.isEmpty() -> {
                    EmptyState(
                        message = "No favorite coins yet.\nAdd some from the dashboard!"
                    )
                }
                else -> {
                    FavoritesList(
                        coins = state.favoriteCoins,
                        onCoinClick = onCoinClick,
                        onRemoveFavorite = { coinId ->
                            viewModel.onIntent(FavoritesIntent.RemoveFavorite(coinId))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoritesList(
    coins: List<Coin>,
    onCoinClick: (String) -> Unit,
    onRemoveFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            items = coins,
            key = { it.id }
        ) { coin ->
            FavoriteListItem(
                coin = coin,
                onClick = { onCoinClick(coin.id) },
                onRemove = { onRemoveFavorite(coin.id) }
            )
            HorizontalDivider()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteListItem(
    coin: Coin,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val numberFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }
    val percentFormat = remember {
        NumberFormat.getPercentInstance(Locale.US).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
    }

    val priceChange = coin.priceChangePercentage24h.toDouble() / 100.0
    val priceChangeColor = if (priceChange >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Remove Favorite") },
            text = { Text("Remove ${coin.name} from favorites?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRemove()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

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

            Spacer(modifier = Modifier.width(8.dp))

            // Delete Button
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove from favorites",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// region Preview & Testing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoritesScreenContent(
    state: FavoritesState,
    onNavigateBack: () -> Unit,
    onCoinClick: (String) -> Unit,
    onRemoveFavorite: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading && state.favoriteCoins.isEmpty() -> {
                    LoadingIndicator()
                }
                state.error != null && state.favoriteCoins.isEmpty() -> {
                    ErrorState(
                        message = state.error ?: "Unknown error",
                        onRetry = onRetry
                    )
                }
                state.favoriteCoins.isEmpty() -> {
                    EmptyState(
                        message = "No favorite coins yet.\nAdd some from the dashboard!"
                    )
                }
                else -> {
                    FavoritesList(
                        coins = state.favoriteCoins,
                        onCoinClick = onCoinClick,
                        onRemoveFavorite = onRemoveFavorite
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Favorites - With Data")
@Composable
private fun FavoritesScreenWithDataPreview() {
    MaterialTheme {
        FavoritesScreenContent(
            state = FavoritesState(
                favoriteCoins = sampleFavoriteCoins,
                isLoading = false
            ),
            onNavigateBack = {},
            onCoinClick = {},
            onRemoveFavorite = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, name = "Favorites - Empty")
@Composable
private fun FavoritesScreenEmptyPreview() {
    MaterialTheme {
        FavoritesScreenContent(
            state = FavoritesState(
                favoriteCoins = emptyList(),
                isLoading = false
            ),
            onNavigateBack = {},
            onCoinClick = {},
            onRemoveFavorite = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, name = "Favorites - Loading")
@Composable
private fun FavoritesScreenLoadingPreview() {
    MaterialTheme {
        FavoritesScreenContent(
            state = FavoritesState(
                favoriteCoins = emptyList(),
                isLoading = true
            ),
            onNavigateBack = {},
            onCoinClick = {},
            onRemoveFavorite = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, name = "Favorites - Error")
@Composable
private fun FavoritesScreenErrorPreview() {
    MaterialTheme {
        FavoritesScreenContent(
            state = FavoritesState(
                favoriteCoins = emptyList(),
                isLoading = false,
                error = "Failed to load favorites. Please try again."
            ),
            onNavigateBack = {},
            onCoinClick = {},
            onRemoveFavorite = {},
            onRetry = {}
        )
    }
}

private val sampleFavoriteCoins = listOf(
    Coin(
        id = "bitcoin",
        symbol = "BTC",
        name = "Bitcoin",
        image = "",
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
        marketCap = BigDecimal("500000000000"),
        marketCapRank = 2,
        priceChangePercentage24h = BigDecimal("-1.2"),
        priceChangePercentage7d = BigDecimal("2.0"),
        priceChangePercentage30d = BigDecimal("5.0"),
        high24h = BigDecimal("3100.00"),
        low24h = BigDecimal("2900.00"),
        circulatingSupply = BigDecimal("120000000"),
        totalSupply = BigDecimal("120000000"),
        maxSupply = null,
        ath = BigDecimal("4800.00"),
        athDate = "2021-11-10",
        atl = BigDecimal("0.42"),
        atlDate = "2015-10-21",
        lastUpdated = "2023-01-01T12:00:00Z"
    ),
    Coin(
        id = "solana",
        symbol = "SOL",
        name = "Solana",
        image = "",
        currentPrice = BigDecimal("120.00"),
        marketCap = BigDecimal("50000000000"),
        marketCapRank = 5,
        priceChangePercentage24h = BigDecimal("5.8"),
        priceChangePercentage7d = BigDecimal("12.0"),
        priceChangePercentage30d = BigDecimal("25.0"),
        high24h = BigDecimal("125.00"),
        low24h = BigDecimal("115.00"),
        circulatingSupply = BigDecimal("400000000"),
        totalSupply = BigDecimal("500000000"),
        maxSupply = null,
        ath = BigDecimal("260.00"),
        athDate = "2021-11-06",
        atl = BigDecimal("0.50"),
        atlDate = "2020-05-11",
        lastUpdated = "2023-01-01T12:00:00Z"
    )
)

// endregion
