package com.cryptovision.domain.usecase

import app.cash.turbine.test
import com.cryptovision.domain.model.Coin
import com.cryptovision.domain.model.Result
import com.cryptovision.domain.repository.CoinRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class GetCoinDetailsUseCaseTest {

    private lateinit var repository: CoinRepository
    private lateinit var useCase: GetCoinDetailsUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        useCase = GetCoinDetailsUseCase(repository)
    }

    @Test
    fun `when repository returns success, use case should emit success with coin details`() = runTest {
        // Given
        val expectedCoin = createTestCoin(id = "bitcoin", name = "Bitcoin")
        coEvery { repository.getCoinById("bitcoin") } returns flowOf(Result.Success(expectedCoin))

        // When
        val result = useCase.invoke("bitcoin")

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission is Result.Success)
            assertEquals(expectedCoin, (emission as Result.Success).data)
            awaitComplete()
        }
    }

    @Test
    fun `when repository returns error, use case should emit error`() = runTest {
        // Given
        val expectedException = Exception("Coin not found")
        coEvery { repository.getCoinById("invalid") } returns flowOf(Result.Error(expectedException))

        // When
        val result = useCase.invoke("invalid")

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission is Result.Error)
            assertEquals(expectedException, (emission as Result.Error).exception)
            awaitComplete()
        }
    }

    @Test
    fun `when repository emits loading then success, use case should emit both`() = runTest {
        // Given
        val expectedCoin = createTestCoin(id = "ethereum", name = "Ethereum")
        coEvery { repository.getCoinById("ethereum") } returns flowOf(
            Result.Loading,
            Result.Success(expectedCoin)
        )

        // When
        val result = useCase.invoke("ethereum")

        // Then
        result.test {
            assertTrue(awaitItem() is Result.Loading)
            val successEmission = awaitItem()
            assertTrue(successEmission is Result.Success)
            assertEquals(expectedCoin, (successEmission as Result.Success).data)
            awaitComplete()
        }
    }

    private fun createTestCoin(
        id: String = "bitcoin",
        name: String = "Bitcoin",
        symbol: String = "BTC",
        currentPrice: BigDecimal = BigDecimal("50000.00")
    ) = Coin(
        id = id,
        symbol = symbol,
        name = name,
        image = "https://example.com/image.png",
        currentPrice = currentPrice,
        marketCap = BigDecimal("1000000000"),
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
        athDate = "2021-11-10T14:24:11.849Z",
        atl = BigDecimal("67.81"),
        atlDate = "2013-07-06T00:00:00.000Z",
        lastUpdated = "2024-01-01T00:00:00.000Z"
    )
}
