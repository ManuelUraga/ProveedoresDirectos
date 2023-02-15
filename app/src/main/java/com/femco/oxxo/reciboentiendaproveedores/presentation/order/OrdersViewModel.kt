package com.femco.oxxo.reciboentiendaproveedores.presentation.order

import android.text.Editable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.femco.oxxo.reciboentiendaproveedores.R
import com.femco.oxxo.reciboentiendaproveedores.domain.model.ProductScanned
import com.femco.oxxo.reciboentiendaproveedores.domain.model.SKUProviders
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

    fun validateIfSKUExisting() {
        viewModelScope.launch {
            getSKUUseCase().also { listProviders ->
                if (listProviders.first().isNotEmpty()) {
                    uiState.value =
                        OrdersState.ValidateData(true, R.drawable.disabled_rounded_button)
                    setDataInformation(listProviders.first())
                } else {
                    uiState.value =
                        OrdersState.ValidateData(false, R.drawable.yellow_rounded_button)
                }
            }
        }
    }

    private fun setDataInformation(listProviders: List<SKUProviders>) {
        val listSupply =
            listProviders.distinctBy { it.supplySource }.map { it.supplySource }.toList()
        val listSkU = listProviders.map { it.sku }.toList()
        uiState.value = OrdersState.SetLists(listSupply, listSkU)
    }

    fun insertSKUIntoList(skuScan: String) {
        val indexSKUExisting = skuListScanned.indexOfFirst { it.skuScanned == skuScan }
        if (indexSKUExisting != -1) {
            plusProduct(indexSKUExisting)
        } else {
            skuListScanned.add(ProductScanned(skuScanned = skuScan))
            if (skuListScanned.size == 1) timestamp = System.currentTimeMillis()
            uiState.value = OrdersState.SKUListState(skuListScanned)
        }
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

    fun changeValueManual(value: Int, position: Int) {
        skuListScanned[position].apply {
            this.amount = value
        }
        uiState.value = OrdersState.SKUListState(skuListScanned)
    }

    fun validateAutoComplete(it: Editable?) {
        if (it?.isBlank() == true) uiState.value =
            OrdersState.SetButtonsScanOrAdd(showScan = true, showAdd = false)
        else uiState.value = OrdersState.SetButtonsScanOrAdd(showScan = false, showAdd = true)
    }

    fun reloadTotal(skus: MutableList<ProductScanned>) {
        var grandAmount = 0
        skus.forEach {
            grandAmount += it.amount
        }
        uiState.value = OrdersState.ReloadGrandTotal(grandAmount)
    }
}