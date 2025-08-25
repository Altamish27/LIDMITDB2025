/*
 * Copyright 2022 The TensorFlow Authors. All Rights Rese    private fun setupClickListeners() {
        binding.btnCobaDeteksi?.setOnClickListener {
            animateButtonClick(it) {
                try {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("HomeActivity", "Failed to start MainActivity: ${e.message}", e)
                    Toast.makeText(this, "Error starting camera: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
        
        // Handle card view clicks - hapus karena tidak ada lagi tombol About dan Settings
    }icensed under the Apache License, Version 2.0 (the "License");
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
package com.google.mediapipe.examples.gesturerecognizer.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityHomeBinding
import com.google.mediapipe.examples.gesturerecognizer.ui.main.MainActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupClickListeners()
        startAnimations()
    }

    private fun setupUI() {
        // Hide action bar for full screen experience
        supportActionBar?.hide()
        
        // Set status bar to transparent and handle insets
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        
        // Handle system bars insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupClickListeners() {
        binding.btnCobaDeteksi?.setOnClickListener {
            animateButtonClick(it) {
                try {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("HomeActivity", "Failed to start MainActivity: ${e.message}", e)
                    Toast.makeText(this, "Error starting camera: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
        
        // Navigate to Hijaiyah Learning
        binding.cardHijaiyah?.setOnClickListener {
            animateButtonClick(it) {
                try {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("navigate_to", "hijaiyah_list")
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("HomeActivity", "Failed to start Hijaiyah learning: ${e.message}", e)
                    Toast.makeText(this, "Error starting Hijaiyah learning: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
        
        // Handle card view clicks - hapus karena tidak ada lagi tombol About dan Settings
    }

    private fun startAnimations() {
        // Animate elements fade in
        binding.btnCobaDeteksi?.alpha = 0f

        // Create fade in animations        
        val buttonFadeIn = ObjectAnimator.ofFloat(binding.btnCobaDeteksi, "alpha", 0f, 1f).apply {
            duration = 800
            startDelay = 800
        }

        // Start all animations
        AnimatorSet().apply {
            playTogether(buttonFadeIn)
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun animateButtonClick(view: View, action: () -> Unit) {
        val scaleDown = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f),
                ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f)
            )
            duration = 100
        }
        
        val scaleUp = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 0.95f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 0.95f, 1f)
            )
            duration = 100
            startDelay = 100
        }
        
        scaleDown.start()
        scaleUp.start()
        
        view.postDelayed(action, 200)
    }
}
