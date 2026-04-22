package com.example.fxinsight.data.network.dto.alert.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AlertUpdate(

    @SerialName("is_active")
    val isActive: Boolean? = null,

    @SerialName("alert_target")
    val alertTarget: Double? = null

)