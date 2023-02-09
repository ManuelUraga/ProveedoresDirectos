package com.femco.oxxo.reciboentiendaproveedores.presentation.loadcatalog

import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.femco.oxxo.reciboentiendaproveedores.R
import com.femco.oxxo.reciboentiendaproveedores.databinding.FragmentLoadCatalogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoadCatalogFragment : Fragment() {

    private var _binding: FragmentLoadCatalogBinding? = null

    private val binding get() = _binding!!

    private val viewModel by viewModels<LoadCatalogViewModel>()

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { viewModel.getNameFileAndUri(it) }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoadCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.load_catalog_fragment_title)
        initView()
        setObservers()
    }

    private fun setObservers() {
        viewModel.loadCatalogState.observe(requireActivity()) {
            when (it) {
                is LoadCatalogState.NameFile -> setField(it.nameFile)
                LoadCatalogState.Success -> showAlertDialog()
            }
        }
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(context)
            .setMessage(R.string.load_catalog_dialog_message)
            .setPositiveButton(R.string.load_catalog_dialog_confirm) { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            .setNegativeButton(R.string.load_catalog_dialog_cancel) { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            .setCancelable(false)
            .create().show()
    }

    private fun setField(it: String) {
        binding.loadCatalogPathText.text = it
    }

    private fun initView() {
        binding.loadCatalogPathCard.setOnClickListener {
            getContent.launch("text/*")
        }
        binding.uploadCatalogButton.setOnClickListener {
            viewModel.saveSKUDataInDB()
        }
    }

}