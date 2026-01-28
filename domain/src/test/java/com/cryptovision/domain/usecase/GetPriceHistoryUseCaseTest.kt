package com.cryptovision.domain.usecase

import app.cash.turbine.test
import com.cryptovision.domain.model.PriceHistory
import com.cryptovision.domain.model.Result
import com.cryptovision.domain.repository.CoinRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class GetPriceHistoryUseCaseTest {

    private lateinit var repository: CoinRepository
    private lateinit var useCase: GetPriceHistoryUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        useCase = GetPriceHistoryUseCase(repository)
    }

    @Test
    fun `when repository returns success, use case should emit price history`() = runTest {
        // Given
        val expectedHistory = createTestPriceHistory("bitcoin")
        coEvery { repository.getPriceHistory("bitcoin", 7) } returns flowOf(Result.Success(expectedHistory))

        // When
        val result = useCase.invoke("bitcoin", 7)

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission is Result.Success)
            assertEquals(expectedHistory, (emission as Result.Success).data)
            awaitComplete()
        }
    }

    @Test
    fun `when repository returns error, use case should emit error`() = runTest {
        // Given
        val expectedException = Exception("Network error")
        coEvery { repository.getPriceHistory("bitcoin", 30) } returns flowOf(Result.Error(expectedException))

        // When
        val result = useCase.invoke("bitcoin", 30)

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission is Result.Error)
            assertEquals(expectedException, (emission as Result.Error).exception)
            awaitComplete()
        }
    }

    @Test
    fun `when days not specified, should use default value of 7`() = runTest {
        // Given
        val expectedHistory = createTestPriceHistory("ethereum")
        coEvery { repository.getPriceHistory("ethereum", 7) } returns flowOf(Result.Success(expectedHistory))

        // When
        val result = useCase.invoke("ethereum")

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission is Result.Success)
            awaitComplete()
        }
        verify { repository.getPriceHistory("ethereum", 7) }
    }

    @Test
    fun `when repository emits loading then success, use case should emit both`() = runTest {
        // Given
        val expectedHistory = createTestPriceHistory("bitcoin")
        coEvery { repository.getPriceHistory("bitcoin", 90) } returns flowOf(
            Result.Loading,
            Result.Success(expectedHistory)
        )

        // When
        val result = useCase.invoke("bitcoin", 90)

        // Then
        result.test {
            assertTrue(awaitItem() is Result.Loading)
            val successEmission = awaitItem()
            assertTrue(successEmission is Result.Success)
            assertEquals(expectedHistory, (successEmission as Result.Success).data)
            awaitComplete()
        }
    }

    @Test
    fun `should pass correct days parameter to repository`() = runTest {
        // Given
        val expectedHistory = createTestPriceHistory("bitcoin")
        coEvery { repository.getPriceHistory("bitcoin", 365) } returns flowOf(Result.Success(expectedHistory))

        // When
        val result = useCase.invoke("bitcoin", 365)

        // Then
        result.test {
            awaitItem()
            awaitComplete()
        }
        verify { repository.getPriceHistory("bitcoin", 365) }
    }

    private fun createTestPriceHistory(coinId: String) = PriceHistory(
        coinId = coinId,
        prices = listOf(
            PriceHistory.PricePoint(1609459200000, BigDecimal("29000.00")),
            PriceHistory.PricePoint(1609545600000, BigDecimal("32000.00")),
            PriceHistory.PricePoint(1609632000000, BigDecimal("33000.00")),
            PriceHistory.PricePoint(1609718400000, BigDecimal("34000.00")),
            PriceHistory.PricePoint(1609804800000, BigDecimal("40000.00"))
        )
    )
}
