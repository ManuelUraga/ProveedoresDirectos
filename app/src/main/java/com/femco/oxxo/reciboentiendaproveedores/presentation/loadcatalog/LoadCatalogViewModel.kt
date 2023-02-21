package com.femco.oxxo.reciboentiendaproveedores.presentation.loadcatalog

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
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
            val mimeType = getFileExtension(uri)
            if (!mimeType.equals("csv")) {
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
                    loadCatalogState.value = getFileName(uri)?.let { it1 ->
                        LoadCatalogState.NameFile(
                            it1
                        )
                    }
                })
            }
        }
    }

    private fun getFileExtension(uri: Uri): String? =
        if (uri.scheme == ContentResolver.SCHEME_CONTENT)
            MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(application.contentResolver.getType(uri))
        else uri.path?.let {
            MimeTypeMap.getFileExtensionFromUrl(
                Uri.fromFile(File(it)).toString()
            )
        }

    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val cursor: Cursor? = application.contentResolver.query(uri, null, null, null, null)
            cursor.use { c ->
                if (c != null && c.moveToFirst()) {
                    result = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    c.close()
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = cut?.plus(1)?.let { result?.substring(it) }
            }
        }
        return result
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