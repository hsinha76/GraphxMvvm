package com.hsdroid.openinappassignment.data.repository

import com.hsdroid.openinappassignment.data.models.ServerResponse
import com.hsdroid.openinappassignment.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class HomeRepository @Inject constructor(private val apiService: ApiService) {
    fun getResponse(): Flow<ServerResponse> = flow {
        emit(apiService.getHomeData())
    }.flowOn(Dispatchers.IO)
}