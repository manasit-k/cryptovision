package com.cryptovision.domain.usecase

import app.cash.turbine.test
import com.cryptovision.domain.repository.CoinRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CheckCoinFavoriteUseCaseTest {

    private lateinit var repository: CoinRepository
    private lateinit var useCase: CheckCoinFavoriteUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        useCase = CheckCoinFavoriteUseCase(repository)
    }

    @Test
    fun `when coin is favorite, should emit true`() = runTest {
        // Given
        every { repository.isFavorite("bitcoin") } returns flowOf(true)

        // When
        val result = useCase.invoke("bitcoin")

        // Then
        result.test {
            assertTrue(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `when coin is not favorite, should emit false`() = runTest {
        // Given
        every { repository.isFavorite("ethereum") } returns flowOf(false)

        // When
        val result = useCase.invoke("ethereum")

        // Then
        result.test {
            assertFalse(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `when favorite status changes, should emit new values`() = runTest {
        // Given
        every { repository.isFavorite("solana") } returns flowOf(false, true, false)

        // When
        val result = useCase.invoke("solana")

        // Then
        result.test {
            assertFalse(awaitItem())
            assertTrue(awaitItem())
            assertFalse(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `should pass correct coinId to repository`() = runTest {
        // Given
        every { repository.isFavorite("cardano") } returns flowOf(true)

        // When
        val result = useCase.invoke("cardano")

        // Then
        result.test {
            awaitItem()
            awaitComplete()
        }
        verify { repository.isFavorite("cardano") }
    }
}
