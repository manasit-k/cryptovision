package com.cryptovision.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cryptovision.core.database.CryptoDatabase
import com.cryptovision.core.database.entity.FavoriteEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for FavoriteDao.
 * Tests run on Android device/emulator using in-memory database.
 */
@RunWith(AndroidJUnit4::class)
class FavoriteDaoTest {

    private lateinit var database: CryptoDatabase
    private lateinit var favoriteDao: FavoriteDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, CryptoDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        favoriteDao = database.favoriteDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertFavorite_and_getAllFavorites_returnsFavorites() = runTest {
        // Given
        val favorite = FavoriteEntity(coinId = "bitcoin", addedAt = 1000L)
        favoriteDao.insertFavorite(favorite)

        // When
        val result = favoriteDao.getAllFavorites().first()

        // Then
        assertEquals(1, result.size)
        assertEquals("bitcoin", result[0].coinId)
    }

    @Test
    fun getAllFavorites_returnsOrderedByAddedAtDesc() = runTest {
        // Given
        favoriteDao.insertFavorite(FavoriteEntity(coinId = "bitcoin", addedAt = 1000L))
        favoriteDao.insertFavorite(FavoriteEntity(coinId = "ethereum", addedAt = 3000L))
        favoriteDao.insertFavorite(FavoriteEntity(coinId = "cardano", addedAt = 2000L))

        // When
        val result = favoriteDao.getAllFavorites().first()

        // Then
        assertEquals(3, result.size)
        assertEquals("ethereum", result[0].coinId) // Most recent first
        assertEquals("cardano", result[1].coinId)
        assertEquals("bitcoin", result[2].coinId)
    }

    @Test
    fun isFavorite_existingFavorite_returnsTrue() = runTest {
        // Given
        favoriteDao.insertFavorite(FavoriteEntity(coinId = "bitcoin"))

        // When
        val result = favoriteDao.isFavorite("bitcoin").first()

        // Then
        assertTrue(result)
    }

    @Test
    fun isFavorite_nonExistingFavorite_returnsFalse() = runTest {
        // When
        val result = favoriteDao.isFavorite("bitcoin").first()

        // Then
        assertFalse(result)
    }

    @Test
    fun isFavoriteSync_existingFavorite_returnsTrue() = runTest {
        // Given
        favoriteDao.insertFavorite(FavoriteEntity(coinId = "bitcoin"))

        // When
        val result = favoriteDao.isFavoriteSync("bitcoin")

        // Then
        assertTrue(result)
    }

    @Test
    fun isFavoriteSync_nonExistingFavorite_returnsFalse() = runTest {
        // When
        val result = favoriteDao.isFavoriteSync("bitcoin")

        // Then
        assertFalse(result)
    }

    @Test
    fun deleteFavorite_removesFavorite() = runTest {
        // Given
        favoriteDao.insertFavorite(FavoriteEntity(coinId = "bitcoin"))
        favoriteDao.insertFavorite(FavoriteEntity(coinId = "ethereum"))

        // When
        favoriteDao.deleteFavorite("bitcoin")
        val result = favoriteDao.getAllFavorites().first()

        // Then
        assertEquals(1, result.size)
        assertEquals("ethereum", result[0].coinId)
    }

    @Test
    fun deleteFavorite_nonExisting_doesNotThrow() = runTest {
        // When/Then - Should not throw
        favoriteDao.deleteFavorite("nonexistent")
    }

    @Test
    fun deleteAllFavorites_removesAllFavorites() = runTest {
        // Given
        favoriteDao.insertFavorite(FavoriteEntity(coinId = "bitcoin"))
        favoriteDao.insertFavorite(FavoriteEntity(coinId = "ethereum"))

        // When
        favoriteDao.deleteAllFavorites()
        val result = favoriteDao.getAllFavorites().first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun insertFavorite_duplicateCoinId_replacesExisting() = runTest {
        // Given
        favoriteDao.insertFavorite(FavoriteEntity(coinId = "bitcoin", addedAt = 1000L))

        // When
        favoriteDao.insertFavorite(FavoriteEntity(coinId = "bitcoin", addedAt = 2000L))
        val result = favoriteDao.getAllFavorites().first()

        // Then
        assertEquals(1, result.size)
        assertEquals(2000L, result[0].addedAt)
    }

    @Test
    fun getAllFavorites_emptyDatabase_returnsEmptyList() = runTest {
        // When
        val result = favoriteDao.getAllFavorites().first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun isFavorite_afterDelete_returnsFalse() = runTest {
        // Given
        favoriteDao.insertFavorite(FavoriteEntity(coinId = "bitcoin"))
        assertTrue(favoriteDao.isFavoriteSync("bitcoin"))

        // When
        favoriteDao.deleteFavorite("bitcoin")

        // Then
        assertFalse(favoriteDao.isFavoriteSync("bitcoin"))
    }
}
