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

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.core.animation.ViewAnimationUtils
import com.google.mediapipe.examples.gesturerecognizer.core.main.MainActivity
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahData
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahProgressManager
import com.google.mediapipe.examples.gesturerecognizer.data.LatihanPageData
import com.google.mediapipe.examples.gesturerecognizer.data.api.PrayerTimeApiService
import com.google.mediapipe.examples.gesturerecognizer.data.api.SignQuranApiService
import com.google.mediapipe.examples.gesturerecognizer.data.manager.AuthManager
import com.google.mediapipe.examples.gesturerecognizer.data.models.AladhanTimingsData
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityHomeBinding
import com.google.mediapipe.examples.gesturerecognizer.features.auth.LoginActivity
import com.google.mediapipe.examples.gesturerecognizer.features.auth.ProfileActivity
import com.google.mediapipe.examples.gesturerecognizer.features.panduan.PanduanHijaiyahActivity
import com.google.mediapipe.examples.gesturerecognizer.features.surat.SuratListActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var authManager: AuthManager
    private lateinit var progressManager: HijaiyahProgressManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val prayerTimeApi = PrayerTimeApiService.getInstance()
    private var clockJob: Job? = null
    private var prayerJob: Job? = null

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.any { it.value }
        if (granted) {
            fetchPrayerScheduleWithLocation()
        } else {
            prayerJob?.cancel()
            prayerJob = lifecycleScope.launch {
                setPrayerLoadingState("Menggunakan lokasi default")
                binding.tvLocation.text = DEFAULT_LOCATION_LABEL
                requestPrayerTimings(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable hardware acceleration for smooth animations
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authManager = AuthManager(this)
        progressManager = HijaiyahProgressManager(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Load data dari API di background
        loadApiData()
        
        setupUI()
        setupClickListeners()
        setupCustomFonts()
        setupSidebar()
        startClockTicker()
        startPrayerScheduleFlow()
        
        // Delay animations to ensure views are laid out properly
        binding.root.post {
            startEntranceAnimations()
        }
    }
    
    /**
     * Load data dari API secara async saat app dimulai
     */
    private fun loadApiData() {
        lifecycleScope.launch {
            try {
                // Load hijaiyah dan jilid data dari API with context for auth
                HijaiyahData.loadFromApi(this@HomeActivity)
                LatihanPageData.loadJilidFromApi(this@HomeActivity)
                
                // Sync progress from server to local cache
                progressManager.syncProgressFromServer()
                
                // Update UI with synced progress
                updateHijaiyahProgress()
                updateLatihanProgress()
            } catch (e: Exception) {
                Log.e("HomeActivity", "Failed to load API data: ${e.message}", e)
                // Data fallback akan digunakan otomatis
                // Still update progress from local cache
                updateHijaiyahProgress()
                updateLatihanProgress()
            }
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
    }
    
    private fun setupSidebar() {
        // Get navigation drawer views
        val navigationDrawer = findViewById<View>(R.id.navigation_drawer) ?: return
        
        // Update profile section with user data if logged in
        updateProfileSection(navigationDrawer)
        
        // Setup profile section click listener
        navigationDrawer.findViewById<View>(R.id.profile_section)?.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            if (authManager.isLoggedIn) {
                try {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("HomeActivity", "Failed to start ProfileActivity: ${e.message}", e)
                    Toast.makeText(this, "Error opening profile: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } else {
                try {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("HomeActivity", "Failed to start LoginActivity: ${e.message}", e)
                    Toast.makeText(this, "Error opening login: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
        
        // Setup menu item click listeners
        navigationDrawer.findViewById<View>(R.id.menu_home)?.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            // Already on home, do nothing
        }
        
        navigationDrawer.findViewById<View>(R.id.menu_profile)?.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            if (authManager.isLoggedIn) {
                try {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("HomeActivity", "Failed to start ProfileActivity: ${e.message}", e)
                    Toast.makeText(this, "Error opening profile: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } else {
                try {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("HomeActivity", "Failed to start LoginActivity: ${e.message}", e)
                    Toast.makeText(this, "Error opening login: ${e.message}", Toast.LENGTH_LONG).show()
                }
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

        // Update login button based on auth status
        val loginButton = navigationDrawer.findViewById<View>(R.id.menu_button_login)
        if (authManager.isLoggedIn) {
            // If user is logged in, hide login button and show logout button
            loginButton?.visibility = View.GONE
            // Show logout button if it exists
            val logoutButton = navigationDrawer.findViewById<View?>(R.id.menu_button_logout)
            logoutButton?.visibility = View.VISIBLE
            logoutButton?.setOnClickListener {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                // Perform logout
                authManager.clearAuthData()
                Toast.makeText(this, "Berhasil logout", Toast.LENGTH_SHORT).show()
                
                // Update UI after logout
                updateProfileSection(navigationDrawer)
                // Reconfigure menu items to reflect logged-out state
                updateMenuItemsForLoggedOut(navigationDrawer)
            }
        } else {
            // If user is not logged in, show login button
            loginButton?.visibility = View.VISIBLE
            loginButton?.setOnClickListener {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                try {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("HomeActivity", "Failed to start LoginActivity: ${e.message}", e)
                    Toast.makeText(this, "Error opening login: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
            
            // Hide logout button if exists
            val logoutButton = navigationDrawer.findViewById<View?>(R.id.menu_button_logout)
            logoutButton?.visibility = View.GONE
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

    private fun startClockTicker() {
        clockJob?.cancel()
        val formatter = DateTimeFormatter.ofPattern("HH.mm")
        clockJob = lifecycleScope.launch {
            while (isActive) {
                val now = LocalTime.now(ZoneId.systemDefault())
                binding.tvCurrentTime.text = now.format(formatter)
                delay(30_000)
            }
        }
    }

    private fun startPrayerScheduleFlow() {
        if (hasLocationPermission()) {
            fetchPrayerScheduleWithLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    private fun fetchPrayerScheduleWithLocation() {
        prayerJob?.cancel()
        prayerJob = lifecycleScope.launch {
            setPrayerLoadingState("Mencari lokasi...")
            val location = getAccurateLocation()
            if (location != null) {
                val label = reverseGeocodeLabel(location) ?: DEFAULT_LOCATION_LABEL
                binding.tvLocation.text = label
                val success = requestPrayerTimings(location.latitude, location.longitude)
                if (!success) {
                    binding.tvLocation.text = DEFAULT_LOCATION_LABEL
                    requestPrayerTimings(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
                }
            } else {
                setPrayerLoadingState("Lokasi tidak tersedia")
                binding.tvLocation.text = DEFAULT_LOCATION_LABEL
                requestPrayerTimings(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
            }
        }
    }

    private suspend fun requestPrayerTimings(lat: Double, lon: Double): Boolean {
        val timingsData = prayerTimeApi.getTimings(lat, lon).getOrElse {
            Log.e("HomeActivity", "Prayer API error: ${it.message}", it)
            return false
        }
        updatePrayerTimesUI(timingsData)
        return true
    }

    private suspend fun getAccurateLocation(): Location? {
        val lastKnown = try {
            fusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            Log.w("HomeActivity", "Last location unavailable: ${e.message}")
            null
        }
        if (lastKnown != null) return lastKnown
        return fetchCurrentLocation()
    }

    private suspend fun fetchCurrentLocation(): Location? {
        val tokenSource = CancellationTokenSource()
        return try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                tokenSource.token
            ).await()
        } catch (e: Exception) {
            Log.e("HomeActivity", "Current location request failed: ${e.message}", e)
            null
        } finally {
            tokenSource.cancel()
        }
    }

    private suspend fun reverseGeocodeLabel(location: Location): String? = withContext(Dispatchers.IO) {
        val geocoder = Geocoder(this@HomeActivity, Locale("id", "ID"))
        val address = try {
            @Suppress("DEPRECATION")
            geocoder.getFromLocation(location.latitude, location.longitude, 1)?.firstOrNull()
        } catch (e: Exception) {
            Log.e("HomeActivity", "Reverse geocode failed: ${e.message}", e)
            null
        }
        address?.let {
            listOfNotNull(
                it.subLocality,
                it.locality,
                it.subAdminArea,
                it.adminArea
            ).distinct().joinToString(", ").ifBlank { null }
        }
    }

    private fun updatePrayerTimesUI(data: AladhanTimingsData) {
        val times = data.timings
        updatePrayerTimeText(
            formatPrayerTime(times.fajr),
            formatPrayerTime(times.dhuhr),
            formatPrayerTime(times.asr),
            formatPrayerTime(times.maghrib),
            formatPrayerTime(times.isha)
        )
    }

    private fun updatePrayerTimeText(
        subuh: String,
        dzuhur: String,
        ashar: String,
        maghrib: String,
        isya: String
    ) {
        binding.tvSubuhTime.text = subuh
        binding.tvDzuhurTime.text = dzuhur
        binding.tvAsharTime.text = ashar
        binding.tvMaghribTime.text = maghrib
        binding.tvIsyaTime.text = isya
    }

    private fun formatPrayerTime(raw: String?): String {
        if (raw.isNullOrBlank()) return PRAYER_PLACEHOLDER
        return raw.replace(":", ".")
    }

    private fun setPrayerLoadingState(status: String) {
        binding.tvLocation.text = status
        updatePrayerTimeText(
            PRAYER_PLACEHOLDER,
            PRAYER_PLACEHOLDER,
            PRAYER_PLACEHOLDER,
            PRAYER_PLACEHOLDER,
            PRAYER_PLACEHOLDER
        )
    }

    private fun updateProfileSection(navigationDrawer: View) {
        val usernameView = navigationDrawer.findViewById<TextView>(R.id.tv_username)
        val userEmailView = navigationDrawer.findViewById<TextView>(R.id.tv_user_email)
        
        if (authManager.isLoggedIn) {
            // Show user info
            usernameView?.text = authManager.userName
            userEmailView?.text = authManager.userEmail
            
            // Update login button text if needed
            val loginButton = navigationDrawer.findViewById<View>(R.id.menu_button_login)
            loginButton?.findViewById<TextView>(android.R.id.text1)?.text = "Login"
        } else {
            // Show placeholder text
            usernameView?.text = "Guest User"
            userEmailView?.text = "Silakan login"
        }
    }
    
    private fun updateMenuItemsForLoggedOut(navigationDrawer: View) {
        // Update profile menu item text
        val profileMenuItem = navigationDrawer.findViewById<TextView>(R.id.menu_profile)
        profileMenuItem?.text = "Login / Register"
    }

    override fun onResume() {
        super.onResume()
        // Update UI when returning to home activity (e.g. after logging out from profile)
        val navigationDrawer = findViewById<View>(R.id.navigation_drawer)
        if (navigationDrawer != null) {
            updateProfileSection(navigationDrawer)
            setupSidebar() // Refresh sidebar setup
        }
        startClockTicker()
        if (hasLocationPermission()) {
            fetchPrayerScheduleWithLocation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clockJob?.cancel()
        prayerJob?.cancel()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    
    /**
     * Update hijaiyah progress display from database
     */
    private fun updateHijaiyahProgress() {
        val (completed, total) = progressManager.getTotalProgress()
        val progressPercentage = if (total > 0) (completed * 100) / total else 0
        
        binding.tvHijaiyahProgress?.text = "$completed/$total"
        binding.progressBarHijaiyah?.progress = progressPercentage
        
        Log.d("HomeActivity", "Updated hijaiyah progress: $completed/$total ($progressPercentage%)")
    }
    
    /**
     * Update latihan progress display from database
     */
    private fun updateLatihanProgress() {
        lifecycleScope.launch {
            try {
                val apiService = SignQuranApiService.getInstance()
                val token = if (authManager.isLoggedIn) authManager.authToken else null
                
                if (token == null) {
                    Log.w("HomeActivity", "No auth token, skipping latihan progress update")
                    return@launch
                }
                
                // Get all jilid to count total pages
                var totalPages = 0
                var completedPages = 0
                
                // Count pages from all 6 jilid
                for (jilidId in 1..6) {
                    val pagesResult = apiService.getJilidPages(jilidId, token)
                    pagesResult.onSuccess { response ->
                        totalPages += response.pages.size
                    }
                    
                    val progressResult = apiService.getJilidProgress(jilidId, token)
                    progressResult.onSuccess { progressResponse ->
                        completedPages += progressResponse.progress.count { it.status == 1 }
                    }
                }
                
                // Update UI
                val progressPercentage = if (totalPages > 0) (completedPages * 100) / totalPages else 0
                binding.tvLatihanProgress?.text = "$completedPages/$totalPages"
                binding.progressBarLatihan?.progress = progressPercentage
                
                Log.d("HomeActivity", "Updated latihan progress: $completedPages/$totalPages ($progressPercentage%)")
            } catch (e: Exception) {
                Log.e("HomeActivity", "Failed to update latihan progress: ${e.message}", e)
                binding.tvLatihanProgress?.text = "0/0"
                binding.progressBarLatihan?.progress = 0
            }
        }
    }

    companion object {
        private const val DEFAULT_LATITUDE = -6.200000
        private const val DEFAULT_LONGITUDE = 106.816666
        private const val DEFAULT_LOCATION_LABEL = "Jakarta (default)"
        private const val PRAYER_PLACEHOLDER = "--:--"
    }
}
