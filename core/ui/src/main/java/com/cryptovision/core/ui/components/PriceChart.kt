package com.cryptovision.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PricePoint(
    val timestamp: Long,
    val price: BigDecimal
)

@Composable
fun PriceChart(
    prices: List<PricePoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    gradientColors: List<Color> = listOf(
        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        MaterialTheme.colorScheme.primary.copy(alpha = 0.0f)
    ),
    showGrid: Boolean = true,
    onPointSelected: ((PricePoint?) -> Unit)? = null
) {
    if (prices.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No data available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val minPrice = prices.minOf { it.price.toDouble() }
    val maxPrice = prices.maxOf { it.price.toDouble() }
    val priceRange = maxPrice - minPrice

    var selectedPoint by remember { mutableStateOf<PricePoint?>(null) }

    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val numberFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }
    val dateFormat = remember { SimpleDateFormat("MMM dd", Locale.US) }

    Column(modifier = modifier) {
        // Selected point info
        selectedPoint?.let { point ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateFormat.format(Date(point.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = numberFormat.format(point.price.toDouble()),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .pointerInput(prices) {
                    detectTapGestures { offset ->
                        val pointWidth = size.width.toFloat() / (prices.size - 1)
                        val index = (offset.x / pointWidth).toInt().coerceIn(0, prices.size - 1)
                        selectedPoint = prices[index]
                        onPointSelected?.invoke(prices[index])
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val padding = 16.dp.toPx()

            val chartWidth = width - padding * 2
            val chartHeight = height - padding * 2

            // Draw grid lines
            if (showGrid) {
                val gridLines = 4
                for (i in 0..gridLines) {
                    val y = padding + (chartHeight / gridLines) * i
                    drawLine(
                        color = gridColor,
                        start = Offset(padding, y),
                        end = Offset(width - padding, y),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                    )
                }
            }

            // Calculate points
            val points = prices.mapIndexed { index, pricePoint ->
                val x = padding + (chartWidth / (prices.size - 1)) * index
                val normalizedPrice = if (priceRange > 0) {
                    (pricePoint.price.toDouble() - minPrice) / priceRange
                } else {
                    0.5
                }
                val y = padding + chartHeight - (chartHeight * normalizedPrice.toFloat())
                Offset(x, y)
            }

            // Draw gradient fill
            if (points.isNotEmpty()) {
                val fillPath = Path().apply {
                    moveTo(points.first().x, height - padding)
                    lineTo(points.first().x, points.first().y)

                    for (i in 1 until points.size) {
                        val prev = points[i - 1]
                        val current = points[i]

                        // Smooth curve using cubic bezier
                        val controlX1 = prev.x + (current.x - prev.x) / 2
                        val controlX2 = prev.x + (current.x - prev.x) / 2
                        cubicTo(controlX1, prev.y, controlX2, current.y, current.x, current.y)
                    }

                    lineTo(points.last().x, height - padding)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(gradientColors)
                )
            }

            // Draw line
            if (points.size >= 2) {
                val linePath = Path().apply {
                    moveTo(points.first().x, points.first().y)

                    for (i in 1 until points.size) {
                        val prev = points[i - 1]
                        val current = points[i]

                        // Smooth curve using cubic bezier
                        val controlX1 = prev.x + (current.x - prev.x) / 2
                        val controlX2 = prev.x + (current.x - prev.x) / 2
                        cubicTo(controlX1, prev.y, controlX2, current.y, current.x, current.y)
                    }
                }

                drawPath(
                    path = linePath,
                    color = lineColor,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )
            }

            // Draw selected point indicator
            selectedPoint?.let { selected ->
                val index = prices.indexOf(selected)
                if (index >= 0 && index < points.size) {
                    val point = points[index]

                    // Vertical line
                    drawLine(
                        color = lineColor.copy(alpha = 0.5f),
                        start = Offset(point.x, padding),
                        end = Offset(point.x, height - padding),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
                    )

                    // Point circle
                    drawCircle(
                        color = lineColor,
                        radius = 6.dp.toPx(),
                        center = point
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3.dp.toPx(),
                        center = point
                    )
                }
            }
        }

        // Price range labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = numberFormat.format(minPrice),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = numberFormat.format(maxPrice),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PriceChartPreview() {
    val samplePrices = listOf(
        PricePoint(1609459200000, BigDecimal("29000.00")),
        PricePoint(1609545600000, BigDecimal("32000.00")),
        PricePoint(1609632000000, BigDecimal("31000.00")),
        PricePoint(1609718400000, BigDecimal("34000.00")),
        PricePoint(1609804800000, BigDecimal("33500.00")),
        PricePoint(1609891200000, BigDecimal("42000.00")),
        PricePoint(1609977600000, BigDecimal("40000.00")),
        PricePoint(1610064000000, BigDecimal("45000.00")),
        PricePoint(1610150400000, BigDecimal("50000.00"))
    )

    MaterialTheme {
        PriceChart(
            prices = samplePrices,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}
