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
    package com.google.mediapipe.examples.gesturerecognizer.core.main

    import android.Manifest
    import android.content.pm.PackageManager
    import android.os.Bundle
    import android.util.Log
    import android.widget.Toast
    import androidx.activity.OnBackPressedCallback
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.activity.viewModels
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.content.ContextCompat
    import androidx.navigation.fragment.NavHostFragment
    import androidx.navigation.NavController
    import com.google.mediapipe.examples.gesturerecognizer.core.viewmodel.MainViewModel
    import com.google.mediapipe.examples.gesturerecognizer.R
    import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityMainBinding
    import com.google.mediapipe.examples.gesturerecognizer.core.animation.ViewAnimationUtils

    class MainActivity : AppCompatActivity() {
        private lateinit var activityMainBinding: ActivityMainBinding
        private val viewModel: MainViewModel by viewModels()
        private lateinit var navController: NavController

        companion object {
            private const val TAG = "MainActivity"
        }

        // Permission request launcher
        private val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d(TAG, "Camera permission granted")
                setupNavigation()
            } else {
                Log.e(TAG, "Camera permission denied")
                Toast.makeText(
                    this,
                    "Camera permission is required for gesture recognition",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            
            // Enable hardware acceleration
            window.setFlags(
                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
            )
            
            activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(activityMainBinding.root)

            // Prepare for animation
            activityMainBinding.root.alpha = 0f
            
            // Check camera permission first
            checkCameraPermission()
        }

        private fun checkCameraPermission() {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) -> {
                    Log.d(TAG, "Camera permission already granted")
                    setupNavigation()
                }
                else -> {
                    Log.d(TAG, "Requesting camera permission")
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        }

        private fun setupNavigation() {
            try {
                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
                navController = navHostFragment.navController

                // Check if we need to open camera directly with parameters
                val openCamera = intent.getBooleanExtra("openCamera", false)
                if (openCamera) {
                    // Navigate to camera with parameters
                    val bundle = Bundle().apply {
                        putString("selectedLetter", intent.getStringExtra("selectedLetter"))
                        putString("target_letter", intent.getStringExtra("target_letter"))
                        putString("letterName", intent.getStringExtra("letterName"))
                        putString("target_letter_name", intent.getStringExtra("target_letter_name"))
                        putString("letterType", intent.getStringExtra("letterType"))
                        putString("diacritic", intent.getStringExtra("diacritic"))
                        putInt("letterPosition", intent.getIntExtra("letterPosition", -1))
                    }
                    navController.navigate(R.id.camera_fragment, bundle)
                } else {
                    // Check if we need to navigate to a specific destination
                    val navigateTo = intent.getStringExtra("navigate_to")
                    when (navigateTo) {
                        "latihan" -> {
                            // Use fragment transaction for latihan jilid
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, com.google.mediapipe.examples.gesturerecognizer.features.latihan.LatihanJilidFragment())
                                .commit()
                        }
                        // Default stays at hijaiyah_fragment (start destination)
                    }
                }

                // Handle back press with modern API
                onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        finish()
                    }
                })
                
                // Start entrance animation after navigation is ready
                activityMainBinding.root.post {
                    ViewAnimationUtils.fadeInScreen(activityMainBinding.root, 300)
                }
                
                Log.d(TAG, "Navigation setup completed")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to setup navigation: ${e.message}", e)
                Toast.makeText(this, "Failed to initialize camera interface: ${e.message}", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }