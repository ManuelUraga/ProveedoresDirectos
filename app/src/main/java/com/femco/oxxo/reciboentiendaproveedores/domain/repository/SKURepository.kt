package com.femco.oxxo.reciboentiendaproveedores.domain.repository

import com.femco.oxxo.reciboentiendaproveedores.domain.model.SKUProviders

interface SKURepository {
    suspend fun saveSKUs(listSKU: List<SKUProviders>)
}