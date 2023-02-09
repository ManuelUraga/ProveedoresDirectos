package com.femco.oxxo.reciboentiendaproveedores.presentation.order.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.femco.oxxo.reciboentiendaproveedores.databinding.SkuProductsItemsBinding
import com.femco.oxxo.reciboentiendaproveedores.domain.model.ProductScanned

class ProductsAdapter(
    private var listSKUProviders: List<ProductScanned>,
    val clickRemove: (Int) -> Unit,
    val clickPlus: (Int) -> Unit,
    val clickMinus: (Int) -> Unit
) :
    RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SkuProductsItemsBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = listSKUProviders.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(listSKUProviders[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun fetchData(skus: List<ProductScanned>) {
        listSKUProviders = skus
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: SkuProductsItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(product: ProductScanned) {
            with(binding) {
                this.product = product
                removeProductButton.setOnClickListener { clickRemove(adapterPosition) }
                addProductButton.setOnClickListener { clickPlus(adapterPosition) }//plus(product.skuScanned) }
                minusProductButton.setOnClickListener { clickMinus(adapterPosition) }
                executePendingBindings()
            }
        }
    }
}