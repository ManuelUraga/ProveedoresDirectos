package com.femco.oxxo.reciboentiendaproveedores.presentation.order

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.femco.oxxo.reciboentiendaproveedores.domain.model.ProductScanned
import com.femco.oxxo.reciboentiendaproveedores.domain.usecases.GetSKUUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(private val getSKUUseCase: GetSKUUseCase) : ViewModel() {

    val uiState = MutableLiveData<OrdersState>()
    private val skuListScanned: MutableList<ProductScanned> = mutableListOf()
    private var timestamp: Long = 0L

    init {
        skuListScanned.add(ProductScanned("12",1))
        skuListScanned.add(ProductScanned("123",1))
        uiState.value = OrdersState.SKUListState(skuListScanned)
    }
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

    fun insertSKUIntoList(skuScan: String) {
        skuListScanned.add(ProductScanned(skuScanned = skuScan))
        if (skuListScanned.size == 1) {
            timestamp = System.currentTimeMillis()
        }
        uiState.value = OrdersState.SKUListState(skuListScanned)
    }
    fun plusProduct(position: Int) {
        skuListScanned[position].apply {
            this.amount = skuListScanned[position].amount.plus(1)
        }
        uiState.value = OrdersState.SKUListState(skuListScanned)
    }

    fun minusProduct(position: Int) {
        skuListScanned[position].apply {
            if (this.amount != 1) {
                this.amount = skuListScanned[position].amount.minus(1)
            }
        }
        uiState.value = OrdersState.SKUListState(skuListScanned)
    }

    fun removeProduct(position: Int) {
        skuListScanned.removeAt(position)
        uiState.value = OrdersState.SKUListState(skuListScanned)
    }

}