package com.example.fxinsight.data.repositiory

import com.example.fxinsight.data.network.APIService.FavoriteAPIService
import com.example.fxinsight.data.network.dto.favorite.request.FavoriteCreate
import com.example.fxinsight.data.network.dto.favorite.response.DeleteAllFavoriteResponse
import com.example.fxinsight.data.network.dto.favorite.response.DeleteFavoriteResponse
import com.example.fxinsight.data.network.dto.favorite.response.FavoriteResponse


interface FavoriteRepository{

    suspend fun createFavorite(
        create: FavoriteCreate
    ): Result<FavoriteResponse>

    suspend fun fetchFavorites(): Result<List<FavoriteResponse>>

    suspend fun deleteAllFavorites(): Result<DeleteAllFavoriteResponse>

    suspend fun deleteFavorite(
        favoriteId: String
    ): Result<DeleteFavoriteResponse>
}


class DefaultFavoriteRepository(private val favoriteAPIService: FavoriteAPIService
): FavoriteRepository {

    override suspend fun createFavorite(create: FavoriteCreate): Result<FavoriteResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchFavorites(): Result<List<FavoriteResponse>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllFavorites(): Result<DeleteAllFavoriteResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFavorite(favoriteId: String): Result<DeleteFavoriteResponse> {
        TODO("Not yet implemented")
    }

}