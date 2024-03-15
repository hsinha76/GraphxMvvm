package com.hsdroid.openinappassignment.data.network

import com.hsdroid.openinappassignment.data.models.ServerResponse
import retrofit2.http.GET

interface ApiService {

    @GET("api/v1/dashboardNew")
    suspend fun getHomeData(): ServerResponse

}