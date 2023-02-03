package com.femco.oxxo.reciboentiendaproveedores.presentation.scanning

import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

@ExperimentalGetImage
class MyImageAnalyser : ImageAnalysis.Analyzer {

    interface OnListenScan {
        fun textScan(s: String)
    }

    private var onListenScan: OnListenScan? = null

    fun setListenScan(onListenScan: OnListenScan) {
        this.onListenScan = onListenScan
    }

    override fun analyze(image: ImageProxy) {
        scanBarcode(image)
    }

    private fun scanBarcode(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            val barcodeScannerOptions =
                BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                    .build()
            val scanner = BarcodeScanning.getClient(barcodeScannerOptions)

            image.let {
                scanner.process(it)
                    .addOnSuccessListener { barcodes ->
                        readerBarcodeData(barcodes)
                    }
                    .addOnFailureListener {

                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            }
        }
    }

    private fun readerBarcodeData(barcodes: List<Barcode>) {
        for (barcode in barcodes) {
            when (barcode.valueType) {
                Barcode.FORMAT_QR_CODE -> {
                    barcode.rawValue?.let {
                        onListenScan?.textScan(it)
                    }
                    Log.d("Barcode", "${barcode.rawValue}")
                }
                Barcode.TYPE_TEXT -> {
                    barcode.rawValue?.let {
                        onListenScan?.textScan(it)
                    }
                    Log.d("Barcode", "${barcode.rawValue}")
                }
            }
        }
    }

}