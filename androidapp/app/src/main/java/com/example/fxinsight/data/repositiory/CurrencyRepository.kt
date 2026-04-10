package com.example.fxinsight.data.repositiory

import com.example.fxinsight.data.network.APIService.CurrencyAPIService
import com.example.fxinsight.data.network.dto.currency.TimeGroup
import com.example.fxinsight.data.network.dto.currency.response.AvailableCurrencyResponse
import com.example.fxinsight.data.network.dto.currency.response.CurrencyInformationResponse
import com.example.fxinsight.data.network.dto.currency.response.DailyChangeResponse
import com.example.fxinsight.data.network.dto.currency.response.WeeklyStatisticsResponse
import com.example.fxinsight.data.network.dto.currency.response.HistoricalGraphResponse
import java.lang.Exception

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
    ): Result<List<HistoricalGraphResponse>>
}

class DefaultCurrencyRepository(
    private val currencyAPIService: CurrencyAPIService
): CurrencyRepository {

    override suspend fun getAvailableCurrency(): Result<AvailableCurrencyResponse> {
        try{
            val curriencies = currencyAPIService.currencyType()

            return Result.success(curriencies)
        }
        catch(e: Exception)
        {
            return Result.failure(e)
        }
    }

    override suspend fun getCurrencyInformation(baseCurrency: String): Result<CurrencyInformationResponse> {
        try{
            val currencyInformation = currencyAPIService.currencyInformation(baseCurrency)

            return Result.success(currencyInformation)
        }
        catch(e: Exception){
            return Result.failure(e)
        }
    }

    override suspend fun getWeekStatistics(
        baseCurrency: String,
        targetCurrency: String
    ): Result<WeeklyStatisticsResponse> {
        try{
            val weeklyStatistics = currencyAPIService.weeklyStatistics(baseCurrency = baseCurrency,
                targetCurrency = targetCurrency)

            return Result.success(weeklyStatistics)
        }
        catch(e: Exception)
        {
            return Result.failure(e)
        }
    }

    override suspend fun getDailyChange(
        baseCurrency: String,
        targetCurrency: String
    ): Result<DailyChangeResponse> {
        try{
            val dailyChange = currencyAPIService.dailyChange(baseCurrency = baseCurrency, targetCurrency = targetCurrency)

            return Result.success(dailyChange)
        }
        catch(e: Exception){
            return Result.failure(e)
        }
    }

    override suspend fun getHistoricalGraph(
        baseCurrency: String,
        targetCurrency: String,
        timeGroup: TimeGroup
    ): Result<List<HistoricalGraphResponse>> {
        try{
            val datas = currencyAPIService.historicalGraph(baseCurrency = baseCurrency,
                targetCurrency = targetCurrency,
                timeGroup = timeGroup)

            return Result.success(datas)
        }
        catch(e: Exception)
        {
            return Result.failure(e)
        }
    }

}