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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.mediapipe.examples.gesturerecognizer.R
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
        binding.rvJilidList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@LatihanJilidFragment.adapter
            visibility = View.GONE
        }
    }
    
    private fun loadJilidData() {
        lifecycleScope.launch {
            showLoading(true)
            binding.tvJilidEmptyState.visibility = View.GONE
            
            val success = LatihanPageData.loadJilidFromApi(requireContext())
            
            if (success) {
                var jilidList = LatihanPageData.getAllJilid()
                jilidList = jilidList
                    .filter { it.id <= 2 }
                    .sortedBy { it.id }
                
                adapter.submitList(jilidList)
                
                if (jilidList.isEmpty()) {
                    showEmptyState("Jilid belum tersedia.")
                } else {
                    binding.rvJilidList.visibility = View.VISIBLE
                }
                
                android.util.Log.d("LatihanJilid", "Loaded ${jilidList.size} jilid from API")
            } else {
                showEmptyState("Gagal memuat data jilid.\nPastikan Anda sudah login dan koneksi stabil.")
                android.util.Log.e("LatihanJilid", "Failed to load jilid from API")
            }
            
            showLoading(false)
        }
    }
    
    private fun onJilidClicked(jilid: LatihanJilid) {
        navigateToJilidHalaman(jilid.id, jilid.title)
    }
    
    private fun navigateToJilidHalaman(jilidId: Int, jilidTitle: String) {
        val fragment = LatihanFragment().apply {
            arguments = Bundle().apply {
                putInt("jilidId", jilidId)
                putString("jilidTitle", jilidTitle)
            }
        }
        
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
    
    private fun showLoading(isLoading: Boolean) {
        binding.jilidLoadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        if (isLoading) {
            binding.rvJilidList.visibility = View.GONE
            binding.tvJilidEmptyState.visibility = View.GONE
        }
    }

    private fun showEmptyState(message: String) {
        binding.rvJilidList.visibility = View.GONE
        binding.tvJilidEmptyState.apply {
            text = message
            visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
