package com.example.fxinsight.data.network.dto.favorite.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class FavoriteCreate(
    @SerialName("base_currency")
    val baseCurrency: String,

    @SerialName("target_currency")
    val targetCurrency: String
)