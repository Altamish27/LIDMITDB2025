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

package com.google.mediapipe.examples.gesturerecognizer.features.latihan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.mediapipe.examples.gesturerecognizer.databinding.FragmentLatihanBinding

class LatihanFragment : Fragment() {

    private var _binding: FragmentLatihanBinding? = null
    private val binding get() = _binding!!
    
    private var jilidId: Int = 1
    private var jilidTitle: String = "Jilid 1"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLatihanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Get arguments
        jilidId = arguments?.getInt("jilidId", 1) ?: 1
        jilidTitle = arguments?.getString("jilidTitle", "Jilid 1") ?: "Jilid 1"
        
        // Update UI with jilid info
        binding.tvTitle.text = jilidTitle
        
        setupClickListeners()
        updateProgress()
    }

    private fun setupClickListeners() {
        // Halaman 1 - Unlocked
        binding.cardHalaman1.setOnClickListener {
            navigateToHalaman(1, "Halaman 1", "Pengenalan Huruf Hijaiyah Dasar")
        }
        
        // Halaman 2-5 - Locked
        binding.cardHalaman2.setOnClickListener {
            showLockedMessage()
        }
        
        binding.cardHalaman3.setOnClickListener {
            showLockedMessage()
        }
        
        binding.cardHalaman4.setOnClickListener {
            showLockedMessage()
        }
        
        binding.cardHalaman5.setOnClickListener {
            showLockedMessage()
        }
    }
    
    private fun navigateToHalaman(halamanId: Int, title: String, description: String) {
        val intent = Intent(requireContext(), LatihanPracticeActivity::class.java).apply {
            putExtra("exerciseId", jilidId)
            putExtra("exerciseTitle", jilidTitle)
            putExtra("jilidId", jilidId)
            putExtra("halamanId", halamanId)
        }
        startActivity(intent)
    }
    
    private fun showLockedMessage() {
        Toast.makeText(
            requireContext(),
            "Selesaikan halaman sebelumnya terlebih dahulu",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateProgress() {
        val totalPages = 5
        val completedCount = 0 // TODO: implement progress tracking
        val percentage = if (totalPages > 0) (completedCount * 100) / totalPages else 0
        
        binding.tvProgress.text = "$completedCount / $totalPages Halaman"
        binding.progressBar.progress = percentage
    }

    override fun onResume() {
        super.onResume()
        updateProgress()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
