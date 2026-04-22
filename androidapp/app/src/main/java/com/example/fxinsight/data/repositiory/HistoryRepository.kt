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
        try {
            val history = historyAPIService.createHistory(create)



            return Result.success(history )
        }
        catch(e: Exception)
        {
            return Result.failure(e)
        }
    }

    override suspend fun fetchHistories(): Result<List<HistoryResponse>> {

        try{
            val histories = historyAPIService.fetchHistory()

            return Result.success(histories)
        }
        catch(e: Exception)
        {
            return Result.failure(e)
        }
    }

    override suspend fun deleteHistory(history_id: String): Result<DeleteHistoryResponse> {
        try{
            val message = historyAPIService.deleteHistory(history_id)


            return Result.success(message)
        }
        catch(e: Exception){

            return Result.failure(e)
        }
    }

    override suspend fun deleteAllHistories(): Result<DeleteAllHistoryResponse> {
        try{
            val message = historyAPIService.deleteAllHistory()

            return Result.success(message)

        }
        catch(e: Exception)
        {

            return Result.failure(e)

        }
    }
}