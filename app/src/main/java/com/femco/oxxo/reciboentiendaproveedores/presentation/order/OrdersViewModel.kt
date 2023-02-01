package com.femco.oxxo.reciboentiendaproveedores.presentation.order

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.femco.oxxo.reciboentiendaproveedores.domain.usecases.GetSKUUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(private val getSKUUseCase: GetSKUUseCase) : ViewModel() {

    val uiState = MutableLiveData<OrdersState>()

    fun validateIfSKUExisting() {
        viewModelScope.launch {
            getSKUUseCase().also { listProviders ->
                if (listProviders.first().isNotEmpty()) {
                    uiState.value = OrdersState.ValidateData(true)
                } else {
                    uiState.value = OrdersState.ValidateData(false)
                }
            }
        }
    }

}