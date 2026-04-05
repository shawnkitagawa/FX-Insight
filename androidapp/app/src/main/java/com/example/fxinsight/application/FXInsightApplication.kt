package com.example.fxinsight.application

import android.app.Application

class FXInsightApplication(): Application() {

    lateinit var container: AppContainer
    override fun onCreate()
    {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}