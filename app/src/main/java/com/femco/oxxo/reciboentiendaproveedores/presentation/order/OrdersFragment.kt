package com.femco.oxxo.reciboentiendaproveedores.presentation.order

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.camera.core.ExperimentalGetImage
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.femco.oxxo.reciboentiendaproveedores.R
import com.femco.oxxo.reciboentiendaproveedores.databinding.FragmentOrdersBinding
import com.femco.oxxo.reciboentiendaproveedores.presentation.order.adapter.ProductsAdapter
import com.femco.oxxo.reciboentiendaproveedores.presentation.scanning.ScannerActivity
import com.femco.oxxo.reciboentiendaproveedores.presentation.scanning.constants.SCAN_REQUEST_CODE
import com.femco.oxxo.reciboentiendaproveedores.presentation.scanning.constants.SCAN_RESULT
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalGetImage
@AndroidEntryPoint
class OrdersFragment : Fragment() {

    private var navController: NavController? = null

    private var _binding: FragmentOrdersBinding? = null

    private val binding get() = _binding!!

    private val viewModel by viewModels<OrdersViewModel>()

    private lateinit var productsAdapter: ProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.app_name)
        val navHostFragment = requireActivity().supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        initView()
        viewModel.validateIfSKUExisting()
        setObserver()
    }


    private fun setObserver() {
        viewModel.uiState.observe(requireActivity()) {
            when (it) {
                is OrdersState.ValidateData -> enabledButtonCatalog(it.enabled)
                is OrdersState.SKUListState -> productsAdapter.fetchData(it.skus)
            }
        }
    }

    private fun enabledButtonCatalog(enabled: Boolean) {
        binding.loadCatalogButton.isEnabled = !enabled
        binding.loadCatalogButton.setBackgroundResource(R.drawable.disabled_rounded_button)
    }

    private fun initView() {
        binding.loadCatalogButton.setOnClickListener {
            navController?.navigate(R.id.global_action_LoadCatalogFragment)
        }
        binding.barcodeImageButton.setOnClickListener {
            launcher.launch(Intent(requireContext(), ScannerActivity::class.java))
        }
        productsAdapter = ProductsAdapter(listOf(), {
            viewModel.removeProduct(it)
        }, {
            viewModel.plusProduct(it)
        }, {
            viewModel.minusProduct(it)
        })
        binding.productListRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = productsAdapter
        }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == SCAN_REQUEST_CODE) {
                val intent = it.data
                if (intent != null) {
                    val skuScan = intent.getStringExtra(SCAN_RESULT)
                    skuScan?.let { skuScanned -> viewModel.insertSKUIntoList(skuScanned) }
                }
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}