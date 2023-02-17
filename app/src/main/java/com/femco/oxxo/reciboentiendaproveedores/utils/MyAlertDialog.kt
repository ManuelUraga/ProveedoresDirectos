package com.femco.oxxo.reciboentiendaproveedores.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.femco.oxxo.reciboentiendaproveedores.R

class MyAlertDialog(
    private val context: Context,
) {

    private var builder: AlertDialog.Builder = AlertDialog.Builder(context)

    fun showAlert(
        message: Int,
        positiveMessage: Int? = null,
        negativeMessage: Int? = null,
        onClickPositive: () -> Unit
    ) {

        if (positiveMessage != null) {
            builder.setPositiveButton(positiveMessage) { dialog, _ ->
                dialog.dismiss()
                onClickPositive()
            }
        }
        if (negativeMessage != null) {
            builder.setNegativeButton(negativeMessage) { dialog, _ ->
                dialog.dismiss()
            }
        }
        val alertDialog: AlertDialog = builder.setMessage(message).setCancelable(false).create()

        alertDialog.setOnShowListener {
            alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                .setTextColor(context.getColor(R.color.yellow_sunshine))
        }

        alertDialog.show()
    }
}