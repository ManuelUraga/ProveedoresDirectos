package com.femco.oxxo.reciboentiendaproveedores.presentation.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.femco.oxxo.reciboentiendaproveedores.R
import com.femco.oxxo.reciboentiendaproveedores.databinding.FragmentOrdersBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrdersFragment : Fragment() {

    private var navController : NavController? = null

    private var _binding: FragmentOrdersBinding? = null

    private val binding get() = _binding!!

    private val viewModel by viewModels<OrdersViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = requireActivity().supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        initView()
        viewModel.validateIfSKUExisting()
        setObserver()
    }

    private fun setObserver() {
        viewModel.uiState.observe(requireActivity()) {
            when(it){
                is OrdersState.ValidateData -> enabledButtonCatalog(it.enabled)
            }
        }
    }

    private fun enabledButtonCatalog(enabled: Boolean) {
        binding.loadCatalogButton.isEnabled = !enabled
    }

    private fun initView() {
        binding.loadCatalogButton.setOnClickListener {
            navController?.navigate(R.id.global_action_LoadCatalogFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}