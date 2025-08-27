package com.google.mediapipe.examples.gesturerecognizer.ui.overlay

import android.graphics.PointF
import android.util.Log
import android.util.Size
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult

/**
 * Trajectory Analyzer - Processes MediaPipe results and converts finger landmarks to trajectory points
 * with coordinate validation and clamping to handle out-of-bounds values
 */
class TrajectoryAnalyzer(
    private val trajectoryBuffer: TrajectoryRingBuffer,
    private val trajectoryOverlay: TrajectoryOverlayView
) {
    companion object {
        private const val TAG = "TrajectoryAnalyzer"
        private const val INDEX_FINGER_TIP = 8 // MediaPipe landmark index for finger tip
        private const val COORDINATE_TOLERANCE = 0.1f // Allow slight out-of-bounds coordinates
    }
    
    /**
     * Process MediaPipe gesture recognition results and extract trajectory points
     */
    fun processResult(
        result: GestureRecognizerResult, 
        viewSize: Size,
        imageSize: Size,
        isFrontCamera: Boolean,
        rotationDegrees: Int
    ) {
        try {
            // Check if we have hand landmarks
            if (result.landmarks().isNotEmpty()) {
                val landmarks = result.landmarks()[0] // First hand
                
                if (landmarks.size > INDEX_FINGER_TIP) {
                    val fingerTip = landmarks[INDEX_FINGER_TIP]
                    
                    // Get normalized coordinates from MediaPipe
                    val normalizedX = fingerTip.x()
                    val normalizedY = fingerTip.y()
                    
                    // Log original coordinates for debugging
                    Log.d(TAG, "Original coordinates: ($normalizedX, $normalizedY)")
                    
                    // Validate and clamp coordinates with tolerance
                    if (isValidCoordinate(normalizedX, normalizedY)) {
                        // Clamp coordinates to valid [0,1] range
                        var clampedX = normalizedX.coerceIn(0f, 1f)
                        val clampedY = normalizedY.coerceIn(0f, 1f)
                        
                        // Fix mirroring for front camera
                        // Front camera shows mirrored view, so we need to flip X coordinate
                        if (isFrontCamera) {
                            clampedX = 1.0f - clampedX
                            Log.d(TAG, "Front camera detected - flipped X: $normalizedX -> $clampedX")
                        }
                        
                        // Convert to screen coordinates (normalized [0,1] range)
                        // The TrajectoryRingBuffer and TrajectoryOverlayView will handle 
                        // the conversion to actual screen coordinates
                        
                        // Add point to buffer using pushPoint
                        trajectoryBuffer.pushPoint(clampedX, clampedY)
                        
                        // Update overlay with all points
                        trajectoryOverlay.updateTrajectory(
                            trajectoryBuffer.asList(),
                            viewSize,
                            imageSize,
                            isFrontCamera,
                            rotationDegrees
                        )
                        
                        Log.d(TAG, "Added trajectory point: normalized ($clampedX, $clampedY)")
                    } else {
                        Log.w(TAG, "Invalid normalized coordinates: ($normalizedX, $normalizedY) - outside tolerance range")
                    }
                }
            } else {
                Log.d(TAG, "No hand landmarks detected")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing trajectory result", e)
        }
    }
    
    /**
     * Validate if coordinates are within acceptable tolerance range
     */
    private fun isValidCoordinate(x: Float, y: Float): Boolean {
        val minBound = -COORDINATE_TOLERANCE
        val maxBound = 1f + COORDINATE_TOLERANCE
        
        return x >= minBound && x <= maxBound && y >= minBound && y <= maxBound
    }
    
    /**
     * Clear trajectory buffer and overlay
     */
    fun clearTrajectory() {
        try {
            trajectoryBuffer.clear()
            trajectoryOverlay.clearTrajectory()
            Log.d(TAG, "Trajectory cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing trajectory", e)
        }
    }
}
