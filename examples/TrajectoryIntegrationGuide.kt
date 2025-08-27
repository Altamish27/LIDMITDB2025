/**
 * TRAJECTORY INTEGRATION untuk CameraFragment
 * Patch untuk menambahkan trajectory tracking ke existing CameraFragment
 */

// 1. Tambahkan import statements di bagian atas CameraFragment.kt
/*
import com.google.mediapipe.examples.gesturerecognizer.ui.overlay.TrajectoryOverlayView
import com.google.mediapipe.examples.gesturerecognizer.ui.overlay.TrajectoryRingBuffer
import com.google.mediapipe.examples.gesturerecognizer.ui.overlay.TrajectoryAnalyzer
import android.util.Size
import android.widget.FrameLayout
*/

// 2. Tambahkan properties di CameraFragment class
class CameraFragmentWithTrajectory {
    
    // Existing properties...
    // private lateinit var progressManager: HijaiyahProgressManager
    
    // NEW: Trajectory components
    private lateinit var trajectoryOverlay: TrajectoryOverlayView
    private lateinit var trajectoryBuffer: TrajectoryRingBuffer
    private lateinit var trajectoryAnalyzer: TrajectoryAnalyzer
    private var isTrajectoryEnabled = true
    
    // 3. Initialize trajectory components di onViewCreated()
    fun initializeTrajectoryComponents() {
        // Create trajectory components
        trajectoryBuffer = TrajectoryRingBuffer(
            capacity = 48,
            smoothingAlpha = 0.25f
        )
        
        trajectoryOverlay = TrajectoryOverlayView(requireContext()).apply {
            // Configure overlay appearance
            setBackgroundColor(Color.TRANSPARENT)
        }
        
        trajectoryAnalyzer = TrajectoryAnalyzer(
            trajectoryBuffer = trajectoryBuffer,
            overlayView = trajectoryOverlay,
            targetFps = 15
        )
        
        // Setup callbacks
        setupTrajectoryCallbacks()
        
        // Add overlay to fragment layout
        addTrajectoryOverlayToView()
    }
    
    // 4. Add overlay view to existing layout
    private fun addTrajectoryOverlayToView() {
        // Find the main container (biasanya FrameLayout yang berisi PreviewView)
        val mainContainer = fragmentCameraBinding.root as? FrameLayout
            ?: throw IllegalStateException("Root view must be FrameLayout untuk trajectory overlay")
        
        // Add trajectory overlay di atas PreviewView
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        
        trajectoryOverlay.layoutParams = layoutParams
        mainContainer.addView(trajectoryOverlay)
        
        // Ensure overlay is on top
        trajectoryOverlay.bringToFront()
        
        Log.d(TAG, "Trajectory overlay added to view hierarchy")
    }
    
    // 5. Setup trajectory callbacks
    private fun setupTrajectoryCallbacks() {
        trajectoryAnalyzer.setOnTrajectoryRecordedListener { trajectoryData ->
            Log.d(TAG, "Trajectory recorded with ${trajectoryData.points.size} points")
            // Optionally save to file or send to server
        }
        
        trajectoryAnalyzer.setOnSegmentDetectedListener { startIdx, endIdx ->
            Log.d(TAG, "Trajectory segment detected: $startIdx -> $endIdx")
            // Handle gesture segmentation
        }
        
        trajectoryOverlay.setOnDirectionChangedListener { direction ->
            Log.d(TAG, "Direction changed to: $direction")
        }
    }
    
    // 6. MODIFY existing onResults method
    override fun onResults(resultBundle: GestureRecognizerHelper.ResultBundle) {
        activity?.runOnUiThread {
            if (_fragmentCameraBinding != null) {
                // EXISTING CODE untuk gesture recognition...
                val gestureCategories = resultBundle.results.first().gestures()
                if (gestureCategories.isNotEmpty()) {
                    gestureRecognizerResultAdapter.updateResults(
                        gestureCategories.first()
                    )
                    
                    val topGesture = gestureCategories.first().first()
                    val detectedGestureName = topGesture.categoryName()
                    handleGestureDetection(detectedGestureName)
                } else {
                    gestureRecognizerResultAdapter.updateResults(emptyList())
                    handleGestureDetection("")
                }

                // EXISTING overlay code...
                fragmentCameraBinding.bottomSheetLayout.inferenceTimeVal.text =
                    String.format("%d ms", resultBundle.inferenceTime)

                fragmentCameraBinding.overlay.setResults(
                    resultBundle.results.first(),
                    resultBundle.inputImageHeight,
                    resultBundle.inputImageWidth,
                    RunningMode.LIVE_STREAM
                )

                // Force a redraw
                fragmentCameraBinding.overlay.invalidate()
                
                // NEW: Process trajectory
                if (isTrajectoryEnabled) {
                    processTrajectoryFromResult(resultBundle)
                }
            }
        }
    }
    
