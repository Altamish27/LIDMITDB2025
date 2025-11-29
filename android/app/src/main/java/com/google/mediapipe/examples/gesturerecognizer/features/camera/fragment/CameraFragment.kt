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
package com.google.mediapipe.examples.gesturerecognizer.features.camera.fragment

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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.mediapipe.examples.gesturerecognizer.core.helper.GestureRecognizerHelper
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahData
import com.google.mediapipe.examples.gesturerecognizer.core.viewmodel.MainViewModel
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.databinding.FragmentCameraBinding
import com.google.mediapipe.examples.gesturerecognizer.core.adapter.GestureRecognizerResultsAdapter
import com.google.mediapipe.examples.gesturerecognizer.core.permissions.PermissionsFragment
import com.google.mediapipe.examples.gesturerecognizer.core.overlay.OverlayView
import com.google.mediapipe.examples.gesturerecognizer.core.overlay.TrajectoryOverlayView
import com.google.mediapipe.examples.gesturerecognizer.core.overlay.TrajectoryRingBuffer
import com.google.mediapipe.examples.gesturerecognizer.core.overlay.TrajectoryAnalyzer
import com.google.mediapipe.examples.gesturerecognizer.core.overlay.MovementDetectionListener
import com.google.mediapipe.examples.gesturerecognizer.core.overlay.MovementType
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahProgressManager
import com.google.mediapipe.examples.gesturerecognizer.data.FathahData
import com.google.mediapipe.examples.gesturerecognizer.data.KasrahData
import com.google.mediapipe.examples.gesturerecognizer.data.DhammahData
import com.google.mediapipe.examples.gesturerecognizer.data.api.SignQuranApiService
import com.google.mediapipe.examples.gesturerecognizer.data.manager.AuthManager
import com.google.mediapipe.examples.gesturerecognizer.data.manager.RoomPreferenceManager
import com.google.mediapipe.examples.gesturerecognizer.features.praga.PragaActivity
import android.content.Intent
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.launch

