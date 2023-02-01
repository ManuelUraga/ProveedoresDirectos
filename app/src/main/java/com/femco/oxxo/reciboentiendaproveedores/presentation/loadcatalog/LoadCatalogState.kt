package com.femco.oxxo.reciboentiendaproveedores.presentation.loadcatalog

sealed class LoadCatalogState {
    data class NameFile(val nameFile: String): LoadCatalogState()
    object Success: LoadCatalogState()
}