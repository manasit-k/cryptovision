package com.cryptovision.core.common.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Unit tests for DateTimeFormatter utility.
 */
class DateTimeFormatterTest {

    @Nested
    @DisplayName("formatDateTime()")
    inner class FormatDateTimeTests {

        @Test
        fun `should format ISO 8601 date to readable format`() {
            // Given
            val isoDate = "2024-01-15T14:30:45.123Z"

            // When
            val result = DateTimeFormatter.formatDateTime(isoDate)

            // Then
            assertTrue(result.contains("Jan"))
            assertTrue(result.contains("15"))
            assertTrue(result.contains("2024"))
        }

        @Test
        fun `should return original string for invalid date format`() {
            // Given
            val invalidDate = "invalid-date-format"

            // When
            val result = DateTimeFormatter.formatDateTime(invalidDate)

            // Then
            assertEquals("invalid-date-format", result)
        }

        @Test
        fun `should handle empty string`() {
            // Given
            val emptyDate = ""

            // When
            val result = DateTimeFormatter.formatDateTime(emptyDate)

            // Then
            assertEquals("", result)
        }

        @Test
        fun `should format date with different time`() {
            // Given
            val isoDate = "2023-12-25T08:00:00.000Z"

            // When
            val result = DateTimeFormatter.formatDateTime(isoDate)

            // Then
            assertTrue(result.contains("Dec"))
            assertTrue(result.contains("25"))
            assertTrue(result.contains("2023"))
        }
    }

    @Nested
    @DisplayName("formatDate()")
    inner class FormatDateTests {

        @Test
        fun `should format timestamp to readable date`() {
            // Given - Jan 15, 2024 00:00:00 UTC
            val timestamp = 1705276800000L

            // When
            val result = DateTimeFormatter.formatDate(timestamp)

            // Then
            assertTrue(result.contains("Jan"))
            assertTrue(result.contains("2024"))
        }

        @Test
        fun `should format current timestamp`() {
            // Given
            val timestamp = System.currentTimeMillis()
            val expectedFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
            val expected = expectedFormat.format(Date(timestamp))

            // When
            val result = DateTimeFormatter.formatDate(timestamp)

            // Then
            assertEquals(expected, result)
        }

        @Test
        fun `should handle epoch timestamp`() {
            // Given - Jan 1, 1970
            val timestamp = 0L

            // When
            val result = DateTimeFormatter.formatDate(timestamp)

            // Then
            assertTrue(result.contains("1970"))
        }
    }

    @Nested
    @DisplayName("formatTime()")
    inner class FormatTimeTests {

        @Test
        fun `should format timestamp to time only`() {
            // Given
            val timestamp = System.currentTimeMillis()
            val expectedFormat = SimpleDateFormat("HH:mm", Locale.US)
            val expected = expectedFormat.format(Date(timestamp))

            // When
            val result = DateTimeFormatter.formatTime(timestamp)

            // Then
            assertEquals(expected, result)
        }

        @Test
        fun `should return time in HH:mm format`() {
            // When
            val result = DateTimeFormatter.formatTime(System.currentTimeMillis())

            // Then
            assertTrue(result.matches(Regex("\\d{2}:\\d{2}")))
        }
    }

    @Nested
    @DisplayName("getRelativeTime()")
    inner class GetRelativeTimeTests {

        @Test
        fun `should return Just now for very recent timestamp`() {
            // Given
            val timestamp = System.currentTimeMillis() - 30_000 // 30 seconds ago

            // When
            val result = DateTimeFormatter.getRelativeTime(timestamp)

            // Then
            assertEquals("Just now", result)
        }

        @Test
        fun `should return minutes ago for timestamp within hour`() {
            // Given
            val timestamp = System.currentTimeMillis() - 300_000 // 5 minutes ago

            // When
            val result = DateTimeFormatter.getRelativeTime(timestamp)

            // Then
            assertEquals("5 minutes ago", result)
        }

        @Test
        fun `should return hours ago for timestamp within day`() {
            // Given
            val timestamp = System.currentTimeMillis() - 7200_000 // 2 hours ago

            // When
            val result = DateTimeFormatter.getRelativeTime(timestamp)

            // Then
            assertEquals("2 hours ago", result)
        }

        @Test
        fun `should return days ago for timestamp within week`() {
            // Given
            val timestamp = System.currentTimeMillis() - 259200_000 // 3 days ago

            // When
            val result = DateTimeFormatter.getRelativeTime(timestamp)

            // Then
            assertEquals("3 days ago", result)
        }

        @Test
        fun `should return formatted date for timestamp older than week`() {
            // Given
            val timestamp = System.currentTimeMillis() - 1209600_000 // 14 days ago

            // When
            val result = DateTimeFormatter.getRelativeTime(timestamp)

            // Then
            // Should return formatted date instead of relative time
            assertTrue(result.matches(Regex("[A-Z][a-z]{2} \\d{2}, \\d{4}")))
        }

        @Test
        fun `should return 1 minutes ago for timestamp just over minute`() {
            // Given
            val timestamp = System.currentTimeMillis() - 61_000 // 61 seconds ago

            // When
            val result = DateTimeFormatter.getRelativeTime(timestamp)

            // Then
            assertEquals("1 minutes ago", result)
        }

        @Test
        fun `should return 1 hours ago for timestamp just over hour`() {
            // Given
            val timestamp = System.currentTimeMillis() - 3660_000 // 61 minutes ago

            // When
            val result = DateTimeFormatter.getRelativeTime(timestamp)

            // Then
            assertEquals("1 hours ago", result)
        }

        @Test
        fun `should return 1 days ago for timestamp just over day`() {
            // Given
            val timestamp = System.currentTimeMillis() - 90000_000 // 25 hours ago

            // When
            val result = DateTimeFormatter.getRelativeTime(timestamp)

            // Then
            assertEquals("1 days ago", result)
        }
    }
}
