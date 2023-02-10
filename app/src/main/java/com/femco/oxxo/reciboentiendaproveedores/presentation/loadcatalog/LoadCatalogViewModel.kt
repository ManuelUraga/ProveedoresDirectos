package com.femco.oxxo.reciboentiendaproveedores.presentation.loadcatalog

import android.app.Application
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.femco.oxxo.reciboentiendaproveedores.R
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
) : ViewModel() {

    fun setUp(arguments: Bundle?) {}

    val loadCatalogState = MutableLiveData<LoadCatalogState>()

    var listSku: MutableList<SKUProviders> = mutableListOf()

    fun getNameFileAndUri(uri: Uri) {
        uri.path?.let {
            val name = File(it).name
            val extension = name.substring(name.lastIndexOf("."))
            if (!extension.contains("csv")) {
                loadCatalogState.value =
                    LoadCatalogState.ShowErrorMessage(
                        R.string.load_catalog_diloag_error_message_invalid
                    )
            } else {
                val csvFile = uri.let { uriPath ->
                    application.contentResolver.openInputStream(uriPath)
                }
                val isr = InputStreamReader(csvFile)
                mappingIntoObject(BufferedReader(isr).readLines(), {
                    loadCatalogState.value =
                        LoadCatalogState.ShowErrorMessage(
                            R.string.load_catalog_diloag_error_message_format
                        )
                }, {
                    loadCatalogState.value = LoadCatalogState.NameFile(name)
                })
            }
        }
    }

    private fun mappingIntoObject(readLines: List<String>, error: () -> Unit, success: () -> Unit) {
        val firstElement = readLines[0]
        for (line in readLines) {
            if (!line.contentEquals(firstElement)) {
                val items = line.split(",")
                if (items.size < 4) {
                    error()
                    return
                }
                val skuProviders = SKUProviders(
                    id = items[0].toInt(),
                    sku = items[1],
                    description = items[2],
                    supplySource = items[3]
                )
                listSku.add(skuProviders)
            }
        }
        success()
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