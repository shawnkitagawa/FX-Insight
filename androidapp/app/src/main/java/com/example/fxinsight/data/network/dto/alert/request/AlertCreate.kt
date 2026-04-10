package com.example.fxinsight.data.network.dto.alert.request

import com.example.fxinsight.data.network.dto.alert.DirectionStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AlertCreate(
    @SerialName("alert_target")
    val alertTarget: Double,

    val direction: DirectionStatus,

    @SerialName("base_currency")
    val baseCurrency: String,

    @SerialName("target_currency")
    val targetCurrency: String,

    @SerialName("is_active")
    val isActive: Boolean = true

)