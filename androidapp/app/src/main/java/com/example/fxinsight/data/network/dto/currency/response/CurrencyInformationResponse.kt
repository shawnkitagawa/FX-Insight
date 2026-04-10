package com.example.fxinsight.data.network.dto.currency.response

import kotlinx.serialization.Serializable

@Serializable
data class CurrencyInformationResponse(
    val isoCode: String,
    val isoNumeric: Int,
    val currencyName: String,
    val currencySymbol: String,
    val providers: List<String>
)