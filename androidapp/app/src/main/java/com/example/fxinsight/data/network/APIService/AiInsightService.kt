package com.example.fxinsight.data.network.APIService

import com.example.fxinsight.data.network.dto.insight.response.AiInsightResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface AiInsightService {
    @GET("insight")
    suspend fun getAiInsight(
        @Query("base") base: String,
        @Query("target")target: String): AiInsightResponse
}
