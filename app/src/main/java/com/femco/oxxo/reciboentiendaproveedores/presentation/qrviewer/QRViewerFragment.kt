package com.femco.oxxo.reciboentiendaproveedores.presentation.qrviewer

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.femco.oxxo.reciboentiendaproveedores.R
import com.femco.oxxo.reciboentiendaproveedores.databinding.FragmentQRViewerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QRViewerFragment : Fragment() {

    private lateinit var binding: FragmentQRViewerBinding

    private val viewModel by viewModels<QRViewerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQRViewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        viewModel.initTimer()
        viewModel.createQR(arguments)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.clear()
    }

    private fun setObserver() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                is QRViewerState.UpdateDate -> setTextTimer(it.dateString)
                QRViewerState.NavigateToUp -> navigateToBackAndDeleteStack()
                is QRViewerState.SetQRCode -> binding.qrCodeView.setImageBitmap(it.bitmap)
                is QRViewerState.ShowError -> showMessage(it.message)
            }
        }
    }

    private fun navigateToBackAndDeleteStack(){
        findNavController().navigate(R.id.action_order_fragment)
    }

    private fun showMessage(message: Int) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun setTextTimer(dateString: String) {
        val text = resources.getString(R.string.qr_viewer_title_counter_down, dateString)
        binding.counterTextView.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}