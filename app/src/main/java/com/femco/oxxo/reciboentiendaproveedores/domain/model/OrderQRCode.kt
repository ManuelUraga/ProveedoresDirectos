package com.femco.oxxo.reciboentiendaproveedores.domain.model


data class OrderQRCode(
    val orderType: Int,
    val supplySource: String,
    val skuScanned: MutableList<ProductScanned>,
    val grandTotal: Int,
    val initialDateOrder: String,
    val finalDateOrder: String
)