package com.example.fxinsight.data.network.APIService

import com.example.fxinsight.data.network.dto.profile.request.ProfileCreate
import com.example.fxinsight.data.network.dto.profile.response.DeleteProfileResponse
import com.example.fxinsight.data.network.dto.profile.response.ProfileResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface ProfileAPIService {

    @POST("profile")
    suspend fun createProfile(
        @Body create: ProfileCreate
    ): ProfileResponse

    @GET("profile/me")
    suspend fun fetchProfile(): ProfileResponse

    @DELETE("profile/me")
    suspend fun deleteProfile(): DeleteProfileResponse

}