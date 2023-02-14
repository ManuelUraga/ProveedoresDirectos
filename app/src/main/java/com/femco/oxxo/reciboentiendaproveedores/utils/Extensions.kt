package com.femco.oxxo.reciboentiendaproveedores.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.closeKeyboard() {
    val inputManager: InputMethodManager =
        this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(this.applicationWindowToken, 0)
}

fun View.setVisibility(show: Boolean) {
    this.visibility = if (show) View.VISIBLE
    else View.GONE
}