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
        try{
            val favorite = favoriteAPIService.createFavorite(create)


            return Result.success(favorite)
        }
        catch(e: Exception)
        {
            return Result.failure(e)
        }
    }

    override suspend fun fetchFavorites(): Result<List<FavoriteResponse>> {
        try{
            val favorites = favoriteAPIService.fetchFavorite()


            return Result.success(favorites)

        }
        catch(e: Exception){
            return Result.failure(e)
        }
    }

    override suspend fun deleteAllFavorites(): Result<DeleteAllFavoriteResponse> {
        try{
            val message = favoriteAPIService.deleteAllFavorite()

            return Result.success(message)
        }
        catch(e: Exception)
        {
            return Result.failure(e)
        }
    }

    override suspend fun deleteFavorite(favoriteId: String): Result<DeleteFavoriteResponse> {
        try{
            val message = favoriteAPIService.deleteFavorite(favoriteId)

            return Result.success(message)
        }
        catch(e: Exception){

            return Result.failure(e)
        }
    }

}