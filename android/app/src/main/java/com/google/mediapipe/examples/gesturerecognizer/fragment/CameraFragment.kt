/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.mediapipe.examples.gesturerecognizer.fragment

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.PointF
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.mediapipe.examples.gesturerecognizer.GestureRecognizerHelper
import com.google.mediapipe.examples.gesturerecognizer.HijaiyahData
import com.google.mediapipe.examples.gesturerecognizer.MainViewModel
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.databinding.FragmentCameraBinding
import com.google.mediapipe.examples.gesturerecognizer.ui.adapter.GestureRecognizerResultsAdapter
import com.google.mediapipe.examples.gesturerecognizer.ui.permissions.PermissionsFragment
import com.google.mediapipe.examples.gesturerecognizer.ui.overlay.TrajectoryOverlayView
import com.google.mediapipe.examples.gesturerecognizer.ui.overlay.TrajectoryRingBuffer
import com.google.mediapipe.examples.gesturerecognizer.ui.overlay.TrajectoryAnalyzer
import com.google.mediapipe.examples.gesturerecognizer.ui.overlay.MovementDetectionListener
import com.google.mediapipe.examples.gesturerecognizer.ui.overlay.MovementType
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahProgressManager
import com.google.mediapipe.examples.gesturerecognizer.data.FathahData
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CameraFragment : Fragment(),
    GestureRecognizerHelper.GestureRecognizerListener,
    MovementDetectionListener {

    companion object {
        private const val TAG = "Hand gesture recognizer"
        private const val REQUIRED_DURATION = 2000L // 2 seconds
        private const val RESET_DELAY = 500L // Reset after 0.5s of wrong gesture
    }

    private var _fragmentCameraBinding: FragmentCameraBinding? = null

    private val fragmentCameraBinding
        get() = _fragmentCameraBinding!!

    private lateinit var gestureRecognizerHelper: GestureRecognizerHelper
    private val viewModel: MainViewModel by activityViewModels()
    private var defaultNumResults = 1
    private val gestureRecognizerResultAdapter: GestureRecognizerResultsAdapter by lazy {
        GestureRecognizerResultsAdapter().apply {
            updateAdapterSize(defaultNumResults)
        }
    }
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraFacing = CameraSelector.LENS_FACING_FRONT

    /** Blocking ML operations are performed using this executor */
    private lateinit var backgroundExecutor: ExecutorService
    
    // Trajectory system components
    private lateinit var trajectoryBuffer: TrajectoryRingBuffer
    private lateinit var trajectoryOverlay: TrajectoryOverlayView
    private lateinit var trajectoryAnalyzer: TrajectoryAnalyzer
    
    // Hijaiyah practice properties
    private var targetLetter: String? = null
    private var targetLetterName: String? = null
    private var letterType: String? = null
    private var diacritic: String? = null
    private var practiceTimer: CountDownTimer? = null
    private var resetTimer: CountDownTimer? = null
    private var countdownTimer: CountDownTimer? = null
    private lateinit var progressManager: HijaiyahProgressManager
    private var isDetecting = false
    private var currentGesture: String? = null
    private var gestureStartTime = 0L
    private var consecutiveCorrectCount = 0
    
    // Fathah specific properties
    private var isFathahMode = false
    private var hijaiyahGestureDetected = false
    private var isWaitingForLeftMovement = false
    private var fathahPatternDetected = false // Prevent multiple detection
    
    // Movement history tracking for diacritics (Fathah, Dhammah, etc.)
    enum class MovementDirection {
        STATIC, LEFT, RIGHT, UP, DOWN, DIAGONAL_UP_LEFT, DIAGONAL_UP_RIGHT, 
        DIAGONAL_DOWN_LEFT, DIAGONAL_DOWN_RIGHT, UNKNOWN
    }
    
    private val movementHistory = mutableListOf<MovementDirection>()
    private val MAX_MOVEMENT_HISTORY = 5
    private var lastHandPosition: PointF? = null
    private val MOVEMENT_THRESHOLD = 30.0f // pixels threshold for movement detection
    
    // Track current static state from unified movement detection
    private var isCurrentlyStatic: Boolean = true

    override fun onResume() {
        super.onResume()
        // Make sure that all permissions are still present, since the
        // user could have removed them while the app was in paused state.
        if (!PermissionsFragment.hasPermissions(requireContext())) {
            Navigation.findNavController(
                requireActivity(), R.id.fragment_container
            ).navigate(R.id.action_camera_to_permissions)
        }

        // Start the GestureRecognizerHelper again when users come back
        // to the foreground.
        backgroundExecutor.execute {
            if (gestureRecognizerHelper.isClosed()) {
                gestureRecognizerHelper.setupGestureRecognizer()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (this::gestureRecognizerHelper.isInitialized) {
            viewModel.setMinHandDetectionConfidence(gestureRecognizerHelper.minHandDetectionConfidence)
            viewModel.setMinHandTrackingConfidence(gestureRecognizerHelper.minHandTrackingConfidence)
            viewModel.setMinHandPresenceConfidence(gestureRecognizerHelper.minHandPresenceConfidence)
            viewModel.setDelegate(gestureRecognizerHelper.currentDelegate)

            // Close the Gesture Recognizer helper and release resources
            backgroundExecutor.execute { gestureRecognizerHelper.clearGestureRecognizer() }
        }
    }

    override fun onDestroyView() {
        _fragmentCameraBinding = null
        super.onDestroyView()

        // Shut down our background executor
        backgroundExecutor.shutdown()
        backgroundExecutor.awaitTermination(
            Long.MAX_VALUE, TimeUnit.NANOSECONDS
        )
        
        // Cancel timer if active
        practiceTimer?.cancel()
        resetTimer?.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentCameraBinding =
            FragmentCameraBinding.inflate(inflater, container, false)

        return fragmentCameraBinding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Get target letter from arguments
        targetLetter = arguments?.getString("selectedLetter") ?: arguments?.getString("target_letter")
        targetLetterName = arguments?.getString("letterName") ?: arguments?.getString("target_letter_name")
        letterType = arguments?.getString("letterType")
        diacritic = arguments?.getString("diacritic")
        
        // Set Fathah mode
        isFathahMode = diacritic == "fathah"
        
        // Debug logging for received arguments
        Log.d(TAG, "Received arguments:")
        Log.d(TAG, "- targetLetter: $targetLetter")
        Log.d(TAG, "- targetLetterName: $targetLetterName")
        Log.d(TAG, "- letterType: $letterType")
        Log.d(TAG, "- diacritic: $diacritic")
        Log.d(TAG, "- isFathahMode: $isFathahMode")
        Log.d(TAG, "- all arguments: ${arguments?.keySet()?.joinToString { "$it=${arguments?.get(it)}" }}")
        
        // Setup UI with target letter
        setupHijaiyahUI()
        
        with(fragmentCameraBinding.recyclerviewResults) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = gestureRecognizerResultAdapter
        }

        // Initialize our background executor
        backgroundExecutor = Executors.newSingleThreadExecutor()
    // Initialize progress manager for marking letters completed
    progressManager = HijaiyahProgressManager(requireContext())
        
        // Initialize trajectory system
        trajectoryBuffer = TrajectoryRingBuffer()
        trajectoryOverlay = TrajectoryOverlayView(requireContext())
        
        // Set up unified movement detection listener
        trajectoryOverlay.setMovementDetectionListener(this)
        
        // Add trajectory overlay to the camera container programmatically
        val cameraContainer = fragmentCameraBinding.cameraContainer
        cameraContainer.addView(trajectoryOverlay)
        
        trajectoryAnalyzer = TrajectoryAnalyzer(trajectoryBuffer, trajectoryOverlay)
        
        // DEPRECATED: Old movement detection listener - now using unified detection via TrajectoryOverlayView
        // if (isFathahMode) {
        //     trajectoryAnalyzer.setMovementListener(this)
        // }

        // Wait for the views to be properly laid out
        fragmentCameraBinding.viewFinder.post {
            // Set up the camera and its use cases
            setUpCamera()
        }

        // Create the Hand Gesture Recognition Helper that will handle the
        // inference
        backgroundExecutor.execute {
            gestureRecognizerHelper = GestureRecognizerHelper(
                context = requireContext(),
                runningMode = RunningMode.LIVE_STREAM,
                minHandDetectionConfidence = viewModel.currentMinHandDetectionConfidence,
                minHandTrackingConfidence = viewModel.currentMinHandTrackingConfidence,
                minHandPresenceConfidence = viewModel.currentMinHandPresenceConfidence,
                currentDelegate = viewModel.currentDelegate,
                gestureRecognizerListener = this
            )
        }

        // Attach listeners to UI control widgets
        initBottomSheetControls()
    }
    
    private fun setupHijaiyahUI() {
        // Setup target letter display
        targetLetter?.let { letter ->
            fragmentCameraBinding.textTargetLetter.text = letter
        }
        
        targetLetterName?.let { name ->
            fragmentCameraBinding.textLetterName.text = "Huruf $name"
            // instruction text removed from layout to avoid overlaying camera preview
        }
        
        // Setup back button
        fragmentCameraBinding.buttonBack.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.fragment_container)
                .navigateUp()
        }
        
        // Start automatic detection
        startAutomaticDetection()
        
        // Initially hide result overlay
        fragmentCameraBinding.overlayResult.visibility = View.GONE
    }
    
    private fun startAutomaticDetection() {
        isDetecting = true
        gestureStartTime = 0L
        consecutiveCorrectCount = 0
        
        // Hide start button and show detection status
        fragmentCameraBinding.buttonStart.visibility = View.GONE
        fragmentCameraBinding.progressTimer.visibility = View.VISIBLE
        fragmentCameraBinding.textCountdown.visibility = View.VISIBLE
        
        // Reset progress
        fragmentCameraBinding.progressTimer.progress = 0
        fragmentCameraBinding.textCountdown.text = "Mulai gesture..."
        
    // Instruction text removed; no UI update here
    }
    
    private fun checkHandStaticStatus(): Boolean {
        // Use trajectory overlay logic to check if hand is static
        val trajectoryPoints = trajectoryBuffer.asList()
        if (trajectoryPoints.size < 2) return false
        
        val start = trajectoryPoints.first()
        val end = trajectoryPoints.last()
        val dx = end.x - start.x
        val dy = end.y - start.y
        
        // Convert to screen coordinates for threshold comparison
        val screenDx = dx * fragmentCameraBinding.viewFinder.width
        val screenDy = dy * fragmentCameraBinding.viewFinder.height
        
        // Static if movement is less than 20 pixels (same as TrajectoryOverlayView)
        return (kotlin.math.abs(screenDx) < 20 && kotlin.math.abs(screenDy) < 20)
    }
    
    private fun checkStaticDurationForMovement(isHandStatic: Boolean, currentTime: Long): Boolean {
        // This method is deprecated - using movement history instead
        return true
    }
    
    private fun resetStaticTracking() {
        // This method is deprecated - using movement history instead
        movementHistory.clear()
    }
    
    private fun detectMovementDirection(currentHandPosition: PointF): MovementDirection {
        if (lastHandPosition == null) {
            lastHandPosition = currentHandPosition
            Log.d(TAG, "🎯 Initial hand position set: (${"%.3f".format(currentHandPosition.x)}, ${"%.3f".format(currentHandPosition.y)})")
            return MovementDirection.STATIC
        }
        
        val dx = currentHandPosition.x - lastHandPosition!!.x
        val dy = currentHandPosition.y - lastHandPosition!!.y
        
        // Convert to screen coordinates for threshold comparison
        val screenDx = dx * fragmentCameraBinding.viewFinder.width
        val screenDy = dy * fragmentCameraBinding.viewFinder.height
        
        val absDx = kotlin.math.abs(screenDx)
        val absDy = kotlin.math.abs(screenDy)
        
        // Debug coordinate information
        Log.d(TAG, "👆 Hand position: (${"%.3f".format(currentHandPosition.x)}, ${"%.3f".format(currentHandPosition.y)})")
        Log.d(TAG, "📏 Delta: dx=${"%.1f".format(screenDx)}px, dy=${"%.1f".format(screenDy)}px")
        Log.d(TAG, "📐 Abs values: absDx=${"%.1f".format(absDx)}px, absDy=${"%.1f".format(absDy)}px")
        Log.d(TAG, "🎚️ Threshold: ${MOVEMENT_THRESHOLD}px")
        
        // Update last position
        lastHandPosition = currentHandPosition
        
        // Check if movement is significant enough
        if (absDx < MOVEMENT_THRESHOLD && absDy < MOVEMENT_THRESHOLD) {
            Log.d(TAG, "⏸️ Movement below threshold → STATIC")
            return MovementDirection.STATIC
        }
        
        // Determine direction based on dx and dy
        val direction = when {
            // Diagonal movements (check first for more precise detection)
            screenDx < -MOVEMENT_THRESHOLD && screenDy < -MOVEMENT_THRESHOLD -> {
                Log.d(TAG, "↖️ Diagonal up-left detected")
                MovementDirection.DIAGONAL_UP_LEFT
            }
            screenDx > MOVEMENT_THRESHOLD && screenDy < -MOVEMENT_THRESHOLD -> {
                Log.d(TAG, "↗️ Diagonal up-right detected")
                MovementDirection.DIAGONAL_UP_RIGHT
            }
            screenDx < -MOVEMENT_THRESHOLD && screenDy > MOVEMENT_THRESHOLD -> {
                Log.d(TAG, "↙️ Diagonal down-left detected")
                MovementDirection.DIAGONAL_DOWN_LEFT
            }
            screenDx > MOVEMENT_THRESHOLD && screenDy > MOVEMENT_THRESHOLD -> {
                Log.d(TAG, "↘️ Diagonal down-right detected")
                MovementDirection.DIAGONAL_DOWN_RIGHT
            }
            
            // Primary directions
            absDx > absDy && screenDx > MOVEMENT_THRESHOLD -> {
                Log.d(TAG, "➡️ RIGHT movement detected (dx > dy)")
                MovementDirection.RIGHT
            }
            absDx > absDy && screenDx < -MOVEMENT_THRESHOLD -> {
                Log.d(TAG, "⬅️ LEFT movement detected (dx > dy)")
                MovementDirection.LEFT
            }
            absDy > absDx && screenDy > MOVEMENT_THRESHOLD -> {
                Log.d(TAG, "⬇️ DOWN movement detected (dy > dx)")
                MovementDirection.DOWN
            }
            absDy > absDx && screenDy < -MOVEMENT_THRESHOLD -> {
                Log.d(TAG, "⬆️ UP movement detected (dy > dx)")
                MovementDirection.UP
            }
            
            else -> {
                Log.d(TAG, "❓ Unknown movement pattern")
                MovementDirection.UNKNOWN
            }
        }
        
        Log.d(TAG, "🎯 Final direction: $direction")
        return direction
    }
    
    private fun addMovementToHistory(movement: MovementDirection) {
        // IMPROVED: Only add movement if it's different from the last movement
        // This prevents repeated movements like LEFT->LEFT->LEFT->LEFT->LEFT
        if (movementHistory.isNotEmpty() && movementHistory.last() == movement) {
            Log.d(TAG, "🚫 Skipping duplicate movement: $movement (same as previous)")
            return
        }
        
        movementHistory.add(movement)
        
        // Keep only last MAX_MOVEMENT_HISTORY movements
        if (movementHistory.size > MAX_MOVEMENT_HISTORY) {
            val removedMovement = movementHistory.removeAt(0)
            Log.d(TAG, "🗑️ Removed oldest movement: $removedMovement")
        }
        
        // Enhanced logging untuk debugging
        val currentSize = movementHistory.size
        val fullHistory = movementHistory.joinToString(" → ")
        val lastThree = movementHistory.takeLast(3).joinToString(" → ")
        
        Log.d(TAG, "📋 MOVEMENT ADDED: $movement (unique)")
        Log.d(TAG, "📊 History size: $currentSize/$MAX_MOVEMENT_HISTORY")
        Log.d(TAG, "🔄 Last 3: $lastThree")
        Log.d(TAG, "📜 Full history: [$fullHistory]")
        
        // Check for Fathah pattern in last 3 movements using regex
        checkFathahPatternInHistory()
        
        Log.d(TAG, "═══════════════════════════════════")
    }
    
    /**
     * Check for Fathah pattern (STATIC → LEFT) in movement history using regex
     * This checks the last 3 movements for any occurrence of STATIC → LEFT
     */
    private fun checkFathahPatternInHistory() {
        if (movementHistory.size < 2) return
        
        // Get last 3 movements as string for regex matching
        val last3 = movementHistory.takeLast(3).joinToString("→")
        val last2 = movementHistory.takeLast(2).joinToString("→")
        
        // Regex pattern to match STATIC → LEFT anywhere in the sequence
        val fathahPattern = Regex("STATIC→LEFT")
        
        val hasFathahPattern = fathahPattern.containsMatchIn(last3) || fathahPattern.containsMatchIn(last2)
        
        Log.d(TAG, "🔍 REGEX PATTERN CHECK:")
        Log.d(TAG, "📝 Last 3: '$last3'")
        Log.d(TAG, "📝 Last 2: '$last2'") 
        Log.d(TAG, "🎯 Pattern 'STATIC→LEFT' found: $hasFathahPattern")
        
        if (hasFathahPattern) {
            Log.d(TAG, "🎉 ✅ FATHAH PATTERN DETECTED IN HISTORY!")
            Log.d(TAG, "🏆 Regex match successful for STATIC→LEFT")
        }
    }

    // Debug helper method untuk manual inspection
    private fun debugMovementHistory() {
        Log.d(TAG, "🔬 DEBUG MOVEMENT HISTORY DUMP")
        Log.d(TAG, "📊 Total movements stored: ${movementHistory.size}/$MAX_MOVEMENT_HISTORY")
        
        if (movementHistory.isEmpty()) {
            Log.d(TAG, "📝 History is empty")
        } else {
            movementHistory.forEachIndexed { index, movement ->
                val position = if (index == movementHistory.size - 1) "CURRENT" 
                              else if (index == movementHistory.size - 2) "PREVIOUS"
                              else "[$index]"
                Log.d(TAG, "📋 $position: $movement")
            }
            
            Log.d(TAG, "🔄 Full sequence: ${movementHistory.joinToString(" → ")}")
            Log.d(TAG, "🎯 Last 3: ${movementHistory.takeLast(3).joinToString(" → ")}")
            
            // Check current pattern potential
            if (movementHistory.size >= 2) {
                val current = movementHistory.last()
                val previous = movementHistory[movementHistory.size - 2]
                val isFathahPattern = current == MovementDirection.LEFT && previous == MovementDirection.STATIC
                Log.d(TAG, "🎯 Current pattern ($previous → $current): ${if (isFathahPattern) "✅ FATHAH MATCH" else "❌ No match"}")
            }
        }
        Log.d(TAG, "🔬 DEBUG DUMP END")
        Log.d(TAG, "═══════════════════════════════════")
    }
    
    private fun checkFathahMovementPattern(): Boolean {
        // IMPROVED: Check for STATIC → LEFT pattern using regex in movement history
        // This is more flexible and handles various scenarios
        
        if (movementHistory.size < 2) {
            Log.d(TAG, "🚫 Fathah pattern check: insufficient history (${movementHistory.size}/2 required)")
            return false
        }
        
        // Convert movement history to string for regex matching
        val historyString = movementHistory.joinToString("→")
        val last3String = movementHistory.takeLast(3).joinToString("→")
        val last2String = movementHistory.takeLast(2).joinToString("→")
        
        // Regex pattern to find STATIC → LEFT anywhere in recent movements
        val fathahPattern = Regex("STATIC→LEFT")
        
        // Check for pattern in last 3 and last 2 movements
        val foundInLast3 = fathahPattern.containsMatchIn(last3String)
        val foundInLast2 = fathahPattern.containsMatchIn(last2String)
        val hasFathahPattern = foundInLast3 || foundInLast2
        
        Log.d(TAG, "🔍 IMPROVED FATHAH PATTERN CHECK")
        Log.d(TAG, "📋 Full history: [$historyString]")
        Log.d(TAG, "🎯 Last 3: '$last3String'")
        Log.d(TAG, "🎯 Last 2: '$last2String'")
        Log.d(TAG, "� Searching for: 'STATIC→LEFT'")
        Log.d(TAG, "✅ Found in last 3: $foundInLast3")
        Log.d(TAG, "✅ Found in last 2: $foundInLast2")
        Log.d(TAG, "📊 Final result: $hasFathahPattern")
        
        if (hasFathahPattern) {
            Log.d(TAG, "🎉 ✅ FATHAH PATTERN MATCHED using regex!")
            Log.d(TAG, "🏆 Pattern 'STATIC→LEFT' found in movement history")
        } else {
            Log.d(TAG, "❌ FATHAH PATTERN NOT FOUND")
            Log.d(TAG, "📝 Expected: Pattern containing 'STATIC→LEFT'")
        }
        
        Log.d(TAG, "🔍 FATHAH PATTERN CHECK END")
        Log.d(TAG, "═══════════════════════════════════")
        
        return hasFathahPattern
    }
    
    private fun handleGestureDetection(detectedGesture: String) {
        if (!isDetecting) return
        
        // Note: Hand static status is now checked via unified movement detection
        // val isHandStatic = checkHandStaticStatus() // DEPRECATED - using unified detection
        
        val currentTime = System.currentTimeMillis()
        
        // Debug logging for gesture detection
        Log.d(TAG, "Gesture Detection:")
        Log.d(TAG, "- detectedGesture: '$detectedGesture'")
        Log.d(TAG, "- isHandStatic: $isCurrentlyStatic")
        Log.d(TAG, "- targetLetterName: '$targetLetterName'")
        Log.d(TAG, "- targetLetter: '$targetLetter'")
        Log.d(TAG, "- isFathahMode: $isFathahMode")
        Log.d(TAG, "- hijaiyahGestureDetected: $hijaiyahGestureDetected")
        Log.d(TAG, "- isWaitingForLeftMovement: $isWaitingForLeftMovement")
        
        if (isFathahMode) {
            handleFathahGestureDetection(detectedGesture, currentTime, isCurrentlyStatic)
        } else {
            handleHijaiyahGestureDetection(detectedGesture, currentTime, isCurrentlyStatic)
        }
    }
    
    private fun handleHijaiyahGestureDetection(detectedGesture: String, currentTime: Long, isHandStatic: Boolean) {
        // Check if this is the target gesture
        // Try multiple matching strategies:
        // 1. Direct match with targetLetterName (transliteration)
        // 2. Match with gesture name from HijaiyahData
        // 3. Case-insensitive matching
        
        val isCorrectGesture = when {
            // Direct match with targetLetterName
            detectedGesture.equals(targetLetterName, ignoreCase = true) -> true
            // Try to find the target letter in HijaiyahData and match with its gesture name
            targetLetter != null -> {
                val hijaiyahLetter = HijaiyahData.letters.find { it.arabic == targetLetter }
                hijaiyahLetter?.gestureName?.equals(detectedGesture, ignoreCase = true) == true
            }
            // Try to find by targetLetterName and match gesture name
            targetLetterName != null -> {
                val hijaiyahLetter = HijaiyahData.letters.find { it.transliteration.equals(targetLetterName, ignoreCase = true) }
                hijaiyahLetter?.gestureName?.equals(detectedGesture, ignoreCase = true) == true
            }
            else -> false
        }
        
        Log.d(TAG, "- isCorrectGesture: $isCorrectGesture")
        
        // Additional debug: show all possible matches
        if (targetLetter != null) {
            val hijaiyahLetter = HijaiyahData.letters.find { it.arabic == targetLetter }
            Log.d(TAG, "- HijaiyahData for '$targetLetter': $hijaiyahLetter")
        }
        
        // Enforce static hand requirement
        if (isCorrectGesture && !isHandStatic) {
            updatePredictionText("$detectedGesture - jaga tangan tetap diam...")
            // Reset progress if already started
            if (currentGesture != null) {
                currentGesture = null
                gestureStartTime = 0L
                consecutiveCorrectCount = 0
                fragmentCameraBinding.progressTimer.progress = 0
            }
            return
        }
        
        if (isCorrectGesture && isHandStatic) {
            // Correct gesture detected with static hand
            if (currentGesture != detectedGesture) {
                // New correct gesture sequence starts
                currentGesture = detectedGesture
                gestureStartTime = currentTime
                consecutiveCorrectCount = 1
                updatePredictionText("$detectedGesture (mulai hitung - tangan diam)")
            } else {
                // Continue correct gesture sequence
                consecutiveCorrectCount++
                val elapsedTime = currentTime - gestureStartTime
                val progress = (elapsedTime * 100 / REQUIRED_DURATION).toInt().coerceAtMost(100)
                
                fragmentCameraBinding.progressTimer.progress = progress
                fragmentCameraBinding.textCountdown.text = "${(REQUIRED_DURATION - elapsedTime) / 1000 + 1}"
                
                updatePredictionText("$detectedGesture (${elapsedTime}ms / ${REQUIRED_DURATION}ms - statis)")
                
                // Check if 2 seconds completed
                if (elapsedTime >= REQUIRED_DURATION) {
                    onGestureSuccess()
                }
            }
        } else {
            // Wrong gesture or no gesture
            if (detectedGesture.isNotEmpty() && detectedGesture != "Unknown") {
                updatePredictionText(
                    if (isHandStatic) "$detectedGesture - tidak cocok (tangan diam)" else "$detectedGesture - tidak cocok (tangan bergerak)"
                )
            } else {
                updatePredictionText(if (isHandStatic) "Tidak ada gesture (tangan diam)" else "Tidak ada gesture (tangan bergerak)")
            }
            
            // Reset if there was a previous correct sequence
            if (currentGesture != null) {
                resetGestureDetection()
            }
        }
    }
    
    private fun handleFathahGestureDetection(detectedGesture: String, currentTime: Long, isHandStatic: Boolean) {
        // Get the Fathah letter data for gesture matching
        val fathahLetter = when {
            targetLetter != null -> FathahData.getLetterByArabic(targetLetter!!)
            targetLetterName != null -> FathahData.getAllLetters().find { 
                it.transliteration.equals(targetLetterName, ignoreCase = true) 
            }
            else -> null
        }
        
        // Get base hijaiyah letter for gesture matching (remove fathah diacritic)
        val baseArabic = targetLetter?.replace("َ", "") ?: ""
        val baseHijaiyahLetter = HijaiyahData.letters.find { it.arabic == baseArabic }
        
        val isCorrectHijaiyahGesture = baseHijaiyahLetter?.gestureName?.equals(detectedGesture, ignoreCase = true) == true
        
        Log.d(TAG, "Fathah detection - target: $targetLetter, base: $baseArabic, gesture: $detectedGesture, correct: $isCorrectHijaiyahGesture")
        
        if (!hijaiyahGestureDetected && !isWaitingForLeftMovement) {
            // Phase 1: Detect the correct Hijaiyah gesture
            
            // Enforce static hand requirement for Hijaiyah gesture
            if (isCorrectHijaiyahGesture && !isHandStatic) {
                updatePredictionText("$detectedGesture - jaga tangan tetap diam...")
                // Reset progress if already started
                if (currentGesture != null) {
                    currentGesture = null
                    gestureStartTime = 0L
                    consecutiveCorrectCount = 0
                    fragmentCameraBinding.progressTimer.progress = 0
                }
                return
            }
            
            if (isCorrectHijaiyahGesture && isHandStatic) {
                if (currentGesture != detectedGesture) {
                    // New correct gesture sequence starts
                    currentGesture = detectedGesture
                    gestureStartTime = currentTime
                    consecutiveCorrectCount = 1
                    updatePredictionText("$detectedGesture (mulai hitung - tangan diam)")
                } else {
                    // Continue correct gesture sequence
                    consecutiveCorrectCount++
                    val elapsedTime = currentTime - gestureStartTime
                    val progress = (elapsedTime * 100 / REQUIRED_DURATION).toInt().coerceAtMost(100)
                    
                    fragmentCameraBinding.progressTimer.progress = progress
                    fragmentCameraBinding.textCountdown.text = "${(REQUIRED_DURATION - elapsedTime) / 1000 + 1}"
                    
                    updatePredictionText("$detectedGesture (${elapsedTime}ms / ${REQUIRED_DURATION}ms - statis)")
                    
                    // Check if 2 seconds completed
                    if (elapsedTime >= REQUIRED_DURATION) {
                        onHijaiyahGestureSuccess()
                    }
                }
            } else {
                // Wrong gesture or no gesture
                if (detectedGesture.isNotEmpty() && detectedGesture != "Unknown") {
                    updatePredictionText(
                        if (isHandStatic) 
                            "$detectedGesture - tidak cocok dengan ${baseHijaiyahLetter?.gestureName} (tangan diam)" 
                        else 
                            "$detectedGesture - tidak cocok dengan ${baseHijaiyahLetter?.gestureName} (tangan bergerak)"
                    )
                } else {
                    updatePredictionText(
                        if (isHandStatic) 
                            "Tidak ada gesture - coba ${baseHijaiyahLetter?.gestureName} (tangan diam)" 
                        else 
                            "Tidak ada gesture - coba ${baseHijaiyahLetter?.gestureName} (tangan bergerak)"
                    )
                }
                
                // Reset if there was a previous correct sequence
                if (currentGesture != null) {
                    resetGestureDetection()
                }
            }
        } else if (hijaiyahGestureDetected && isWaitingForLeftMovement && !fathahPatternDetected) {
            // Phase 2: Wait for Fathah movement pattern (STATIC then LEFT)
            Log.d(TAG, "🔍 Phase 2 - Gesture: '$detectedGesture', isCorrect: $isCorrectHijaiyahGesture, target: ${baseHijaiyahLetter?.gestureName}")
            
            // Debug current state
            debugMovementHistory()
            
            // Check for Fathah movement pattern
            val isFathahPattern = checkFathahMovementPattern()
            
            Log.d(TAG, "🎯 Pattern check result: isFathahPattern=$isFathahPattern, isCorrectGesture=$isCorrectHijaiyahGesture")
            
            if (isCorrectHijaiyahGesture && isFathahPattern) {
                // Fathah pattern detected with correct gesture!
                Log.d(TAG, "🎉 Fathah pattern confirmed! Success!")
                Log.d(TAG, "✅ BOTH CONDITIONS MET: Correct gesture + Fathah pattern")
                fathahPatternDetected = true // Prevent multiple detections
                onFathahSuccess()
                return
            }
            
            if (isCorrectHijaiyahGesture) {
                // Correct gesture but waiting for movement pattern
                val lastMovements = if (movementHistory.size >= 2) {
                    "${movementHistory[movementHistory.size - 2]} → ${movementHistory.last()}"
                } else {
                    movementHistory.joinToString(" → ")
                }
                
                updatePredictionText("Gerak: $lastMovements. Untuk Fathah: diam dulu, lalu ke KIRI")
            } else {
                // Wrong gesture, reset movement tracking for this attempt
                updatePredictionText(
                    "Pertahankan gesture ${baseHijaiyahLetter?.gestureName} dan lakukan gerakan: diam → kiri"
                )
            }
        }
    }
    
    private fun onHijaiyahGestureSuccess() {
        hijaiyahGestureDetected = true
        isWaitingForLeftMovement = true
        fathahPatternDetected = false // Reset pattern detection flag
        
        // Reset gesture detection variables for movement phase
        currentGesture = null
        gestureStartTime = 0L
        consecutiveCorrectCount = 0
        fragmentCameraBinding.progressTimer.progress = 0
        
        // Clear movement history for fresh tracking
        movementHistory.clear()
        lastHandPosition = null
        
        // Add initial STATIC movement to establish baseline for pattern detection
        movementHistory.add(MovementDirection.STATIC)
        Log.d(TAG, "Added initial STATIC to movement history. History[0] = STATIC")
        
        // Update UI to show next instruction
        updatePredictionText("Bagus! Sekarang untuk Fathah: diam dulu, lalu gerak ke KIRI")
        
        // Show instruction overlay or update text
        fragmentCameraBinding.textLetterName.text = "Fathah: DIAM → KIRI"
        
        Log.d(TAG, "Hijaiyah gesture detected successfully. Waiting for Fathah movement pattern.")
        Log.d(TAG, "Movement history initialized with STATIC baseline: ${movementHistory.joinToString(" → ")}")
    }
    
    private fun resetToWaitingForMovement() {
        // Keep hijaiyahGestureDetected = true but reset movement waiting
        currentGesture = null
        gestureStartTime = 0L
        consecutiveCorrectCount = 0
        fragmentCameraBinding.progressTimer.progress = 0
    }
    
    private fun updatePredictionText(text: String) {
        fragmentCameraBinding.textCountdown.text = text
    }
    
    private fun resetGestureDetection() {
        currentGesture = null
        gestureStartTime = 0L
        consecutiveCorrectCount = 0
        fragmentCameraBinding.progressTimer.progress = 0
        updatePredictionText("Reset - coba lagi")
        
        // Reset movement pattern tracking
        resetStaticTracking()
        fathahPatternDetected = false
        
        // Brief pause before allowing new detection
        resetTimer?.cancel()
        resetTimer = object : CountDownTimer(RESET_DELAY, 100) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                if (isDetecting) {
                    updatePredictionText("Siap deteksi...")
                }
            }
        }
        resetTimer?.start()
    }
    
    private fun onGestureSuccess() {
        isDetecting = false
        practiceTimer?.cancel()
        resetTimer?.cancel()
        
        // Show success result
        showResult(true)
    }
    
    private fun showResult(success: Boolean) {
        fragmentCameraBinding.overlayResult.visibility = View.VISIBLE
        
        if (success) {
            fragmentCameraBinding.iconResult.setImageResource(R.drawable.ic_check_circle)
            fragmentCameraBinding.textResult.text = "Bagus!"
            fragmentCameraBinding.textResultDetail.text = "Anda berhasil memperagakan huruf ${targetLetterName}"
            
            // Mark letter as completed
            val letterPosition = arguments?.getInt("letterPosition", -1) ?: -1
            if (letterPosition > 0) {
                progressManager.markLetterCompleted(letterPosition)
                Log.d(TAG, "Letter $letterPosition ($targetLetterName) marked as completed")
            }
            
            // Auto navigate back to hijaiyah learning page after success (only when not embedded)
            // Instruction text removed; handled via overlayResult and navigation
            
            // Show option to stay or go back
            fragmentCameraBinding.buttonStart.visibility = View.VISIBLE
            fragmentCameraBinding.buttonStart.text = "Tetap Di Sini"
            fragmentCameraBinding.buttonStart.setOnClickListener {
                countdownTimer?.cancel()
                fragmentCameraBinding.overlayResult.visibility = View.GONE
                fragmentCameraBinding.buttonStart.visibility = View.GONE
                // instruction TextView removed; no action needed here
            }
            
            // If embedded inside `LatihanPracticeActivity`, return result via FragmentResult
            val isEmbedded = arguments?.getBoolean("embedded", false) ?: false
            if (isEmbedded) {
                val letterPosition = arguments?.getInt("letterPosition", -1) ?: -1
                val result = Bundle().apply {
                    putBoolean("success", true)
                    putInt("letterPosition", letterPosition)
                }
                parentFragmentManager.setFragmentResult("camera_result", result)

                // Remove self from container
                activity?.runOnUiThread {
                    try {
                        parentFragmentManager.beginTransaction().remove(this@CameraFragment).commitAllowingStateLoss()
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to remove embedded CameraFragment: ${e.message}")
                    }
                }
                return
            }

            // Show countdown for non-embedded mode
            var countdown = 3
            countdownTimer?.cancel() // Cancel any existing countdown
            countdownTimer = object : CountDownTimer(3000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // countdown tick; UI instruction removed
                    countdown--
                }

                override fun onFinish() {
                    // Navigate back after countdown
                    try {
                        Navigation.findNavController(requireActivity(), R.id.fragment_container)
                            .navigateUp()
                    } catch (e: Exception) {
                        Log.e(TAG, "Navigation error: ${e.message}")
                        // Fallback navigation
                        requireActivity().onBackPressed()
                    }
                }
            }
            countdownTimer?.start()
            
        } else {
            fragmentCameraBinding.iconResult.setImageResource(R.drawable.ic_error_circle)
            fragmentCameraBinding.textResult.text = "Coba Lagi"
            fragmentCameraBinding.textResultDetail.text = "Gesture belum tepat. Silakan coba lagi!"
        }
        
        // Show start button again
        fragmentCameraBinding.buttonStart.visibility = View.VISIBLE
        fragmentCameraBinding.buttonStart.text = "Coba Lagi"
        fragmentCameraBinding.buttonStart.setOnClickListener {
            startAutomaticDetection()
        }
    // instruction text removed from layout
    }

    private fun initBottomSheetControls() {
        // init bottom sheet settings
        fragmentCameraBinding.bottomSheetLayout.detectionThresholdValue.text =
            String.format(
                Locale.US, "%.2f", viewModel.currentMinHandDetectionConfidence
            )
        fragmentCameraBinding.bottomSheetLayout.trackingThresholdValue.text =
            String.format(
                Locale.US, "%.2f", viewModel.currentMinHandTrackingConfidence
            )
        fragmentCameraBinding.bottomSheetLayout.presenceThresholdValue.text =
            String.format(
                Locale.US, "%.2f", viewModel.currentMinHandPresenceConfidence
            )

        // When clicked, lower hand detection score threshold floor
        fragmentCameraBinding.bottomSheetLayout.detectionThresholdMinus.setOnClickListener {
            if (gestureRecognizerHelper.minHandDetectionConfidence >= 0.2) {
                gestureRecognizerHelper.minHandDetectionConfidence -= 0.1f
                updateControlsUi()
            }
        }

        // When clicked, raise hand detection score threshold floor
        fragmentCameraBinding.bottomSheetLayout.detectionThresholdPlus.setOnClickListener {
            if (gestureRecognizerHelper.minHandDetectionConfidence <= 0.8) {
                gestureRecognizerHelper.minHandDetectionConfidence += 0.1f
                updateControlsUi()
            }
        }

        // When clicked, lower hand tracking score threshold floor
        fragmentCameraBinding.bottomSheetLayout.trackingThresholdMinus.setOnClickListener {
            if (gestureRecognizerHelper.minHandTrackingConfidence >= 0.2) {
                gestureRecognizerHelper.minHandTrackingConfidence -= 0.1f
                updateControlsUi()
            }
        }

        // When clicked, raise hand tracking score threshold floor
        fragmentCameraBinding.bottomSheetLayout.trackingThresholdPlus.setOnClickListener {
            if (gestureRecognizerHelper.minHandTrackingConfidence <= 0.8) {
                gestureRecognizerHelper.minHandTrackingConfidence += 0.1f
                updateControlsUi()
            }
        }

        // When clicked, lower hand presence score threshold floor
        fragmentCameraBinding.bottomSheetLayout.presenceThresholdMinus.setOnClickListener {
            if (gestureRecognizerHelper.minHandPresenceConfidence >= 0.2) {
                gestureRecognizerHelper.minHandPresenceConfidence -= 0.1f
                updateControlsUi()
            }
        }

        // When clicked, raise hand presence score threshold floor
        fragmentCameraBinding.bottomSheetLayout.presenceThresholdPlus.setOnClickListener {
            if (gestureRecognizerHelper.minHandPresenceConfidence <= 0.8) {
                gestureRecognizerHelper.minHandPresenceConfidence += 0.1f
                updateControlsUi()
            }
        }

        // When clicked, change the underlying hardware used for inference.
        // Current options are CPU and GPU
        fragmentCameraBinding.bottomSheetLayout.spinnerDelegate.setSelection(
            viewModel.currentDelegate, false
        )
        fragmentCameraBinding.bottomSheetLayout.spinnerDelegate.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long
                ) {
                    try {
                        gestureRecognizerHelper.currentDelegate = p2
                        updateControlsUi()
                    } catch(e: UninitializedPropertyAccessException) {
                        Log.e(TAG, "GestureRecognizerHelper has not been initialized yet.")

                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    /* no op */
                }
            }
    }

    // Update the values displayed in the bottom sheet. Reset recognition
    // helper.
    private fun updateControlsUi() {
        fragmentCameraBinding.bottomSheetLayout.detectionThresholdValue.text =
            String.format(
                Locale.US,
                "%.2f",
                gestureRecognizerHelper.minHandDetectionConfidence
            )
        fragmentCameraBinding.bottomSheetLayout.trackingThresholdValue.text =
            String.format(
                Locale.US,
                "%.2f",
                gestureRecognizerHelper.minHandTrackingConfidence
            )
        fragmentCameraBinding.bottomSheetLayout.presenceThresholdValue.text =
            String.format(
                Locale.US,
                "%.2f",
                gestureRecognizerHelper.minHandPresenceConfidence
            )

        // Needs to be cleared instead of reinitialized because the GPU
        // delegate needs to be initialized on the thread using it when applicable
        backgroundExecutor.execute {
            gestureRecognizerHelper.clearGestureRecognizer()
            gestureRecognizerHelper.setupGestureRecognizer()
        }
        fragmentCameraBinding.overlay.clear()
    }

    // Initialize CameraX, and prepare to bind the camera use cases
    private fun setUpCamera() {
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            {
                // CameraProvider
                cameraProvider = cameraProviderFuture.get()

                // Build and bind the camera use cases
                bindCameraUseCases()
            }, ContextCompat.getMainExecutor(requireContext())
        )
    }

    // Declare and bind preview, capture and analysis use cases
    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases() {

        // CameraProvider
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(cameraFacing).build()

        // Preview. Only using the 4:3 ratio because this is the closest to our models
        preview = Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
            .build()

        // ImageAnalysis. Using RGBA 8888 to match how our models work
        imageAnalyzer =
            ImageAnalysis.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                // The analyzer can then be assigned to the instance
                .also {
                    it.setAnalyzer(backgroundExecutor) { image ->
                        recognizeHand(image)
                    }
                }

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageAnalyzer
            )

            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(fragmentCameraBinding.viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun recognizeHand(imageProxy: ImageProxy) {
        gestureRecognizerHelper.recognizeLiveStream(
            imageProxy = imageProxy,
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        imageAnalyzer?.targetRotation =
            fragmentCameraBinding.viewFinder.display.rotation
    }

    // Update UI after a hand gesture has been recognized. Extracts original
    // image height/width to scale and place the landmarks properly through
    // OverlayView. Only one result is expected at a time. If two or more
    // hands are seen in the camera frame, only one will be processed.
    override fun onResults(
        resultBundle: GestureRecognizerHelper.ResultBundle
    ) {
        activity?.runOnUiThread {
            if (_fragmentCameraBinding != null) {
                // Process trajectory from hand landmarks
                try {
                    val viewSize = android.util.Size(
                        fragmentCameraBinding.viewFinder.width,
                        fragmentCameraBinding.viewFinder.height
                    )
                    val imageSize = android.util.Size(
                        resultBundle.inputImageWidth,
                        resultBundle.inputImageHeight
                    )
                    
                    trajectoryAnalyzer.processResult(
                        resultBundle.results.first(),
                        viewSize,
                        imageSize,
                        cameraFacing == CameraSelector.LENS_FACING_FRONT,
                        0 // rotation degrees
                    )
                    
                    // Track hand movement direction for diacritics detection
                    val landmarks = resultBundle.results.first().landmarks()
                    if (landmarks.isNotEmpty()) {
                        val handLandmarks = landmarks.first()
                        if (handLandmarks.isNotEmpty()) {
                            // Use index finger tip (landmark 8) for movement tracking
                            val indexTip = handLandmarks[8]
                            val currentHandPosition = PointF(indexTip.x(), indexTip.y())
                            
                            Log.d(TAG, "🎬 Frame processing - Hand detected, tracking movement...")
                            // DEPRECATED: Movement detection now handled by unified TrajectoryOverlayView listener
                            // val movement = detectMovementDirection(currentHandPosition)
                            // addMovementToHistory(movement)
                        } else {
                            Log.d(TAG, "⚠️ Hand landmarks empty")
                        }
                    } else {
                        Log.d(TAG, "⚠️ No landmarks detected")
                    }
                    
                    // DEPRECATED: Static detection now handled by unified system
                    // val isHandStatic = checkHandStaticStatus()
                    
                    // NOTE: Static hand validation is now handled through unified movement detection
                    // The movement listener will handle static detection and gesture progress reset
                    // if (isDetecting && !isHandStatic && currentGesture != null && fragmentCameraBinding.progressTimer.progress > 0) {
                    //     updatePredictionText("Tangan bergerak - progres direset")
                    //     resetGestureDetection()
                    //     return@runOnUiThread // Skip gesture processing when hand is moving
                    // }
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing trajectory", e)
                }
                
                // Show result of recognized gesture
                val gestureCategories = resultBundle.results.first().gestures()
                if (gestureCategories.isNotEmpty()) {
                    gestureRecognizerResultAdapter.updateResults(
                        gestureCategories.first()
                    )
                    
                    // Real-time gesture detection for practice
                    val topGesture = gestureCategories.first().first()
                    val detectedGestureName = topGesture.categoryName()
                    
                    // Handle the detected gesture
                    handleGestureDetection(detectedGestureName)
                    
                } else {
                    gestureRecognizerResultAdapter.updateResults(emptyList())
                    
                    // No gesture detected
                    handleGestureDetection("")
                }

                fragmentCameraBinding.bottomSheetLayout.inferenceTimeVal.text =
                    String.format("%d ms", resultBundle.inferenceTime)

                // Pass necessary information to OverlayView for drawing on the canvas
                fragmentCameraBinding.overlay.setResults(
                    resultBundle.results.first(),
                    resultBundle.inputImageHeight,
                    resultBundle.inputImageWidth,
                    RunningMode.LIVE_STREAM
                )

                // Force a redraw
                fragmentCameraBinding.overlay.invalidate()
            }
        }
    }

    override fun onError(error: String, errorCode: Int) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            gestureRecognizerResultAdapter.updateResults(emptyList())

            if (errorCode == GestureRecognizerHelper.GPU_ERROR) {
                fragmentCameraBinding.bottomSheetLayout.spinnerDelegate.setSelection(
                    GestureRecognizerHelper.DELEGATE_CPU, false
                )
            }
        }
    }
    
    // DEPRECATED: Legacy MovementDetectionListener methods - kept for reference but no longer used
    // These were part of the old TrajectoryAnalyzer movement detection system
    
    /*
    private fun onLeftMovementDetected() {
        activity?.runOnUiThread {
            Log.d(TAG, "Left movement detected via trajectory analyzer")
            // Note: Fathah detection is now handled via movement pattern checking in handleFathahGestureDetection
            // This listener is kept for potential future use or debugging
        }
    }
    
    private fun onRightMovementDetected() {
        activity?.runOnUiThread {
            Log.d(TAG, "Right movement detected - not relevant for Fathah")
        }
    }
    
    private fun onMovementStarted() {
        activity?.runOnUiThread {
            Log.d(TAG, "Movement tracking started")
        }
    }
    
    private fun onMovementStopped() {
        activity?.runOnUiThread {
            Log.d(TAG, "Movement tracking stopped")
        }
    }
    */
    
    // New unified movement detection method
    override fun onMovementDetected(movementType: MovementType, isStatic: Boolean) {
        activity?.runOnUiThread {
            Log.d(TAG, "Unified movement detected: type=$movementType, static=$isStatic")
            
            // Update current static state
            isCurrentlyStatic = isStatic
            
            // Convert MovementType to MovementDirection for existing history system
            val movementDirection = convertMovementTypeToDirection(movementType)
            addMovementToHistory(movementDirection)
        }
    }
    
    /**
     * Convert MovementType (from unified detection) to MovementDirection (legacy enum)
     */
    private fun convertMovementTypeToDirection(movementType: MovementType): MovementDirection {
        return when (movementType) {
            MovementType.STATIC -> MovementDirection.STATIC
            MovementType.LEFT -> MovementDirection.LEFT
            MovementType.RIGHT -> MovementDirection.RIGHT
            MovementType.UP -> MovementDirection.UP
            MovementType.DOWN -> MovementDirection.DOWN
            MovementType.DIAGONAL_UP_LEFT -> MovementDirection.DIAGONAL_UP_LEFT
            MovementType.DIAGONAL_UP_RIGHT -> MovementDirection.DIAGONAL_UP_RIGHT
            MovementType.DIAGONAL_DOWN_LEFT -> MovementDirection.DIAGONAL_DOWN_LEFT
            MovementType.DIAGONAL_DOWN_RIGHT -> MovementDirection.DIAGONAL_DOWN_RIGHT
            MovementType.UNKNOWN -> MovementDirection.UNKNOWN
        }
    }
    
    // Legacy methods - will be deprecated once fully migrated to unified detection
    override fun onMovementDirectionChanged(direction: String, movementType: MovementType) {
        // Optional: can be used for additional direction-specific handling
        Log.d(TAG, "Movement direction changed: $direction -> $movementType")
    }
    
    override fun onStaticStatusChanged(isStatic: Boolean) {
        // Optional: can be used for additional static status handling  
        Log.d(TAG, "Static status changed: $isStatic")
    }
    
    private fun onFathahSuccess() {
        // Success! User performed correct Hijaiyah gesture + left movement for Fathah
        updatePredictionText("BERHASIL! Fathah $targetLetterName terdeteksi!")
        
        // Update UI to show success
        fragmentCameraBinding.textLetterName.text = "Berhasil: Fathah $targetLetterName"
        fragmentCameraBinding.progressTimer.progress = 100
        
        // Mark as completed and save progress
        val letterPosition = arguments?.getInt("letterPosition", -1) ?: -1
        if (letterPosition >= 0) {
            progressManager.markLetterCompleted(letterPosition)
        }
        
        // Stop detection and reset all states
        isDetecting = false
        isWaitingForLeftMovement = false
        resetStaticTracking()
        
        // Show success dialog or navigate back
        showFathahSuccessDialog()
        
        Log.d(TAG, "Fathah gesture completed successfully!")
    }
    
    private fun showFathahSuccessDialog() {
        try {
            // Simple success message, could be enhanced with dialog
            Toast.makeText(requireContext(), "Berhasil! Fathah $targetLetterName telah selesai!", Toast.LENGTH_LONG).show()
            
            // Auto navigate back after delay
            view?.postDelayed({
                try {
                    Navigation.findNavController(requireView()).navigateUp()
                } catch (e: Exception) {
                    Log.e(TAG, "Error navigating back", e)
                }
            }, 2000)
        } catch (e: Exception) {
            Log.e(TAG, "Error showing success dialog", e)
        }
    }
}
