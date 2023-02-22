package com.femco.oxxo.reciboentiendaproveedores.presentation.qrviewer

sealed class QRViewerState {
    data class UpdateDate(val dateString: String): QRViewerState()
}