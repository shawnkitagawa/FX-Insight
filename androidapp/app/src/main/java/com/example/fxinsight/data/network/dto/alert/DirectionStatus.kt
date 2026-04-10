package com.example.fxinsight.data.network.dto.alert

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class DirectionStatus {
    @SerialName("above")
    ABOVE,

    @SerialName("below")
    BELOW
}