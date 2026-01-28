package com.cryptovision.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseDto(
    @SerialName("coins")
    val coins: List<SearchCoinDto> = emptyList()
)

@Serializable
data class SearchCoinDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("symbol")
    val symbol: String,
    @SerialName("market_cap_rank")
    val marketCapRank: Int? = null,
    @SerialName("thumb")
    val thumb: String? = null,
    @SerialName("large")
    val large: String? = null
)
