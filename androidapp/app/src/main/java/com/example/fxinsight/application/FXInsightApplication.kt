package com.example.fxinsight.application

import android.app.Application
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.fxinsight.data.worker.AlertWorker
import java.util.concurrent.TimeUnit

class FXInsightApplication(): Application() {

    lateinit var container: AppContainer
    override fun onCreate()
    {
        super.onCreate()
        container = DefaultAppContainer(this)
        setupAlertWorker()
    }

    private fun setupAlertWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val alertRequest = PeriodicWorkRequestBuilder<AlertWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "FXAlertWorker",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            alertRequest
        )
    }
}
