package com.example.fxinsight.data.network.dto.currency

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
enum class TimeGroup {
    @SerialName("day")
    DAY,

    @SerialName("week")
    WEEK,

    @SerialName("month")
    MONTH
}