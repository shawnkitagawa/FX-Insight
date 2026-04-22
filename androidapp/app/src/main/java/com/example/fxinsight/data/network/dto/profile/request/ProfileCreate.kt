package com.example.fxinsight.data.network.dto.profile.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileCreate(

    @SerialName("user_name")
    val userName: String
)
