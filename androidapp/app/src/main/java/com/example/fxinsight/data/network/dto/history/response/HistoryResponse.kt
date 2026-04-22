package com.example.fxinsight.data.network.dto.history.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HistoryResponse(
    val id: String,

//    @SerialName("user_id")
//    val userId: String,

    @SerialName("base_currency")
    val baseCurrency: String,

    @SerialName("target_currency")
    val targetCurrency: String,

    val rate: Double,

    @SerialName("created_at")
    val createdAt: String
)