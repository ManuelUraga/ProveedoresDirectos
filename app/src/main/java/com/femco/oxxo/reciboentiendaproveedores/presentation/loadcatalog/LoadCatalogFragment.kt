package com.femco.oxxo.reciboentiendaproveedores.presentation.loadcatalog

import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.femco.oxxo.reciboentiendaproveedores.R
import com.femco.oxxo.reciboentiendaproveedores.databinding.FragmentLoadCatalogBinding
import com.femco.oxxo.reciboentiendaproveedores.utils.MyAlertDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoadCatalogFragment : Fragment() {

    private lateinit var binding: FragmentLoadCatalogBinding

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
        binding = FragmentLoadCatalogBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.fragment_load_catalog)
        initView()
        setObservers()
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.clear()
    }

    private fun setObservers() {
        viewModel.loadCatalogState.observe(viewLifecycleOwner) {
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
        MyAlertDialog(requireContext()).showAlert(message, R.string.load_catalog_dialog_confirm) {
            if (navigateBack) requireActivity().supportFragmentManager.popBackStack()
        }
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