package com.cryptovision.domain.usecase

import com.cryptovision.domain.model.Result
import com.cryptovision.domain.repository.CoinRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ToggleFavoriteUseCaseTest {

    private lateinit var repository: CoinRepository
    private lateinit var useCase: ToggleFavoriteUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        useCase = ToggleFavoriteUseCase(repository)
    }

    @Test
    fun `when toggle favorite succeeds, should return success`() = runTest {
        // Given
        coEvery { repository.toggleFavorite("bitcoin") } returns Result.Success(Unit)

        // When
        val result = useCase.invoke("bitcoin")

        // Then
        assertTrue(result is Result.Success)
        coVerify { repository.toggleFavorite("bitcoin") }
    }

    @Test
    fun `when toggle favorite fails, should return error`() = runTest {
        // Given
        val expectedException = Exception("Database error")
        coEvery { repository.toggleFavorite("bitcoin") } returns Result.Error(expectedException)

        // When
        val result = useCase.invoke("bitcoin")

        // Then
        assertTrue(result is Result.Error)
        assertEquals(expectedException, (result as Result.Error).exception)
    }

    @Test
    fun `should pass correct coinId to repository`() = runTest {
        // Given
        coEvery { repository.toggleFavorite("ethereum") } returns Result.Success(Unit)

        // When
        useCase.invoke("ethereum")

        // Then
        coVerify { repository.toggleFavorite("ethereum") }
    }

    @Test
    fun `when called multiple times with same coinId, should call repository each time`() = runTest {
        // Given
        coEvery { repository.toggleFavorite("solana") } returns Result.Success(Unit)

        // When
        useCase.invoke("solana")
        useCase.invoke("solana")
        useCase.invoke("solana")

        // Then
        coVerify(exactly = 3) { repository.toggleFavorite("solana") }
    }
}
