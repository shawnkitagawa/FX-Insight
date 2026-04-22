package com.example.fxinsight.data.repositiory

import com.example.fxinsight.data.network.APIService.AiInsightService
import com.example.fxinsight.data.network.dto.insight.response.AiInsightResponse

interface InsightRepository {

    suspend fun getAiInsight(base: String, target: String): Result<AiInsightResponse>
}






class DefaultInsightRepository(private val aiInsightService: AiInsightService): InsightRepository
{

    override suspend fun getAiInsight(base: String, target: String ): Result<AiInsightResponse>
    {
        try {
            val result = aiInsightService.getAiInsight(base, target)
            return Result.success(result)
        }
        catch(e: Exception)
        {
           return  Result.failure(e)
        }
    }

}