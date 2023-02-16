package com.femco.oxxo.reciboentiendaproveedores.utils

import android.content.Context
import android.content.SharedPreferences


class PreferencesManager private constructor(context: Context) {
    private val mPref: SharedPreferences

    init {
        mPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    var firstTime: Boolean
        get() = mPref.getBoolean(KEY_VALUE, false)
        set(value) {
            mPref.edit()
                .putBoolean(KEY_VALUE, value)
                .apply()
        }

    fun remove(key: String?) {
        mPref.edit()
            .remove(key)
            .apply()
    }

    fun clear(): Boolean {
        return mPref.edit()
            .clear()
            .commit()
    }

    companion object {
        private const val PREF_NAME = "RECIBO_EN_TIENDA"
        private const val KEY_VALUE = "IS_FIRST_TIME"
        private var sInstance: PreferencesManager? = null
        @Synchronized
        fun initializeInstance(context: Context) {
            if (sInstance == null) {
                sInstance = PreferencesManager(context)
            }
        }

        @get:Synchronized
        val instance: PreferencesManager?
            get() {
                checkNotNull(sInstance) {
                    PreferencesManager::class.java.simpleName +
                            " is not initialized, call initializeInstance(..) method first."
                }
                return sInstance
            }
    }
}