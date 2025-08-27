/**
 * BITMAP-BASED Gesture Recognition Implementation
 * Best for: Static images, gallery photos, testing, or when you have pre-processed images
 */

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

class BitmapGestureRecognizer {
    
    companion object {
        private const val TAG = "BitmapGestureRecognizer"
        private const val MAX_IMAGE_SIZE = 1024 // Optimize size for performance
    }
    
    private var gestureRecognizer: GestureRecognizer? = null
    
    interface BitmapRecognitionListener {
        fun onGestureRecognized(results: List<GestureRecognizerResult>, bitmap: Bitmap)
        fun onError(error: String)
    }
    
    fun setGestureRecognizer(recognizer: GestureRecognizer) {
        this.gestureRecognizer = recognizer
    }
    
    /**
     * METHOD 1: Recognize gesture from Bitmap (for static images)
     */
    suspend fun recognizeFromBitmap(
        bitmap: Bitmap, 
        listener: BitmapRecognitionListener
    ) = withContext(Dispatchers.Default) {
        try {
            val processedBitmap = preprocessBitmap(bitmap)
            val mpImage = BitmapImageBuilder(processedBitmap).build()
            
            // For bitmap recognition, use IMAGE running mode
            val result = gestureRecognizer?.recognize(mpImage)
            
            result?.let { 
                withContext(Dispatchers.Main) {
                    listener.onGestureRecognized(listOf(it), processedBitmap)
                }
            } ?: run {
                withContext(Dispatchers.Main) {
                    listener.onError("Recognition failed: No result returned")
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error recognizing from bitmap: ${e.message}", e)
            withContext(Dispatchers.Main) {
                listener.onError("Recognition error: ${e.message}")
            }
        }
    }
    
    /**
     * METHOD 2: Recognize from Image URI (gallery, camera capture, etc.)
     */
    suspend fun recognizeFromUri(
        uri: Uri, 
        inputStream: InputStream,
        listener: BitmapRecognitionListener
    ) = withContext(Dispatchers.IO) {
        try {
            // Decode bitmap with efficient sampling
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            
            // Calculate sample size for optimization
            val sampleSize = calculateInSampleSize(options, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE)
            
            // Reset stream and decode with sampling
            inputStream.reset()
            val finalOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            
            val bitmap = BitmapFactory.decodeStream(inputStream, null, finalOptions)
            bitmap?.let {
                recognizeFromBitmap(it, listener)
            } ?: run {
                withContext(Dispatchers.Main) {
                    listener.onError("Failed to decode image from URI")
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error recognizing from URI: ${e.message}", e)
            withContext(Dispatchers.Main) {
                listener.onError("URI recognition error: ${e.message}")
            }
        }
    }
    
    /**
     * METHOD 3: Batch processing multiple bitmaps
     */
    suspend fun recognizeBatch(
        bitmaps: List<Bitmap>,
        listener: (results: List<Pair<Bitmap, GestureRecognizerResult?>>) -> Unit
    ) = withContext(Dispatchers.Default) {
        try {
            val results = mutableListOf<Pair<Bitmap, GestureRecognizerResult?>>()
            
            bitmaps.forEach { bitmap ->
                val processedBitmap = preprocessBitmap(bitmap)
                val mpImage = BitmapImageBuilder(processedBitmap).build()
                val result = gestureRecognizer?.recognize(mpImage)
                results.add(Pair(processedBitmap, result))
            }
            
            withContext(Dispatchers.Main) {
                listener(results)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in batch recognition: ${e.message}", e)
        }
    }
    
    /**
     * Preprocess bitmap for optimal recognition
     */
    private fun preprocessBitmap(bitmap: Bitmap): Bitmap {
        var processedBitmap = bitmap
        
        // 1. Resize if too large
        if (bitmap.width > MAX_IMAGE_SIZE || bitmap.height > MAX_IMAGE_SIZE) {
            val scale = minOf(
                MAX_IMAGE_SIZE.toFloat() / bitmap.width,
                MAX_IMAGE_SIZE.toFloat() / bitmap.height
            )
            val newWidth = (bitmap.width * scale).toInt()
            val newHeight = (bitmap.height * scale).toInt()
            
            processedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        }
        
        // 2. Ensure correct format
        if (processedBitmap.config != Bitmap.Config.ARGB_8888) {
            val argbBitmap = processedBitmap.copy(Bitmap.Config.ARGB_8888, false)
            if (processedBitmap != bitmap) {
                processedBitmap.recycle()
            }
            processedBitmap = argbBitmap
        }
        
        // 3. Apply any rotation correction if needed
        // processedBitmap = correctOrientation(processedBitmap)
        
        return processedBitmap
    }
    
    /**
     * Calculate optimal sample size for large images
     */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            
            while ((halfHeight / inSampleSize) >= reqHeight && 
                   (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        
        return inSampleSize
    }
    
    /**
     * Optional: Correct image orientation based on EXIF data
     */
    private fun correctOrientation(bitmap: Bitmap, rotationDegrees: Int = 0): Bitmap {
        if (rotationDegrees == 0) return bitmap
        
        val matrix = Matrix().apply {
            postRotate(rotationDegrees.toFloat())
        }
        
        return Bitmap.createBitmap(
            bitmap, 0, 0, 
            bitmap.width, bitmap.height, 
            matrix, true
        )
    }
}

/**
 * USAGE EXAMPLES
 */
class BitmapRecognitionExamples {
    
    private val bitmapRecognizer = BitmapGestureRecognizer()
    
    // Example 1: Recognize from gallery image
    suspend fun recognizeGalleryImage(uri: Uri, inputStream: InputStream) {
        bitmapRecognizer.recognizeFromUri(uri, inputStream, object : BitmapGestureRecognizer.BitmapRecognitionListener {
            override fun onGestureRecognized(results: List<GestureRecognizerResult>, bitmap: Bitmap) {
                // Handle recognition results
                results.forEach { result ->
                    if (result.gestures().isNotEmpty()) {
                        val gesture = result.gestures().first().first()
                        Log.d("Recognition", "Detected: ${gesture.categoryName()} (${gesture.score()})")
                    }
                }
            }
            
            override fun onError(error: String) {
                Log.e("Recognition", "Error: $error")
            }
        })
    }
    
    // Example 2: Recognize from camera capture
    suspend fun recognizeCameraCapture(bitmap: Bitmap) {
        bitmapRecognizer.recognizeFromBitmap(bitmap, object : BitmapGestureRecognizer.BitmapRecognitionListener {
            override fun onGestureRecognized(results: List<GestureRecognizerResult>, bitmap: Bitmap) {
                // Process single image result
                val topResult = results.firstOrNull()
                topResult?.let { result ->
                    if (result.gestures().isNotEmpty()) {
                        val topGesture = result.gestures().first().first()
                        showResult(topGesture.categoryName(), topGesture.score())
                    }
                }
            }
            
            override fun onError(error: String) {
                showError(error)
            }
        })
    }
    
    private fun showResult(gestureName: String, confidence: Float) {
        // Update UI with recognition result
    }
    
    private fun showError(error: String) {
        // Show error message to user
    }
}
