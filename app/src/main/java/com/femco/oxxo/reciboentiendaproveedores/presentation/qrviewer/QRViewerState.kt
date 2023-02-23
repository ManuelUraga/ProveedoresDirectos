package com.femco.oxxo.reciboentiendaproveedores.presentation.qrviewer

import android.graphics.Bitmap

sealed class QRViewerState {
    data class UpdateDate(val dateString: String): QRViewerState()
    data class SetQRCode(val bitmap: Bitmap): QRViewerState()
    data class ShowError(val message: Int): QRViewerState()
    object NavigateToUp: QRViewerState()
}