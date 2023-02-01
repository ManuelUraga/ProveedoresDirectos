package com.femco.oxxo.reciboentiendaproveedores.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.femco.oxxo.reciboentiendaproveedores.domain.model.SKUProviders

@Dao
interface ReciboEnTiendaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(skuProviders: List<SKUProviders>)
}