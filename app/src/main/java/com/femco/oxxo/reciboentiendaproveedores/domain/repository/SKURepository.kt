package com.femco.oxxo.reciboentiendaproveedores.domain.repository

import com.femco.oxxo.reciboentiendaproveedores.domain.model.SKUProviders
import kotlinx.coroutines.flow.Flow

interface SKURepository {
    suspend fun saveSKUs(listSKU: List<SKUProviders>)

    fun getSKU(): Flow<List<SKUProviders>>
}