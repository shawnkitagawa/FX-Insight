package com.example.fxinsight.data.network.APIService

import com.example.fxinsight.data.network.dto.currency.TimeGroup
import com.example.fxinsight.data.network.dto.currency.response.AvailableCurrencyResponse
import com.example.fxinsight.data.network.dto.currency.response.CurrencyInformationResponse
import com.example.fxinsight.data.network.dto.currency.response.DailyChangeResponse
import com.example.fxinsight.data.network.dto.currency.response.HistoricalGraphResponse
import com.example.fxinsight.data.network.dto.currency.response.WeeklyStatisticsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CurrencyAPIService {
    @GET("currency")
    suspend fun currencyType(): AvailableCurrencyResponse

    @GET("currency/{base}")
    suspend fun currencyInformation(
        @Path("base") baseCurrency: String
    ): CurrencyInformationResponse

    @GET("currency/{base}/{target}/week/")
    suspend fun weeklyStatistics(
        @Path ("base") baseCurrency: String,
        @Path ("target") targetCurrency: String
    ): WeeklyStatisticsResponse

    @GET("currency/{base}/{target}/day")
    suspend fun dailyChange(
        @Path ("base") baseCurrency: String,
        @Path ("target") targetCurrency: String

    ): DailyChangeResponse

    @GET("currency/{base}/{target}/market")
    suspend fun historicalGraph(
        @Path("base") baseCurrency: String,
        @Path("target") targetCurrency: String,

        @Query("time_group") timeGroup: TimeGroup

    ): List<HistoricalGraphResponse>

}