package com.example.fxinsight.data.network.dto.history.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class HistoryCreate(

    @SerialName("base_currency")
    val baseCurrency: String,

    @SerialName("target_currency")
    val targetCurrency: String,

    @SerialName("base_amount")
    val baseAmount: String
)