package com.example.fxinsight.data.network.dto.currency.response

import kotlinx.serialization.Serializable

@Serializable
data class AvailableCurrencyResponse(
    val availableCurrency: List<String>
)