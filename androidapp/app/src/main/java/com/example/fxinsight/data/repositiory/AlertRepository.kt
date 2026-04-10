package com.example.fxinsight.data.repositiory

import com.example.fxinsight.data.network.APIService.AlertAPIService
import com.example.fxinsight.data.network.APIService.ProfileAPIService
import com.example.fxinsight.data.network.dto.alert.request.AlertCreate
import com.example.fxinsight.data.network.dto.alert.request.AlertUpdate
import com.example.fxinsight.data.network.dto.alert.response.AlertResponse
import com.example.fxinsight.data.network.dto.alert.response.DeleteAlertResponse
import com.example.fxinsight.data.network.dto.alert.response.DeleteAllAlertResponse


interface AlertRepository{

    suspend fun createAlert(
        create: AlertCreate
    ): Result<AlertResponse>

    suspend fun updateAlert(
        alertId: String,
        update: AlertUpdate
    ): Result<AlertResponse>

    suspend fun fetchAlerts(): Result<List<AlertResponse>>

    suspend fun deleteAlert(
        alertId: String
    ): Result<DeleteAlertResponse>


    suspend fun deleteAllAlerts(): Result<DeleteAllAlertResponse>
}

class DefaultAlertRepository(private val alertAPIService: AlertAPIService
): AlertRepository {

    override suspend fun createAlert(create: AlertCreate): Result<AlertResponse> {
        try{
            val alert = alertAPIService.createAlert(create)

            return Result.success(alert)

        }
        catch(e: Exception)
        {
            return Result.failure(e)
        }
    }

    override suspend fun updateAlert(alertId: String, update: AlertUpdate): Result<AlertResponse> {
        try{
            val updatedAlert = alertAPIService.updateAlert(alertId = alertId, update = update)


            return Result.success(updatedAlert)
        }
        catch(e: Exception)
        {
            return Result.failure(e)
        }
    }

    override suspend fun fetchAlerts(): Result<List<AlertResponse>> {
        try{
            val alerts = alertAPIService.fetchAlert()


            return Result.success(alerts)

        }
        catch(e: Exception)
        {
            return Result.failure(e)
        }
    }

    override suspend fun deleteAlert(alertId: String): Result<DeleteAlertResponse> {
        try{
            val alert = alertAPIService.deleteAlert(alertId = alertId)


            return Result.success(alert)

        }
        catch(e: Exception){

            return Result.failure(e)

        }
    }

    override suspend fun deleteAllAlerts(): Result<DeleteAllAlertResponse> {
        try{
            val message = alertAPIService.deleteAllAlert()

            return Result.success(message)

        }
        catch(e: Exception)
        {
            return Result.failure(e)
        }
    }
}