package com.femco.oxxo.reciboentiendaproveedores.presentation.loadcatalog

import android.app.Application
import android.net.Uri
import android.os.Bundle
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

    val loadCatalogState = MutableLiveData<LoadCatalogState>()

    var listSku: MutableList<SKUProviders> = mutableListOf()

    fun getNameFileAndUri(uri: Uri) {
        val csvFile = uri.let { application.contentResolver.openInputStream(it) }
        val isr = InputStreamReader(csvFile)
        mappingIntoObject(BufferedReader(isr).readLines())
        uri.path?.let {
            loadCatalogState.value = LoadCatalogState.NameFile(File(it).name)
        }
    }

    private fun mappingIntoObject(readLines: List<String>) {
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
                listSku.add(skuProviders)
            }
        }
    }

    fun saveSKUDataInDB() {
        viewModelScope.launch {
            if (listSku.isNotEmpty()) {
                saveAllSKUs(listSku).also {
                    loadCatalogState.value = LoadCatalogState.Success
                }
            }
        }
    }
}