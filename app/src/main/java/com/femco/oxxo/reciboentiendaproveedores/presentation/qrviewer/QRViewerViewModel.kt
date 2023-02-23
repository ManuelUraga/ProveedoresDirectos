package com.femco.oxxo.reciboentiendaproveedores.presentation.qrviewer

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.femco.oxxo.reciboentiendaproveedores.R
import com.femco.oxxo.reciboentiendaproveedores.presentation.scanning.constants
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QRViewerViewModel @Inject constructor() : ViewModel() {

    lateinit var counterDown: CountDownTimer
    private var timeLeftInMilliseconds = 600000L

    private val _uiState = MutableLiveData<QRViewerState>()
    val uiState: LiveData<QRViewerState> = _uiState

    fun initTimer() {
        counterDown = object : CountDownTimer(timeLeftInMilliseconds, 1000) {
            override fun onTick(time: Long) {
                timeLeftInMilliseconds = time
                updateTimer()
            }

            override fun onFinish() {}

        }.start()
    }

    private fun navigateToUp() {
        _uiState.value = QRViewerState.NavigateToUp
    }

    fun updateTimer() {
        val minutes = timeLeftInMilliseconds / 60000
        val seconds = timeLeftInMilliseconds % 60000 / 1000

        if (minutes == 0L && seconds == 0L) navigateToUp()

        val dateStringBuilder = StringBuilder()

        dateStringBuilder.append("" + minutes)
        dateStringBuilder.append(":")
        if (seconds < 10) dateStringBuilder.append("0")
        dateStringBuilder.append(seconds)
        _uiState.value = QRViewerState.UpdateDate(dateStringBuilder.toString())
    }

    fun createQR(arguments: Bundle?) {
        val jsonDataQRCode = arguments?.getString(constants.DATA_QR_CODE) as String
        if (jsonDataQRCode.isEmpty()) {
            _uiState.value = QRViewerState.ShowError(R.string.qr_viewer_message_error_empty)
        } else {
            val write = QRCodeWriter()
            try {
                val bitMatrix = write.encode(jsonDataQRCode, BarcodeFormat.QR_CODE, 512, 512)
                val width = bitMatrix.width
                val height = bitMatrix.height
                val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                    }
                }
                _uiState.value = QRViewerState.SetQRCode(bmp)
            } catch (e: WriterException) {
                _uiState.value = QRViewerState.ShowError(R.string.qr_viewer_message_error_qr)
            }
        }
    }
}