package com.google.mediapipe.examples.gesturerecognizer.ui.overlay

import android.graphics.PointF
import android.util.Size
import kotlin.math.*

/**
 * Utility untuk mapping koordinat normalized MediaPipe ke view coordinates
 * Handles rotation dan mirroring untuk front camera
 */
object CoordinateMapper {
    
    /**
     * Map normalized coordinates [0..1] dari MediaPipe ke view coordinates
     * 
     * @param xNorm Normalized X coordinate [0..1] dari MediaPipe
     * @param yNorm Normalized Y coordinate [0..1] dari MediaPipe
     * @param imageSize Ukuran image yang digunakan untuk inference
     * @param viewSize Ukuran view overlay
     * @param rotationDegrees Rotation dalam derajat (0, 90, 180, 270)
     * @param isFrontCamera Apakah menggunakan front camera (perlu mirroring)
     * @return PointF dalam koordinat view
     */
    fun mapNormalizedToView(
        xNorm: Float,
        yNorm: Float,
        imageSize: Size,
        viewSize: Size,
        rotationDegrees: Int,
        isFrontCamera: Boolean
    ): PointF {
        // Step 1: Convert normalized ke image coordinates
        var x = xNorm * imageSize.width
        var y = yNorm * imageSize.height
        
        // Step 2: Apply rotation transformation
        val rotatedPoint = applyRotation(x, y, imageSize, rotationDegrees)
        x = rotatedPoint.x
        y = rotatedPoint.y
        
        // Step 3: Get rotated image dimensions
        val rotatedImageSize = getRotatedSize(imageSize, rotationDegrees)
        
        // Step 4: Apply front camera mirroring (hanya untuk display)
        if (isFrontCamera) {
            x = rotatedImageSize.width - x
        }
        
        // Step 5: Scale to view size dengan aspect ratio preservation
        val scaleX = viewSize.width.toFloat() / rotatedImageSize.width
        val scaleY = viewSize.height.toFloat() / rotatedImageSize.height
        val scale = minOf(scaleX, scaleY)
        
        // Center the scaled image dalam view
        val scaledWidth = rotatedImageSize.width * scale
        val scaledHeight = rotatedImageSize.height * scale
        val offsetX = (viewSize.width - scaledWidth) / 2
        val offsetY = (viewSize.height - scaledHeight) / 2
        
        return PointF(
            offsetX + x * scale,
            offsetY + y * scale
        )
    }
    
    /**
     * Apply rotation transformation ke coordinates
     */
    private fun applyRotation(
        x: Float,
        y: Float,
        imageSize: Size,
        rotationDegrees: Int
    ): PointF {
        val centerX = imageSize.width / 2f
        val centerY = imageSize.height / 2f
        
        // Translate ke origin
        val translatedX = x - centerX
        val translatedY = y - centerY
        
        // Apply rotation
        val radians = Math.toRadians(rotationDegrees.toDouble())
        val cos = cos(radians).toFloat()
        val sin = sin(radians).toFloat()
        
        val rotatedX = translatedX * cos - translatedY * sin
        val rotatedY = translatedX * sin + translatedY * cos
        
        // Translate back, tapi ke rotated image space
        val rotatedImageSize = getRotatedSize(imageSize, rotationDegrees)
        
        return PointF(
            rotatedX + rotatedImageSize.width / 2f,
            rotatedY + rotatedImageSize.height / 2f
        )
    }
    
    /**
     * Get size setelah rotation
     */
    private fun getRotatedSize(originalSize: Size, rotationDegrees: Int): Size {
        return when (rotationDegrees) {
            90, 270 -> Size(originalSize.height, originalSize.width)
            else -> originalSize
        }
    }
    
    /**
     * Map view coordinates kembali ke normalized coordinates
     * Useful untuk testing atau reverse mapping
     */
    fun mapViewToNormalized(
        viewX: Float,
        viewY: Float,
        imageSize: Size,
        viewSize: Size,
        rotationDegrees: Int,
        isFrontCamera: Boolean
    ): PointF {
        // Reverse the scaling
        val rotatedImageSize = getRotatedSize(imageSize, rotationDegrees)
        val scaleX = viewSize.width.toFloat() / rotatedImageSize.width
        val scaleY = viewSize.height.toFloat() / rotatedImageSize.height
        val scale = minOf(scaleX, scaleY)
        
        val scaledWidth = rotatedImageSize.width * scale
        val scaledHeight = rotatedImageSize.height * scale
        val offsetX = (viewSize.width - scaledWidth) / 2
        val offsetY = (viewSize.height - scaledHeight) / 2
        
        var x = (viewX - offsetX) / scale
        var y = (viewY - offsetY) / scale
        
        // Reverse front camera mirroring
        if (isFrontCamera) {
            x = rotatedImageSize.width - x
        }
        
        // Reverse rotation
        val originalPoint = reverseRotation(x, y, imageSize, rotationDegrees)
        
        // Convert back to normalized
        return PointF(
            originalPoint.x / imageSize.width,
            originalPoint.y / imageSize.height
        )
    }
    
    private fun reverseRotation(
        x: Float,
        y: Float,
        originalImageSize: Size,
        rotationDegrees: Int
    ): PointF {
        val rotatedImageSize = getRotatedSize(originalImageSize, rotationDegrees)
        
        // Translate to origin of rotated space
        val translatedX = x - rotatedImageSize.width / 2f
        val translatedY = y - rotatedImageSize.height / 2f
        
        // Apply reverse rotation
        val radians = Math.toRadians(-rotationDegrees.toDouble())
        val cos = cos(radians).toFloat()
        val sin = sin(radians).toFloat()
        
        val originalX = translatedX * cos - translatedY * sin
        val originalY = translatedX * sin + translatedY * cos
        
        // Translate back to original space
        return PointF(
            originalX + originalImageSize.width / 2f,
            originalY + originalImageSize.height / 2f
        )
    }
    
    /**
     * Calculate distance antara dua points dalam view space
     */
    fun calculateDistance(point1: PointF, point2: PointF): Float {
        val dx = point2.x - point1.x
        val dy = point2.y - point1.y
        return sqrt(dx * dx + dy * dy)
    }
    
    /**
     * Calculate angle dalam derajat antara dua points
     */
    fun calculateAngle(from: PointF, to: PointF): Float {
        val dx = to.x - from.x
        val dy = to.y - from.y
        return Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
    }
    
    /**
     * Check apakah point berada dalam bounds view
     */
    fun isPointInView(point: PointF, viewSize: Size): Boolean {
        return point.x >= 0 && point.x <= viewSize.width &&
               point.y >= 0 && point.y <= viewSize.height
    }
    
    /**
     * Clamp point ke dalam view bounds
     */
    fun clampToView(point: PointF, viewSize: Size): PointF {
        return PointF(
            point.x.coerceIn(0f, viewSize.width.toFloat()),
            point.y.coerceIn(0f, viewSize.height.toFloat())
        )
    }
}
