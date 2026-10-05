package com.it.ptdei.global


import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors


//@Composable
//fun QRScannerApp() {
//
//    var qrResult by remember { mutableStateOf("") }
//
//    var hasCameraPermission by remember {
//        mutableStateOf(false)
//    }
//
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        hasCameraPermission = isGranted
//    }
//
//    LaunchedEffect(Unit) {
//        launcher.launch(Manifest.permission.CAMERA)
//    }
//
//    Text("qrResult: $qrResult")
//
//    Column(modifier = Modifier.fillMaxSize()) {
//
//        if (hasCameraPermission) {
//
//            // 📷 Camera Preview
//            Box(modifier = Modifier.weight(1f)) {
//                HiddenCameraQrScanner { result ->
//                    qrResult = result
//                }
//            }
//
//        } else {
//
//            Box(modifier = Modifier.weight(1f)) {
//                Text("Camera permission denied")
//            }
//        }
//
//        // 🧾 Textbox
//        OutlinedTextField(
//            value = qrResult,
//            onValueChange = { qrResult = it },
//            label = { Text("Scanned Code") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        )
//    }
//}

//@OptIn(ExperimentalGetImage::class)
//@Composable
//fun CameraPreview(onQrDetected: (String) -> Unit) {
//
//    val context = LocalContext.current
//    val lifecycleOwner = LocalLifecycleOwner.current
//
//    AndroidView(
//        modifier = Modifier.fillMaxSize(),
//        factory = { ctx ->
//
//            val previewView = PreviewView(ctx)
//
//            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
//
//            cameraProviderFuture.addListener({
//
//                val cameraProvider = cameraProviderFuture.get()
//
//                // ✅ Preview
//                val preview = Preview.Builder().build().also {
//                    it.setSurfaceProvider(previewView.surfaceProvider)
//                }
//
//                // ✅ QR Scanner
//                val scanner = BarcodeScanning.getClient()
//
//                // ✅ Image Analysis
//                val analysis = ImageAnalysis.Builder()
//                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                    .build()
//
//                analysis.setAnalyzer(
//                    ContextCompat.getMainExecutor(ctx)
//                ) { imageProxy ->
//
//                    val mediaImage = imageProxy.image
//                    if (mediaImage == null) {
//                        imageProxy.close()
//                        return@setAnalyzer
//                    }
//
//                    val image = InputImage.fromMediaImage(
//                        mediaImage,
//                        imageProxy.imageInfo.rotationDegrees
//                    )
//
//                    scanner.process(image)
//                        .addOnSuccessListener { barcodes ->
//                            val result = barcodes.firstOrNull()?.rawValue
//                            if (!result.isNullOrEmpty()) {
//                                onQrDetected(result)
//                            }
//                        }
//                        .addOnCompleteListener {
//                            imageProxy.close()
//                        }
//                }
//
//                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//                cameraProvider.unbindAll()
////                cameraProvider.bindToLifecycle(
////                    lifecycleOwner,
////                    cameraSelector,
////                    preview,
////                    analysis
////                )
//
//            }, ContextCompat.getMainExecutor(ctx))
//
//            previewView
//        }
//    )
//}


@OptIn(ExperimentalGetImage::class)
@Composable
fun HiddenCameraQrScanner(
    hasPermission: Boolean,
    onCameraReady: () -> Unit,
    onQrDetected: (String) -> Unit,
) {

    if (!hasPermission) return

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // ✅ Background thread
    val cameraExecutor = remember {
        Executors.newSingleThreadExecutor()
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    LaunchedEffect(Unit) {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({

            val cameraProvider = cameraProviderFuture.get()

            val scanner = BarcodeScanning.getClient()

            val analysis = ImageAnalysis.Builder().setBackpressureStrategy(
                ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
            ).build()

            // ✅ RUNS IN BACKGROUND THREAD
            analysis.setAnalyzer(cameraExecutor) { imageProxy ->

                val mediaImage = imageProxy.image

                if (mediaImage == null) {
                    imageProxy.close()
                    return@setAnalyzer
                }

                val image = InputImage.fromMediaImage(
                    mediaImage, imageProxy.imageInfo.rotationDegrees
                )

                scanner.process(image).addOnSuccessListener { barcodes ->

                    val result = barcodes.firstOrNull()?.rawValue

                    if (!result.isNullOrEmpty()) {
                        onQrDetected(result)
                    }
                }.addOnCompleteListener {
                    imageProxy.close()
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, analysis
            )

            onCameraReady()

        }, ContextCompat.getMainExecutor(context))
    }
}






