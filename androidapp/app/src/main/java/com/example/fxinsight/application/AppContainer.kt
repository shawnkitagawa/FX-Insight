package com.example.fxinsight.application

import android.content.Context
import com.example.fxinsight.data.network.APIService.AlertAPIService
import com.example.fxinsight.data.network.APIService.CurrencyAPIService
import com.example.fxinsight.data.network.APIService.FavoriteAPIService
import com.example.fxinsight.data.network.APIService.HistoryAPIService
import com.example.fxinsight.data.network.APIService.ProfileAPIService
import com.example.fxinsight.data.network.interceptor.AuthInterceptor
import com.example.fxinsight.data.repositiory.AlertRepository
import com.example.fxinsight.data.repositiory.CurrencyRepository
import com.example.fxinsight.data.repositiory.DefaultAlertRepository
import com.example.fxinsight.data.repositiory.DefaultCurrencyRepository
import com.example.fxinsight.data.repositiory.DefaultFavoriteRepository
import com.example.fxinsight.data.repositiory.DefaultHistoryRepository
import com.example.fxinsight.data.repositiory.DefaultProfileRepository
import com.example.fxinsight.data.repositiory.FavoriteRepository
import com.example.fxinsight.data.repositiory.HistoryRepository
import com.example.fxinsight.data.repositiory.ProfileRepository
import com.example.fxinsight.data.repository.DefaultFXInsightRepository
import com.example.fxinsight.data.repository.FXInsightRepository
import com.example.fxinsight.ui.uistate.History
import kotlinx.serialization.json.Json
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
interface AppContainer {
    val fxInsightRepository: FXInsightRepository
    val alertRepository: AlertRepository
    val currencyRepository: CurrencyRepository
    val historyRepository: HistoryRepository
    val profileRepository: ProfileRepository

    val favoriteRepository: FavoriteRepository
}



class DefaultAppContainer(private val context: Context): AppContainer
{

    private val supabase by lazy {
        createSupabaseClient(
            supabaseUrl = "https://nilbmddhnsvndzrahbmk.supabase.co",
            supabaseKey= "sb_publishable_7ZaM6t91OXXo3E7FxE18Aw_SiSn4rv7"
        )
        {
            install(Auth)
            install(Postgrest)
        }
    }

    val authInterceptor = AuthInterceptor(getToken = {
        supabase.auth.currentSessionOrNull()?.accessToken
    }
    )
    val logging = HttpLoggingInterceptor().apply{
        level = HttpLoggingInterceptor.Level.BODY
    }
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .build()


    private val BASE_URL = "https://currency-api-7wf5ft5rtq-an.a.run.app"

    private val json = Json {
        ignoreUnknownKeys = true
    }


    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    private val retrofitServiceCurrency: CurrencyAPIService by lazy{
        retrofit.create(CurrencyAPIService::class.java)
    }
    private val retrofitServiceHistory: HistoryAPIService by lazy{
        retrofit.create(HistoryAPIService::class.java)
    }
    private val retrofitServiceProfile: ProfileAPIService by lazy{
        retrofit.create(ProfileAPIService::class.java)
    }
    private val retrofitServiceFavorite: FavoriteAPIService by lazy{
        retrofit.create(FavoriteAPIService::class.java)
    }
    private val retrofitServiceAlert: AlertAPIService by lazy{
        retrofit.create(AlertAPIService::class.java)
    }



    override val fxInsightRepository: FXInsightRepository by lazy{
        DefaultFXInsightRepository(supabase = supabase)
    }

    override val alertRepository: AlertRepository by lazy{
        DefaultAlertRepository(alertAPIService = retrofitServiceAlert)
    }

    override val currencyRepository: CurrencyRepository by lazy {
        DefaultCurrencyRepository(currencyAPIService = retrofitServiceCurrency)
    }

    override val historyRepository: HistoryRepository by lazy {
        DefaultHistoryRepository(historyAPIService = retrofitServiceHistory)
    }

    override val profileRepository: ProfileRepository by lazy {
        DefaultProfileRepository(profileAPIService = retrofitServiceProfile)
    }
    override val favoriteRepository: FavoriteRepository by lazy {
        DefaultFavoriteRepository(favoriteAPIService = retrofitServiceFavorite)
    }

}