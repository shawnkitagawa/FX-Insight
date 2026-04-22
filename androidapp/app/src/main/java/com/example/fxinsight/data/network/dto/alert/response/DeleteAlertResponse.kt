package com.example.fxinsight.data.network.dto.alert.response

import kotlinx.serialization.Serializable


@Serializable
data class DeleteAlertResponse (
    val message: String,
    val alert: AlertResponse
)


