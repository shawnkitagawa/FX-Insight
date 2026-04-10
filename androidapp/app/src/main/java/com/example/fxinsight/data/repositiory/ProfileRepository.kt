package com.example.fxinsight.data.repositiory

import com.example.fxinsight.data.network.APIService.ProfileAPIService
import com.example.fxinsight.data.network.dto.profile.request.ProfileCreate
import com.example.fxinsight.data.network.dto.profile.response.DeleteProfileResponse
import com.example.fxinsight.data.network.dto.profile.response.ProfileResponse


interface ProfileRepository{

    suspend fun createProfile(create: ProfileCreate): Result<ProfileResponse>

    suspend fun fetchProfile(): Result<ProfileResponse>

    // must signout -> initial page -> deleteProfile
    suspend fun deleteProfile(): Result<DeleteProfileResponse>

}

class DefaultProfileRepository(
    private val profileAPIService: ProfileAPIService
): ProfileRepository{


    override suspend fun createProfile(create: ProfileCreate): Result<ProfileResponse> {
        try
        {
            val profile = profileAPIService.createProfile(create)

            return Result.success(profile)
        }
        catch(e: Exception)
        {

            return Result.failure(e)
        }
    }

    override suspend fun fetchProfile(): Result<ProfileResponse> {
        try
        {
            val profile = profileAPIService.fetchProfile()

            return Result.success(profile)
        }
        catch(e: Exception)
        {
            return Result.failure(e)
        }
    }

    override suspend fun deleteProfile(): Result<DeleteProfileResponse> {
        try{

           val message =  profileAPIService.deleteProfile()


            return Result.success(message)
        }
        catch(e: Exception)
        {
            return Result.failure(e)
        }
    }
}