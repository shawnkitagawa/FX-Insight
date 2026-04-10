package com.example.fxinsight.data.network.APIService

import com.example.fxinsight.data.network.dto.favorite.response.FavoriteResponse
import com.example.fxinsight.data.network.dto.favorite.request.FavoriteCreate
import com.example.fxinsight.data.network.dto.favorite.response.DeleteAllFavoriteResponse
import com.example.fxinsight.data.network.dto.favorite.response.DeleteFavoriteResponse
import kotlinx.serialization.SerialName
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface FavoriteAPIService {

    @POST("favorite")
    suspend fun createFavorite(
        @Body create: FavoriteCreate,
    ): FavoriteResponse

    @GET("favorite/me")
    suspend fun fetchFavorite(): List<FavoriteResponse>

    @DELETE ("favorite/me")
    suspend fun deleteAllFavorite(): DeleteAllFavoriteResponse

    @DELETE("favorite/{favorite_id}")
    suspend fun deleteFavorite(
        @Path("favorite_id") favoriteId: String
    ): DeleteFavoriteResponse
}