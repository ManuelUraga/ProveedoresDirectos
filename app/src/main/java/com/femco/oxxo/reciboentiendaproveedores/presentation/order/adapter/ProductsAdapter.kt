package com.femco.oxxo.reciboentiendaproveedores.presentation.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.femco.oxxo.reciboentiendaproveedores.R
import com.femco.oxxo.reciboentiendaproveedores.domain.model.SKUProviders

class ProductsAdapter (private val listSKUProviders: List<SKUProviders>) :
        RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sku_products_items, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = listSKUProviders.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }


}

class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

}