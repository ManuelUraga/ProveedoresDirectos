package com.femco.oxxo.reciboentiendaproveedores.presentation.loadcatalog

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.femco.oxxo.reciboentiendaproveedores.R
import com.femco.oxxo.reciboentiendaproveedores.databinding.FragmentLoadCatalogBinding
import com.femco.oxxo.reciboentiendaproveedores.utils.MyAlertDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoadCatalogFragment : Fragment() {

    private var _binding: FragmentLoadCatalogBinding? = null

    private val binding get() = _binding!!

    private val viewModel by viewModels<LoadCatalogViewModel>()

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                viewModel.getNameFileAndUri(it)
            }
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
                LoadCatalogState.Success -> showAlertDialog(
                    R.string.load_catalog_dialog_message,
                    true
                )
                is LoadCatalogState.ShowErrorMessage -> showAlertDialog(it.message, false)
            }
        }
    }

    private fun showAlertDialog(message: Int, navigateBack: Boolean) {
        MyAlertDialog(requireContext(), message) {
            if (navigateBack) requireActivity().supportFragmentManager.popBackStack()
        }.show()
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