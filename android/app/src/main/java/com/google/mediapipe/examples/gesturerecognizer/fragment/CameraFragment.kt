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
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahProgressManager
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CameraFragment : Fragment(),
    GestureRecognizerHelper.GestureRecognizerListener {

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
    private var practiceTimer: CountDownTimer? = null
    private var resetTimer: CountDownTimer? = null
    private var countdownTimer: CountDownTimer? = null
    private lateinit var progressManager: HijaiyahProgressManager
    private var isDetecting = false
    private var currentGesture: String? = null
    private var gestureStartTime = 0L
    private var consecutiveCorrectCount = 0

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
        
        // Debug logging for received arguments
        Log.d(TAG, "Received arguments:")
        Log.d(TAG, "- targetLetter: $targetLetter")
        Log.d(TAG, "- targetLetterName: $targetLetterName")
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
        
        // Add trajectory overlay to the camera container programmatically
        val cameraContainer = fragmentCameraBinding.cameraContainer
        cameraContainer.addView(trajectoryOverlay)
        
        trajectoryAnalyzer = TrajectoryAnalyzer(trajectoryBuffer, trajectoryOverlay)

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
    
    private fun handleGestureDetection(detectedGesture: String) {
        if (!isDetecting) return
        
        val currentTime = System.currentTimeMillis()
        
        // Debug logging for gesture detection
        Log.d(TAG, "Gesture Detection:")
        Log.d(TAG, "- detectedGesture: '$detectedGesture'")
        Log.d(TAG, "- targetLetterName: '$targetLetterName'")
        Log.d(TAG, "- targetLetter: '$targetLetter'")
        
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
        
        if (isCorrectGesture) {
            // Correct gesture detected
            if (currentGesture != detectedGesture) {
                // New correct gesture sequence starts
                currentGesture = detectedGesture
                gestureStartTime = currentTime
                consecutiveCorrectCount = 1
                updatePredictionText("$detectedGesture (mulai hitung)")
            } else {
                // Continue correct gesture sequence
                consecutiveCorrectCount++
                val elapsedTime = currentTime - gestureStartTime
                val progress = (elapsedTime * 100 / REQUIRED_DURATION).toInt().coerceAtMost(100)
                
                fragmentCameraBinding.progressTimer.progress = progress
                fragmentCameraBinding.textCountdown.text = "${(REQUIRED_DURATION - elapsedTime) / 1000 + 1}"
                
                updatePredictionText("$detectedGesture (${elapsedTime}ms)")
                
                // Check if 2 seconds completed
                if (elapsedTime >= REQUIRED_DURATION) {
                    onGestureSuccess()
                }
            }
        } else {
            // Wrong gesture or no gesture
            if (detectedGesture.isNotEmpty() && detectedGesture != "Unknown") {
                updatePredictionText("$detectedGesture - tidak cocok")
            } else {
                updatePredictionText("Tidak ada gesture")
            }
            
            // Reset if there was a previous correct sequence
            if (currentGesture != null) {
                resetGestureDetection()
            }
        }
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
}
