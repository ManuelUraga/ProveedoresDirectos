package com.femco.oxxo.reciboentiendaproveedores.data.repository

import android.util.Log
import com.femco.oxxo.reciboentiendaproveedores.data.database.ReciboEnTiendaDao
import com.femco.oxxo.reciboentiendaproveedores.domain.model.SKUProviders
import com.femco.oxxo.reciboentiendaproveedores.domain.repository.SKURepository
import kotlinx.coroutines.flow.Flow

class SKURepositoryImpl(private val dao: ReciboEnTiendaDao): SKURepository {
    override suspend fun saveSKUs(listSKU: List<SKUProviders>) {
       dao.insertAll(listSKU)
    }

    override fun getSKU(): Flow<List<SKUProviders>> {
       return dao.getSKU()
    }
}