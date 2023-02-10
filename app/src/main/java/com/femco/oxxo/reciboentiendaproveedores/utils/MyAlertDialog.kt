package com.femco.oxxo.reciboentiendaproveedores.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.femco.oxxo.reciboentiendaproveedores.R

class MyAlertDialog(private val context: Context,
                    private val message: Int,
                    private val onClickPositive: () -> Unit) {

    private var builder: AlertDialog.Builder = AlertDialog.Builder(context)
    private lateinit var alertDialog: AlertDialog

    init {
        alertDialog = builder.setMessage(message)
            .setPositiveButton(R.string.load_catalog_dialog_confirm) { dialogInterface, i ->
                alertDialog.dismiss()
                onClickPositive()
            }
            .setCancelable(false)
            .create()
        alertDialog.setOnShowListener {
            alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                .setTextColor(context.getColor(R.color.yellow_sunshine))
        }
    }

    fun show(){
        alertDialog.show()
    }
}