package com.example.fxinsight.data.network.dto.favorite.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteResponse(
    val id: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("base_currency")
    val baseCurrency: String,

    @SerialName("target_currency")
    val targetCurrency: String,

    @SerialName("created_at")
    val createdAt: String

)