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
package com.google.mediapipe.examples.gesturerecognizer.features.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityHomeBinding
import com.google.mediapipe.examples.gesturerecognizer.core.main.MainActivity
import com.google.mediapipe.examples.gesturerecognizer.features.panduan.PanduanHijaiyahActivity
import com.google.mediapipe.examples.gesturerecognizer.features.surat.SuratListActivity
import com.google.mediapipe.examples.gesturerecognizer.features.auth.ProfileActivity
import com.google.mediapipe.examples.gesturerecognizer.features.auth.LoginActivity
import com.google.mediapipe.examples.gesturerecognizer.core.animation.ViewAnimationUtils

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable hardware acceleration for smooth animations
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupClickListeners()
        setupCustomFonts()
        setupSidebar()
        
        // Delay animations to ensure views are laid out properly
        binding.root.post {
            startEntranceAnimations()
        }
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
        
        // Prepare views for entrance animations
        binding.root.alpha = 0f
        ViewAnimationUtils.prepareViewsForAnimation(
            binding.btnMenu,
            binding.cardHijaiyah,
            binding.cardQuiz,
            binding.cardSurat,
            binding.btnLihatSemuaTabel
        )
    }

    private fun setupClickListeners() {
        // Menu Hamburger Click
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        // Navigate to Hijaiyah Learning
        binding.cardHijaiyah?.setOnClickListener {
            animateButtonClick(it) {
                try {
                    val intent = Intent(this, MainActivity::class.java)
                    // Navigate to default hijaiyah_fragment (header hijau dengan search bar)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("HomeActivity", "Failed to start Hijaiyah learning: ${e.message}", e)
                    Toast.makeText(this, "Error starting Hijaiyah learning: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
        
        // Navigate to Panduan Hijaiyah (Lihat Semua Tabel)
        binding.btnLihatSemuaTabel?.setOnClickListener {
            animateButtonClick(it) {
                try {
                    val intent = Intent(this, PanduanHijaiyahActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("HomeActivity", "Failed to start PanduanHijaiyahActivity: ${e.message}", e)
                    Toast.makeText(this, "Error opening panduan: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Navigate to Latihan (Quiz renamed)
        binding.cardQuiz?.setOnClickListener {
            animateButtonClick(it) {
                try {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("navigate_to", "latihan")
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("HomeActivity", "Failed to start Latihan: ${e.message}", e)
                    Toast.makeText(this, "Error starting latihan: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Navigate to Surat list
        binding.cardSurat?.setOnClickListener {
            animateButtonClick(it) {
                try {
                    val intent = Intent(this, SuratListActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("HomeActivity", "Failed to open Surat list: ${e.message}", e)
                    Toast.makeText(this, "Error opening Surat: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    private fun setupCustomFonts() {
        try {
            // Load custom Hijaiyah font
            val hijaiyahTypeface = Typeface.createFromAsset(assets, "fonts/fonthurufhijaiyah.TTF")
            
            // Apply font to any available Hijaiyah font example (commenting out for now since the view doesn't exist)
            // binding.tvHijaiyahFontExample?.typeface = hijaiyahTypeface
            
            Log.d("HomeActivity", "Custom fonts loaded successfully")
        } catch (e: Exception) {
            Log.e("HomeActivity", "Error loading custom fonts: ${e.message}", e)
        }
    }

    private fun startEntranceAnimations() {
        // Step 1: Fade in the entire screen first
        ViewAnimationUtils.fadeInScreen(binding.root, 300)
        
        // Step 2: Animate menu button with rotation
        binding.btnMenu?.let { view ->
            ViewAnimationUtils.animateViewEntrance(
                view = view,
                delay = 200,
                duration = 700,
                translationY = -50f,
                rotationDegrees = 180f,
                overshoot = 1.8f
            )
        }
        
        // Step 3: Animate cards with cascading effect
        binding.cardHijaiyah?.let { ViewAnimationUtils.animateCardEntrance(it, 400) }
        binding.cardQuiz?.let { ViewAnimationUtils.animateCardEntrance(it, 500) }
        binding.cardSurat?.let { ViewAnimationUtils.animateCardEntrance(it, 600) }
        
        // Step 4: Animate button
        binding.btnLihatSemuaTabel?.let { ViewAnimationUtils.animateButtonEntrance(it, 1000) }
        
        Log.d("HomeActivity", "Entrance animations started")
    }
    
    private fun animateButtonClick(view: View, action: () -> Unit) {
        ViewAnimationUtils.animateClick(view, action)
    }    private fun setupSidebar() {
        // Get navigation drawer views
        val navigationDrawer = findViewById<View>(R.id.navigation_drawer) ?: return
        
        // Setup profile section click listener
        navigationDrawer.findViewById<View>(R.id.profile_section)?.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            try {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("HomeActivity", "Failed to start ProfileActivity: ${e.message}", e)
                Toast.makeText(this, "Error opening profile: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
        
        // Setup menu item click listeners
        navigationDrawer.findViewById<View>(R.id.menu_home)?.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            // Already on home, do nothing
        }
        
        navigationDrawer.findViewById<View>(R.id.menu_profile)?.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            try {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("HomeActivity", "Failed to start ProfileActivity: ${e.message}", e)
                Toast.makeText(this, "Error opening profile: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
        
        navigationDrawer.findViewById<View>(R.id.menu_panduan)?.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            try {
                val intent = Intent(this, PanduanHijaiyahActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("HomeActivity", "Failed to start PanduanHijaiyahActivity: ${e.message}", e)
                Toast.makeText(this, "Error opening panduan: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
        
        navigationDrawer.findViewById<View>(R.id.menu_camera)?.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            try {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("HomeActivity", "Failed to start Camera: ${e.message}", e)
                Toast.makeText(this, "Error starting camera: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
        
        navigationDrawer.findViewById<View>(R.id.menu_learning)?.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            try {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("navigate_to", "latihan")
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("HomeActivity", "Failed to start Learning: ${e.message}", e)
                Toast.makeText(this, "Error starting learning: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
        
        navigationDrawer.findViewById<View>(R.id.menu_settings)?.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            try {
                val intent = Intent(this, com.google.mediapipe.examples.gesturerecognizer.features.settings.SettingsActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("HomeActivity", "Failed to start SettingsActivity: ${e.message}", e)
                Toast.makeText(this, "Error opening settings: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

            // Footer login button (no icon)
            navigationDrawer.findViewById<View>(R.id.menu_button_login)?.setOnClickListener {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                try {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("HomeActivity", "Failed to start LoginActivity: ${e.message}", e)
                    Toast.makeText(this, "Error opening login: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        
        navigationDrawer.findViewById<View>(R.id.menu_about)?.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            try {
                val intent = Intent(this, com.google.mediapipe.examples.gesturerecognizer.features.about.AboutActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("HomeActivity", "Failed to start AboutActivity: ${e.message}", e)
                Toast.makeText(this, "Error opening about: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
