package com.femco.oxxo.reciboentiendaproveedores.presentation.qrviewer

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.femco.oxxo.reciboentiendaproveedores.presentation.order.OrdersState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QRViewerViewModel @Inject constructor() : ViewModel() {

    lateinit var counterDown: CountDownTimer
    private var timeLeftInMilliseconds = 600000L
    private val timerRunning = false

    private val _uiState = MutableLiveData<QRViewerState>()
    val uiState: LiveData<QRViewerState> = _uiState

    fun initTimer() {
        counterDown = object : CountDownTimer(timeLeftInMilliseconds, 1000) {
            override fun onTick(p0: Long) {
                timeLeftInMilliseconds = p0
                updateTimer()
            }

            override fun onFinish() {}

        }.start()
    }

    fun updateTimer() {
        val minutes = timeLeftInMilliseconds / 60000
        val seconds = timeLeftInMilliseconds % 60000 / 1000

        val dateStringBuilder = StringBuilder()

        dateStringBuilder.append("" + minutes)
        dateStringBuilder.append(":")
        if (seconds < 10) dateStringBuilder.append("0")
        dateStringBuilder.append(seconds)
        _uiState.value = QRViewerState.UpdateDate(dateStringBuilder.toString())
    }
}