package com.femco.oxxo.reciboentiendaproveedores.presentation.loadcatalog

import android.app.Application
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.femco.oxxo.reciboentiendaproveedores.domain.model.SKUProviders
import com.femco.oxxo.reciboentiendaproveedores.domain.usecases.SaveAllSKUsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import javax.inject.Inject

@HiltViewModel
class LoadCatalogViewModel @Inject constructor(
    private val application: Application,
    private val saveAllSKUs: SaveAllSKUsUseCase
): ViewModel() {

    fun setUp(arguments: Bundle?) {}

    private val _listSKU = MutableLiveData<List<SKUProviders>>()
    var listSKU: LiveData<List<SKUProviders>> = _listSKU

    private val _namePath = MutableLiveData<String>()
    var namePath: LiveData<String> = _namePath

    fun readFile(uri: Uri){
        _namePath.postValue( uri.path?.let { File(it).name } ?: "" )
        val csvFile = application.contentResolver.openInputStream(uri)
        val isr = InputStreamReader(csvFile)
        mappingIntoObject(BufferedReader(isr).readLines())
    }

    private fun mappingIntoObject(readLines: List<String>) {
        val listSKU = mutableListOf<SKUProviders>()
        val firstElement = readLines[0]
        for (line in readLines) {
            if (!line.contentEquals(firstElement)) {
                val item = line.split(",")
                val skuProviders = SKUProviders(
                    id = item[0].toInt(),
                    sku = item[1],
                    description = item[2],
                    supplySource = item[3]
                )
                listSKU.add(skuProviders)
            }
        }
        _listSKU.postValue(listSKU)
    }

    fun saveSKUDataInDB(){
        viewModelScope.launch {
            listSKU.value?.let {
                saveAllSKUs(it).also {
                    Log.d("data saved","")
                }
            }
        }
    }

}