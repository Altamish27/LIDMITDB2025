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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.mediapipe.examples.gesturerecognizer.data.LatihanJilid
import com.google.mediapipe.examples.gesturerecognizer.data.LatihanPageData
import com.google.mediapipe.examples.gesturerecognizer.databinding.FragmentLatihanJilidBinding
import kotlinx.coroutines.launch

class LatihanJilidFragment : Fragment() {

    private var _binding: FragmentLatihanJilidBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: JilidListAdapter

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
        
        setupRecyclerView()
        loadJilidData()
    }

    private fun setupRecyclerView() {
        adapter = JilidListAdapter { jilid ->
            onJilidClicked(jilid)
        }
        
        // Cek apakah ada RecyclerView di layout, jika tidak gunakan click listeners lama
        try {
            binding.rvJilidList?.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = this@LatihanJilidFragment.adapter
            }
        } catch (e: Exception) {
            // Fallback ke click listeners lama jika layout belum di-update
            setupClickListeners()
        }
    }
    
    private fun loadJilidData() {
        lifecycleScope.launch {
            // Load jilid dari API
            LatihanPageData.loadJilidFromApi()
            
            // Get data jilid
            val jilidList = LatihanPageData.getAllJilid()
            
            // Update adapter jika RecyclerView ada
            try {
                adapter.submitList(jilidList)
            } catch (e: Exception) {
                // Jika RecyclerView tidak ada, tidak perlu update adapter
            }
        }
    }
    
    private fun onJilidClicked(jilid: LatihanJilid) {
        // Cek apakah jilid memiliki halaman
        if (jilid.halamanList.isEmpty()) {
            showLockedMessage()
        } else {
            navigateToJilidHalaman(jilid.id, jilid.title)
        }
    }

    private fun setupClickListeners() {
        // Fallback untuk layout lama yang masih menggunakan CardView
        try {
            binding.cardJilid1?.setOnClickListener {
                navigateToJilidHalaman(1, "Jilid 1")
            }
            
            binding.cardJilid2?.setOnClickListener {
                showLockedMessage()
            }
            
            binding.cardJilid3?.setOnClickListener {
                showLockedMessage()
            }
        } catch (e: Exception) {
            // Ignore jika view tidak ada
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
