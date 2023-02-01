package com.femco.oxxo.reciboentiendaproveedores.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.femco.oxxo.reciboentiendaproveedores.domain.model.SKUProviders
import kotlinx.coroutines.flow.Flow

@Dao
interface ReciboEnTiendaDao {

    @Query("SELECT * FROM sku_providers")
    fun getSKU(): Flow<List<SKUProviders>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(skuProviders: List<SKUProviders>)
}