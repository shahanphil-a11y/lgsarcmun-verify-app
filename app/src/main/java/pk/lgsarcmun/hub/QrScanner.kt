package pk.lgsarcmun.hub

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

@Composable
fun QrScanner(modifier: Modifier, onResult: (String) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    AndroidView(modifier = modifier, factory = { viewContext ->
        PreviewView(viewContext).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            val future = ProcessCameraProvider.getInstance(viewContext)
            future.addListener({
                val provider = future.get()
                val preview = Preview.Builder().build().also { it.setSurfaceProvider(surfaceProvider) }
                val analysis = ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
                val scanner = BarcodeScanning.getClient()
                analysis.setAnalyzer(ContextCompat.getMainExecutor(viewContext)) { proxy ->
                    val image = proxy.image
                    if (image == null) { proxy.close(); return@setAnalyzer }
                    scanner.process(InputImage.fromMediaImage(image, proxy.imageInfo.rotationDegrees))
                        .addOnSuccessListener { codes -> codes.firstOrNull()?.rawValue?.let(onResult) }
                        .addOnCompleteListener { proxy.close() }
                }
                provider.unbindAll()
                provider.bindToLifecycle(context as androidx.lifecycle.LifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
            }, ContextCompat.getMainExecutor(viewContext))
        }
    })
}
