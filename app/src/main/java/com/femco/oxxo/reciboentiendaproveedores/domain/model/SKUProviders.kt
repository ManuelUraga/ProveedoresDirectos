package com.femco.oxxo.reciboentiendaproveedores.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sku_providers")
data class SKUProviders(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "sku") val sku: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "supply_source") val supplySource: String
)
