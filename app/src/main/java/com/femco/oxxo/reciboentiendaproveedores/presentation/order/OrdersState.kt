package com.femco.oxxo.reciboentiendaproveedores.presentation.order

sealed class OrdersState {
    data class ValidateData(val enabled: Boolean): OrdersState()
}