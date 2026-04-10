package com.example.fxinsight.data.repositiory

import com.example.fxinsight.data.network.APIService.CurrencyAPIService
import com.example.fxinsight.data.network.dto.currency.TimeGroup
import com.example.fxinsight.data.network.dto.currency.response.AvailableCurrencyResponse
import com.example.fxinsight.data.network.dto.currency.response.CurrencyInformationResponse
import com.example.fxinsight.data.network.dto.currency.response.DailyChangeResponse
import com.example.fxinsight.data.network.dto.currency.response.WeeklyStatisticsResponse
import com.example.fxinsight.data.network.dto.currency.response.HistoricalGraphResponse

interface CurrencyRepository{

    suspend fun getAvailableCurrency(): Result<AvailableCurrencyResponse>

    suspend fun getCurrencyInformation(baseCurrency: String): Result<CurrencyInformationResponse>

    suspend fun getWeekStatistics(baseCurrency: String,
                               targetCurrency: String): Result<WeeklyStatisticsResponse>

    suspend fun getDailyChange(baseCurrency: String,
                            targetCurrency: String ): Result<DailyChangeResponse>

    suspend fun getHistoricalGraph(baseCurrency: String,
                                targetCurrency: String,
                                timeGroup: TimeGroup
    ): Result<HistoricalGraphResponse>
}


class DefaultCurrencyRepository(
    private val currencyAPIService: CurrencyAPIService
): CurrencyRepository {

    override suspend fun getAvailableCurrency(): Result<AvailableCurrencyResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrencyInformation(baseCurrency: String): Result<CurrencyInformationResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getWeekStatistics(
        baseCurrency: String,
        targetCurrency: String
    ): Result<WeeklyStatisticsResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getDailyChange(
        baseCurrency: String,
        targetCurrency: String
    ): Result<DailyChangeResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getHistoricalGraph(
        baseCurrency: String,
        targetCurrency: String,
        timeGroup: TimeGroup
    ): Result<HistoricalGraphResponse> {
        TODO("Not yet implemented")
    }

}