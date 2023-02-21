package com.femco.oxxo.reciboentiendaproveedores.presentation.order

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ExperimentalGetImage
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.femco.oxxo.reciboentiendaproveedores.R
import com.femco.oxxo.reciboentiendaproveedores.databinding.FragmentOrdersBinding
import com.femco.oxxo.reciboentiendaproveedores.presentation.order.adapter.ProductsAdapter
import com.femco.oxxo.reciboentiendaproveedores.presentation.scanning.ScannerActivity
import com.femco.oxxo.reciboentiendaproveedores.presentation.scanning.constants.ORDER_TYPE_PREINVENTARISTA
import com.femco.oxxo.reciboentiendaproveedores.presentation.scanning.constants.ORDER_TYPE_REPARTIDOR
import com.femco.oxxo.reciboentiendaproveedores.presentation.scanning.constants.SCAN_REQUEST_CODE
import com.femco.oxxo.reciboentiendaproveedores.presentation.scanning.constants.SCAN_RESULT
import com.femco.oxxo.reciboentiendaproveedores.utils.AlertDialogWithEditText
import com.femco.oxxo.reciboentiendaproveedores.utils.MyAlertDialog
import com.femco.oxxo.reciboentiendaproveedores.utils.closeKeyboard
import com.femco.oxxo.reciboentiendaproveedores.utils.setVisibility
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalGetImage
@AndroidEntryPoint
class OrdersFragment : Fragment() {

    private var navController: NavController? = null

    private lateinit var binding: FragmentOrdersBinding

    private val viewModel by viewModels<OrdersViewModel>()

    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var arrayAdapterSupply: ArrayAdapter<String>
    private lateinit var arrayAdapterSKUs: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.app_name)
        val navHostFragment = requireActivity().supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setUpAdapters()
        initListeners()
        setObserver()
        viewModel.validateIfSKUExisting()
    }

    private fun setUpAdapters() {
        productsAdapter = ProductsAdapter(listOf(), {
            viewModel.removeProduct(it)
        }, {
            viewModel.plusProduct(it)
        }, {
            viewModel.minusProduct(it)
        }) { position ->
            AlertDialogWithEditText(
                requireContext(),
                R.string.dialog_title_confirm
            ) {
                viewModel.changeValueManual(it, position)
            }.alertDialog()
        }
        arrayAdapterSupply =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, arrayOf())
        arrayAdapterSKUs =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, arrayOf())
    }


    private fun setObserver() {
        viewModel.uiState.observe(requireActivity()) {
            when (it) {
                is OrdersState.ValidateData -> enabledButtonCatalog(it.enabled, it.drawableRes)
                is OrdersState.SKUListState -> {
                    productsAdapter.fetchData(it.skus)
                    viewModel.reloadTotal(it.skus)
                }
                is OrdersState.SetLists -> setDataIntoAutoComplete(it.listSupply, it.listSkU)
                is OrdersState.SetButtonsScanOrAdd -> setVisibilityButtons(it.showScan, it.showAdd)
                is OrdersState.ReloadGrandTotal -> setGrandTotal(it.total)
                OrdersState.ShowMessageError -> showErrorMessage()
                is OrdersState.ShowMessageSuccess -> navController?.navigate(R.id.action_q_r_viewer_fragment, it.bundle)
            }
        }
    }

    private fun showErrorMessage() {
        MyAlertDialog(requireContext()).showAlert(
            message = R.string.order_fragment_dialog_message_error,
            positiveMessage = R.string.order_fragment_dialog_confirm,
        ) {}
    }

    private fun setGrandTotal(total: Int) {
        binding.grandTotalField.setText("$total")
    }

    private fun setVisibilityButtons(showScan: Boolean, showAdd: Boolean) {
        with(binding) {
            barcodeImageButton.setVisibility(showScan)
            barcodeInputImageButton.setVisibility(showAdd)
        }
    }

    private fun setDataIntoAutoComplete(listSupply: List<String>, listSkU: List<String>) {
        arrayAdapterSupply =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listSupply)
        binding.supplySourceAutoComplete.setAdapter(arrayAdapterSupply)
        arrayAdapterSKUs =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listSkU)
        binding.barcodeAutoComplete.setAdapter(arrayAdapterSKUs)
    }

    private fun enabledButtonCatalog(enabled: Boolean, drawableRes: Int) {
        binding.loadCatalogButton.isEnabled = !enabled
        binding.loadCatalogButton.setBackgroundResource(drawableRes)
    }

    private fun initListeners() {
        with(binding) {
            loadCatalogButton.setOnClickListener {
                navController?.navigate(R.id.action_load_catalog_fragment)
            }
            barcodeImageButton.setOnClickListener {
                launcher.launch(Intent(requireContext(), ScannerActivity::class.java))
            }
            barcodeInputImageButton.setOnClickListener {
                viewModel.insertSKUIntoList(barcodeAutoComplete.text.toString())
                barcodeAutoComplete.text.clear()
            }

            orderTypeRadioGroup.setOnCheckedChangeListener { _, id ->
                viewModel.setOrderType(onRadioButtonClicked(id))
            }

            continueButton.setOnClickListener {
                MyAlertDialog(requireContext()).showAlert(
                    message = R.string.order_fragment_dialog_message,
                    positiveMessage = R.string.order_fragment_dialog_positive,
                    negativeMessage = R.string.order_fragment_dialog_negative
                ) {
                    viewModel.validateForm(supplySourceAutoComplete,grandTotalField)
                }
            }

            productListRecycler.apply {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = productsAdapter
            }
            supplySourceAutoComplete.setAdapter(arrayAdapterSupply)
            supplySourceAutoComplete.setOnItemClickListener { _, view, _, _ -> view.closeKeyboard() }

            with(barcodeAutoComplete) {
                setAdapter(arrayAdapterSKUs)
                setOnItemClickListener { _, view, _, _ -> view.closeKeyboard() }
                setOnClickListener {
                    barcodeImageButton.visibility = View.GONE
                }
                addTextChangedListener {
                    viewModel.validateAutoComplete(it)
                }
            }
        }
    }

    private fun onRadioButtonClicked(radio: Int) =
        if (radio == R.id.entregaRadioButton) ORDER_TYPE_REPARTIDOR else ORDER_TYPE_PREINVENTARISTA

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
}