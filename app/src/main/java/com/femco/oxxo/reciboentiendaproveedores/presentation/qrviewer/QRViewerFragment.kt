package com.femco.oxxo.reciboentiendaproveedores.presentation.qrviewer

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.femco.oxxo.reciboentiendaproveedores.R
import com.femco.oxxo.reciboentiendaproveedores.databinding.FragmentQRViewerBinding
import com.femco.oxxo.reciboentiendaproveedores.presentation.scanning.constants.DATA_QR_CODE
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

class QRViewerFragment : Fragment() {

    private lateinit var binding: FragmentQRViewerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQRViewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val jsonDataQRCode = arguments?.getString(DATA_QR_CODE) as String
        if (jsonDataQRCode.isEmpty()) {
            Toast.makeText(requireContext(), "Data empty",Toast.LENGTH_SHORT).show()
        } else {
            val write = QRCodeWriter()
            try {
                val bitMatrix = write.encode(jsonDataQRCode, BarcodeFormat.QR_CODE, 512, 512)
                val width = bitMatrix.width
                val height = bitMatrix.height
                val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        bmp.setPixel(x,y, if (bitMatrix[x,y]) Color.BLACK else Color.WHITE)
                    }
                }
                binding.qrCodeView.setImageBitmap(bmp)
            } catch (e: WriterException) {
                Toast.makeText(requireContext(), "The QR isn´t generate",Toast.LENGTH_SHORT).show()
            }
        }
    }
}