package com.cryptovision.core.common.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

/**
 * Unit tests for CurrencyFormatter utility.
 */
class CurrencyFormatterTest {

    @Nested
    @DisplayName("formatCurrency()")
    inner class FormatCurrencyTests {

        @Test
        fun `should format positive value with dollar sign and commas`() {
            // Given
            val value = BigDecimal("1234.56")

            // When
            val result = CurrencyFormatter.formatCurrency(value)

            // Then
            assertEquals("$1,234.56", result)
        }

        @Test
        fun `should format large value with proper comma separators`() {
            // Given
            val value = BigDecimal("1234567.89")

            // When
            val result = CurrencyFormatter.formatCurrency(value)

            // Then
            assertEquals("$1,234,567.89", result)
        }

        @Test
        fun `should format zero value`() {
            // Given
            val value = BigDecimal.ZERO

            // When
            val result = CurrencyFormatter.formatCurrency(value)

            // Then
            assertEquals("$0.00", result)
        }

        @Test
        fun `should format value less than one dollar`() {
            // Given
            val value = BigDecimal("0.99")

            // When
            val result = CurrencyFormatter.formatCurrency(value)

            // Then
            assertEquals("$0.99", result)
        }

        @Test
        fun `should format whole number with two decimal places`() {
            // Given
            val value = BigDecimal("100")

            // When
            val result = CurrencyFormatter.formatCurrency(value)

            // Then
            assertEquals("$100.00", result)
        }

        @Test
        fun `should format negative value`() {
            // Given
            val value = BigDecimal("-1234.56")

            // When
            val result = CurrencyFormatter.formatCurrency(value)

            // Then
            assertTrue(result.contains("1,234.56"))
        }
    }

    @Nested
    @DisplayName("formatCompactCurrency()")
    inner class FormatCompactCurrencyTests {

        @Test
        fun `should format trillions with T suffix`() {
            // Given
            val value = BigDecimal("1500000000000")

            // When
            val result = CurrencyFormatter.formatCompactCurrency(value)

            // Then
            assertEquals("$1.5T", result)
        }

        @Test
        fun `should format billions with B suffix`() {
            // Given
            val value = BigDecimal("2500000000")

            // When
            val result = CurrencyFormatter.formatCompactCurrency(value)

            // Then
            assertEquals("$2.5B", result)
        }

        @Test
        fun `should format millions with M suffix`() {
            // Given
            val value = BigDecimal("3500000")

            // When
            val result = CurrencyFormatter.formatCompactCurrency(value)

            // Then
            assertEquals("$3.5M", result)
        }

        @Test
        fun `should format thousands with K suffix`() {
            // Given
            val value = BigDecimal("4500")

            // When
            val result = CurrencyFormatter.formatCompactCurrency(value)

            // Then
            assertEquals("$4.5K", result)
        }

        @Test
        fun `should format values under 1000 as regular currency`() {
            // Given
            val value = BigDecimal("999.99")

            // When
            val result = CurrencyFormatter.formatCompactCurrency(value)

            // Then
            assertEquals("$999.99", result)
        }

        @Test
        fun `should format exactly 1 billion`() {
            // Given
            val value = BigDecimal("1000000000")

            // When
            val result = CurrencyFormatter.formatCompactCurrency(value)

            // Then
            assertEquals("$1.0B", result)
        }

        @Test
        fun `should format exactly 1 million`() {
            // Given
            val value = BigDecimal("1000000")

            // When
            val result = CurrencyFormatter.formatCompactCurrency(value)

            // Then
            assertEquals("$1.0M", result)
        }

        @Test
        fun `should format exactly 1 thousand`() {
            // Given
            val value = BigDecimal("1000")

            // When
            val result = CurrencyFormatter.formatCompactCurrency(value)

            // Then
            assertEquals("$1.0K", result)
        }

        @Test
        fun `should handle zero`() {
            // Given
            val value = BigDecimal.ZERO

            // When
            val result = CurrencyFormatter.formatCompactCurrency(value)

            // Then
            assertEquals("$0.00", result)
        }
    }

    @Nested
    @DisplayName("formatPercentage()")
    inner class FormatPercentageTests {

        @Test
        fun `should format positive percentage with plus sign`() {
            // Given
            val value = BigDecimal("5.25")

            // When
            val result = CurrencyFormatter.formatPercentage(value)

            // Then
            assertEquals("+5.25%", result)
        }

        @Test
        fun `should format negative percentage with minus sign`() {
            // Given
            val value = BigDecimal("-3.50")

            // When
            val result = CurrencyFormatter.formatPercentage(value)

            // Then
            assertEquals("-3.50%", result)
        }

        @Test
        fun `should format zero percentage with plus sign`() {
            // Given
            val value = BigDecimal.ZERO

            // When
            val result = CurrencyFormatter.formatPercentage(value)

            // Then
            assertEquals("+0.00%", result)
        }

        @Test
        fun `should format large positive percentage`() {
            // Given
            val value = BigDecimal("150.75")

            // When
            val result = CurrencyFormatter.formatPercentage(value)

            // Then
            assertEquals("+150.75%", result)
        }

        @Test
        fun `should format small decimal percentage`() {
            // Given
            val value = BigDecimal("0.01")

            // When
            val result = CurrencyFormatter.formatPercentage(value)

            // Then
            assertEquals("+0.01%", result)
        }
    }

    @Nested
    @DisplayName("isPositiveChange()")
    inner class IsPositiveChangeTests {

        @Test
        fun `should return true for positive value`() {
            // Given
            val value = BigDecimal("5.25")

            // When
            val result = CurrencyFormatter.isPositiveChange(value)

            // Then
            assertTrue(result)
        }

        @Test
        fun `should return false for negative value`() {
            // Given
            val value = BigDecimal("-3.50")

            // When
            val result = CurrencyFormatter.isPositiveChange(value)

            // Then
            assertFalse(result)
        }

        @Test
        fun `should return true for zero value`() {
            // Given
            val value = BigDecimal.ZERO

            // When
            val result = CurrencyFormatter.isPositiveChange(value)

            // Then
            assertTrue(result)
        }

        @Test
        fun `should return true for very small positive value`() {
            // Given
            val value = BigDecimal("0.0001")

            // When
            val result = CurrencyFormatter.isPositiveChange(value)

            // Then
            assertTrue(result)
        }

        @Test
        fun `should return false for very small negative value`() {
            // Given
            val value = BigDecimal("-0.0001")

            // When
            val result = CurrencyFormatter.isPositiveChange(value)

            // Then
            assertFalse(result)
        }
    }
}
