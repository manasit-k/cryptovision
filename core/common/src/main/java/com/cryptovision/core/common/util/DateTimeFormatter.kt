package com.cryptovision.core.common.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility object for date and time formatting.
 */
object DateTimeFormatter {
    
    private val dateTimeFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US)
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
    
    /**
     * Format ISO 8601 date string to readable format.
     */
    fun formatDateTime(isoDate: String): String {
        return try {
            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).parse(isoDate)
            date?.let { dateTimeFormat.format(it) } ?: isoDate
        } catch (e: Exception) {
            isoDate
        }
    }
    
    /**
     * Format timestamp to readable date.
     */
    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }
    
    /**
     * Format timestamp to time only.
     */
    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }
    
    /**
     * Get relative time string (e.g., "2 hours ago").
     */
    fun getRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60_000 -> "Just now"
            diff < 3600_000 -> "${diff / 60_000} minutes ago"
            diff < 86400_000 -> "${diff / 3600_000} hours ago"
            diff < 604800_000 -> "${diff / 86400_000} days ago"
            else -> formatDate(timestamp)
        }
    }
}
