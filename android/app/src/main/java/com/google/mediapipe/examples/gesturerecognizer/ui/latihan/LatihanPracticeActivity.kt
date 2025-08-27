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

package com.google.mediapipe.examples.gesturerecognizer.ui.latihan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.recyclerview.widget.GridLayoutManager
import android.view.View
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityLatihanPracticeBinding
import com.google.mediapipe.examples.gesturerecognizer.fragment.CameraFragment
import com.google.mediapipe.examples.gesturerecognizer.ui.main.MainActivity

data class HurufItem(
    val arabic: String,
    val latin: String,
    val isCompleted: Boolean = false,
    val isActive: Boolean = false,
    val position: Int
)

class LatihanPracticeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLatihanPracticeBinding
    private lateinit var adapter: HurufGridAdapter
    private var currentRow = 1
    private var exerciseId = 1
    private var exerciseTitle = "Latihan 1"
    // Track whether we are running a sequential row test
    private var sequenceMode = false
    // Track completed letter positions in this activity session
    private val completedPositions = mutableSetOf<Int>()
    
    // Data huruf untuk setiap baris
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
        binding = ActivityLatihanPracticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                loadCurrentRow()

                if (sequenceMode) {
                    // continue to next letter in the same row
                    advanceSequence(letterPos)
                } else {
                    // hide camera container if not sequence
                    hideEmbeddedCamera()
                }
            } else {
                // on failure just hide embedded camera for now
                hideEmbeddedCamera()
            }
        }

        // Get data from intent
        exerciseId = intent.getIntExtra("exerciseId", 1)
        exerciseTitle = intent.getStringExtra("exerciseTitle") ?: "Latihan 1"

        setupUI()
        setupRecyclerView()
        setupClickListeners()
        loadCurrentRow()
    }

    private fun setupUI() {
        binding.tvTitle.text = exerciseTitle
        binding.tvSubtitle.text = "Halaman $currentRow"
        binding.tvRowTitle.text = "Baris $currentRow"
    }

    private fun setupRecyclerView() {
        adapter = HurufGridAdapter { huruf ->
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
        
        binding.btnCamera.setOnClickListener {
            openCamera()
        }
        
        binding.btnNext.setOnClickListener {
            nextRow()
        }
        
        binding.btnPrevious.setOnClickListener {
            previousRow()
        }
    }

    private fun loadCurrentRow() {
        if (currentRow <= hurufData.size) {
            // Merge persisted completed letters from progress manager
            try {
                val persisted = com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahProgressManager(this).getCompletedLetters()
                completedPositions.addAll(persisted)
            } catch (_: Exception) {}

            // Map completion state from session `completedPositions` so default is not-tested (blue)
            val currentHurufList = hurufData[currentRow - 1].map { huruf ->
                if (completedPositions.contains(huruf.position)) {
                    huruf.copy(isCompleted = true)
                } else {
                    huruf.copy(isCompleted = false)
                }
            }

            adapter.updateHuruf(currentHurufList)
            
            // Update UI
            binding.tvSubtitle.text = "Halaman $currentRow"
            binding.tvRowTitle.text = "Baris $currentRow"
            
            // Enable/disable navigation buttons
            binding.btnPrevious.isEnabled = currentRow > 1
            binding.btnNext.isEnabled = currentRow < hurufData.size
        }
    }

    private fun onHurufClick(huruf: HurufItem) {
        // Embed CameraFragment for this specific huruf (single mode)
        sequenceMode = false
        embedCameraForLetter(huruf, sequenceMode)
    }

    private fun openCamera() {
        // Start sequential practice for current row
        sequenceMode = true
        startSequenceFromCurrentRow()
    }

    private fun embedCameraForLetter(huruf: HurufItem, sequence: Boolean) {
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

    private fun startSequenceFromCurrentRow() {
        val rowList = hurufData.getOrNull(currentRow - 1) ?: emptyList()
        // find first not-completed letter
        val next = rowList.firstOrNull { !completedPositions.contains(it.position) }
        if (next == null) {
            Toast.makeText(this, "Semua huruf di baris ini sudah selesai", Toast.LENGTH_SHORT).show()
            return
        }
        embedCameraForLetter(next, true)
    }

    private fun advanceSequence(completedLetterPosition: Int) {
        // find current row list and locate next not-completed after the completed position
        val rowList = hurufData.getOrNull(currentRow - 1) ?: return
        val currentIndex = rowList.indexOfFirst { it.position == completedLetterPosition }
        var nextIndex = -1
        for (i in currentIndex + 1 until rowList.size) {
            if (!completedPositions.contains(rowList[i].position)) {
                nextIndex = i
                break
            }
        }

        if (nextIndex >= 0) {
            val nextHuruf = rowList[nextIndex]
            // replace fragment with next letter
            embedCameraForLetter(nextHuruf, true)
        } else {
            // finished row
            Toast.makeText(this, "Selesai baris ini", Toast.LENGTH_SHORT).show()
            sequenceMode = false
            hideEmbeddedCamera()
        }
    }

    private fun nextRow() {
        if (currentRow < hurufData.size) {
            currentRow++
            loadCurrentRow()
        }
    }

    private fun previousRow() {
        if (currentRow > 1) {
            currentRow--
            loadCurrentRow()
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload data when returning from camera
        loadCurrentRow()
    }
}
