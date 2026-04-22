package com.example.fxinsight.data.network.dto.insight.response

import android.R
import kotlinx.serialization.Serializable


@Serializable
data class AiInsightResponse(
    val pair: String,
    val insight: String,
)
