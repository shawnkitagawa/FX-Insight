package com.example.fxinsight.data.network.dto.profile.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ProfileResponse(

    @SerialName("user_id")
    val userId: String,

    @SerialName("user_name")
    val userName: String
)
