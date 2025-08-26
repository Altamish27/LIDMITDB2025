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
import androidx.recyclerview.widget.GridLayoutManager
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityLatihanPracticeBinding
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
    
    // Data huruf untuk setiap baris
    private val hurufData = listOf(
        // Baris 1
        listOf(
            HurufItem("ا", "ALIF", false, false, 1),
            HurufItem("ب", "BA", false, false, 2),
            HurufItem("ت", "TA", false, false, 3),
            HurufItem("ث", "TSA", false, false, 4),
            HurufItem("ج", "JA", true, false, 5), // Completed
            HurufItem("ح", "HA", true, false, 6)  // Completed
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
            val currentHurufList = hurufData[currentRow - 1]
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
        // Navigate to camera for this specific huruf
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("selectedLetter", huruf.arabic)
            putExtra("letterName", huruf.latin)
            putExtra("letterPosition", huruf.position)
            putExtra("exerciseTitle", exerciseTitle)
            putExtra("navigate_to", "camera_practice")
        }
        startActivity(intent)
    }

    private fun openCamera() {
        // Open camera for general practice
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("navigate_to", "camera_practice")
            putExtra("exerciseTitle", exerciseTitle)
        }
        startActivity(intent)
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
