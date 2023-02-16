package com.femco.oxxo.reciboentiendaproveedores

import android.app.Application
import com.femco.oxxo.reciboentiendaproveedores.utils.PreferencesManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ReciboEnTiendaProveedores : Application() {
    override fun onCreate() {
        super.onCreate()
        PreferencesManager.initializeInstance(context = this)
    }
}
