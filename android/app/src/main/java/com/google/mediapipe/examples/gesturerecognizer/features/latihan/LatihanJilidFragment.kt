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
import com.google.mediapipe.examples.gesturerecognizer.databinding.FragmentLatihanJilidBinding

class LatihanJilidFragment : Fragment() {

    private var _binding: FragmentLatihanJilidBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLatihanJilidBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Jilid 1 - Available
        binding.cardJilid1.setOnClickListener {
            navigateToJilidHalaman(1, "Jilid 1")
        }
        
        // Jilid 2-3 - Locked
        binding.cardJilid2.setOnClickListener {
            showLockedMessage()
        }
        
        binding.cardJilid3.setOnClickListener {
            showLockedMessage()
        }
    }
    
    private fun navigateToJilidHalaman(jilidId: Int, jilidTitle: String) {
        // Navigate to LatihanFragment dengan jilidId
        val fragment = LatihanFragment().apply {
            arguments = Bundle().apply {
                putInt("jilidId", jilidId)
                putString("jilidTitle", jilidTitle)
            }
        }
        
        parentFragmentManager.beginTransaction()
            .replace(android.R.id.content, fragment)
            .addToBackStack(null)
            .commit()
    }
    
    private fun showLockedMessage() {
        Toast.makeText(
            requireContext(),
            "Jilid ini belum tersedia. Selesaikan jilid sebelumnya terlebih dahulu.",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
