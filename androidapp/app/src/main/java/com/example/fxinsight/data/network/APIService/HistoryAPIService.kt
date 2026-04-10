package com.example.fxinsight.data.network.APIService

import androidx.room.Delete
import com.example.fxinsight.data.network.dto.history.response.HistoryResponse
import com.example.fxinsight.data.network.dto.history.request.HistoryCreate
import com.example.fxinsight.data.network.dto.history.response.DeleteAllHistoryResponse
import com.example.fxinsight.data.network.dto.history.response.DeleteHistoryResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HistoryAPIService {

    @POST("history")
    suspend fun createHistory(
        @Body create: HistoryCreate
    ): HistoryResponse

    @GET("history/me")
    suspend fun fetchHistory(): List<HistoryResponse>

    @DELETE("history/{history_id}")
    suspend fun deleteHistory(
        @Path("history_id") historyId: String
    ): DeleteHistoryResponse

    @DELETE("history/me")
    suspend fun deleteAllHistory(): DeleteAllHistoryResponse

}