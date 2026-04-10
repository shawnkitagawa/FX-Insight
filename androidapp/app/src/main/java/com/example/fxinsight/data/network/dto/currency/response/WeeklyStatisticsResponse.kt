package com.example.fxinsight.data.network.dto.currency.response

import kotlinx.serialization.Serializable


// make an enum
@Serializable
data class WeeklyStatisticsResponse(
    val trend: String,
    val changePercent: Double,
    val average: Double,
    val median: Double,
    val min: Double,
    val max: Double
)