package com.femco.oxxo.reciboentiendaproveedores.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.femco.oxxo.reciboentiendaproveedores.R


class AlertDialogWithEditText(
    private val context: Context,
    private val message: Int,
    private val confirmInserted: (Int) -> Unit
) {

    fun alertDialog() {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.alert_dialog_with_edit_text, null)

        val builder = AlertDialog.Builder(context)
        builder.setView(view)

        val editText = view.findViewById<EditText>(R.id.amountInsertedDialog)
        val title = view.findViewById<TextView>(R.id.titleDialog)
        title.text = context.getString(message)

        builder
            .setCancelable(false)
            .setPositiveButton(R.string.load_catalog_dialog_confirm) { dialog, _ ->
                if (editText.text.isNotBlank()) {
                    val amount = editText.text.toString().toInt()
                    confirmInserted(amount)
                }
                view.closeKeyboard()
                dialog.dismiss()
            }
        val alertDialog = builder.create()
        alertDialog.show()
    }

}