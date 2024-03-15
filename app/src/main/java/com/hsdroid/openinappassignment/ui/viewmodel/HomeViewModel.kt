package com.hsdroid.openinappassignment.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsdroid.openinappassignment.data.models.ServerResponse
import com.hsdroid.openinappassignment.data.repository.HomeRepository
import com.hsdroid.openinappassignment.utils.APIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) : ViewModel() {

    private val _response: MutableStateFlow<APIState<ServerResponse>> =
        MutableStateFlow(APIState.EMPTY)
    val response: StateFlow<APIState<ServerResponse>> = _response

    init {
        getResponse()
    }

    fun getResponse() = viewModelScope.launch {
        homeRepository.getResponse()
            .onStart {
                _response.value = APIState.LOADING
            }
            .catch {
                _response.value = APIState.FAILURE(it)
            }
            .collect {
                _response.value = APIState.SUCCESS(it)
            }
    }
}