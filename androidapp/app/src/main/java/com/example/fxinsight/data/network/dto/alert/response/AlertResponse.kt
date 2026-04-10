package com.example.fxinsight.data.network.dto.alert.response

import com.example.fxinsight.data.network.dto.alert.DirectionStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AlertResponse (
    val id: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("alert_target")
    val alertTarget: Double,

    val direction: DirectionStatus,

    @SerialName("last_checked_rate")
    val lastCheckedRate: Double,

    @SerialName("last_checked_at")
    val lastCheckedAt: String,

    @SerialName("base_currency")
    val baseCurrency: Double,

    @SerialName("target_currency")
    val TargetCurrency: Double,

    @SerialName("is_active")
    val isActive: Boolean,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("is_triggered")
    val isTriggered: Boolean,

    @SerialName("triggered_at")
    val triggeredAt: String?,
)