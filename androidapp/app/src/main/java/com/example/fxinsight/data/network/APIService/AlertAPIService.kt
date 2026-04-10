package com.example.fxinsight.data.network.APIService

import com.example.fxinsight.data.network.dto.alert.request.AlertCreate
import com.example.fxinsight.data.network.dto.alert.request.AlertUpdate
import com.example.fxinsight.data.network.dto.alert.response.AlertResponse
import com.example.fxinsight.data.network.dto.alert.response.DeleteAlertResponse
import com.example.fxinsight.data.network.dto.alert.response.DeleteAllAlertResponse
import com.example.fxinsight.data.network.dto.profile.request.ProfileCreate
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AlertAPIService {

    @POST("alert")
    suspend fun createAlert(
        @Body create: AlertCreate
    ): AlertResponse

    @PUT("alert/{alert_id}")
    suspend fun updateAlert(
        @Path("alert_id") alertId: String,
        @Body update: AlertUpdate
    ): AlertResponse

    @GET("alert/me")
    suspend fun fetchAlert(): List<AlertResponse>

    @DELETE("alert/{alert_id}")
    suspend fun deleteAlert(
        @Path("alert_id") alertId: String
    ): DeleteAlertResponse

    @DELETE("alert/me")
    suspend fun deleteAllAlert(): DeleteAllAlertResponse
}