    // 7. NEW: Process trajectory dari MediaPipe result
    private fun processTrajectoryFromResult(resultBundle: GestureRecognizerHelper.ResultBundle) {
        val result = resultBundle.results.first()
        val imageSize = Size(resultBundle.inputImageWidth, resultBundle.inputImageHeight)
        val viewSize = Size(
            fragmentCameraBinding.viewFinder.width,
            fragmentCameraBinding.viewFinder.height
        )
        
        // Get camera rotation
        val rotationDegrees = fragmentCameraBinding.viewFinder.display.rotation * 90
        val isFrontCamera = cameraFacing == CameraSelector.LENS_FACING_FRONT
        
        // Process pada background thread untuk avoid blocking UI
        backgroundExecutor.execute {
            trajectoryAnalyzer.processResult(
                result = result,
                imageSize = imageSize,
                viewSize = viewSize,
                isFrontCamera = isFrontCamera,
                rotationDegrees = rotationDegrees
            )
        }
    }
    
    // 8. NEW: Trajectory control methods
    fun startTrajectoryRecording() {
        trajectoryAnalyzer.startRecording()
        Log.d(TAG, "Started trajectory recording")
    }
    
    fun stopTrajectoryRecording(): TrajectoryData? {
        val data = trajectoryAnalyzer.stopRecording()
        Log.d(TAG, "Stopped trajectory recording")
        return data
    }
    
    fun clearTrajectory() {
        trajectoryAnalyzer.clearTrajectory()
        Log.d(TAG, "Trajectory cleared")
    }
    
    fun toggleTrajectoryVisibility() {
        isTrajectoryEnabled = !isTrajectoryEnabled
        trajectoryOverlay.visibility = if (isTrajectoryEnabled) View.VISIBLE else View.GONE
        Log.d(TAG, "Trajectory visibility: $isTrajectoryEnabled")
    }
    
    // 9. MODIFY onPause untuk cleanup trajectory
    override fun onPause() {
        super.onPause()
        
        // Existing cleanup...
        if (this::gestureRecognizerHelper.isInitialized) {
            // ... existing code
        }
        
        // NEW: Cleanup trajectory
        if (::trajectoryAnalyzer.isInitialized) {
            trajectoryAnalyzer.clearTrajectory()
        }
        
        // Cancel all timers
        practiceTimer?.cancel()
        resetTimer?.cancel()
        countdownTimer?.cancel()
    }
    
    // 10. MODIFY onDestroyView untuk cleanup
    override fun onDestroyView() {
        // NEW: Cleanup trajectory overlay
        if (::trajectoryOverlay.isInitialized) {
            val parent = trajectoryOverlay.parent as? ViewGroup
            parent?.removeView(trajectoryOverlay)
        }
        
        // Existing cleanup...
        _fragmentCameraBinding = null
        super.onDestroyView()
        
        backgroundExecutor.shutdown()
        backgroundExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
        
        practiceTimer?.cancel()
        resetTimer?.cancel()
    }
}

// 11. USAGE EXAMPLE: Add control buttons to layout
/*
Di fragment_camera.xml, tambahkan controls:

<LinearLayout
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="bottom|start"
    android:layout_margin="16dp"
    android:orientation="vertical">
    
    <Button
        android:id="@+id/btnTrajectoryRecord"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Record"
        android:background="@drawable/circle_button_background" />
    
    <Button
        android:id="@+id/btnTrajectoryClear"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Clear"
        android:layout_marginTop="8dp"
        android:background="@drawable/circle_button_background" />
        
    <Button
        android:id="@+id/btnTrajectoryToggle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Toggle"
        android:layout_marginTop="8dp"
        android:background="@drawable/circle_button_background" />
        
</LinearLayout>
*/

// 12. Setup button click listeners
fun setupTrajectoryControls() {
    fragmentCameraBinding.btnTrajectoryRecord?.setOnClickListener {
        if (trajectoryAnalyzer.isRecording()) {
            stopTrajectoryRecording()
            it.text = "Record"
        } else {
            startTrajectoryRecording()
            it.text = "Stop"
        }
    }
    
    fragmentCameraBinding.btnTrajectoryClear?.setOnClickListener {
        clearTrajectory()
    }
    
    fragmentCameraBinding.btnTrajectoryToggle?.setOnClickListener {
        toggleTrajectoryVisibility()
    }
}

/*
FINAL INTEGRATION STEPS:

1. Add import statements
2. Add trajectory properties to class
3. Call initializeTrajectoryComponents() di onViewCreated()
4. Modify onResults() untuk call processTrajectoryFromResult()
5. Add trajectory cleanup di onPause() dan onDestroyView()
6. Add control buttons ke layout (optional)
7. Setup button click listeners (optional)

PERFORMANCE TIPS:
- Trajectory processing di background thread
- UI updates di main thread
- 15 FPS throttling untuk efficiency
- Ring buffer prevents memory leaks
- Coordinate mapping optimized untuk different rotations

THREAD SAFETY:
- TrajectoryAnalyzer handles thread coordination
- TrajectoryRingBuffer is thread-safe
- UI updates always di main thread
- Background processing untuk heavy operations
*/
