package com.example.fxinsight.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.fxinsight.application.FXInsightApplication
import com.example.fxinsight.util.NotificationHelper

class AlertWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val application = applicationContext as FXInsightApplication
        val alertRepository = application.container.alertRepository
        val notificationHelper = NotificationHelper(applicationContext)

        return try {
            val result = alertRepository.fetchAlerts()
            result.fold(
                onSuccess = { alerts ->
                    alerts.forEach { alert ->
                        if (alert.isTriggered && alert.isActive) {
                            notificationHelper.showAlertNotification(
                                title = "Price Alert Hit!",
                                message = "${alert.baseCurrency} to ${alert.targetCurrency} has reached your target of ${alert.alertTarget}",
                                notificationId = alert.id.hashCode()
                            )
                        }
                    }
                    Result.success()
                },
                onFailure = {
                    Log.e("AlertWorker", "Failed to fetch alerts: ${it.message}")
                    Result.retry()
                }
            )
        } catch (e: Exception) {
            Log.e("AlertWorker", "Error in AlertWorker: ${e.message}")
            Result.failure()
        }
    }
}
