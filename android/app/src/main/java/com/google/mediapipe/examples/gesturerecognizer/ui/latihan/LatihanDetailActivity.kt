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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityLatihanDetailBinding
import com.google.mediapipe.examples.gesturerecognizer.ui.main.MainActivity

data class LatihanHuruf(
    val id: Int,
    val number: String,
    val title: String,
    val status: String, // "Selesai", "Aktif", "Terkunci"
    val arabic: String = ""
)

class LatihanDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLatihanDetailBinding
    private lateinit var adapter: LatihanHurufAdapter
    private var exerciseId: Int = 3
    private var exerciseTitle: String = "Latihan 3"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLatihanDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from intent
        exerciseId = intent.getIntExtra("exerciseId", 3)
        exerciseTitle = intent.getStringExtra("exerciseTitle") ?: "Latihan 3"

        setupUI()
        setupRecyclerView()
        setupClickListeners()
        loadHurufLatihan()
        updateStats()
    }

    private fun setupUI() {
        binding.tvTitle.text = exerciseTitle
        binding.tvSubtitle.text = "Sedang dipelajari - 30 Halaman"
        binding.tvProgress.text = "3/30 Halaman"
        binding.progressBar.progress = 10 // 3/30 * 100 = 10%
    }

    private fun setupRecyclerView() {
        adapter = LatihanHurufAdapter { huruf ->
            navigateToHurufDetail(huruf)
        }
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@LatihanDetailActivity)
            adapter = this@LatihanDetailActivity.adapter
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.btnLanjutkan.setOnClickListener {
            // Find next active huruf and navigate to it
            val activeHuruf = getCurrentActiveHuruf()
            activeHuruf?.let { navigateToHurufDetail(it) }
        }
        
        binding.btnReset.setOnClickListener {
            // Reset progress for this exercise
            resetProgress()
        }

        // Setup filter tabs
        binding.tabSemua.setOnClickListener { filterByStatus("Semua") }
        binding.tabSelesai.setOnClickListener { filterByStatus("Selesai") }
        binding.tabAktif.setOnClickListener { filterByStatus("Aktif") }
        binding.tabTerkunci.setOnClickListener { filterByStatus("Terkunci") }
    }

    private fun loadHurufLatihan() {
        // Create sample data based on the image
        val hurufList = listOf(
            LatihanHuruf(1, "1", "Huruf Alif & Ba", "Selesai", "ا"),
            LatihanHuruf(2, "2", "Huruf Ta & Tsa", "Selesai", "ت"),
            LatihanHuruf(3, "3", "Huruf Jim & Ha", "Aktif", "ج"),
            LatihanHuruf(4, "4", "Huruf Kho & Dal", "Terkunci", "خ"),
            LatihanHuruf(5, "5", "Huruf Dzal & Ra", "Terkunci", "ذ")
        )
        
        adapter.updateHuruf(hurufList)
    }

    private fun updateStats() {
        val completedCount = 2
        val activeCount = 1
        val lockedCount = 27
        
        binding.tvSelesaiCount.text = completedCount.toString()
        binding.tvAktifCount.text = activeCount.toString()
        binding.tvTerkunciCount.text = lockedCount.toString()
    }

    private fun getCurrentActiveHuruf(): LatihanHuruf? {
        return LatihanHuruf(3, "3", "Huruf Jim & Ha", "Aktif", "ج")
    }

    private fun navigateToHurufDetail(huruf: LatihanHuruf) {
        if (huruf.status == "Terkunci") {
            Toast.makeText(this, "Huruf ini masih terkunci", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Navigate to practice page with grid layout
        val intent = Intent(this, LatihanPracticeActivity::class.java).apply {
            putExtra("exerciseId", exerciseId)
            putExtra("exerciseTitle", exerciseTitle)
            putExtra("selectedHuruf", huruf.id)
        }
        startActivity(intent)
    }

    private fun resetProgress() {
        Toast.makeText(this, "Progress telah direset", Toast.LENGTH_SHORT).show()
        loadHurufLatihan()
        updateStats()
    }

    private fun filterByStatus(status: String) {
        // Update tab appearance
        resetTabColors()
        
        when (status) {
            "Semua" -> binding.tabSemua.setBackgroundResource(android.R.color.holo_blue_light)
            "Selesai" -> binding.tabSelesai.setBackgroundResource(android.R.color.holo_blue_light)
            "Aktif" -> binding.tabAktif.setBackgroundResource(android.R.color.holo_blue_light)
            "Terkunci" -> binding.tabTerkunci.setBackgroundResource(android.R.color.holo_blue_light)
        }
        
        // Filter adapter based on status
        adapter.filterByStatus(status)
    }

    private fun resetTabColors() {
        binding.tabSemua.setBackgroundResource(android.R.color.transparent)
        binding.tabSelesai.setBackgroundResource(android.R.color.transparent)
        binding.tabAktif.setBackgroundResource(android.R.color.transparent)
        binding.tabTerkunci.setBackgroundResource(android.R.color.transparent)
    }
}
