package com.cryptovision.core.common.util

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

/**
 * Utility object for formatting currency values.
 */
object CurrencyFormatter {
    
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }
    
    /**
     * Format a BigDecimal as currency (e.g., $1,234.56).
     */
    fun formatCurrency(value: BigDecimal): String {
        return currencyFormatter.format(value)
    }
    
    /**
     * Format a BigDecimal as compact currency (e.g., $1.2M).
     * Custom implementation for API compatibility.
     */
    fun formatCompactCurrency(value: BigDecimal): String {
        val absValue = value.abs()
        return when {
            absValue >= BigDecimal("1000000000000") -> {
                "$${String.format("%.1fT", value.divide(BigDecimal("1000000000000")).toDouble())}"
            }
            absValue >= BigDecimal("1000000000") -> {
                "$${String.format("%.1fB", value.divide(BigDecimal("1000000000")).toDouble())}"
            }
            absValue >= BigDecimal("1000000") -> {
                "$${String.format("%.1fM", value.divide(BigDecimal("1000000")).toDouble())}"
            }
            absValue >= BigDecimal("1000") -> {
                "$${String.format("%.1fK", value.divide(BigDecimal("1000")).toDouble())}"
            }
            else -> formatCurrency(value)
        }
    }
    
    /**
     * Format a percentage change with sign and color indication.
     */
    fun formatPercentage(value: BigDecimal): String {
        val sign = if (value >= BigDecimal.ZERO) "+" else ""
        return "$sign${String.format("%.2f", value)}%"
    }
    
    /**
     * Check if a percentage represents a positive change.
     */
    fun isPositiveChange(value: BigDecimal): Boolean {
        return value >= BigDecimal.ZERO
    }
}
