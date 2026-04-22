package com.example.fxinsight.data.repositiory

import android.util.Log
import com.example.fxinsight.data.network.APIService.ProfileAPIService
import com.example.fxinsight.data.network.dto.profile.request.ProfileCreate
import com.example.fxinsight.data.network.dto.profile.response.DeleteProfileResponse
import com.example.fxinsight.data.network.dto.profile.response.ProfileResponse
import kotlinx.io.IOException
import retrofit2.HttpException


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
        return try {
            val response = profileAPIService.createProfile(create)
            Result.success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Result.failure(Exception(errorBody ?: "HTTP error"))
        } catch (e: IOException) {
            Log.e("ProfileError", "IO Exception", e)
            Result.failure(Exception("Network error"))
        } catch (e: Exception) {
            Result.failure(e)
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