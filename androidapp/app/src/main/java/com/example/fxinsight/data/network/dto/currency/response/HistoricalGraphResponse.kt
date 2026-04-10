package com.example.fxinsight.data.network.dto.currency.response

import kotlinx.serialization.Serializable

@Serializable
data class HistoricalGraphResponse(
    val date: String,
    val baseCurrency: String,
    val quoteCurrency: String,
    val rate: Double
)