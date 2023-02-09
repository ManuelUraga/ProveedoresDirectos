package com.femco.oxxo.reciboentiendaproveedores.presentation.order

import com.femco.oxxo.reciboentiendaproveedores.domain.model.ProductScanned

sealed class OrdersState {
    data class ValidateData(val enabled: Boolean): OrdersState()
    data class SKUListState(val skus: MutableList<ProductScanned>): OrdersState()

}