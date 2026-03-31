package com.example.fxinsight.application

import android.content.Context
import com.example.fxinsight.data.repositiory.DefaultFXInsightRepositiory
import com.example.fxinsight.data.repositiory.FXInsightRepository
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

interface AppContainer {
    val fxInsightRepository: FXInsightRepository
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

    override val fxInsightRepository: FXInsightRepository by lazy{
        DefaultFXInsightRepositiory(supabase = supabase)
    }

}