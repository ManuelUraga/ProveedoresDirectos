package com.femco.oxxo.reciboentiendaproveedores.presentation.loadcatalog

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.femco.oxxo.reciboentiendaproveedores.databinding.FragmentLoadCatalogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoadCatalogFragment : Fragment() {

    private var _binding: FragmentLoadCatalogBinding? = null

    private val viewModel by viewModels<LoadCatalogViewModel>()

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { viewModel.readFile(it) }
        }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoadCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setObservers()
    }

    private fun setObservers() {
        viewModel.listSKU.observe(requireActivity()) {
            Log.d("Observer:", " ${it[0].description}")
        }
        viewModel.namePath.observe(requireActivity()) {
            setField(it)
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