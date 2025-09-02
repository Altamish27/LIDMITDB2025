package com.google.mediapipe.examples.gesturerecognizer.ui.overlay

import android.graphics.PointF
import android.util.Log
import android.util.Size
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult

/**
 * Interface for movement detection callbacks
 */
interface MovementDetectionListener {
    fun onLeftMovementDetected()
    fun onRightMovementDetected()
    fun onMovementStarted()
    fun onMovementStopped()
}

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
        private const val WRIST = 0 // MediaPipe landmark index for wrist
        private const val COORDINATE_TOLERANCE = 0.1f // Allow slight out-of-bounds coordinates
        private const val MOVEMENT_THRESHOLD = 0.05f // Minimum movement distance to register
        private const val LEFT_MOVEMENT_THRESHOLD = 0.1f // Minimum left movement for Fathah
    }
    
    private var movementListener: MovementDetectionListener? = null
    private var lastPosition: PointF? = null
    private var movementStartPosition: PointF? = null
    private var isTracking = false
    private var lastLogTime = 0L
    private val logIntervalMs = 500L
    
    fun setMovementListener(listener: MovementDetectionListener?) {
        this.movementListener = listener
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
                
                if (landmarks.size > WRIST) {
                    val wrist = landmarks[WRIST]
                    
                    // Get normalized coordinates from MediaPipe
                    val normalizedX = wrist.x()
                    val normalizedY = wrist.y()
                    
                    // Log original coordinates for debugging
                    val now = System.currentTimeMillis()
                    if (now - lastLogTime > logIntervalMs) {
                        Log.d(TAG, "Wrist coordinates: ($normalizedX, $normalizedY)")
                        lastLogTime = now
                    }
                    
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
                        
                        // Track movement for Fathah detection
                        trackMovement(clampedX, clampedY)
                        
                        if (System.currentTimeMillis() - lastLogTime > logIntervalMs) {
                            Log.d(TAG, "Added wrist trajectory point: ($clampedX, $clampedY)")
                        }
                    } else {
                        Log.w(TAG, "Invalid normalized coordinates: ($normalizedX, $normalizedY) - outside tolerance range")
                    }
                }
            } else {
                // No hand landmarks detected - reset trajectory
                Log.d(TAG, "No hand landmarks detected - resetting trajectory")
                clearTrajectory()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing trajectory result", e)
        }
    }
    
    /**
     * Track movement for Fathah detection
     */
    private fun trackMovement(x: Float, y: Float) {
        val currentPosition = PointF(x, y)
        
        lastPosition?.let { lastPos ->
            val deltaX = currentPosition.x - lastPos.x
            val deltaY = currentPosition.y - lastPos.y
            val distance = kotlin.math.sqrt(deltaX * deltaX + deltaY * deltaY)
            
            // Start tracking if significant movement detected
            if (!isTracking && distance > MOVEMENT_THRESHOLD) {
                isTracking = true
                movementStartPosition = lastPos
                movementListener?.onMovementStarted()
                Log.d(TAG, "Movement tracking started at ($lastPos)")
            }
            
            // If tracking, check for left movement
            if (isTracking) {
                movementStartPosition?.let { startPos ->
                    val totalDeltaX = currentPosition.x - startPos.x
                    val totalDistance = kotlin.math.abs(totalDeltaX)
                    
                    // For front camera (mirrored), left movement appears as positive deltaX
                    // For back camera (normal), left movement appears as negative deltaX
                    // Since we're using front camera, we check for positive deltaX for left movement
                    if (totalDeltaX > LEFT_MOVEMENT_THRESHOLD) {
                        Log.d(TAG, "Left movement detected (mirrored camera)! Delta: $totalDeltaX, Distance: $totalDistance")
                        movementListener?.onLeftMovementDetected()
                        resetMovementTracking()
                    } 
                    // Check for right movement (negative deltaX in mirrored camera)
                    else if (totalDeltaX < -LEFT_MOVEMENT_THRESHOLD) {
                        Log.d(TAG, "Right movement detected (mirrored camera)! Delta: $totalDeltaX, Distance: $totalDistance")
                        movementListener?.onRightMovementDetected()
                        resetMovementTracking()
                    }
                }
            }
        }
        
        lastPosition = currentPosition
    }
    
    /**
     * Reset movement tracking state
     */
    private fun resetMovementTracking() {
        if (isTracking) {
            isTracking = false
            movementStartPosition = null
            movementListener?.onMovementStopped()
            Log.d(TAG, "Movement tracking reset")
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
            resetMovementTracking()
            lastPosition = null
            Log.d(TAG, "Trajectory cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing trajectory", e)
        }
    }
}
