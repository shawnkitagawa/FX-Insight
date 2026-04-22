package com.example.fxinsight.data.network.dto.currency.response

import kotlinx.serialization.Serializable

@Serializable
data class HistoricalGraphResponse(
    val date: String,
    val base: String,
    val quote: String,
    val rate: Double
)