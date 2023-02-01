package com.femco.oxxo.reciboentiendaproveedores.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.femco.oxxo.reciboentiendaproveedores.domain.model.SKUProviders

@Database(
    entities = [SKUProviders::class],
    version = 1,
    exportSchema = false
)
abstract class ReciboEnTiendaDataBase: RoomDatabase() {
    abstract val getReciboEnTiendaDao: ReciboEnTiendaDao

    companion object {
        const val RECIBO_EN_TIENDA_DB_NAME = "recibo_en_tienda_db"
    }
}