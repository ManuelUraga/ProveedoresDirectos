package com.femco.oxxo.reciboentiendaproveedores.data.repository

import com.femco.oxxo.reciboentiendaproveedores.data.database.ReciboEnTiendaDao
import com.femco.oxxo.reciboentiendaproveedores.domain.model.SKUProviders
import com.femco.oxxo.reciboentiendaproveedores.domain.repository.SKURepository

class SKURepositoryImpl(private val dao: ReciboEnTiendaDao): SKURepository {
    override suspend fun saveSKUs(listSKU: List<SKUProviders>) {
       dao.insertAll(listSKU)
    }
}