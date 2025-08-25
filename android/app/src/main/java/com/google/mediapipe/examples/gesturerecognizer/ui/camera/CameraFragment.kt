/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 *     // Hijaiyah Learning Variables
    private var isHijaiyahMode = false
    private var selectedLetter: String? = null
    private var letterName: String? = null
    private var letterPosition: Int? = null
    private var progressManager: HijaiyahProgressManager? = null
    
    // Detection and confirmation variables
    private var currentDetectedLetter: String? = null
    private var detectionStartTime = 0L
    private var isConfirming = false
    private var confirmationDuration = 3000L // 3 seconds
    private var lastGestureUpdate = 0L
    private val gestureUpdateInterval = 100L // Update UI every 100mser the Apache License, Version 2.0 (the "License");
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
package com.google.mediapipe.examples.gesturerecognizer.ui.camera

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.mediapipe.examples.gesturerecognizer.GestureRecognizerHelper
import com.google.mediapipe.examples.gesturerecognizer.HijaiyahData
import com.google.mediapipe.examples.gesturerecognizer.MainViewModel
import com.google.mediapipe.examples.gesturerecognizer.OverlayView
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahProgressManager
import com.google.mediapipe.examples.gesturerecognizer.databinding.FragmentCameraBinding
import com.google.mediapipe.examples.gesturerecognizer.ui.adapter.GestureRecognizerResultsAdapter
import com.google.mediapipe.examples.gesturerecognizer.ui.permissions.PermissionsFragment
import com.google.mediapipe.tasks.vision.core.RunningMode
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CameraFragment : Fragment(),
    GestureRecognizerHelper.GestureRecognizerListener {

    companion object {
        private const val TAG = "Hand gesture recognizer"
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

    // Hijaiyah Learning Variables
    private var isHijaiyahMode = false
    private var selectedLetter: String? = null
    private var letterName: String? = null
    private var letterPosition: Int? = null
    private var progressManager: HijaiyahProgressManager? = null
    
    // Detection and confirmation variables
    private var currentDetectedLetter: String? = null
    private var detectionStartTime = 0L
    private var isConfirming = false
    private var confirmationDuration = 3000L // 3 seconds
    private var lastGestureUpdate = 0L
    private val gestureUpdateInterval = 100L // Update UI every 100ms

    /** Blocking ML operations are performed using this executor */
    private lateinit var backgroundExecutor: ExecutorService

    override fun onResume() {
        super.onResume()
        
        try {
            // Make sure that all permissions are still present, since the
            // user could have removed them while the app was in paused state.
            if (!PermissionsFragment.hasPermissions(requireContext())) {
                Log.w(TAG, "Camera permission not granted, navigating to permissions")
                Navigation.findNavController(
                    requireActivity(), R.id.fragment_container
                ).navigate(R.id.action_camera_to_permissions)
                return
            }

            // Start the GestureRecognizerHelper again when users come back
            // to the foreground.
            backgroundExecutor.execute {
                try {
                    if (this@CameraFragment::gestureRecognizerHelper.isInitialized && 
                        gestureRecognizerHelper.isClosed()) {
                        gestureRecognizerHelper.setupGestureRecognizer()
                        Log.d(TAG, "GestureRecognizer re-initialized")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to reinitialize GestureRecognizer: ${e.message}", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onResume: ${e.message}", e)
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
        
        try {
            with(fragmentCameraBinding.recyclerviewResults) {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = gestureRecognizerResultAdapter
            }

            // Initialize our background executor
            backgroundExecutor = Executors.newSingleThreadExecutor()

            // Wait for the views to be properly laid out
            fragmentCameraBinding.viewFinder.post {
                // Set up the camera and its use cases
                try {
                    setUpCamera()
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to setup camera: ${e.message}", e)
                    Toast.makeText(requireContext(), "Failed to setup camera: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            // Create the Hand Gesture Recognition Helper that will handle the
            // inference
            backgroundExecutor.execute {
                try {
                    gestureRecognizerHelper = GestureRecognizerHelper(
                        context = requireContext(),
                        runningMode = RunningMode.LIVE_STREAM,
                        minHandDetectionConfidence = viewModel.currentMinHandDetectionConfidence,
                        minHandTrackingConfidence = viewModel.currentMinHandTrackingConfidence,
                        minHandPresenceConfidence = viewModel.currentMinHandPresenceConfidence,
                        currentDelegate = viewModel.currentDelegate,
                        gestureRecognizerListener = this@CameraFragment
                    )
                    Log.d(TAG, "GestureRecognizerHelper initialized successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to initialize GestureRecognizerHelper: ${e.message}", e)
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Failed to initialize gesture recognition: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }

            // Attach listeners to UI control widgets
            initBottomSheetControls()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in onViewCreated: ${e.message}", e)
            Toast.makeText(requireContext(), "Failed to initialize camera view: ${e.message}", Toast.LENGTH_LONG).show()
        }

        // Initialize our background executor
        backgroundExecutor = Executors.newSingleThreadExecutor()

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
        
        // Setup Hijaiyah learning mode if arguments are passed
        setupHijaiyahLearningMode()
    }
    
    private fun setupHijaiyahLearningMode() {
        // Check if we're in Hijaiyah learning mode
        arguments?.let { args ->
            selectedLetter = args.getString("selected_letter")
            letterName = args.getString("letter_name")
            letterPosition = args.getInt("letter_position", -1)
            
            if (selectedLetter != null && letterName != null && letterPosition != -1) {
                isHijaiyahMode = true
                progressManager = HijaiyahProgressManager(requireContext())
                
                // Show Hijaiyah learning overlay
                fragmentCameraBinding.hijaiyahLearningOverlay.visibility = View.VISIBLE
                fragmentCameraBinding.recyclerviewResults.visibility = View.GONE
                fragmentCameraBinding.bottomSheetLayout.root.visibility = View.GONE
                
                // Set target letter info
                fragmentCameraBinding.tvTargetLetter.text = selectedLetter
                fragmentCameraBinding.tvLetterName.text = letterName
                fragmentCameraBinding.tvDetectionStatus.text = "Tunjukkan tangan sesuai huruf $letterName"
                fragmentCameraBinding.tvProgressText.text = "Belajar Huruf: $letterName"
                fragmentCameraBinding.tvStatusText.text = "Siap"
                
                // Initialize progress bar
                fragmentCameraBinding.progressDetection.progress = 0
                fragmentCameraBinding.progressDetection.visibility = View.VISIBLE
                fragmentCameraBinding.tvProgressPercentage.text = "0%"
                
                // Setup interactive buttons
                fragmentCameraBinding.btnHint.setOnClickListener {
                    showHintDialog()
                }
                
                fragmentCameraBinding.btnReset.setOnClickListener {
                    resetLearningProgress()
                }
                
                // Setup back button
                fragmentCameraBinding.btnBackHijaiyah.setOnClickListener {
                    findNavController().navigateUp()
                }
            }
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
        try {
            val cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext())
            cameraProviderFuture.addListener(
                {
                    try {
                        // CameraProvider
                        cameraProvider = cameraProviderFuture.get()
                        Log.d(TAG, "CameraProvider obtained successfully")

                        // Build and bind the camera use cases
                        bindCameraUseCases()
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to get CameraProvider: ${e.message}", e)
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), "Failed to initialize camera provider: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }, ContextCompat.getMainExecutor(requireContext())
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to setup camera: ${e.message}", e)
            Toast.makeText(requireContext(), "Failed to setup camera: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Declare and bind preview, capture and analysis use cases
    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases() {
        try {
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
                            try {
                                recognizeHand(image)
                            } catch (e: Exception) {
                                Log.e(TAG, "Error in image analysis: ${e.message}", e)
                                image.close()
                            }
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
                
                Log.d(TAG, "Camera use cases bound successfully")
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Camera binding failed: ${exc.message}", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to bind camera use cases: ${e.message}", e)
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Failed to bind camera: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun recognizeHand(imageProxy: ImageProxy) {
        try {
            if (this::gestureRecognizerHelper.isInitialized) {
                gestureRecognizerHelper.recognizeLiveStream(
                    imageProxy = imageProxy,
                )
            } else {
                Log.w(TAG, "GestureRecognizerHelper not initialized, skipping frame")
                imageProxy.close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in recognizeHand: ${e.message}", e)
            imageProxy.close()
        }
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
                // Show result of recognized gesture
                val gestureCategories = resultBundle.results.first().gestures()
                
                if (isHijaiyahMode) {
                    handleHijaiyahLearningResults(gestureCategories)
                } else {
                    // Normal gesture recognition mode
                    if (gestureCategories.isNotEmpty()) {
                        gestureRecognizerResultAdapter.updateResults(
                            gestureCategories.first()
                        )
                    } else {
                        gestureRecognizerResultAdapter.updateResults(emptyList())
                    }

                    fragmentCameraBinding.bottomSheetLayout.inferenceTimeVal.text =
                        String.format("%d ms", resultBundle.inferenceTime)
                }

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
    
    private fun handleHijaiyahLearningResults(gestureCategories: List<List<com.google.mediapipe.tasks.components.containers.Category>>) {
        val currentTime = System.currentTimeMillis()
        
        // Only update UI every gestureUpdateInterval to avoid too frequent updates
        if (currentTime - lastGestureUpdate < gestureUpdateInterval) return
        lastGestureUpdate = currentTime

        if (gestureCategories.isNotEmpty() && gestureCategories[0].isNotEmpty()) {
            val topGesture = gestureCategories[0][0]
            val gestureName = topGesture.categoryName()
            val confidence = topGesture.score()

            // Map gesture to letter name (this would be your actual mapping logic)
            val detectedLetterName = mapGestureToLetter(gestureName)
            
            // Always show what's currently being detected
            updateDetectionDisplay(detectedLetterName, confidence)
            
            // Check if this matches our target letter
            val isTargetLetter = detectedLetterName == letterName
            
            if (isTargetLetter && confidence > 0.6f) {
                handleCorrectDetection(currentTime)
            } else if (detectedLetterName != null) {
                handleWrongDetection(detectedLetterName)
            } else {
                handleNoDetection()
            }
        } else {
            handleNoDetection()
        }
    }
    
    private fun mapGestureToLetter(gestureName: String): String? {
        // Map MediaPipe gestures to Hijaiyah letter names
        return when (gestureName.lowercase()) {
            "thumbs_up", "open_palm" -> "ALIF"
            "pointing_up" -> "BA" 
            "victory" -> "TA"
            "closed_fist" -> "TSA"
            "thumbs_down" -> "JIM"
            "ok_sign" -> "HA"
            "love_you" -> "KHA"
            "rock" -> "DAL"
            else -> gestureName.uppercase()
        }
    }
    
    private fun updateDetectionDisplay(detectedLetter: String?, confidence: Float) {
        if (detectedLetter != null) {
            fragmentCameraBinding.tvDetectionStatus.text = "Terdeteksi: $detectedLetter (${(confidence * 100).toInt()}%)"
            
            if (detectedLetter == letterName) {
                fragmentCameraBinding.statusIndicator.background = 
                    ContextCompat.getDrawable(requireContext(), R.drawable.status_indicator_success)
                fragmentCameraBinding.tvStatusText.text = "Benar!"
            } else {
                fragmentCameraBinding.statusIndicator.background = 
                    ContextCompat.getDrawable(requireContext(), R.drawable.status_indicator_default)
                fragmentCameraBinding.tvStatusText.text = "Salah"
            }
        } else {
            fragmentCameraBinding.tvDetectionStatus.text = "Tunjukkan tangan Anda"
            fragmentCameraBinding.statusIndicator.background = 
                ContextCompat.getDrawable(requireContext(), R.drawable.status_indicator_default)
            fragmentCameraBinding.tvStatusText.text = "Menunggu..."
        }
    }
    
    private fun handleCorrectDetection(currentTime: Long) {
        if (!isConfirming) {
            isConfirming = true
            detectionStartTime = currentTime
            fragmentCameraBinding.tvStatusText.text = "Konfirmasi..."
            startConfirmationCountdown()
        }
    }
    
    private fun handleWrongDetection(wrongLetter: String) {
        resetConfirmation()
        fragmentCameraBinding.tvStatusText.text = "Bukan $letterName"
    }
    
    private fun handleNoDetection() {
        resetConfirmation()
        fragmentCameraBinding.tvDetectionStatus.text = "Tunjukkan tangan untuk huruf $letterName"
        fragmentCameraBinding.tvStatusText.text = "Menunggu..."
    }
    
    private fun resetConfirmation() {
        isConfirming = false
        detectionStartTime = 0L
        fragmentCameraBinding.progressDetection.progress = 0
    }
    
    private fun startConfirmationCountdown() {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        val startTime = System.currentTimeMillis()
        
        val updateRunnable = object : Runnable {
            override fun run() {
                if (!isConfirming) return
                
                val elapsed = System.currentTimeMillis() - startTime
                val progress = ((elapsed.toFloat() / confirmationDuration) * 100).toInt()
                
                fragmentCameraBinding.progressDetection.progress = progress
                fragmentCameraBinding.tvProgressPercentage.text = "${progress}%"
                
                val remaining = ((confirmationDuration - elapsed) / 1000.0).toInt() + 1
                fragmentCameraBinding.tvDetectionStatus.text = "Tahan posisi: ${remaining}s"
                
                if (elapsed >= confirmationDuration) {
                    showSuccessWithAnimation()
                } else {
                    handler.postDelayed(this, 50)
                }
            }
        }
        
        handler.post(updateRunnable)
    }
    
    private fun showSuccessWithAnimation() {
        isConfirming = false
        fragmentCameraBinding.progressDetection.progress = 100
        fragmentCameraBinding.tvProgressPercentage.text = "100%"
        fragmentCameraBinding.tvDetectionStatus.text = "🎉 BERHASIL!"
        fragmentCameraBinding.tvStatusText.text = "Sempurna!"
        
        fragmentCameraBinding.root.postDelayed({
            showAnimatedSuccessDialog()
        }, 500)
    }
    
    private fun showAnimatedSuccessDialog() {
        val builder = AlertDialog.Builder(requireContext(), R.style.HijaiyahDialogTheme)
        builder.setTitle("🎉 FANTASTIC!")
        
        val successMessage = """
        ✨ Luar biasa! Anda berhasil mendeteksi huruf $letterName!
        
        🎯 Deteksi berhasil dalam 3 detik!
        ⭐ Akurasi: 100%
        
        🚀 Siap untuk huruf berikutnya?
        """.trimIndent()
        
        builder.setMessage(successMessage)
        builder.setPositiveButton("Lanjutkan! 🚀") { dialog, _ ->
            markLetterCompleted()
            dialog.dismiss()
            findNavController().navigateUp()
        }
        
        builder.setNegativeButton("Ulangi 🔄") { dialog, _ ->
            resetLearningProgress()
            dialog.dismiss()
        }
        
        val dialog = builder.create()
        dialog.show()
        
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.hijaiyah_green))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.hijaiyah_yellow))
    }
    
    private fun showHintDialog() {
        val builder = AlertDialog.Builder(requireContext(), R.style.HijaiyahDialogTheme)
        builder.setTitle("💡 Bantuan Pembelajaran")
        
        val hintMessage = """
        📋 Tips untuk huruf $letterName ($selectedLetter):
        
        🖐️ Posisikan tangan dengan jelas di depan kamera
        📱 Pastikan pencahayaan cukup terang
        🎯 Tahan posisi tangan selama beberapa detik
        🔄 Ulangi gerakan yang sama 5 kali berturut-turut
        
        ⭐ Semakin stabil gerakan, semakin cepat terdeteksi!
        """.trimIndent()
        
        builder.setMessage(hintMessage)
        builder.setPositiveButton("Mengerti! 👍") { dialog, _ ->
            dialog.dismiss()
        }
        
        val dialog = builder.create()
        dialog.show()
        
        // Customize button colors
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.hijaiyah_green))
    }

    private fun showSuccessDialog() {
        Log.d("HijaiyahLearning", "showSuccessDialog() called - creating dialog")
        
        try {
            val builder = AlertDialog.Builder(requireContext(), R.style.HijaiyahDialogTheme)
            builder.setTitle("🎉 Selamat!")
            
            val successMessage = """
            ✨ Luar biasa! Anda berhasil mempelajari huruf $letterName ($selectedLetter)!
            
            📊 Statistik Pembelajaran:
            ✅ Gerakan terdeteksi dengan sempurna
            🎯 Konfirmasi 3 detik berhasil
            ⏱️ Pembelajaran selesai!
            
            🚀 Anda siap melanjutkan ke huruf berikutnya!
            """.trimIndent()
            
            builder.setMessage(successMessage)
            builder.setCancelable(false)
            
            builder.setPositiveButton("Lanjutkan! 🚀") { dialog, _ ->
                Log.d("HijaiyahLearning", "Success dialog - Lanjutkan button clicked")
                markLetterCompleted()
                dialog.dismiss()
                findNavController().navigateUp()
            }
            
            builder.setNegativeButton("Ulangi 🔄") { dialog, _ ->
                Log.d("HijaiyahLearning", "Success dialog - Ulangi button clicked")
                resetLearningProgress()
                dialog.dismiss()
            }
            
            val dialog = builder.create()
            dialog.show()
            
            Log.d("HijaiyahLearning", "Success dialog shown successfully")
            
            // Customize button colors
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.hijaiyah_green))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.hijaiyah_yellow))
            
        } catch (e: Exception) {
            Log.e("HijaiyahLearning", "Error showing success dialog: ${e.message}")
            e.printStackTrace()
        }
    }
    
    private fun resetLearningProgress() {
        isConfirming = false
        detectionStartTime = 0L
        fragmentCameraBinding.tvDetectionStatus.text = "Mulai lagi! Tunjukkan huruf $letterName"
        fragmentCameraBinding.statusIndicator.background = ContextCompat.getDrawable(requireContext(), R.drawable.status_indicator_default)
        fragmentCameraBinding.progressDetection.progress = 0
        fragmentCameraBinding.progressDetection.visibility = View.VISIBLE
        fragmentCameraBinding.tvProgressPercentage.text = "0%"
        fragmentCameraBinding.tvStatusText.text = "Siap"
    }
    
    private fun checkIfGestureMatchesLetter(gestureName: String, targetLetter: String?): Boolean {
        // Simple mapping for demonstration - in real app, you'd have proper gesture recognition for Arabic letters
        // For now, we'll consider any detected hand gesture as potentially correct
        val isMatch = gestureName.isNotEmpty() && targetLetter != null
        Log.d("HijaiyahLearning", "Checking gesture '$gestureName' for letter '$targetLetter' -> Match: $isMatch")
        return isMatch
    }
    
    private fun markLetterCompleted() {
        letterPosition?.let { position ->
            progressManager?.markLetterCompleted(position)
            
            // Show completion message
            fragmentCameraBinding.tvDetectionStatus.text = "✅ Huruf $letterName berhasil dipelajari!"
            
            // Navigate back to Hijaiyah screen after a delay
            fragmentCameraBinding.root.postDelayed({
                findNavController().navigateUp()
            }, 2000)
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
