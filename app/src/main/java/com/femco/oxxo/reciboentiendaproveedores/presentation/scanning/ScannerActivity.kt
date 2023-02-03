package com.femco.oxxo.reciboentiendaproveedores.presentation.scanning

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.femco.oxxo.reciboentiendaproveedores.databinding.ActivityScannerBinding
import com.femco.oxxo.reciboentiendaproveedores.presentation.scanning.constants.SCAN_REQUEST_CODE
import com.femco.oxxo.reciboentiendaproveedores.presentation.scanning.constants.SCAN_RESULT
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@ExperimentalGetImage
class ScannerActivity : AppCompatActivity(), MyImageAnalyser.OnListenScan {

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var analyzer: MyImageAnalyser

    private lateinit var _binding: ActivityScannerBinding
    private val binding get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        window.setFlags(1024, 1024)
        initCamera()
        setListeners()
    }

    private fun initCamera() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        analyzer = MyImageAnalyser()
    }

    private fun setListeners() {
        analyzer.setListenScan(this)
        cameraProviderFuture.let {
            it.addListener({
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.CAMERA
                        ) != (PackageManager.PERMISSION_GRANTED)
                    ) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.CAMERA),
                            100
                        )
                    } else {
                        val processCameraProvider =
                            cameraProviderFuture.get() as ProcessCameraProvider
                        bindPreview(processCameraProvider)
                    }
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(this))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty()) {
            val processCameraProvider = cameraProviderFuture.get() as ProcessCameraProvider
            bindPreview(processCameraProvider)
        }
    }

    private fun bindPreview(processCameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build()
        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        preview.setSurfaceProvider(binding.previewView.surfaceProvider)
        val imageCapture = ImageCapture.Builder().build()
        val imageAnalyser = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalyser.setAnalyzer(cameraExecutor, analyzer)
        processCameraProvider.unbindAll()
        processCameraProvider.bindToLifecycle(
            this,
            cameraSelector,
            preview,
            imageCapture,
            imageAnalyser
        )
    }

    override fun textScan(s: String) {
        val intent = Intent()
        intent.putExtra(SCAN_RESULT, s)
        setResult(SCAN_REQUEST_CODE, intent)
        finish()
    }
}