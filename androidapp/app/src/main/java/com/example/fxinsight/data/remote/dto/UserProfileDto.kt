package com.example.fxinsight.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto (
    val user_id: String,
    val user_name: String,
)