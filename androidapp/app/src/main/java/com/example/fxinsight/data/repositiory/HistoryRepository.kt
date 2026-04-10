package com.example.fxinsight.data.repositiory

import com.example.fxinsight.data.network.APIService.HistoryAPIService
import com.example.fxinsight.data.network.dto.history.request.HistoryCreate
import com.example.fxinsight.data.network.dto.history.response.DeleteAllHistoryResponse
import com.example.fxinsight.data.network.dto.history.response.DeleteHistoryResponse
import com.example.fxinsight.data.network.dto.history.response.HistoryResponse


interface HistoryRepository{

    suspend fun createHistory(
        create: HistoryCreate
    ): Result<HistoryResponse>

    suspend fun fetchHistories(): Result<List<HistoryResponse>>

    suspend fun deleteHistory(
        history_id: String
    ): Result<DeleteHistoryResponse>

    suspend fun deleteAllHistories(): Result<DeleteAllHistoryResponse>
}


class DefaultHistoryRepository(private val historyAPIService: HistoryAPIService
): HistoryRepository {
    override suspend fun createHistory(create: HistoryCreate): Result<HistoryResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchHistories(): Result<List<HistoryResponse>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteHistory(history_id: String): Result<DeleteHistoryResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllHistories(): Result<DeleteAllHistoryResponse> {
        TODO("Not yet implemented")
    }
}