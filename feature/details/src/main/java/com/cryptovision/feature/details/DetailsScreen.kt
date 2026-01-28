package com.cryptovision.feature.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.cryptovision.core.ui.components.CoinDetailShimmer
import com.cryptovision.core.ui.components.ErrorState
import com.cryptovision.core.ui.components.LoadingIndicator
import com.cryptovision.core.ui.components.PriceChart
import com.cryptovision.core.ui.components.PricePoint
import com.cryptovision.domain.model.Coin
import com.cryptovision.domain.model.PriceHistory
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

/**
 * Details screen showing detailed cryptocurrency information.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.coin?.name ?: "Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onIntent(DetailsIntent.ToggleFavorite) }) {
                        Icon(
                            imageVector = if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (state.isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (state.isFavorite) Color.Red else LocalContentColor.current
                        )
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
                state.isLoadingDetails && state.coin == null -> {
                    CoinDetailShimmer()
                }
                state.error != null && state.coin == null -> {
                    ErrorState(
                        message = state.error ?: "Unknown error",
                        onRetry = { viewModel.onIntent(DetailsIntent.LoadDetails) }
                    )
                }
                state.coin != null -> {
                    DetailsContent(
                        coin = state.coin!!,
                        priceHistory = state.priceHistory,
                        selectedTimeRange = state.selectedTimeRange,
                        isLoadingChart = state.isLoadingChart,
                        onTimeRangeSelected = { days ->
                            viewModel.onIntent(DetailsIntent.SelectTimeRange(days))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailsContent(
    coin: Coin,
    priceHistory: PriceHistory?,
    selectedTimeRange: Int,
    isLoadingChart: Boolean,
    onTimeRangeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val numberFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }
    val percentFormat = remember {
        NumberFormat.getPercentInstance(Locale.US).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Coin Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = coin.image,
                contentDescription = coin.name,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = coin.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = coin.symbol.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Current Price
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Current Price",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = numberFormat.format(coin.currentPrice.toDouble()),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                val priceChange = coin.priceChangePercentage24h.toDouble() / 100.0
                val priceChangeColor = if (priceChange >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                
                Text(
                    text = "${if (priceChange >= 0) "+" else ""}${percentFormat.format(priceChange)} (24h)",
                    style = MaterialTheme.typography.titleMedium,
                    color = priceChangeColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Price Chart
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Price Chart",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Time Range Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TimeRangeChip("7D", 7, selectedTimeRange, onTimeRangeSelected)
                    TimeRangeChip("30D", 30, selectedTimeRange, onTimeRangeSelected)
                    TimeRangeChip("90D", 90, selectedTimeRange, onTimeRangeSelected)
                    TimeRangeChip("1Y", 365, selectedTimeRange, onTimeRangeSelected)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Chart Placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isLoadingChart -> {
                            CircularProgressIndicator()
                        }
                        priceHistory != null -> {
                            SimplePriceChart(priceHistory = priceHistory)
                        }
                        else -> {
                            Text("No chart data available")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Market Stats
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Market Stats",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                StatRow("Market Cap", numberFormat.format(coin.marketCap.toDouble()))
                StatRow("Market Cap Rank", "#${coin.marketCapRank}")
                
                coin.high24h?.let {
                    StatRow("24h High", numberFormat.format(it.toDouble()))
                }
                
                coin.low24h?.let {
                    StatRow("24h Low", numberFormat.format(it.toDouble()))
                }
                
                coin.circulatingSupply?.let {
                    StatRow("Circulating Supply", "${it.toPlainString()} ${coin.symbol.uppercase()}")
                }
                
                coin.maxSupply?.let {
                    StatRow("Max Supply", "${it.toPlainString()} ${coin.symbol.uppercase()}")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // All-Time Stats
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "All-Time Stats",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                coin.ath?.let {
                    StatRow("All-Time High", numberFormat.format(it.toDouble()))
                }
                
                coin.athDate?.let {
                    StatRow("ATH Date", it.take(10))
                }
                
                coin.atl?.let {
                    StatRow("All-Time Low", numberFormat.format(it.toDouble()))
                }
                
                coin.atlDate?.let {
                    StatRow("ATL Date", it.take(10))
                }
            }
        }
    }
}

@Composable
private fun TimeRangeChip(
    label: String,
    days: Int,
    selectedDays: Int,
    onSelected: (Int) -> Unit
) {
    FilterChip(
        selected = days == selectedDays,
        onClick = { onSelected(days) },
        label = { Text(label) }
    )
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SimplePriceChart(
    priceHistory: PriceHistory,
    modifier: Modifier = Modifier
) {
    val priceChange = if (priceHistory.prices.size >= 2) {
        val first = priceHistory.prices.first().price.toDouble()
        val last = priceHistory.prices.last().price.toDouble()
        last - first
    } else 0.0

    val lineColor = if (priceChange >= 0) {
        Color(0xFF4CAF50) // Green
    } else {
        Color(0xFFF44336) // Red
    }

    val gradientColors = if (priceChange >= 0) {
        listOf(
            Color(0xFF4CAF50).copy(alpha = 0.3f),
            Color(0xFF4CAF50).copy(alpha = 0.0f)
        )
    } else {
        listOf(
            Color(0xFFF44336).copy(alpha = 0.3f),
            Color(0xFFF44336).copy(alpha = 0.0f)
        )
    }

    PriceChart(
        prices = priceHistory.prices.map { point ->
            PricePoint(
                timestamp = point.timestamp,
                price = point.price
            )
        },
        lineColor = lineColor,
        gradientColors = gradientColors,
        modifier = modifier
    )
}

// region Preview & Testing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DetailsScreenContent(
    state: DetailsState,
    onNavigateBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onTimeRangeSelected: (Int) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.coin?.name ?: "Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (state.isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (state.isFavorite) Color.Red else LocalContentColor.current
                        )
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
                state.isLoadingDetails && state.coin == null -> {
                    LoadingIndicator()
                }
                state.error != null && state.coin == null -> {
                    ErrorState(
                        message = state.error ?: "Unknown error",
                        onRetry = onRetry
                    )
                }
                state.coin != null -> {
                    DetailsContent(
                        coin = state.coin!!,
                        priceHistory = state.priceHistory,
                        selectedTimeRange = state.selectedTimeRange,
                        isLoadingChart = state.isLoadingChart,
                        onTimeRangeSelected = onTimeRangeSelected
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Details - With Data")
@Composable
private fun DetailsScreenWithDataPreview() {
    MaterialTheme {
        DetailsScreenContent(
            state = DetailsState(
                coin = sampleCoin,
                priceHistory = samplePriceHistory,
                isFavorite = true,
                isLoadingDetails = false,
                isLoadingChart = false,
                selectedTimeRange = 7
            ),
            onNavigateBack = {},
            onToggleFavorite = {},
            onTimeRangeSelected = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, name = "Details - Loading")
@Composable
private fun DetailsScreenLoadingPreview() {
    MaterialTheme {
        DetailsScreenContent(
            state = DetailsState(
                coin = null,
                isLoadingDetails = true
            ),
            onNavigateBack = {},
            onToggleFavorite = {},
            onTimeRangeSelected = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, name = "Details - Error")
@Composable
private fun DetailsScreenErrorPreview() {
    MaterialTheme {
        DetailsScreenContent(
            state = DetailsState(
                coin = null,
                isLoadingDetails = false,
                error = "Failed to load coin details. Please check your connection."
            ),
            onNavigateBack = {},
            onToggleFavorite = {},
            onTimeRangeSelected = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, name = "Details - Not Favorite")
@Composable
private fun DetailsScreenNotFavoritePreview() {
    MaterialTheme {
        DetailsScreenContent(
            state = DetailsState(
                coin = sampleCoin,
                priceHistory = samplePriceHistory,
                isFavorite = false,
                isLoadingDetails = false,
                isLoadingChart = false,
                selectedTimeRange = 30
            ),
            onNavigateBack = {},
            onToggleFavorite = {},
            onTimeRangeSelected = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, name = "Details - Chart Loading")
@Composable
private fun DetailsScreenChartLoadingPreview() {
    MaterialTheme {
        DetailsScreenContent(
            state = DetailsState(
                coin = sampleCoin,
                priceHistory = null,
                isFavorite = true,
                isLoadingDetails = false,
                isLoadingChart = true,
                selectedTimeRange = 7
            ),
            onNavigateBack = {},
            onToggleFavorite = {},
            onTimeRangeSelected = {},
            onRetry = {}
        )
    }
}

private val sampleCoin = Coin(
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
)

private val samplePriceHistory = PriceHistory(
    coinId = "bitcoin",
    prices = listOf(
        PriceHistory.PricePoint(1609459200000, BigDecimal("29000.00")),
        PriceHistory.PricePoint(1609545600000, BigDecimal("32000.00")),
        PriceHistory.PricePoint(1609632000000, BigDecimal("33000.00")),
        PriceHistory.PricePoint(1609718400000, BigDecimal("34000.00")),
        PriceHistory.PricePoint(1609804800000, BigDecimal("40000.00")),
        PriceHistory.PricePoint(1609891200000, BigDecimal("42000.00")),
        PriceHistory.PricePoint(1609977600000, BigDecimal("50000.00"))
    )
)

// endregion
