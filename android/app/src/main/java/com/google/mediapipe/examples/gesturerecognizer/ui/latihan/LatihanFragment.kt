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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.mediapipe.examples.gesturerecognizer.data.LatihanData
import com.google.mediapipe.examples.gesturerecognizer.data.LatihanProgressManager
import com.google.mediapipe.examples.gesturerecognizer.databinding.FragmentLatihanBinding

class LatihanFragment : Fragment() {

    private var _binding: FragmentLatihanBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var progressManager: LatihanProgressManager
    private lateinit var adapter: LatihanAdapter

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
        
        progressManager = LatihanProgressManager(requireContext())
        setupRecyclerView()
        loadExercises()
        updateProgress()
    }

    private fun setupRecyclerView() {
        adapter = LatihanAdapter { exercise ->
            // Handle exercise click
            // TODO: Navigate to exercise detail or start exercise
        }
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@LatihanFragment.adapter
        }
    }

    private fun loadExercises() {
        val exercisesWithProgress = progressManager.getExercisesWithProgress()
        adapter.updateExercises(exercisesWithProgress)
    }

    private fun updateProgress() {
        val completedExercises = progressManager.getCompletedExercises()
        val totalCount = LatihanData.getAllExercises().size
        val completedCount = completedExercises.size
        val percentage = if (totalCount > 0) (completedCount * 100) / totalCount else 0
        
        binding.tvProgress.text = "$completedCount / $totalCount Latihan"
        binding.progressBar.progress = percentage
        binding.tvPercentage.text = "$percentage%"
    }

    override fun onResume() {
        super.onResume()
        // Refresh progress when returning to fragment
        loadExercises()
        updateProgress()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
