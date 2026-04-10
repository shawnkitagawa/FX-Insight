package com.example.fxinsight.data.repositiory

import com.example.fxinsight.data.network.APIService.ProfileAPIService
import com.example.fxinsight.data.network.dto.profile.response.DeleteProfileResponse
import com.example.fxinsight.data.network.dto.profile.response.ProfileResponse


interface ProfileRepository{

    suspend fun fetchProfile(): Result<ProfileResponse>

    // must signout -> initial page -> deleteProfile
    suspend fun deleteProfile(): Result<DeleteProfileResponse>

}

class DefaultProfileRepository(
    private val profileAPIService: ProfileAPIService
): ProfileRepository{

    override suspend fun fetchProfile(): Result<ProfileResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProfile(): Result<DeleteProfileResponse> {
        TODO("Not yet implemented")
    }
}