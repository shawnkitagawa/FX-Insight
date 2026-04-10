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
        TODO("Not yet implemented")
    }

    override suspend fun updateAlert(alertId: String, update: AlertUpdate): Result<AlertResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchAlerts(): Result<List<AlertResponse>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlert(alertId: String): Result<DeleteAlertResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllAlerts(): Result<DeleteAllAlertResponse> {
        TODO("Not yet implemented")
    }
}