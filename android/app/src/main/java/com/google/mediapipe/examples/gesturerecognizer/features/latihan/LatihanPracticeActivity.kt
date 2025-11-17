/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (th        loadCurrentBaris()
    }

    private fun loadPageData() {
        currentHalaman = LatihanPageData.getHalamanById(currentJilidId, currentHalamanId)
        currentBaris = currentHalaman?.barisList?.find { it.id == currentBarisId }
    }

    private fun setupUI() {
        binding.tvTitle.text = exerciseTitle
        updateUI()
    }

    private fun updateUI() {
        currentHalaman?.let { halaman ->
            binding.tvSubtitle.text = "Halaman ${halaman.id}"
            binding.tvRowTitle.text = "Baris $currentBarisId"
        }
    }se");
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

package com.google.mediapipe.examples.gesturerecognizer.features.latihan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import android.view.View
import android.view.animation.AnimationUtils
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityLatihanPracticeBinding
import com.google.mediapipe.examples.gesturerecognizer.features.camera.fragment.CameraFragment
import com.google.mediapipe.examples.gesturerecognizer.core.main.MainActivity
import com.google.mediapipe.examples.gesturerecognizer.data.LatihanPageData
import com.google.mediapipe.examples.gesturerecognizer.data.LatihanHalaman
import com.google.mediapipe.examples.gesturerecognizer.data.LatihanBaris
import com.google.mediapipe.examples.gesturerecognizer.data.LatihanHuruf
import com.google.mediapipe.examples.gesturerecognizer.data.api.SignQuranApiService
import com.google.mediapipe.examples.gesturerecognizer.data.manager.AuthManager
import kotlinx.coroutines.launch

// Keep old HurufItem for backward compatibility if needed
data class HurufItem(
    val arabic: String,
    val latin: String,
    val isCompleted: Boolean = false,
    val isActive: Boolean = false,
    val position: Int
)

class LatihanPracticeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLatihanPracticeBinding
    private lateinit var adapter: LatihanHurufGridAdapter
    private lateinit var authManager: AuthManager
    private lateinit var apiService: SignQuranApiService
    
    // New structure variables
    private var currentJilidId = 1
    private var currentHalamanId = 1
    private var realHalamanId = "1-1"  // Real halaman_id from database (e.g., "1-1")
    private var currentBarisId = 1
    
    private var exerciseId = 1
    private var exerciseTitle = "Latihan 1"
    
    // Track page completion status
    private var isPageAlreadyCompleted = false
    
    // Track whether we are running a sequential row test
    private var sequenceMode = false
    // Track completed letter positions in this activity session
    private val completedPositions = mutableSetOf<Int>()
    // Auto-start camera on first launch of this activity
    private var firstLaunch = true
    
    // Current page data
    private var currentHalaman: LatihanHalaman? = null
    private var currentBaris: LatihanBaris? = null
    
    // Data huruf untuk setiap baris (legacy - keep for backward compatibility)
    private val hurufData = listOf(
        // Baris 1
        listOf(
            HurufItem("ا", "ALIF", false, false, 1),
            HurufItem("ب", "BA", false, false, 2),
            HurufItem("ت", "TA", false, false, 3),
            HurufItem("ث", "TSA", false, false, 4),
            HurufItem("ج", "JIM", false, false, 5),
            HurufItem("ح", "HA", false, false, 6)
        ),
        // Baris 2 (kosong untuk sekarang)
        listOf(),
        // Baris 3 (kosong untuk sekarang)
        listOf(),
        // Baris 4 (kosong untuk sekarang)
        listOf()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            binding = ActivityLatihanPracticeBinding.inflate(layoutInflater)
            setContentView(binding.root)

            android.util.Log.d("LatihanPractice", "onCreate started")

            // Listen for results from embedded CameraFragment
            supportFragmentManager.setFragmentResultListener(
                "camera_result",
                this
            ) { _, bundle ->
                val success = bundle.getBoolean("success", false)
                val letterPos = bundle.getInt("letterPosition", -1)
                if (success && letterPos > 0) {
                    // mark as completed locally and refresh UI
                    completedPositions.add(letterPos)
                    loadCurrentBaris()

                    if (sequenceMode) {
                        // continue to next letter in the same row
                        advanceSequence(letterPos)
                    } else {
                        // hide camera container if not sequence
                        hideEmbeddedCamera()
                        
                        // Check if current baris is completed and auto advance if possible
                        if (isCurrentBarisCompleted() && canGoNextBaris()) {
                            Toast.makeText(this@LatihanPracticeActivity, "Baris selesai! Auto pindah ke baris selanjutnya...", Toast.LENGTH_SHORT).show()
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                currentBarisId++
                                loadCurrentBaris()
                            }, 1500) // 1.5 second delay
                        } else if (isCurrentHalamanCompleted()) {
                            // Check if entire halaman is completed
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                showPageCompletionDialog()
                            }, 1500) // 1.5 second delay
                        }
                    }
                } else {
                    // on failure just hide embedded camera for now
                    hideEmbeddedCamera()
                }
            }

            // Get data from intent
            exerciseId = intent.getIntExtra("exerciseId", 1)
            exerciseTitle = intent.getStringExtra("exerciseTitle") ?: "Latihan 1"
            currentJilidId = intent.getIntExtra("jilidId", 1)
            currentHalamanId = intent.getIntExtra("halamanId", 1)
            realHalamanId = intent.getStringExtra("realHalamanId") ?: "$currentJilidId-$currentHalamanId"
            currentBarisId = 1 // Always start from first baris

            android.util.Log.d("LatihanPractice", "Intent data: jilidId=$currentJilidId, halamanId=$currentHalamanId, realHalamanId=$realHalamanId, exerciseTitle=$exerciseTitle")

            // Initialize auth manager and API service
            authManager = AuthManager(this)
            apiService = SignQuranApiService.getInstance()

            // Setup UI first
            setupUI()
            setupRecyclerView()
            setupClickListeners()
            
            // Check page completion status first, then load data
            checkPageCompletionStatus()
            
            android.util.Log.d("LatihanPractice", "onCreate completed successfully")
            
        } catch (e: Exception) {
            android.util.Log.e("LatihanPractice", "Error in onCreate: ${e.message}", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun loadPageData() {
        lifecycleScope.launch {
            android.util.Log.d("LatihanPractice", "========================================")
            android.util.Log.d("LatihanPractice", "Loading page: jilid=$currentJilidId, halaman=$currentHalamanId")
            
            try {
                // Show loading indicator
                runOnUiThread {
                    binding.tvSubtitle.text = "Memuat data..."
                }
                
                android.util.Log.d("LatihanPractice", "About to call LatihanPageData.loadHalamanFromApi with:")
                android.util.Log.d("LatihanPractice", "  - jilidId: $currentJilidId")
                android.util.Log.d("LatihanPractice", "  - halamanId: $currentHalamanId")
                android.util.Log.d("LatihanPractice", "  - context: ${this@LatihanPracticeActivity}")
                
                // Load halaman dari API dengan context
                val halaman = LatihanPageData.loadHalamanFromApi(currentJilidId, currentHalamanId, this@LatihanPracticeActivity)
                
                android.util.Log.d("LatihanPractice", "LatihanPageData.loadHalamanFromApi returned: $halaman")
                
                if (halaman != null) {
                    android.util.Log.d("LatihanPractice", "✓ API Success!")
                    android.util.Log.d("LatihanPractice", "  - Title: ${halaman.title}")
                    android.util.Log.d("LatihanPractice", "  - Baris count: ${halaman.barisList.size}")
                    halaman.barisList.forEachIndexed { index, baris ->
                        android.util.Log.d("LatihanPractice", "  - Baris ${baris.id}: ${baris.hurufList.size} huruf")
                    }
                    
                    currentHalaman = halaman
                    currentBaris = halaman.barisList.find { it.id == currentBarisId }
                    
                    if (currentBaris != null) {
                        android.util.Log.d("LatihanPractice", "✓ Current baris loaded: ${currentBaris!!.hurufList.size} huruf")
                        
                        // Update UI on main thread
                        runOnUiThread {
                            updateUI()
                            loadCurrentBaris()
                        }
                    } else {
                        android.util.Log.e("LatihanPractice", "✗ Baris $currentBarisId not found!")
                        android.util.Log.w("LatihanPractice", "Available baris: ${halaman.barisList.map { it.id }}")
                        
                        // Try to use first available baris instead of finishing
                        val firstBaris = halaman.barisList.firstOrNull()
                        if (firstBaris != null) {
                            android.util.Log.w("LatihanPractice", "Using first available baris: ${firstBaris.id}")
                            currentBarisId = firstBaris.id
                            currentBaris = firstBaris
                            
                            runOnUiThread {
                                Toast.makeText(
                                    this@LatihanPracticeActivity,
                                    "Baris $currentBarisId tidak ditemukan. Menggunakan baris ${firstBaris.id}.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                updateUI()
                                loadCurrentBaris()
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(
                                    this@LatihanPracticeActivity, 
                                    "Tidak ada baris ditemukan di halaman ini", 
                                    Toast.LENGTH_LONG
                                ).show()
                                finish()
                            }
                        }
                    }
                } else {
                    android.util.Log.e("LatihanPractice", "✗ API Failed: halaman is null")
                    android.util.Log.e("LatihanPractice", "  Check network connection and API URL")
                    android.util.Log.e("LatihanPractice", "  jilidId=$currentJilidId, halamanId=$currentHalamanId")
                    
                    runOnUiThread {
                        Toast.makeText(
                            this@LatihanPracticeActivity, 
                            "Gagal memuat data halaman. Menggunakan data default.", 
                            Toast.LENGTH_LONG
                        ).show()
                        
                        // Try fallback with default data
                        setupDefaultData()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("LatihanPractice", "✗ Exception: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(
                        this@LatihanPracticeActivity, 
                        "Error loading data. Using default data.", 
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    // Try fallback with default data
                    setupDefaultData()
                }
            }
            
            android.util.Log.d("LatihanPractice", "========================================")
        }
    }
    
    private fun setupDefaultData() {
        android.util.Log.d("LatihanPractice", "Setting up default data for testing")
        
        try {
            // Create a simple default halaman for testing
            currentHalaman = com.google.mediapipe.examples.gesturerecognizer.data.LatihanHalaman(
                id = currentHalamanId,
                title = "Halaman $currentHalamanId - Default",
                description = "Default halaman untuk testing",
                barisList = listOf(
                    com.google.mediapipe.examples.gesturerecognizer.data.LatihanBaris(
                        id = 1,
                        hurufList = listOf(
                            com.google.mediapipe.examples.gesturerecognizer.data.LatihanHuruf(
                                arabic = "أ", 
                                latin = "Alif", 
                                gestureName = "alif", 
                                position = 1
                            ),
                            com.google.mediapipe.examples.gesturerecognizer.data.LatihanHuruf(
                                arabic = "ب", 
                                latin = "Ba", 
                                gestureName = "ba", 
                                position = 2
                            ),
                            com.google.mediapipe.examples.gesturerecognizer.data.LatihanHuruf(
                                arabic = "ت", 
                                latin = "Ta", 
                                gestureName = "ta", 
                                position = 3
                            )
                        )
                    )
                )
            )
            
            currentBaris = currentHalaman?.barisList?.firstOrNull()
            
            updateUI()
            loadCurrentBaris()
            
            android.util.Log.d("LatihanPractice", "Default data setup successful")
            
        } catch (e: Exception) {
            android.util.Log.e("LatihanPractice", "Failed to setup default data: ${e.message}", e)
            Toast.makeText(this, "Tidak dapat memuat data latihan", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupUI() {
        binding.tvTitle.text = exerciseTitle
        updateUI()
    }

    private fun updateUI() {
        currentHalaman?.let { halaman ->
            binding.tvSubtitle.text = "Halaman ${halaman.id}"
            binding.tvRowTitle.text = "Baris $currentBarisId"
        }
    }

    private fun setupRecyclerView() {
        adapter = LatihanHurufGridAdapter { huruf ->
            onHurufClick(huruf)
        }
        
        binding.recyclerViewGrid.apply {
            // Arrange grid from right-to-left so Arabic letters start at the right
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            layoutManager = GridLayoutManager(this@LatihanPracticeActivity, 6) // 6 columns
            adapter = this@LatihanPracticeActivity.adapter
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        // Camera button removed; camera auto-starts on first resume
        
        binding.btnNext.setOnClickListener {
            nextBaris()
        }
        
        binding.btnPrevious.setOnClickListener {
            previousBaris()
        }
    }

    private fun loadCurrentBaris() {
        android.util.Log.d("LatihanPractice", "loadCurrentBaris: jilidId=$currentJilidId, halamanId=$currentHalamanId, barisId=$currentBarisId")
        
        currentBaris = LatihanPageData.getBarisById(currentJilidId, currentHalamanId, currentBarisId)
        
        if (currentBaris == null) {
            android.util.Log.e("LatihanPractice", "Failed to get baris from LatihanPageData, trying fallback...")
            
            // Fallback: try to get from currentHalaman if available
            currentBaris = currentHalaman?.barisList?.find { it.id == currentBarisId }
            
            if (currentBaris == null) {
                android.util.Log.e("LatihanPractice", "Baris still null, using first available baris")
                currentBaris = currentHalaman?.barisList?.firstOrNull()
                if (currentBaris != null) {
                    currentBarisId = currentBaris!!.id
                }
            }
        }
        
        currentBaris?.let { baris ->
            android.util.Log.d("LatihanPractice", "✓ Loaded baris ${baris.id} with ${baris.hurufList.size} huruf")
            
            // Merge persisted completed letters from progress manager
            try {
                val persisted = com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahProgressManager(this).getCompletedLetters()
                completedPositions.addAll(persisted)
                android.util.Log.d("LatihanPractice", "Added ${persisted.size} persisted completed positions")
            } catch (e: Exception) {
                android.util.Log.w("LatihanPractice", "Could not load persisted progress: ${e.message}")
            }

            // Update adapter with current baris data only if adapter is initialized
            if (::adapter.isInitialized) {
                adapter.updateHuruf(baris.hurufList)
                adapter.updateCompletedPositions(completedPositions)
            } else {
                android.util.Log.w("LatihanPractice", "Adapter not initialized yet, skipping update")
            }
            
            // Update UI
            updateUI()
            
            // Apply fade in animation to RecyclerView
            val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
            binding.recyclerViewGrid.startAnimation(fadeIn)
            
            // Enable/disable navigation buttons
            binding.btnPrevious.isEnabled = canGoPreviousBaris()
            binding.btnNext.isEnabled = canGoNextBaris()
            
            // Debug info untuk memastikan completed positions ter-track
            android.util.Log.d("LatihanPractice", "Baris $currentBarisId loaded. Completed positions: $completedPositions")
        } ?: run {
            android.util.Log.w("LatihanPractice", "currentBaris is null in loadCurrentBaris()")
            Toast.makeText(this, "Error: Tidak dapat memuat baris", Toast.LENGTH_SHORT).show()
        }
    }

    private fun canGoPreviousBaris(): Boolean {
        return currentBarisId > 1
    }

    private fun canGoNextBaris(): Boolean {
        val totalBaris = currentHalaman?.barisList?.size ?: 0
        return currentBarisId < totalBaris
    }

    private fun onHurufClick(huruf: LatihanHuruf) {
        // Embed CameraFragment for this specific huruf (single mode)
        sequenceMode = false
        embedCameraForLetter(huruf, sequenceMode)
    }

    private fun openCamera() {
        // Start sequential practice for current baris
        sequenceMode = true
        startSequenceFromCurrentBaris()
    }

    private fun embedCameraForLetter(huruf: LatihanHuruf, sequence: Boolean) {
        // Create fragment and set arguments
        val frag = CameraFragment().apply {
            arguments = Bundle().apply {
                putString("selectedLetter", huruf.arabic)
                putString("letterName", huruf.latin)
                putInt("letterPosition", huruf.position)
                putBoolean("embedded", true)
                putBoolean("sequence_mode", sequence)
            }
        }

        // Show container and place fragment
        binding.cameraFragmentContainer.visibility = android.view.View.VISIBLE
        supportFragmentManager.commit {
            replace(binding.cameraFragmentContainer.id, frag)
        }
    }

    private fun hideEmbeddedCamera() {
        binding.cameraFragmentContainer.visibility = android.view.View.GONE
        // remove fragment if present
        val existing = supportFragmentManager.findFragmentById(binding.cameraFragmentContainer.id)
        existing?.let {
            supportFragmentManager.commit { remove(it) }
        }
    }

    private fun startSequenceFromCurrentBaris() {
        currentBaris?.let { baris ->
            // find first not-completed letter
            val next = baris.hurufList.firstOrNull { !completedPositions.contains(it.position) }
            if (next == null) {
                Toast.makeText(this, "Semua huruf di baris ini sudah selesai", Toast.LENGTH_SHORT).show()
                return
            }
            embedCameraForLetter(next, true)
        }
    }

    private fun advanceSequence(completedLetterPosition: Int) {
        // find current baris list and locate next not-completed after the completed position
        currentBaris?.let { baris ->
            val currentIndex = baris.hurufList.indexOfFirst { it.position == completedLetterPosition }
            var nextIndex = -1
            for (i in currentIndex + 1 until baris.hurufList.size) {
                if (!completedPositions.contains(baris.hurufList[i].position)) {
                    nextIndex = i
                    break
                }
            }

            if (nextIndex >= 0) {
                val nextHuruf = baris.hurufList[nextIndex]
                // replace fragment with next letter
                embedCameraForLetter(nextHuruf, true)
            } else {
                // finished baris, check if we can auto advance to next baris
                if (canGoNextBaris()) {
                    Toast.makeText(this, "Selesai baris ini. Pindah ke baris selanjutnya...", Toast.LENGTH_SHORT).show()
                    // Auto advance to next baris after a short delay
                    hideEmbeddedCamera()
                    currentBarisId++
                    loadCurrentBaris()
                    // Auto start camera for next baris
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        if (sequenceMode) {
                            startSequenceFromCurrentBaris()
                        }
                    }, 1000) // 1 second delay
                } else {
                    // No more baris available
                    Toast.makeText(this, "Selesai halaman ini!", Toast.LENGTH_SHORT).show()
                    sequenceMode = false
                    hideEmbeddedCamera()
                    
                    // Check if entire halaman is completed and show dialog
                    if (isCurrentHalamanCompleted()) {
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            showPageCompletionDialog()
                        }, 1500) // 1.5 second delay to show toast first
                    }
                }
            }
        }
    }

    private fun nextBaris() {
        if (canGoNextBaris()) {
            currentBarisId++
            loadCurrentBaris()
            // Auto start camera for new baris if sequence mode is active
            if (sequenceMode) {
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    startSequenceFromCurrentBaris()
                }, 500) // 0.5 second delay
            }
        }
    }

    private fun previousBaris() {
        if (canGoPreviousBaris()) {
            currentBarisId--
            loadCurrentBaris()
        }
    }

    override fun onResume() {
        super.onResume()
        android.util.Log.d("LatihanPractice", "onResume called")
        
        // REMOVED: loadCurrentBaris() - this was causing race condition
        // loadCurrentBaris() is now only called AFTER loadPageData() completes successfully
        
        // REMOVED: Automatically start camera on first resume 
        // This was causing the "balik-balik" issue where camera would auto-start
        // and interfere with normal navigation
        
        // Optional: Only auto-start camera if user explicitly wants it
        // You can uncomment this if you want auto-camera behavior:
        // if (firstLaunch) {
        //     firstLaunch = false
        //     openCamera()
        // }
    }

    // Helper function to check if current baris is completed
    private fun isCurrentBarisCompleted(): Boolean {
        return currentBaris?.hurufList?.all { completedPositions.contains(it.position) } ?: false
    }

    // Helper function to check if current halaman is completed
    private fun isCurrentHalamanCompleted(): Boolean {
        return currentHalaman?.let { halaman ->
            val allPositions = halaman.barisList.flatMap { it.hurufList.map { huruf -> huruf.position } }
            val completedCount = allPositions.count { completedPositions.contains(it) }
            val totalCount = allPositions.size
            
            android.util.Log.d("LatihanPractice", "Halaman $currentHalamanId progress: $completedCount/$totalCount")
            
            completedCount == totalCount
        } ?: false
    }

    // Show completion dialog when page is finished
    private fun showPageCompletionDialog() {
        // Save progress to API
        savePageCompletion()
        
        val dialog = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_page_completed, null)
        dialog.setView(dialogView)
        dialog.setCancelable(false)

        val alertDialog = dialog.create()
        
        // Set message
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvMessage)
        tvMessage.text = "Anda telah menyelesaikan Halaman $currentHalamanId!"
        
        // Stop button - back to page list
        dialogView.findViewById<Button>(R.id.btnStop).setOnClickListener {
            alertDialog.dismiss()
            finish() // Go back to previous activity (page list)
        }
        
        // Next page button
        dialogView.findViewById<Button>(R.id.btnNextPage).setOnClickListener {
            alertDialog.dismiss()
            navigateToNextPage()
        }
        
        alertDialog.show()
    }
    
    /**
     * Check if current page is already completed
     */
    private fun checkPageCompletionStatus() {
        lifecycleScope.launch {
            try {
                // Generate halaman_id in format "jilidId-halamanId"
                val halamanId = "$currentJilidId-$currentHalamanId"
                
                // Only check if user is logged in
                if (authManager.isLoggedIn && authManager.authToken.isNotEmpty()) {
                    val result = apiService.checkHalamanProgress(halamanId, authManager.authToken)
                    
                    result.onSuccess { response ->
                        isPageAlreadyCompleted = response.completed
                        android.util.Log.d("LatihanPractice", "Page completion status: $isPageAlreadyCompleted")
                        
                        runOnUiThread {
                            if (isPageAlreadyCompleted) {
                                // Show completion badge or indicator
                                Toast.makeText(
                                    this@LatihanPracticeActivity,
                                    "✓ Halaman ini sudah selesai",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    
                    result.onFailure { error ->
                        android.util.Log.e("LatihanPractice", "Failed to check progress: ${error.message}")
                    }
                } else {
                    android.util.Log.d("LatihanPractice", "User not logged in, skipping progress check")
                }
                
                // Continue loading page data
                loadPageData()
                
            } catch (e: Exception) {
                android.util.Log.e("LatihanPractice", "Error checking completion: ${e.message}", e)
                // Continue loading page data even if check fails
                loadPageData()
            }
        }
    }
    
    /**
     * Save page completion to API
     */
    private fun savePageCompletion() {
        lifecycleScope.launch {
            try {
                // Generate halaman_id in format "jilidId-halamanId"
                val halamanId = "$currentJilidId-$currentHalamanId"
                
                // Only save if user is logged in and page not already completed
                if (authManager.isLoggedIn && authManager.authToken.isNotEmpty() && !isPageAlreadyCompleted) {
                    val result = apiService.saveHalamanProgress(
                        halamanId = halamanId,
                        status = 1, // 1 = completed
                        authToken = authManager.authToken
                    )
                    
                    result.onSuccess { response ->
                        android.util.Log.d("LatihanPractice", "✓ Progress saved successfully")
                        isPageAlreadyCompleted = true
                        
                        runOnUiThread {
                            Toast.makeText(
                                this@LatihanPracticeActivity,
                                "Progress tersimpan!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    
                    result.onFailure { error ->
                        android.util.Log.e("LatihanPractice", "Failed to save progress: ${error.message}")
                        runOnUiThread {
                            Toast.makeText(
                                this@LatihanPracticeActivity,
                                "Gagal menyimpan progress",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    if (!authManager.isLoggedIn) {
                        android.util.Log.d("LatihanPractice", "User not logged in, progress not saved")
                    } else if (isPageAlreadyCompleted) {
                        android.util.Log.d("LatihanPractice", "Page already completed, skipping save")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("LatihanPractice", "Error saving completion: ${e.message}", e)
            }
        }
    }

    // Navigate to next page or show warning if not available
    private fun navigateToNextPage() {
        val nextHalamanId = currentHalamanId + 1
        val nextHalaman = LatihanPageData.getHalamanById(currentJilidId, nextHalamanId)
        
        if (nextHalaman != null) {
            // Next page available, navigate to it
            currentHalamanId = nextHalamanId
            currentBarisId = 1 // Reset to first baris of new page
            
            // Clear completed positions for new page to avoid conflicts
            // Keep only positions that might be relevant for the new page
            val newPagePositions = nextHalaman.barisList.flatMap { it.hurufList.map { huruf -> huruf.position } }
            completedPositions.retainAll(newPagePositions)
            
            loadPageData()
            loadCurrentBaris()
            
            Toast.makeText(this, "Pindah ke Halaman $currentHalamanId", Toast.LENGTH_SHORT).show()
            
            // Auto start sequence mode for new page
            sequenceMode = true
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                startSequenceFromCurrentBaris()
            }, 1000) // 1 second delay
        } else {
            // Next page not available
            AlertDialog.Builder(this)
                .setTitle("Halaman Belum Tersedia")
                .setMessage("Halaman $nextHalamanId belum tersedia. Silakan tunggu update selanjutnya.")
                .setPositiveButton("OK") { _, _ ->
                    finish() // Go back to page list
                }
                .show()
        }
    }
}
