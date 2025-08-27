/**
 * OPTIMIZED CameraX Implementation for Live Gesture Recognition
 * Recommended approach for real-time performance
 */

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer
import java.nio.ByteBuffer

class OptimizedCameraXGestureRecognizer : ImageAnalysis.Analyzer {
    
    companion object {
        private const val TAG = "GestureAnalyzer"
        private const val INFERENCE_THROTTLE_MS = 100L // Limit to 10 FPS for efficiency
    }
    
    private var gestureRecognizer: GestureRecognizer? = null
    private var lastInferenceTime = 0L
    private var listener: GestureRecognitionListener? = null
    
    interface GestureRecognitionListener {
        fun onGestureRecognized(gestureName: String, confidence: Float)
        fun onError(error: String)
    }
    
    fun setGestureRecognizer(recognizer: GestureRecognizer) {
        this.gestureRecognizer = recognizer
    }
    
    fun setListener(listener: GestureRecognitionListener) {
        this.listener = listener
    }
    
    override fun analyze(imageProxy: ImageProxy) {
        val currentTime = SystemClock.uptimeMillis()
        
        // Throttle inference to prevent overwhelming the GPU/CPU
        if (currentTime - lastInferenceTime < INFERENCE_THROTTLE_MS) {
            imageProxy.close()
            return
        }
        
        try {
            // METHOD 1: Direct YUV to RGB conversion (Most Efficient)
            val mpImage = convertImageProxyToMPImageOptimized(imageProxy)
            
            // METHOD 2: Alternative via Bitmap (if YUV conversion issues)
            // val mpImage = convertImageProxyViaBitmap(imageProxy)
            
            // Run inference
            gestureRecognizer?.recognizeAsync(mpImage, currentTime)
            lastInferenceTime = currentTime
            
        } catch (e: Exception) {
            Log.e(TAG, "Error during gesture recognition: ${e.message}", e)
            listener?.onError("Inference failed: ${e.message}")
        } finally {
            imageProxy.close()
        }
    }
    
    /**
     * METHOD 1: Optimized conversion - Direct YUV420 to RGB
     * Best performance for live recognition
     */
    private fun convertImageProxyToMPImageOptimized(imageProxy: ImageProxy): MPImage {
        // Get YUV planes
        val planes = imageProxy.planes
        val yBuffer = planes[0].buffer
        val uBuffer = planes[1].buffer  
        val vBuffer = planes[2].buffer
        
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        
        val nv21 = ByteArray(ySize + uSize + vSize)
        
        // Copy Y plane
        yBuffer.get(nv21, 0, ySize)
        
        // Copy UV planes
        val uvPixelStride = planes[1].pixelStride
        if (uvPixelStride == 1) {
            uBuffer.get(nv21, ySize, uSize)
            vBuffer.get(nv21, ySize + uSize, vSize)
        } else {
            // Handle interleaved UV
            val uvBuffer = ByteArray(uSize + vSize)
            uBuffer.get(uvBuffer, 0, uSize)
            vBuffer.get(uvBuffer, uSize, vSize)
            System.arraycopy(uvBuffer, 0, nv21, ySize, uvBuffer.size)
        }
        
        // Convert to RGB bitmap
        val bitmap = yuvToRgbBitmap(nv21, imageProxy.width, imageProxy.height)
        
        // Apply rotation and flip for front camera
        val processedBitmap = processBitmapForCamera(bitmap, imageProxy.imageInfo.rotationDegrees)
        
        return BitmapImageBuilder(processedBitmap).build()
    }
    
    /**
     * METHOD 2: Fallback conversion via Bitmap
     * More compatible but less efficient
     */
    private fun convertImageProxyViaBitmap(imageProxy: ImageProxy): MPImage {
        // Create bitmap buffer
        val bitmap = Bitmap.createBitmap(
            imageProxy.width, 
            imageProxy.height, 
            Bitmap.Config.ARGB_8888
        )
        
        // Copy pixels from ImageProxy
        imageProxy.use { 
            bitmap.copyPixelsFromBuffer(imageProxy.planes[0].buffer) 
        }
        
        // Process for camera orientation
        val processedBitmap = processBitmapForCamera(bitmap, imageProxy.imageInfo.rotationDegrees)
        
        return BitmapImageBuilder(processedBitmap).build()
    }
    
    /**
     * Process bitmap for camera orientation and front camera flip
     */
    private fun processBitmapForCamera(bitmap: Bitmap, rotationDegrees: Int): Bitmap {
        val matrix = Matrix().apply {
            // Rotate based on camera orientation
            postRotate(rotationDegrees.toFloat())
            
            // Flip horizontally for front camera (mirror effect)
            postScale(-1f, 1f, bitmap.width.toFloat(), bitmap.height.toFloat())
        }
        
        return Bitmap.createBitmap(
            bitmap, 0, 0, 
            bitmap.width, bitmap.height, 
            matrix, true
        )
    }
    
    /**
     * Convert YUV420 to RGB Bitmap (native implementation recommended)
     */
    private fun yuvToRgbBitmap(yuvData: ByteArray, width: Int, height: Int): Bitmap {
        // Note: In production, use RenderScript or native conversion for better performance
        // This is a simplified implementation
        
        val pixels = IntArray(width * height)
        var yIndex = 0
        var uvIndex = width * height
        
        for (y in 0 until height) {
            for (x in 0 until width) {
                val yValue = yuvData[yIndex++].toInt() and 0xFF
                val uValue = yuvData[uvIndex + (y / 2) * (width / 2) + (x / 2)].toInt() and 0xFF
                val vValue = yuvData[uvIndex + (width * height / 4) + (y / 2) * (width / 2) + (x / 2)].toInt() and 0xFF
                
                // YUV to RGB conversion
                val r = (yValue + 1.402 * (vValue - 128)).toInt().coerceIn(0, 255)
                val g = (yValue - 0.344136 * (uValue - 128) - 0.714136 * (vValue - 128)).toInt().coerceIn(0, 255)
                val b = (yValue + 1.772 * (uValue - 128)).toInt().coerceIn(0, 255)
                
                pixels[y * width + x] = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
            }
        }
        
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }
}

/**
 * USAGE IN CAMERA FRAGMENT
 */
class CameraFragmentExample {
    
    private lateinit var gestureAnalyzer: OptimizedCameraXGestureRecognizer
    
    private fun setupCamera() {
        // Initialize gesture analyzer
        gestureAnalyzer = OptimizedCameraXGestureRecognizer()
        gestureAnalyzer.setGestureRecognizer(gestureRecognizer)
        gestureAnalyzer.setListener(object : OptimizedCameraXGestureRecognizer.GestureRecognitionListener {
            override fun onGestureRecognized(gestureName: String, confidence: Float) {
                // Handle recognized gesture
                runOnUiThread {
                    handleGestureDetection(gestureName)
                }
            }
            
            override fun onError(error: String) {
                Log.e("CameraFragment", "Gesture recognition error: $error")
            }
        })
        
        // Setup ImageAnalysis
        val imageAnalyzer = ImageAnalysis.Builder()
            .setTargetResolution(Size(640, 480)) // Optimize resolution for performance
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
            .build()
            
        imageAnalyzer.setAnalyzer(ContextCompat.getMainExecutor(this), gestureAnalyzer)
        
        // Bind to camera lifecycle
        val cameraProvider = ProcessCameraProvider.getInstance(this).get()
        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageAnalyzer
            )
        } catch (exc: Exception) {
            Log.e("CameraFragment", "Use case binding failed", exc)
        }
    }
}