class CameraFragment : Fragment(),
    GestureRecognizerHelper.GestureRecognizerListener,
    MovementDetectionListener {

    companion object {
        private const val TAG = "Hand gesture recognizer"
        private const val REQUIRED_DURATION = 1000L // 1 second (reduced from 2 seconds)
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
    private lateinit var authManager: AuthManager
    private lateinit var roomPreferenceManager: RoomPreferenceManager
    private val apiService = SignQuranApiService.getInstance()
    private var activeRoomId: Int? = null
    private var isDetecting = false
    private var currentGesture: String? = null
    private var gestureStartTime = 0L
    private var consecutiveCorrectCount = 0
    
    // Fathah specific properties
    private var isFathahMode = false
    private var hijaiyahGestureDetected = false
    private var isWaitingForLeftMovement = false
    private var fathahPatternDetected = false // Prevent multiple detection
    
    // Kasrah specific properties
    private var isKasrahMode = false
    private var isWaitingForDownMovement = false
    private var kasrahPatternDetected = false // Prevent multiple detection
    
    // Dhammah specific properties
    private var isDhammahMode = false
    private var isWaitingForUpMovement = false
    private var dhammahPatternDetected = false // Prevent multiple detection
    
    // Mode Latihan (Jilid) vs Mode Belajar (Hijaiyah)
    private var isLatihanMode = false
    private var nextLettersList: List<String> = emptyList()
    
    // Movement history tracking for diacritics (Fathah, Dhammah, etc.)
    enum class MovementDirection {
        STATIC, LEFT, RIGHT, UP, DOWN, DIAGONAL_UP_LEFT, DIAGONAL_UP_RIGHT, 
        DIAGONAL_DOWN_LEFT, DIAGONAL_DOWN_RIGHT, UNKNOWN
    }
    
    private val movementHistory = mutableListOf<MovementDirection>()
    private val MAX_MOVEMENT_HISTORY = 10
    private var lastHandPosition: PointF? = null
    private val MOVEMENT_THRESHOLD = 30.0f // pixels threshold for movement detection
    
    // Track current static state from unified movement detection
    private var isCurrentlyStatic: Boolean = true

    override fun onResume() {
        super.onResume()
        
        // Safety check - make sure fragment is attached
        if (!isAdded) {
            Log.w(TAG, "onResume called but fragment not attached")
            return
        }
        
        val ctx = context ?: return
        
        // Make sure that all permissions are still present, since the
        // user could have removed them while the app was in paused state.
        if (!PermissionsFragment.hasPermissions(ctx)) {
            try {
                Navigation.findNavController(
                    requireActivity(), R.id.fragment_container
                ).navigate(R.id.action_camera_to_permissions)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to navigate to permissions: ${e.message}")
            }
            return
        }

        // Start the GestureRecognizerHelper again when users come back
        // to the foreground.
        if (this::backgroundExecutor.isInitialized && !backgroundExecutor.isShutdown) {
            backgroundExecutor.execute {
                try {
                    if (this::gestureRecognizerHelper.isInitialized && gestureRecognizerHelper.isClosed()) {
                        gestureRecognizerHelper.setupGestureRecognizer()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to setup gesture recognizer: ${e.message}")
                }
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
        // Cancel timers first to prevent callbacks accessing destroyed views
        practiceTimer?.cancel()
        practiceTimer = null
        resetTimer?.cancel()
        resetTimer = null
        countdownTimer?.cancel()
        countdownTimer = null
        
        // Clear binding reference AFTER canceling timers but BEFORE super call
        _fragmentCameraBinding = null
        
        super.onDestroyView()

        // Shut down our background executor asynchronously to avoid ANR
        if (this::backgroundExecutor.isInitialized) {
            backgroundExecutor.shutdown()
            // Don't block - let it terminate in background
            try {
                if (!backgroundExecutor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                    backgroundExecutor.shutdownNow()
                }
            } catch (e: InterruptedException) {
                backgroundExecutor.shutdownNow()
            }
        }
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
        
        // Check if embedded mode (Latihan Jilid) or standalone (Belajar Hijaiyah)
        isLatihanMode = arguments?.getBoolean("embedded", false) ?: false
        
        // Get next letters list for Latihan mode
        val nextLettersArg = arguments?.getStringArrayList("next_letters")
        nextLettersList = nextLettersArg?.toList() ?: emptyList()
        
        // Set Fathah, Kasrah, and Dhammah mode
        isFathahMode = diacritic == "fathah"
        isKasrahMode = diacritic == "kasrah"
        isDhammahMode = diacritic == "dhammah"
        
        // Debug logging for received arguments
        Log.d(TAG, "Received arguments:")
        Log.d(TAG, "- targetLetter: $targetLetter")
        Log.d(TAG, "- targetLetterName: $targetLetterName")
        Log.d(TAG, "- letterType: $letterType")
        Log.d(TAG, "- diacritic: $diacritic")
        Log.d(TAG, "- isLatihanMode: $isLatihanMode")
        Log.d(TAG, "- nextLettersList: $nextLettersList")
        Log.d(TAG, "- isFathahMode: $isFathahMode")
        Log.d(TAG, "- isKasrahMode: $isKasrahMode")
        Log.d(TAG, "- isDhammahMode: $isDhammahMode")
        Log.d(TAG, "- all arguments: ${arguments?.keySet()?.joinToString { "$it=${arguments?.get(it)}" }}")
        
        // Setup UI with target letter first (with loading state)
        setupHijaiyahUI()
        
        // Get context safely - use view.context which is always available in onViewCreated
        val safeContext = view.context
        
        // Preload HijaiyahData dari API
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Load data jika belum ada
                if (HijaiyahData.letters.isEmpty()) {
                    Log.d(TAG, "HijaiyahData empty, loading from API...")
                    val success = HijaiyahData.loadFromApi(safeContext)
                    if (success) {
                        Log.d(TAG, "HijaiyahData loaded successfully: ${HijaiyahData.letters.size} letters")
                    } else {
                        Log.w(TAG, "Failed to load HijaiyahData from API, will use fallback")
                    }
                } else {
                    Log.d(TAG, "HijaiyahData already loaded: ${HijaiyahData.letters.size} letters")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading HijaiyahData", e)
            }
        }
        
        with(fragmentCameraBinding.recyclerviewResults) {
            layoutManager = LinearLayoutManager(safeContext)
            adapter = gestureRecognizerResultAdapter
        }

        // Initialize our background executor
        backgroundExecutor = Executors.newSingleThreadExecutor()
        // Initialize managers
        progressManager = HijaiyahProgressManager(safeContext)
        authManager = AuthManager(safeContext)
        roomPreferenceManager = RoomPreferenceManager(safeContext)
        viewLifecycleOwner.lifecycleScope.launch {
            activeRoomId = getActiveRoomId()
        }
        
        // Initialize trajectory system
        trajectoryBuffer = TrajectoryRingBuffer()
        trajectoryOverlay = TrajectoryOverlayView(safeContext)
        
        // Set up unified movement detection listener
        trajectoryOverlay.setMovementDetectionListener(this)
        
        // Add trajectory overlay to the camera container programmatically
        val cameraContainer = fragmentCameraBinding.cameraContainer
        cameraContainer.addView(trajectoryOverlay)
        
        trajectoryAnalyzer = TrajectoryAnalyzer(trajectoryBuffer, trajectoryOverlay)
        
        // Set up TrajectoryAnalyzer movement listener for hand lost detection
        trajectoryAnalyzer.setMovementListener(this)
        
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
        // inference - use safeContext captured earlier
        backgroundExecutor.execute {
            try {
                // Re-check context availability since this runs on background thread
                val gestureContext = context ?: safeContext
                gestureRecognizerHelper = GestureRecognizerHelper(
                    context = gestureContext,
                    runningMode = RunningMode.LIVE_STREAM,
                    minHandDetectionConfidence = viewModel.currentMinHandDetectionConfidence,
                    minHandTrackingConfidence = viewModel.currentMinHandTrackingConfidence,
                    minHandPresenceConfidence = viewModel.currentMinHandPresenceConfidence,
                    currentDelegate = viewModel.currentDelegate,
                    gestureRecognizerListener = this
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create GestureRecognizerHelper: ${e.message}", e)
                activity?.runOnUiThread {
                    handleCameraInitializationError(e)
                }
            }
        }

        // Attach listeners to UI control widgets
        initBottomSheetControls()
    }
    
    private fun setupHijaiyahUI() {
        // Setup target letter display - always show the letter directly
        val displayLetter = targetLetter ?: getDefaultLetter()
        fragmentCameraBinding.textTargetLetter.text = displayLetter
        
        // Setup letter name
        val displayName = targetLetterName ?: getDefaultLetterName()
        fragmentCameraBinding.textLetterName.text = displayName
        
        // Setup letter type description
        val letterTypeText = when {
            isFathahMode -> "Fathah (ـَ)"
            isKasrahMode -> "Kasrah (ـِ)"
            isDhammahMode -> "Dhammah (ـُ)"
            else -> "Hijaiyah Dasar"
        }
        fragmentCameraBinding.textLetterType.text = letterTypeText
        
        // Setup gesture hint
        val gestureHint = when {
            isFathahMode -> "← Gerakkan tangan ke KIRI"
            isKasrahMode -> "↓ Gerakkan tangan ke BAWAH"
            isDhammahMode -> "∪ Gerakkan tangan membentuk huruf U"
            else -> "🤚 Tunjukkan gesture di depan kamera"
        }
        fragmentCameraBinding.textGestureHint.text = gestureHint
        
        // Show step indicator for Harakat modes
        val isHarakatMode = isFathahMode || isKasrahMode || isDhammahMode
        fragmentCameraBinding.stepIndicatorContainer.visibility = if (isHarakatMode) View.VISIBLE else View.GONE
        
        // Setup step 2 label based on mode
        if (isHarakatMode) {
            val step2Text = when {
                isFathahMode -> "← Kiri"
                isKasrahMode -> "↓ Bawah"
                isDhammahMode -> "∪ Huruf U"
                else -> "Gerakan"
            }
            fragmentCameraBinding.step2Label.text = step2Text
        }
        
        // Setup Mode Latihan: Show next letters list
        setupLatihanModeUI()
        
        // Log for debugging
        Log.d(TAG, "UI Setup - Letter: $displayLetter, Name: $displayName, Type: $letterTypeText, LatihanMode: $isLatihanMode")
        
        // Setup back button
        fragmentCameraBinding.buttonBack.setOnClickListener {
            try {
                if (isAdded) {
                    // For embedded/latihan mode, just close camera overlay
                    if (isLatihanMode) {
                        val result = Bundle().apply {
                            putBoolean("cancelled", true)
                        }
                        parentFragmentManager.setFragmentResult("camera_result", result)
                        parentFragmentManager.beginTransaction().remove(this@CameraFragment).commitAllowingStateLoss()
                    } else {
                        activity?.let { act ->
                            Navigation.findNavController(act, R.id.fragment_container).navigateUp()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error navigating back", e)
                activity?.onBackPressed()
            }
        }
        
        // Setup help/tutorial button
        fragmentCameraBinding.buttonHelp.setOnClickListener {
            showTutorialDialog()
        }
        
        // Start automatic detection
        startAutomaticDetection()
        
        // Initially hide result overlay
        fragmentCameraBinding.overlayResult.visibility = View.GONE
        
        // Setup result buttons
        fragmentCameraBinding.btnTryAgain.setOnClickListener {
            fragmentCameraBinding.overlayResult.visibility = View.GONE
            resetGestureDetection()
            startAutomaticDetection()
        }
        
        fragmentCameraBinding.btnNextLetter.setOnClickListener {
            try {
                if (isAdded) {
                    activity?.let { act ->
                        Navigation.findNavController(act, R.id.fragment_container).navigateUp()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error navigating to next", e)
            }
        }
    }
    
    /**
     * Setup UI for Latihan (Jilid) mode - shows next letters list
     */
    private fun setupLatihanModeUI() {
        if (_fragmentCameraBinding == null) return
        
        if (isLatihanMode && nextLettersList.isNotEmpty()) {
            // Show next letters panel
            fragmentCameraBinding.latihanModeContainer.visibility = View.VISIBLE
            
            // Set next letters (up to 3)
            if (nextLettersList.isNotEmpty()) {
                fragmentCameraBinding.nextLetter1Container.visibility = View.VISIBLE
                fragmentCameraBinding.textNextLetter1.text = nextLettersList.getOrNull(0) ?: ""
            } else {
                fragmentCameraBinding.nextLetter1Container.visibility = View.GONE
            }
            
            if (nextLettersList.size > 1) {
                fragmentCameraBinding.nextLetter2Container.visibility = View.VISIBLE
                fragmentCameraBinding.textNextLetter2.text = nextLettersList.getOrNull(1) ?: ""
            } else {
                fragmentCameraBinding.nextLetter2Container.visibility = View.GONE
            }
            
            if (nextLettersList.size > 2) {
                fragmentCameraBinding.nextLetter3Container.visibility = View.VISIBLE
                fragmentCameraBinding.textNextLetter3.text = nextLettersList.getOrNull(2) ?: ""
            } else {
                fragmentCameraBinding.nextLetter3Container.visibility = View.GONE
            }
            
            // Show "more" indicator if there are more than 3 letters
            fragmentCameraBinding.textMoreLetters.visibility = if (nextLettersList.size > 3) View.VISIBLE else View.GONE
            
            Log.d(TAG, "Latihan mode UI setup: showing ${nextLettersList.size} next letters")
        } else {
            // Hide next letters panel for belajar mode
            fragmentCameraBinding.latihanModeContainer.visibility = View.GONE
            Log.d(TAG, "Belajar mode UI setup: hiding next letters panel")
        }
    }
    
    /**
     * Update the status card UI to show current detection state
     */
    private fun updateStatusUI(
        title: String,
        message: String,
        statusType: StatusType,
        showProgress: Boolean = false,
        progress: Int = 0,
        detectedGesture: String? = null,
        isCorrectGesture: Boolean = false
    ) {
        if (_fragmentCameraBinding == null || !isAdded) return
        
        activity?.runOnUiThread {
            try {
                // Update status card background
                val bgResource = when (statusType) {
                    StatusType.WAITING -> R.drawable.bg_status_waiting
                    StatusType.DETECTING -> R.drawable.bg_status_waiting
                    StatusType.SUCCESS -> R.drawable.bg_status_success
                    StatusType.ERROR -> R.drawable.bg_status_error
                }
                fragmentCameraBinding.statusCard.setBackgroundResource(bgResource)
                
                // Update status icon
                val iconResource = when (statusType) {
                    StatusType.WAITING -> android.R.drawable.ic_menu_search
                    StatusType.DETECTING -> android.R.drawable.ic_menu_view
                    StatusType.SUCCESS -> android.R.drawable.ic_menu_send
                    StatusType.ERROR -> android.R.drawable.ic_dialog_alert
                }
                fragmentCameraBinding.iconStatus.setImageResource(iconResource)
                
                // Update texts
                fragmentCameraBinding.textStatusTitle.text = title
                fragmentCameraBinding.textStatusMessage.text = message
                
                // Update progress bar
                fragmentCameraBinding.progressTimer.visibility = if (showProgress) View.VISIBLE else View.GONE
                if (showProgress) {
                    fragmentCameraBinding.progressTimer.progress = progress
                }
                
                // Update countdown if showing progress
                if (showProgress && progress > 0) {
                    val remainingSeconds = ((100 - progress) * REQUIRED_DURATION / 100 / 1000) + 1
                    fragmentCameraBinding.textCountdown.visibility = View.VISIBLE
                    fragmentCameraBinding.textCountdown.text = "${remainingSeconds}s"
                } else {
                    fragmentCameraBinding.textCountdown.visibility = View.GONE
                }
                
                // Update detected gesture info
                if (detectedGesture != null) {
                    fragmentCameraBinding.detectedGestureContainer.visibility = View.VISIBLE
                    fragmentCameraBinding.textDetectedGesture.text = detectedGesture
                    fragmentCameraBinding.textGestureMatch.text = if (isCorrectGesture) "✅" else "❌"
                } else {
                    fragmentCameraBinding.detectedGestureContainer.visibility = View.GONE
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error updating status UI", e)
            }
        }
    }
    
    /**
     * Update step indicator for Harakat modes
     */
    private fun updateStepIndicator(currentStep: Int) {
        if (_fragmentCameraBinding == null || !isAdded) return
        if (!isFathahMode && !isKasrahMode && !isDhammahMode) return
        
        activity?.runOnUiThread {
            try {
                // Step 1
                val step1Active = currentStep >= 1
                fragmentCameraBinding.step1Circle.setBackgroundResource(
                    if (step1Active) R.drawable.bg_step_number_active else R.drawable.bg_step_number_inactive
                )
                fragmentCameraBinding.step1Label.setTextColor(
                    ContextCompat.getColor(requireContext(), if (step1Active) R.color.teal_primary else R.color.gray_dark)
                )
                
                // Step 2
                val step2Active = currentStep >= 2
                fragmentCameraBinding.step2Circle.setBackgroundResource(
                    if (step2Active) R.drawable.bg_step_number_active else R.drawable.bg_step_number_inactive
                )
                fragmentCameraBinding.step2Label.setTextColor(
                    ContextCompat.getColor(requireContext(), if (step2Active) R.color.teal_primary else R.color.gray_dark)
                )
                
                // Step 3
                val step3Active = currentStep >= 3
                fragmentCameraBinding.step3Circle.setBackgroundResource(
                    if (step3Active) R.drawable.bg_step_number_active else R.drawable.bg_step_number_inactive
                )
                fragmentCameraBinding.step3Label.setTextColor(
                    ContextCompat.getColor(requireContext(), if (step3Active) R.color.teal_primary else R.color.gray_dark)
                )
                
            } catch (e: Exception) {
                Log.e(TAG, "Error updating step indicator", e)
            }
        }
    }
    
    enum class StatusType {
        WAITING, DETECTING, SUCCESS, ERROR
    }
    
    /**
     * Get default letter based on current mode
     */
    private fun getDefaultLetter(): String {
        return when {
            isFathahMode -> FathahData.letters.firstOrNull()?.arabic ?: "اَ"
            isKasrahMode -> KasrahData.letters.firstOrNull()?.arabic ?: "اِ"
            isDhammahMode -> DhammahData.letters.firstOrNull()?.arabic ?: "اُ"
            else -> HijaiyahData.letters.firstOrNull()?.arabic ?: "ا"
        }
    }
    
    /**
     * Get default letter name based on current mode
     */
    private fun getDefaultLetterName(): String {
        return when {
            isFathahMode -> FathahData.letters.firstOrNull()?.transliteration ?: "Alif Fathah"
            isKasrahMode -> KasrahData.letters.firstOrNull()?.transliteration ?: "Alif Kasrah"
            isDhammahMode -> DhammahData.letters.firstOrNull()?.transliteration ?: "Alif Dhammah"
            else -> HijaiyahData.letters.firstOrNull()?.transliteration ?: "Alif"
        }
    }
    
    private fun startAutomaticDetection() {
        isDetecting = true
        gestureStartTime = 0L
        consecutiveCorrectCount = 0
        
        // Hide start button
        fragmentCameraBinding.buttonStart.visibility = View.GONE
        
        // Show progress bar
        fragmentCameraBinding.progressTimer.visibility = View.VISIBLE
        fragmentCameraBinding.progressTimer.progress = 0
        
        // Update status UI
        val gestureTarget = getGestureNameWithFallback() ?: "gesture yang benar"
        updateStatusUI(
            title = "Mendeteksi Gesture...",
            message = "Posisikan tangan Anda dan tunjukkan gesture \"$gestureTarget\"",
            statusType = StatusType.WAITING,
            showProgress = false
        )
        
        // Update step indicator to step 1
        updateStepIndicator(1)
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
        
        // Check for Fathah pattern in last 3-4 movements using regex
        checkFathahPatternInHistory()
        
        Log.d(TAG, "═══════════════════════════════════")
    }
    
    /**
     * Check for Fathah pattern (STATIC → LEFT → STATIC) in movement history using regex
     * This checks the last 3 movements for any occurrence of STATIC → LEFT → STATIC
     */
    private fun checkFathahPatternInHistory() {
        if (movementHistory.size < 3) return
        
        // Get last 3 movements as string for regex matching
        val last3 = movementHistory.takeLast(3).joinToString("→")
        val last4 = if (movementHistory.size >= 4) movementHistory.takeLast(4).joinToString("→") else ""
        
        // Regex pattern to match STATIC → LEFT → STATIC anywhere in the sequence
        val fathahPattern = Regex("STATIC→LEFT→STATIC")
        
        val hasFathahPattern = fathahPattern.containsMatchIn(last3) || 
                               (last4.isNotEmpty() && fathahPattern.containsMatchIn(last4))
        
        Log.d(TAG, "🔍 REGEX PATTERN CHECK:")
        Log.d(TAG, "📝 Last 3: '$last3'")
        if (last4.isNotEmpty()) Log.d(TAG, "📝 Last 4: '$last4'")
        Log.d(TAG, "🎯 Pattern 'STATIC→LEFT→STATIC' found: $hasFathahPattern")
        
        if (hasFathahPattern) {
            Log.d(TAG, "🎉 ✅ FATHAH PATTERN DETECTED IN HISTORY!")
            Log.d(TAG, "🏆 Regex match successful for STATIC→LEFT→STATIC")
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
            
            // Check current pattern potential for STATIC → LEFT → STATIC
            if (movementHistory.size >= 3) {
                val current = movementHistory.last()
                val previous = movementHistory[movementHistory.size - 2]
                val beforePrevious = movementHistory[movementHistory.size - 3]
                val isFathahPattern = current == MovementDirection.STATIC && 
                                    previous == MovementDirection.LEFT && 
                                    beforePrevious == MovementDirection.STATIC
                Log.d(TAG, "🎯 Current pattern ($beforePrevious → $previous → $current): ${if (isFathahPattern) "✅ FATHAH MATCH" else "❌ No match"}")
            } else if (movementHistory.size >= 2) {
                val current = movementHistory.last()
                val previous = movementHistory[movementHistory.size - 2]
                Log.d(TAG, "🎯 Partial pattern ($previous → $current): Building towards STATIC→LEFT→STATIC")
            }
        }
        Log.d(TAG, "🔬 DEBUG DUMP END")
        Log.d(TAG, "═══════════════════════════════════")
    }
    
    private fun checkFathahMovementPattern(): Boolean {
        // IMPROVED: Check for STATIC → LEFT → STATIC pattern using regex in movement history
        // This is more specific and requires complete fathah gesture sequence
        
        if (movementHistory.size < 3) {
            Log.d(TAG, "🚫 Fathah pattern check: insufficient history (${movementHistory.size}/3 required)")
            return false
        }
        
        // Convert movement history to string for regex matching
        val historyString = movementHistory.joinToString("→")
        val last3String = movementHistory.takeLast(3).joinToString("→")
        val last4String = if (movementHistory.size >= 4) movementHistory.takeLast(4).joinToString("→") else ""
        
        // Regex pattern to find STATIC → LEFT → STATIC anywhere in recent movements
        val fathahPattern = Regex("STATIC→LEFT→STATIC")
        
        // Check for pattern in last 3 and last 4 movements
        val foundInLast3 = fathahPattern.containsMatchIn(last3String)
        val foundInLast4 = last4String.isNotEmpty() && fathahPattern.containsMatchIn(last4String)
        val hasFathahPattern = foundInLast3 || foundInLast4
        
        Log.d(TAG, "🔍 IMPROVED FATHAH PATTERN CHECK")
        Log.d(TAG, "📋 Full history: [$historyString]")
        Log.d(TAG, "🎯 Last 3: '$last3String'")
        if (last4String.isNotEmpty()) Log.d(TAG, "🎯 Last 4: '$last4String'")
        Log.d(TAG, "🎯 Searching for: 'STATIC→LEFT→STATIC'")
        Log.d(TAG, "✅ Found in last 3: $foundInLast3")
        if (last4String.isNotEmpty()) Log.d(TAG, "✅ Found in last 4: $foundInLast4")
        Log.d(TAG, "📊 Final result: $hasFathahPattern")
        
        if (hasFathahPattern) {
            Log.d(TAG, "🎉 ✅ FATHAH PATTERN MATCHED using regex!")
            Log.d(TAG, "🏆 Pattern 'STATIC→LEFT→STATIC' found in movement history")
        } else {
            Log.d(TAG, "❌ FATHAH PATTERN NOT FOUND")
            Log.d(TAG, "📝 Expected: Pattern containing 'STATIC→LEFT→STATIC'")
        }
        
        Log.d(TAG, "🔍 FATHAH PATTERN CHECK END")
        Log.d(TAG, "═══════════════════════════════════")
        
        return hasFathahPattern
    }
    
    private fun checkKasrahMovementPattern(): Boolean {
        // Check for STATIC → DOWN → STATIC pattern using regex in movement history
        // Similar to Fathah but looking for DOWN movement instead of LEFT
        
        if (movementHistory.size < 3) {
            Log.d(TAG, "🚫 Kasrah pattern check: insufficient history (${movementHistory.size}/3 required)")
            return false
        }
        
        // Convert movement history to string for regex matching
        val historyString = movementHistory.joinToString("→")
        val last3String = movementHistory.takeLast(3).joinToString("→")
        val last4String = if (movementHistory.size >= 4) movementHistory.takeLast(4).joinToString("→") else ""
        
        // Regex pattern to find STATIC → DOWN → STATIC anywhere in recent movements
        val kasrahPattern = Regex("STATIC→DOWN→STATIC")
        
        // Check for pattern in last 3 and last 4 movements
        val foundInLast3 = kasrahPattern.containsMatchIn(last3String)
        val foundInLast4 = last4String.isNotEmpty() && kasrahPattern.containsMatchIn(last4String)
        val hasKasrahPattern = foundInLast3 || foundInLast4
        
        Log.d(TAG, "🔍 KASRAH PATTERN CHECK")
        Log.d(TAG, "📋 Full history: [$historyString]")
        Log.d(TAG, "🎯 Last 3: '$last3String'")
        if (last4String.isNotEmpty()) Log.d(TAG, "🎯 Last 4: '$last4String'")
        Log.d(TAG, "🎯 Searching for: 'STATIC→DOWN→STATIC'")
        Log.d(TAG, "✅ Found in last 3: $foundInLast3")
        if (last4String.isNotEmpty()) Log.d(TAG, "✅ Found in last 4: $foundInLast4")
        Log.d(TAG, "📊 Final result: $hasKasrahPattern")
        
        if (hasKasrahPattern) {
            Log.d(TAG, "🎉 ✅ KASRAH PATTERN MATCHED using regex!")
            Log.d(TAG, "🏆 Pattern 'STATIC→DOWN→STATIC' found in movement history")
        } else {
            Log.d(TAG, "❌ KASRAH PATTERN NOT FOUND")
            Log.d(TAG, "📝 Expected: Pattern containing 'STATIC→DOWN→STATIC'")
        }
        
        Log.d(TAG, "🔍 KASRAH PATTERN CHECK END")
        Log.d(TAG, "═══════════════════════════════════")
        
        return hasKasrahPattern
    }
    
    private fun checkDhammahMovementPattern(): Boolean {
        // Check for STATIC → DOWN → DIAGONAL_DOWN_LEFT → LEFT → DIAGONAL_UP_LEFT → UP → STATIC pattern using regex in movement history
        // This is a complex 7-movement pattern for Dhammah detection
        
        if (movementHistory.size < 7) {
            Log.d(TAG, "🚫 Dhammah pattern check: insufficient history (${movementHistory.size}/7 required)")
            return false
        }
        
        // Convert movement history to string for regex matching
        val historyString = movementHistory.joinToString("→")
        val last7String = movementHistory.takeLast(7).joinToString("→")
        val last8String = if (movementHistory.size >= 8) movementHistory.takeLast(8).joinToString("→") else ""
        
        // Regex pattern to find STATIC → DOWN → DIAGONAL_DOWN_LEFT → LEFT → DIAGONAL_UP_LEFT → UP → STATIC
        val dhammahPattern = Regex("STATIC→DOWN→DIAGONAL_DOWN_LEFT→LEFT→DIAGONAL_UP_LEFT→UP→STATIC")
        
        // Check for pattern in last 7 and last 8 movements
        val foundInLast7 = dhammahPattern.containsMatchIn(last7String)
        val foundInLast8 = last8String.isNotEmpty() && dhammahPattern.containsMatchIn(last8String)
        val hasDhammahPattern = foundInLast7 || foundInLast8
        
        Log.d(TAG, "🔍 DHAMMAH PATTERN CHECK")
        Log.d(TAG, "📋 Full history: [$historyString]")
        Log.d(TAG, "🎯 Last 7: '$last7String'")
        if (last8String.isNotEmpty()) Log.d(TAG, "🎯 Last 8: '$last8String'")
        Log.d(TAG, "🎯 Searching for: 'STATIC→DOWN→DIAGONAL_DOWN_LEFT→LEFT→DIAGONAL_UP_LEFT→UP→STATIC'")
        Log.d(TAG, "✅ Found in last 7: $foundInLast7")
        if (last8String.isNotEmpty()) Log.d(TAG, "✅ Found in last 8: $foundInLast8")
        Log.d(TAG, "📊 Final result: $hasDhammahPattern")
        
        if (hasDhammahPattern) {
            Log.d(TAG, "🎉 ✅ DHAMMAH PATTERN MATCHED using regex!")
            Log.d(TAG, "🏆 Pattern 'STATIC→DOWN→DIAGONAL_DOWN_LEFT→LEFT→DIAGONAL_UP_LEFT→UP→STATIC' found in movement history")
        } else {
            Log.d(TAG, "❌ DHAMMAH PATTERN NOT FOUND")
            Log.d(TAG, "📝 Expected: Pattern containing 'STATIC→DOWN→DIAGONAL_DOWN_LEFT→LEFT→DIAGONAL_UP_LEFT→UP→STATIC'")
        }
        
        Log.d(TAG, "🔍 DHAMMAH PATTERN CHECK END")
        Log.d(TAG, "═══════════════════════════════════")
        
        return hasDhammahPattern
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
        Log.d(TAG, "- isKasrahMode: $isKasrahMode")
        Log.d(TAG, "- isDhammahMode: $isDhammahMode")
        Log.d(TAG, "- hijaiyahGestureDetected: $hijaiyahGestureDetected")
        Log.d(TAG, "- isWaitingForLeftMovement: $isWaitingForLeftMovement")
        Log.d(TAG, "- isWaitingForDownMovement: $isWaitingForDownMovement")
        Log.d(TAG, "- isWaitingForUpMovement: $isWaitingForUpMovement")
        
        if (isFathahMode) {
            handleFathahGestureDetection(detectedGesture, currentTime, isCurrentlyStatic)
        } else if (isKasrahMode) {
            handleKasrahGestureDetection(detectedGesture, currentTime, isCurrentlyStatic)
        } else if (isDhammahMode) {
            handleDhammahGestureDetection(detectedGesture, currentTime, isCurrentlyStatic)
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
        
        // Get base hijaiyah letter using position from FathahData (more efficient)
        val baseHijaiyahLetter = fathahLetter?.let { fathah ->
            HijaiyahData.getLetterById(fathah.position)
        }
        
        val isCorrectHijaiyahGesture = baseHijaiyahLetter?.gestureName?.equals(detectedGesture, ignoreCase = true) == true
        
        Log.d(TAG, "Fathah detection - fathahLetter: $fathahLetter, baseHijaiyahLetter: $baseHijaiyahLetter, gesture: $detectedGesture, correct: $isCorrectHijaiyahGesture")
        
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
    
    private fun handleKasrahGestureDetection(detectedGesture: String, currentTime: Long, isHandStatic: Boolean) {
        // Get the Kasrah letter data for gesture matching
        val kasrahLetter = when {
            targetLetter != null -> KasrahData.getLetterByArabic(targetLetter!!)
            targetLetterName != null -> KasrahData.getAllLetters().find { 
                it.transliteration.equals(targetLetterName, ignoreCase = true)
            }
            else -> null
        }
        
        // Get base hijaiyah letter using position from KasrahData
        val baseHijaiyahLetter = kasrahLetter?.let { kasrah ->
            HijaiyahData.getLetterByPosition(kasrah.position)
        }
        
        val isCorrectHijaiyahGesture = baseHijaiyahLetter?.gestureName?.equals(detectedGesture, ignoreCase = true) == true
        
        Log.d(TAG, "Kasrah detection - kasrahLetter: $kasrahLetter, baseHijaiyahLetter: $baseHijaiyahLetter, gesture: $detectedGesture, correct: $isCorrectHijaiyahGesture")
        
        if (!hijaiyahGestureDetected && !isWaitingForDownMovement) {
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
                        onKasrahHijaiyahGestureSuccess()
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
        } else if (hijaiyahGestureDetected && isWaitingForDownMovement && !kasrahPatternDetected) {
            // Phase 2: Wait for Kasrah movement pattern (STATIC then DOWN)
            Log.d(TAG, "🔍 Phase 2 Kasrah - Gesture: '$detectedGesture', isCorrect: $isCorrectHijaiyahGesture, target: ${baseHijaiyahLetter?.gestureName}")
            
            // Debug current state
            debugMovementHistory()
            
            // Check for Kasrah movement pattern
            val isKasrahPattern = checkKasrahMovementPattern()
            
            Log.d(TAG, "🎯 Kasrah pattern check result: isKasrahPattern=$isKasrahPattern, isCorrectGesture=$isCorrectHijaiyahGesture")
            
            if (isCorrectHijaiyahGesture && isKasrahPattern) {
                // Kasrah pattern detected with correct gesture!
                Log.d(TAG, "🎉 Kasrah pattern confirmed! Success!")
                Log.d(TAG, "✅ BOTH CONDITIONS MET: Correct gesture + Kasrah pattern")
                kasrahPatternDetected = true // Prevent multiple detections
                onKasrahSuccess()
                return
            }
            
            if (isCorrectHijaiyahGesture) {
                // Correct gesture but waiting for movement pattern
                val lastMovements = if (movementHistory.size >= 2) {
                    "${movementHistory[movementHistory.size - 2]} → ${movementHistory.last()}"
                } else {
                    movementHistory.joinToString(" → ")
                }
                
                updatePredictionText("Gerak: $lastMovements. Untuk Kasrah: diam dulu, lalu ke BAWAH")
            } else {
                // Wrong gesture, reset movement tracking for this attempt
                updatePredictionText(
                    "Pertahankan gesture ${baseHijaiyahLetter?.gestureName} dan lakukan gerakan: diam → bawah"
                )
            }
        }
    }
    
    private fun handleDhammahGestureDetection(detectedGesture: String, currentTime: Long, isHandStatic: Boolean) {
        // Get the Dhammah letter data for gesture matching
        val dhammahLetter = when {
            targetLetter != null -> DhammahData.getLetterByArabic(targetLetter!!)
            targetLetterName != null -> DhammahData.getAllLetters().find { 
                it.transliteration.equals(targetLetterName, ignoreCase = true)
            }
            else -> null
        }
        
        // Get base hijaiyah letter using position from DhammahData
        val baseHijaiyahLetter = dhammahLetter?.let { dhammah ->
            HijaiyahData.getLetterByPosition(dhammah.position)
        }
        
        val isCorrectHijaiyahGesture = baseHijaiyahLetter?.gestureName?.equals(detectedGesture, ignoreCase = true) == true
        
        Log.d(TAG, "Dhammah detection - dhammahLetter: $dhammahLetter, baseHijaiyahLetter: $baseHijaiyahLetter, gesture: $detectedGesture, correct: $isCorrectHijaiyahGesture")
        
        if (!hijaiyahGestureDetected && !isWaitingForUpMovement) {
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
                    
                    // Check if 1 second completed
                    if (elapsedTime >= REQUIRED_DURATION) {
                        onDhammahHijaiyahGestureSuccess()
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
        } else if (hijaiyahGestureDetected && isWaitingForUpMovement && !dhammahPatternDetected) {
            // Phase 2: Wait for Dhammah movement pattern (STATIC → DOWN → DIAGONAL_DOWN_LEFT → LEFT → DIAGONAL_UP_LEFT → UP → STATIC)
            Log.d(TAG, "🔍 Phase 2 Dhammah - Gesture: '$detectedGesture', isCorrect: $isCorrectHijaiyahGesture, target: ${baseHijaiyahLetter?.gestureName}")
            
            // Debug current state
            debugMovementHistory()
            
            // Check for Dhammah movement pattern
            val isDhammahPattern = checkDhammahMovementPattern()
            
            Log.d(TAG, "🎯 Dhammah pattern check result: isDhammahPattern=$isDhammahPattern, isCorrectGesture=$isCorrectHijaiyahGesture")
            
            if (isCorrectHijaiyahGesture && isDhammahPattern) {
                // Dhammah pattern detected with correct gesture!
                Log.d(TAG, "🎉 Dhammah pattern confirmed! Success!")
                Log.d(TAG, "✅ BOTH CONDITIONS MET: Correct gesture + Dhammah pattern")
                dhammahPatternDetected = true // Prevent multiple detections
                onDhammahSuccess()
                return
            }
            
            if (isCorrectHijaiyahGesture) {
                // Correct gesture but waiting for movement pattern
                val lastMovements = if (movementHistory.size >= 2) {
                    "${movementHistory[movementHistory.size - 2]} → ${movementHistory.last()}"
                } else {
                    movementHistory.joinToString(" → ")
                }
                
                updatePredictionText("Gerakkan tangan membentuk huruf U")
            } else {
                // Wrong gesture, reset movement tracking for this attempt
                updatePredictionText(
                    "Tunjukkan gesture ${baseHijaiyahLetter?.gestureName} lalu gerakkan membentuk huruf U"
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
        
        // Update step indicator to step 2
        updateStepIndicator(2)
        
        // Update UI with better guidance
        updateStatusUI(
            title = "Langkah 2: Gerakan Fathah ←",
            message = "Bagus! Sekarang gerakkan tangan ke KIRI",
            statusType = StatusType.SUCCESS,
            showProgress = false
        )
        
        fragmentCameraBinding.textLetterName.text = targetLetterName ?: "Fathah"
        
        Log.d(TAG, "Hijaiyah gesture detected successfully. Waiting for Fathah movement pattern.")
        Log.d(TAG, "Movement history initialized with STATIC baseline: ${movementHistory.joinToString(" → ")}")
    }
    
    private fun onKasrahHijaiyahGestureSuccess() {
        hijaiyahGestureDetected = true
        isWaitingForDownMovement = true
        kasrahPatternDetected = false // Reset pattern detection flag
        
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
        Log.d(TAG, "Added initial STATIC to movement history for Kasrah. History[0] = STATIC")
        
        // Update step indicator to step 2
        updateStepIndicator(2)
        
        // Update UI with better guidance
        updateStatusUI(
            title = "Langkah 2: Gerakan Kasrah ↓",
            message = "Bagus! Sekarang gerakkan tangan ke BAWAH",
            statusType = StatusType.SUCCESS,
            showProgress = false
        )
        
        fragmentCameraBinding.textLetterName.text = targetLetterName ?: "Kasrah"
        
        Log.d(TAG, "Hijaiyah gesture detected successfully. Waiting for Kasrah movement pattern.")
        Log.d(TAG, "Movement history initialized with STATIC baseline: ${movementHistory.joinToString(" → ")}")
    }
    
    private fun onDhammahHijaiyahGestureSuccess() {
        hijaiyahGestureDetected = true
        isWaitingForUpMovement = true
        dhammahPatternDetected = false // Reset pattern detection flag
        
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
        Log.d(TAG, "Added initial STATIC to movement history for Dhammah. History[0] = STATIC")
        
        // Update step indicator to step 2
        updateStepIndicator(2)
        
        // Update UI with better guidance
        updateStatusUI(
            title = "Langkah 2: Gerakan Dhammah ∪",
            message = "Bagus! Sekarang gerakkan tangan membentuk huruf U",
            statusType = StatusType.SUCCESS,
            showProgress = false
        )
        
        fragmentCameraBinding.textLetterName.text = targetLetterName ?: "Dhammah"
        
        Log.d(TAG, "Hijaiyah gesture detected successfully. Waiting for Dhammah movement pattern.")
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
        // Update the status message based on current detection state
        if (_fragmentCameraBinding == null || !isAdded) return
        
        val isSuccess = text.contains("BERHASIL", ignoreCase = true) || text.contains("Bagus", ignoreCase = true)
        val isError = text.contains("tidak cocok", ignoreCase = true) || text.contains("Reset", ignoreCase = true)
        val isProgress = text.contains("mulai hitung", ignoreCase = true) || text.contains("ms", ignoreCase = true)
        
        val statusType = when {
            isSuccess -> StatusType.SUCCESS
            isError -> StatusType.ERROR
            isProgress -> StatusType.DETECTING
            else -> StatusType.WAITING
        }
        
        // Parse detected gesture from text if available
        val gestureMatch = Regex("^([A-Za-z_]+)\\s").find(text)
        val detectedGesture = gestureMatch?.groupValues?.get(1)
        val targetGesture = getGestureNameWithFallback()
        val isCorrectGesture = detectedGesture != null && targetGesture != null && 
                               detectedGesture.equals(targetGesture, ignoreCase = true)
        
        // Determine title based on state
        val title = when {
            isWaitingForLeftMovement -> "Langkah 2: Gerakan Fathah"
            isWaitingForDownMovement -> "Langkah 2: Gerakan Kasrah"
            isWaitingForUpMovement -> "Langkah 2: Gerakan Dhammah"
            isSuccess -> "Progres Gesture"
            isProgress -> "Menahan Gesture..."
            else -> "Mendeteksi Gesture..."
        }
        
        // Show progress if counting
        val showProgress = isProgress || text.contains("tangan diam", ignoreCase = true)
        val progress = if (showProgress) {
            val progressMatch = Regex("(\\d+)ms\\s*/\\s*(\\d+)ms").find(text)
            if (progressMatch != null) {
                val current = progressMatch.groupValues[1].toLongOrNull() ?: 0
                val total = progressMatch.groupValues[2].toLongOrNull() ?: REQUIRED_DURATION
                ((current * 100) / total).toInt().coerceIn(0, 100)
            } else {
                fragmentCameraBinding.progressTimer.progress
            }
        } else 0
        
        updateStatusUI(
            title = title,
            message = text,
            statusType = statusType,
            showProgress = showProgress,
            progress = progress,
            detectedGesture = detectedGesture,
            isCorrectGesture = isCorrectGesture
        )
    }
    
    private fun resetGestureDetection() {
        currentGesture = null
        gestureStartTime = 0L
        consecutiveCorrectCount = 0
        fragmentCameraBinding.progressTimer.progress = 0
        
        // Update UI to reset state
        updateStatusUI(
            title = "Mendeteksi Gesture...",
            message = "Posisikan tangan Anda dan coba lagi",
            statusType = StatusType.WAITING,
            showProgress = false
        )
        
        // Reset step indicator to step 1
        updateStepIndicator(1)
        
        // Reset movement pattern tracking
        resetStaticTracking()
        fathahPatternDetected = false
        kasrahPatternDetected = false
        
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

    private fun submitLetterProgressRemote(letterPosition: Int) {
        if (!this::authManager.isInitialized || !authManager.isLoggedIn) {
            return
        }
        val token = authManager.authToken
        if (token.isEmpty()) {
            return
        }
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val roomId = activeRoomId ?: getActiveRoomId() ?: return@launch
                apiService.submitLetterProgress(
                    roomId = roomId,
                    hijaiyahId = letterPosition,
                    status = "completed",
                    authToken = token
                ).onSuccess {
                    roomPreferenceManager.preferredRoomId = roomId
                }.onFailure {
                    Log.e(TAG, "Failed to submit letter progress: ${it.message}", it)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to submit letter progress: ${e.message}", e)
            }
        }
    }
    
    private fun showResult(success: Boolean) {
        if (_fragmentCameraBinding == null || !isAdded) return
        
        fragmentCameraBinding.overlayResult.visibility = View.VISIBLE
        
        // Update step indicator to complete
        updateStepIndicator(3)
        
        if (success) {
            fragmentCameraBinding.iconResult.setImageResource(R.drawable.ic_check_circle)
            fragmentCameraBinding.textResult.text = "BERHASIL!"
            fragmentCameraBinding.textResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.teal_primary))
            
            val displayLetter = targetLetter ?: getDefaultLetter()
            val displayName = targetLetterName ?: getDefaultLetterName()
            
            fragmentCameraBinding.textResultDetail.text = "Anda berhasil memperagakan huruf $displayName dengan benar! 🎉"
            fragmentCameraBinding.textResultLetter.text = displayLetter
            fragmentCameraBinding.textResultLetterName.text = displayName
            
            // Mark letter as completed
            val letterPosition = arguments?.getInt("letterPosition", -1) ?: -1
            if (letterPosition > 0) {
                progressManager.markLetterCompleted(letterPosition)
                submitLetterProgressRemote(letterPosition)
                Log.d(TAG, "Letter $letterPosition ($targetLetterName) marked as completed")
            }
            
            // If embedded inside `LatihanPracticeActivity`, return result via FragmentResult
            val isEmbedded = arguments?.getBoolean("embedded", false) ?: false
            if (isEmbedded) {
                val result = Bundle().apply {
                    putBoolean("success", true)
                    putInt("letterPosition", letterPosition)
                }
                parentFragmentManager.setFragmentResult("camera_result", result)

                // Remove self from container after delay
                view?.postDelayed({
                    activity?.runOnUiThread {
                        try {
                            if (isAdded) {
                                parentFragmentManager.beginTransaction().remove(this@CameraFragment).commitAllowingStateLoss()
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to remove embedded CameraFragment: ${e.message}")
                        }
                    }
                }, 1500)
                return
            }

            // Show countdown for non-embedded mode
            var countdown = 3
            countdownTimer?.cancel()
            countdownTimer = object : CountDownTimer(3000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    countdown--
                }

                override fun onFinish() {
                    try {
                        if (!isAdded) return
                        activity?.let { act ->
                            Navigation.findNavController(act, R.id.fragment_container).navigateUp()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Navigation error: ${e.message}")
                        try {
                            activity?.onBackPressed()
                        } catch (e2: Exception) {
                            Log.e(TAG, "Fallback navigation error: ${e2.message}")
                        }
                    }
                }
            }
            countdownTimer?.start()
            
        } else {
            fragmentCameraBinding.iconResult.setImageResource(R.drawable.ic_error_circle)
            fragmentCameraBinding.textResult.text = "Coba Lagi"
            fragmentCameraBinding.textResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.fathah_card_color))
            fragmentCameraBinding.textResultDetail.text = "Gesture belum tepat. Silakan coba lagi dengan posisi yang benar."
            fragmentCameraBinding.textResultLetter.text = targetLetter ?: "?"
            fragmentCameraBinding.textResultLetterName.text = targetLetterName ?: "Unknown"
        }
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
        // Safety check - ensure fragment is still attached
        if (!isAdded || _fragmentCameraBinding == null) {
            Log.w(TAG, "setUpCamera called but fragment not attached, skipping")
            return
        }
        
        val ctx = context ?: run {
            Log.e(TAG, "setUpCamera: context is null, cannot initialize camera")
            return
        }
        
        try {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener(
                {
                    try {
                        // Safety check again - fragment might have been detached during async operation
                        if (!isAdded || _fragmentCameraBinding == null) {
                            Log.w(TAG, "Fragment detached before camera provider ready, skipping bind")
                            return@addListener
                        }
                        
                        // CameraProvider
                        cameraProvider = cameraProviderFuture.get()

                        // Build and bind the camera use cases
                        bindCameraUseCases()
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to get camera provider: ${e.message}", e)
                        handleCameraInitializationError(e)
                    }
                }, ContextCompat.getMainExecutor(ctx)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize camera provider: ${e.message}", e)
            handleCameraInitializationError(e)
        }
    }
    
    private fun handleCameraInitializationError(error: Exception) {
        Log.e(TAG, "Camera initialization error: ${error.message}", error)
        
        activity?.runOnUiThread {
            if (isAdded && context != null) {
                // For embedded mode, notify parent about failure and close self
                val isEmbedded = arguments?.getBoolean("embedded", false) ?: false
                if (isEmbedded) {
                    val result = Bundle().apply {
                        putBoolean("success", false)
                        putBoolean("camera_error", true)
                        putString("error", error.message ?: "Camera initialization failed")
                    }
                    parentFragmentManager.setFragmentResult("camera_result", result)
                    
                    // Close self after a short delay
                    view?.postDelayed({
                        try {
                            if (isAdded) {
                                parentFragmentManager.beginTransaction()
                                    .remove(this@CameraFragment)
                                    .commitAllowingStateLoss()
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to remove CameraFragment after error: ${e.message}")
                        }
                    }, 500)
                } else {
                    // Not embedded, show toast and navigate back
                    Toast.makeText(
                        context,
                        "Gagal menginisialisasi kamera. Silakan coba lagi.",
                        Toast.LENGTH_LONG
                    ).show()
                    
                    try {
                        activity?.onBackPressed()
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to navigate back: ${e.message}")
                    }
                }
            }
        }
    }

    // Declare and bind preview, capture and analysis use cases
    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases() {
        // Safety checks
        if (!isAdded || _fragmentCameraBinding == null) {
            Log.w(TAG, "bindCameraUseCases: fragment not attached, skipping")
            return
        }

        // CameraProvider
        val cameraProvider = cameraProvider
        if (cameraProvider == null) {
            Log.e(TAG, "Camera initialization failed: cameraProvider is null")
            handleCameraInitializationError(IllegalStateException("Camera provider not available"))
            return
        }
        
        // Get display rotation safely
        val displayRotation = try {
            fragmentCameraBinding.viewFinder.display?.rotation ?: android.view.Surface.ROTATION_0
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get display rotation, using default: ${e.message}")
            android.view.Surface.ROTATION_0
        }

        try {
            val cameraSelector =
                CameraSelector.Builder().requireLensFacing(cameraFacing).build()

            // Preview. Only using the 4:3 ratio because this is the closest to our models
            preview = Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(displayRotation)
                .build()

            // ImageAnalysis. Using RGBA 8888 to match how our models work
            imageAnalyzer =
                ImageAnalysis.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .setTargetRotation(displayRotation)
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

            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageAnalyzer
            )

            // Attach the viewfinder's surface provider to preview use case
            if (_fragmentCameraBinding != null) {
                preview?.setSurfaceProvider(fragmentCameraBinding.viewFinder.surfaceProvider)
            }
            
            Log.d(TAG, "Camera use cases bound successfully")
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
            handleCameraInitializationError(exc)
        }
    }

    private suspend fun getActiveRoomId(): Int? {
        roomPreferenceManager.preferredRoomId?.let { return it }
        if (!authManager.isLoggedIn || authManager.authToken.isEmpty()) return null
        return try {
            val roomsResult = apiService.getMyRooms(authManager.authToken)
            val roomId = roomsResult.getOrNull()?.rooms?.firstOrNull()?.roomId
            roomId?.let { roomPreferenceManager.preferredRoomId = it }
            roomId
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch rooms: ${e.message}", e)
            null
        }
    }

    private fun recognizeHand(imageProxy: ImageProxy) {
        gestureRecognizerHelper.recognizeLiveStream(
            imageProxy = imageProxy,
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        try {
            if (_fragmentCameraBinding != null && isAdded) {
                val rotation = fragmentCameraBinding.viewFinder.display?.rotation
                    ?: android.view.Surface.ROTATION_0
                imageAnalyzer?.targetRotation = rotation
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to update rotation on config change: ${e.message}")
        }
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
            // Safe check - fragment might be detached
            if (!isAdded || _fragmentCameraBinding == null) return@runOnUiThread
            
            try {
                context?.let { ctx ->
                    Toast.makeText(ctx, error, Toast.LENGTH_SHORT).show()
                }
                gestureRecognizerResultAdapter.updateResults(emptyList())

                if (errorCode == GestureRecognizerHelper.GPU_ERROR) {
                    _fragmentCameraBinding?.bottomSheetLayout?.spinnerDelegate?.setSelection(
                        GestureRecognizerHelper.DELEGATE_CPU, false
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in onError callback", e)
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
    
    override fun onHandLost() {
        activity?.runOnUiThread {
            Log.d(TAG, "🚨 HAND LOST - Clearing movement history completely")
            
            // Clear movement history completely when hand is lost
            val previousHistorySize = movementHistory.size
            movementHistory.clear()
            
            Log.d(TAG, "📋 Movement history cleared: $previousHistorySize movements removed")
            Log.d(TAG, "📊 New history size: ${movementHistory.size}")
            Log.d(TAG, "🔄 Full history now: [${movementHistory.joinToString(" → ")}]")
            
            // Reset movement tracking state
            isCurrentlyStatic = true
            
            // CHECK: If we're in Phase 2 (movement detection), reset back to Phase 1
            if (isWaitingForLeftMovement && hijaiyahGestureDetected) {
                Log.d(TAG, "🔄 FATHAH PHASE 2 HAND LOST - Resetting back to Phase 1 (Hijaiyah detection)")
                
                // Reset all Phase 2 states for Fathah
                hijaiyahGestureDetected = false
                isWaitingForLeftMovement = false
                fathahPatternDetected = false
                
                // Reset gesture detection completely (back to Phase 1)
                resetGestureDetection()
                
                // Update UI to show Phase 1 instruction
                updatePredictionText("Tangan hilang - ulangi dari awal: tunjukkan gesture ${targetLetterName}")
                fragmentCameraBinding.textLetterName.text = "Fase 1: Tunjukkan ${targetLetterName}"
                
                Log.d(TAG, "✅ Successfully reset to Phase 1 - waiting for Hijaiyah gesture")
            } else if (isWaitingForDownMovement && hijaiyahGestureDetected) {
                Log.d(TAG, "🔄 KASRAH PHASE 2 HAND LOST - Resetting back to Phase 1 (Hijaiyah detection)")
                
                // Reset all Phase 2 states for Kasrah
                hijaiyahGestureDetected = false
                isWaitingForDownMovement = false
                kasrahPatternDetected = false
                
                // Reset gesture detection completely (back to Phase 1)
                resetGestureDetection()
                
                // Update UI to show Phase 1 instruction
                updatePredictionText("Tangan hilang - ulangi dari awal: tunjukkan gesture ${targetLetterName}")
                fragmentCameraBinding.textLetterName.text = "Fase 1: Tunjukkan ${targetLetterName}"
                
                Log.d(TAG, "✅ Successfully reset to Phase 1 - waiting for Hijaiyah gesture")
            } else if (isWaitingForUpMovement && hijaiyahGestureDetected) {
                Log.d(TAG, "🔄 DHAMMAH PHASE 2 HAND LOST - Resetting back to Phase 1 (Hijaiyah detection)")
                
                // Reset all Phase 2 states for Dhammah
                hijaiyahGestureDetected = false
                isWaitingForUpMovement = false
                dhammahPatternDetected = false
                
                // Reset gesture detection completely (back to Phase 1)
                resetGestureDetection()
                
                // Update UI to show Phase 1 instruction
                updatePredictionText("Tangan hilang - ulangi dari awal: tunjukkan gesture ${targetLetterName}")
                fragmentCameraBinding.textLetterName.text = "Fase 1: Tunjukkan ${targetLetterName}"
                
                Log.d(TAG, "✅ Successfully reset to Phase 1 - waiting for Hijaiyah gesture")
            } else {
                // We're in Phase 1 or not started yet
                updatePredictionText("Tangan hilang - letakkan kembali untuk melanjutkan")
                Log.d(TAG, "ℹ️ Hand lost during Phase 1 or initial state")
            }
        }
    }
    
    private fun onFathahSuccess() {
        // Success! User performed correct Hijaiyah gesture + left movement for Fathah
        Log.d(TAG, "Fathah gesture completed successfully!")
        
        // Stop detection and reset all states
        isDetecting = false
        isWaitingForLeftMovement = false
        resetStaticTracking()
        practiceTimer?.cancel()
        resetTimer?.cancel()
        
        // Use showResult which properly handles embedded mode and FragmentResult
        showResult(true)
    }
    
    private fun onKasrahSuccess() {
        // Success! User performed correct Hijaiyah gesture + down movement for Kasrah
        Log.d(TAG, "Kasrah gesture completed successfully!")
        
        // Stop detection and reset all states
        isDetecting = false
        isWaitingForDownMovement = false
        resetStaticTracking()
        practiceTimer?.cancel()
        resetTimer?.cancel()
        
        // Use showResult which properly handles embedded mode and FragmentResult
        showResult(true)
    }
    
    private fun onDhammahSuccess() {
        // Success! User performed correct Hijaiyah gesture + complex movement pattern for Dhammah
        Log.d(TAG, "Dhammah gesture completed successfully!")
        
        // Stop detection and reset all states
        isDetecting = false
        isWaitingForUpMovement = false
        resetStaticTracking()
        practiceTimer?.cancel()
        resetTimer?.cancel()
        
        // Use showResult which properly handles embedded mode and FragmentResult
        showResult(true)
    }
    
    /**
     * Get gesture name with fallback mechanism
     * This function tries multiple strategies to get the gesture name
     */
    private fun getGestureNameWithFallback(): String? {
        if (isFathahMode) {
            Log.d(TAG, "Getting gesture name for Fathah mode")
            val fathahLetter = if (targetLetter != null) {
                FathahData.getLetterByArabic(targetLetter!!)
            } else {
                FathahData.getAllLetters().find { it.transliteration.equals(targetLetterName, ignoreCase = true) }
            }
            
            Log.d(TAG, "Fathah letter found: ${fathahLetter?.arabic} at position ${fathahLetter?.position}")
            
            // Try to get from FathahLetter directly first
            fathahLetter?.gestureName?.let { 
                Log.d(TAG, "Got gesture name from FathahLetter: $it")
                return it 
            }
            
            // Fallback: Get from base HijaiyahData
            val baseHijaiyah = fathahLetter?.let { 
                HijaiyahData.getLetterById(it.position) 
                    ?: HijaiyahData.getLetterByPosition(it.position)
            }
            Log.d(TAG, "Base Hijaiyah found: ${baseHijaiyah?.gestureName}")
            return baseHijaiyah?.gestureName
        }
        
        if (isKasrahMode) {
            Log.d(TAG, "Getting gesture name for Kasrah mode")
            val kasrahLetter = if (targetLetter != null) {
                KasrahData.getLetterByArabic(targetLetter!!)
            } else {
                KasrahData.getAllLetters().find { it.transliteration.equals(targetLetterName, ignoreCase = true) }
            }
            
            Log.d(TAG, "Kasrah letter found: ${kasrahLetter?.arabic} at position ${kasrahLetter?.position}")
            
            // Try to get from KasrahLetter directly first
            kasrahLetter?.gestureName?.let { 
                Log.d(TAG, "Got gesture name from KasrahLetter: $it")
                return it 
            }
            
            // Fallback: Get from base HijaiyahData
            val baseHijaiyah = kasrahLetter?.let { 
                HijaiyahData.getLetterByPosition(it.position) 
                    ?: HijaiyahData.getLetterById(it.position)
            }
            Log.d(TAG, "Base Hijaiyah found: ${baseHijaiyah?.gestureName}")
            return baseHijaiyah?.gestureName
        }
        
        if (isDhammahMode) {
            Log.d(TAG, "Getting gesture name for Dhammah mode")
            val dhammahLetter = if (targetLetter != null) {
                DhammahData.getLetterByArabic(targetLetter!!)
            } else {
                DhammahData.getAllLetters().find { it.transliteration.equals(targetLetterName, ignoreCase = true) }
            }
            
            Log.d(TAG, "Dhammah letter found: ${dhammahLetter?.arabic} at position ${dhammahLetter?.position}")
            
            // Try to get from DhammahLetter directly first
            dhammahLetter?.gestureName?.let { 
                Log.d(TAG, "Got gesture name from DhammahLetter: $it")
                return it 
            }
            
            // Fallback: Get from base HijaiyahData
            val baseHijaiyah = dhammahLetter?.let { 
                HijaiyahData.getLetterByPosition(it.position) 
                    ?: HijaiyahData.getLetterById(it.position)
            }
            Log.d(TAG, "Base Hijaiyah found: ${baseHijaiyah?.gestureName}")
            return baseHijaiyah?.gestureName
        }
        
        // Regular Hijaiyah mode
        Log.d(TAG, "Getting gesture name for regular Hijaiyah mode")
        val hijaiyahLetter = if (targetLetter != null) {
            HijaiyahData.letters.find { it.arabic == targetLetter }
                ?: HijaiyahData.getLetterByArabic(targetLetter!!)
        } else {
            HijaiyahData.letters.find { it.transliteration.equals(targetLetterName, ignoreCase = true) }
        }
        Log.d(TAG, "Hijaiyah letter found: ${hijaiyahLetter?.gestureName}")
        return hijaiyahLetter?.gestureName
    }
    
    private fun showTutorialDialog() {
        try {
            // Get current letter data
            val hurufArab = targetLetter ?: "ا"
            val hurufLatin = targetLetterName ?: "Alif"
            val gestureName = getGestureNameWithFallback() ?: ""
            
            Log.d(TAG, "Opening PragaActivity for letter: $hurufArab ($hurufLatin) with gesture: $gestureName")
            
            // Open PragaActivity with letter data
            val intent = Intent(requireContext(), PragaActivity::class.java).apply {
                putExtra("huruf_arab", hurufArab)
                putExtra("huruf_latin", hurufLatin)
                putExtra("gesture_name", gestureName)
            }
            startActivity(intent)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error opening panduan page", e)
            Toast.makeText(requireContext(), "Tidak dapat membuka halaman panduan", Toast.LENGTH_SHORT).show()
        }
    }
}
