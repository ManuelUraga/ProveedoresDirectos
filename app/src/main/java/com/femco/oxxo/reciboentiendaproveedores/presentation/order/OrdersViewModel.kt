package com.femco.oxxo.reciboentiendaproveedores.presentation.order

import android.text.Editable
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.femco.oxxo.reciboentiendaproveedores.R
import com.femco.oxxo.reciboentiendaproveedores.domain.model.OrderQRCode
import com.femco.oxxo.reciboentiendaproveedores.domain.model.ProductScanned
import com.femco.oxxo.reciboentiendaproveedores.domain.model.SKUProviders
import com.femco.oxxo.reciboentiendaproveedores.domain.usecases.GetSKUUseCase
import com.femco.oxxo.reciboentiendaproveedores.presentation.scanning.constants.DATA_QR_CODE
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(private val getSKUUseCase: GetSKUUseCase) : ViewModel() {

    private val _uiState = MutableLiveData<OrdersState>()
    val uiState: LiveData<OrdersState> = _uiState

    private val skuListScanned: MutableList<ProductScanned> = mutableListOf()
    private var timestamp: Long = 0L
    private var orderTYpe = 0


    fun validateIfSKUExisting() {
        viewModelScope.launch {
            getSKUUseCase().also { listProviders ->
                if (listProviders.first().isNotEmpty()) {
                    _uiState.value =
                        OrdersState.ValidateData(true, R.drawable.disabled_rounded_button)
                    setDataInformation(listProviders.first())
                } else {
                    _uiState.value =
                        OrdersState.ValidateData(false, R.drawable.yellow_rounded_button)
                }
            }
        }
    }

    private fun setDataInformation(listProviders: List<SKUProviders>) {
        val listSupply =
            listProviders.distinctBy { it.supplySource }.map { it.supplySource }.toList()
        val listSkU = listProviders.map { it.sku }.toList()
        _uiState.value = OrdersState.SetLists(listSupply, listSkU)
    }

    fun insertSKUIntoList(skuScan: String) {
        val indexSKUExisting = skuListScanned.indexOfFirst { it.skuScanned == skuScan }
        if (indexSKUExisting != -1) {
            plusProduct(indexSKUExisting)
        } else {
            skuListScanned.add(ProductScanned(skuScanned = skuScan))
            if (skuListScanned.size == 1) timestamp = System.currentTimeMillis()
            _uiState.value = OrdersState.SKUListState(skuListScanned)
        }
    }

    fun plusProduct(position: Int) {
        skuListScanned[position].apply {
            this.amount = skuListScanned[position].amount.plus(1)
        }
        _uiState.value = OrdersState.SKUListState(skuListScanned)
    }

    fun minusProduct(position: Int) {
        skuListScanned[position].apply {
            if (this.amount != 1) {
                this.amount = skuListScanned[position].amount.minus(1)
            }
        }
        _uiState.value = OrdersState.SKUListState(skuListScanned)
    }

    fun removeProduct(position: Int) {
        skuListScanned.removeAt(position)
        _uiState.value = OrdersState.SKUListState(skuListScanned)
    }

    fun changeValueManual(value: Int, position: Int) {
        skuListScanned[position].apply {
            this.amount = value
        }
        _uiState.value = OrdersState.SKUListState(skuListScanned)
    }

    fun validateAutoComplete(it: Editable?) {
        if (it?.isBlank() == true) _uiState.value =
            OrdersState.SetButtonsScanOrAdd(showScan = true, showAdd = false)
        else _uiState.value = OrdersState.SetButtonsScanOrAdd(showScan = false, showAdd = true)
    }

    fun reloadTotal(skus: MutableList<ProductScanned>) {
        var grandAmount = 0
        skus.forEach {
            grandAmount += it.amount
        }
        _uiState.value = OrdersState.ReloadGrandTotal(grandAmount)
    }

    fun validateForm(
        supplySourceAutoComplete: AutoCompleteTextView,
        grandTotalField: AppCompatEditText
    ) {
        if (orderTYpe == 0 || supplySourceAutoComplete.text.isEmpty() || skuListScanned.isEmpty()) {
            _uiState.value = OrdersState.ShowMessageError
        } else {
            val request = OrderQRCode(
                orderType = orderTYpe,
                supplySource = supplySourceAutoComplete.text.toString(),
                skuScanned = skuListScanned,
                grandTotal = grandTotalField.text.toString().toInt(),
                initialDateOrder = Date(timestamp).toString(),
                finalDateOrder = Date(System.currentTimeMillis()).toString()
            )
            val gson = Gson()
            val json = gson.toJson(request)
            _uiState.value = OrdersState.ShowMessageSuccess(
                bundleOf(
                    DATA_QR_CODE to json
                )
            )
        }
    }

    fun setOrderType(clicked: Int) {
        orderTYpe = clicked
    }

    fun onResume() {
        if (skuListScanned.isNotEmpty()) {
            _uiState.value = OrdersState.SKUListState(skuListScanned)
        }
    }
}