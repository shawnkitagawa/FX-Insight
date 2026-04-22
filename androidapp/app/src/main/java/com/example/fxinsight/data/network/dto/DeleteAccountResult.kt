package com.example.fxinsight.data.network.dto

import com.example.fxinsight.data.network.dto.profile.response.DeleteProfileResponse

data class DeleteAccountResult(
    val response: DeleteProfileResponse,
    val shouldForceLogout: Boolean
)