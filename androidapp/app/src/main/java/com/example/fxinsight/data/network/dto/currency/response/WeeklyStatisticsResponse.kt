package com.example.fxinsight.data.network.dto.currency.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


// make an enum
@Serializable
data class WeeklyStatisticsResponse(
    val trend: String,
    @SerialName("change_percent")
    val changePercent: Double,
    val average: Double,
    val median: Double,
    val min: Double,
    val max: Double
)