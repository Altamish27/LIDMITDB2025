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

package com.google.mediapipe.examples.gesturerecognizer.features.hijaiyah

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahData
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahLetter
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahProgressManager
import com.google.mediapipe.examples.gesturerecognizer.data.api.SignQuranApiService
import com.google.mediapipe.examples.gesturerecognizer.data.manager.AuthManager
import com.google.mediapipe.examples.gesturerecognizer.data.manager.RoomPreferenceManager
import com.google.mediapipe.examples.gesturerecognizer.databinding.FragmentHijaiyahBinding


class HijaiyahFragment : Fragment() {

    companion object {
        /**
         * Create bundle for navigating to HijaiyahFragment with specific category
         */
        fun createBundle(category: String): Bundle {
            return Bundle().apply {
                putString("defaultCategory", category)
            }
        }
        
        /**
         * Create bundle for Fathah category
         */
        fun createFathahBundle(): Bundle = createBundle("fathah")
        
        /**
         * Create bundle for Kasrah category  
         */
        fun createKasrahBundle(): Bundle = createBundle("kasrah")
        
        /**
         * Create bundle for Dhammah category
         */
        fun createDhammahBundle(): Bundle = createBundle("dhammah")
    }

    private var _binding: FragmentHijaiyahBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var adapter: ArabicLetterAdapter
    private lateinit var progressManager: HijaiyahProgressManager
    private lateinit var authManager: AuthManager
    private lateinit var roomPreferenceManager: RoomPreferenceManager
    private val apiService = SignQuranApiService.getInstance()
    private var masterLetters: List<HijaiyahLetter> = emptyList()
    private var categoryLetters: List<HijaiyahLetter> = emptyList()
    private var displayedLetters: List<HijaiyahLetter> = emptyList()
    private var currentCategory = 0 // 0 = Hijaiyah, 1 = Fathah, 2 = Kasrah, 3 = Dhammah

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHijaiyahBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        progressManager = HijaiyahProgressManager(requireContext())
        authManager = AuthManager(requireContext())
        roomPreferenceManager = RoomPreferenceManager(requireContext())
        
        // Check if default category is specified in arguments
        val defaultCategory = arguments?.getString("defaultCategory")
        currentCategory = when(defaultCategory) {
            "fathah" -> 1
            "kasrah" -> 2
            "dhammah" -> 3
            else -> 0 // default to Hijaiyah
        }
        
        setupUI()
        setupRecyclerView()
        setupSearch()
        setupClickListeners()
        loadLetters()
    }

    private fun setupUI() {
        // Progress will be updated dynamically
        setupCategoryTabs()
    }
    
    private fun setupCategoryTabs() {
        // Set Hijaiyah as active by default
        setActiveTab(0)
        
        binding.btnCategoryHijaiyah.setOnClickListener {
            setActiveTab(0)
            currentCategory = 0
            applyCategoryFilter(resetSearch = true)
        }
        
        binding.btnCategoryFathah.setOnClickListener {
            setActiveTab(1)
            currentCategory = 1
            applyCategoryFilter(resetSearch = true)
        }
        
        binding.btnCategoryKasrah.setOnClickListener {
            setActiveTab(2)
            currentCategory = 2
            applyCategoryFilter(resetSearch = true)
        }
        
        binding.btnCategoryDhommah.setOnClickListener {
            setActiveTab(3)
            currentCategory = 3
            applyCategoryFilter(resetSearch = true)
        }
    }
    
    private fun setActiveTab(position: Int) {
        // Reset all tabs to inactive state
        binding.btnCategoryHijaiyah.apply {
            backgroundTintList = android.content.res.ColorStateList.valueOf(
                resources.getColor(R.color.white, null))
            setTextColor(resources.getColor(R.color.hijaiyah_navy, null))
            strokeWidth = 2
        }
        binding.btnCategoryFathah.apply {
            backgroundTintList = android.content.res.ColorStateList.valueOf(
                resources.getColor(R.color.white, null))
            setTextColor(resources.getColor(R.color.hijaiyah_navy, null))
            strokeWidth = 2
        }
        binding.btnCategoryKasrah.apply {
            backgroundTintList = android.content.res.ColorStateList.valueOf(
                resources.getColor(R.color.white, null))
            setTextColor(resources.getColor(R.color.hijaiyah_navy, null))
            strokeWidth = 2
        }
        binding.btnCategoryDhommah.apply {
            backgroundTintList = android.content.res.ColorStateList.valueOf(
                resources.getColor(R.color.white, null))
            setTextColor(resources.getColor(R.color.hijaiyah_navy, null))
            strokeWidth = 2
        }
        
        // Set active tab
        when (position) {
            0 -> binding.btnCategoryHijaiyah.apply {
                backgroundTintList = android.content.res.ColorStateList.valueOf(
                    resources.getColor(R.color.hijaiyah_green, null))
                setTextColor(resources.getColor(R.color.white, null))
                strokeWidth = 0
            }
            1 -> binding.btnCategoryFathah.apply {
                backgroundTintList = android.content.res.ColorStateList.valueOf(
                    resources.getColor(R.color.hijaiyah_green, null))
                setTextColor(resources.getColor(R.color.white, null))
                strokeWidth = 0
            }
            2 -> binding.btnCategoryKasrah.apply {
                backgroundTintList = android.content.res.ColorStateList.valueOf(
                    resources.getColor(R.color.hijaiyah_green, null))
                setTextColor(resources.getColor(R.color.white, null))
                strokeWidth = 0
            }
            3 -> binding.btnCategoryDhommah.apply {
                backgroundTintList = android.content.res.ColorStateList.valueOf(
                    resources.getColor(R.color.hijaiyah_green, null))
                setTextColor(resources.getColor(R.color.white, null))
                strokeWidth = 0
            }
        }
    }
    
    private fun setupRecyclerView() {
        adapter = ArabicLetterAdapter({ letter ->
            navigateToGestureRecognition(letter)
        })
        
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter
    }
    
    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                applySearchFilter(query)
            }
        })
    }
    
    private fun loadHijaiyahLetters() {
        lifecycleScope.launch {
            try {
                if (HijaiyahData.getAllLetters().isEmpty()) {
                    HijaiyahData.loadFromApi(requireContext())
                }
                
                masterLetters = progressManager.getLettersWithProgress()
                
                val synced = syncServerProgressIfNeeded()
                if (synced) {
                    masterLetters = progressManager.getLettersWithProgress()
                }
                
                applyCategoryFilter(resetSearch = true)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Gagal memuat huruf hijaiyah: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun applyCategoryFilter(resetSearch: Boolean = false) {
        categoryLetters = when (currentCategory) {
            1 -> masterLetters.filter { it.diacritic == "fathah" }
            2 -> masterLetters.filter { it.diacritic == "kasrah" }
            3 -> masterLetters.filter { it.diacritic == "dhammah" }
            else -> masterLetters.filter { it.diacritic.isNullOrEmpty() }
        }
        
        if (resetSearch) {
            binding.searchEditText.setText("")
        }
        
        applySearchFilter(binding.searchEditText.text?.toString() ?: "")
    }
    
    private fun applySearchFilter(query: String) {
        val baseList = categoryLetters
        val filtered = if (query.isBlank()) {
            baseList
        } else {
            baseList.filter { letter ->
                letter.arabic.contains(query, ignoreCase = true) ||
                letter.transliteration.contains(query, ignoreCase = true) ||
                (letter.gestureName?.contains(query, ignoreCase = true) == true)
            }
        }
        displayedLetters = filtered
        adapter.updateLetters(filtered)
        updateProgress()
    }
    
    private fun loadLetters() {
        loadHijaiyahLetters()
    }
    
    private suspend fun syncServerProgressIfNeeded(): Boolean {
        if (!this::authManager.isInitialized || !authManager.isLoggedIn) {
            return false
        }
        val token = authManager.authToken
        if (token.isEmpty()) {
            return false
        }
        val roomId = getActiveRoomId(token) ?: return false
        
        return try {
            val result = apiService.getLetterProgress(authToken = token, roomId = roomId.toString())
            result.onSuccess { response ->
                val completedPositions = response.progress
                    .filter { isCompletedStatus(it.status) }
                    .mapNotNull { it.hijaiyahId }
                    .toSet()
                
                if (completedPositions.isNotEmpty()) {
                    progressManager.replaceCompletedLetters(completedPositions)
                }
            }.onFailure {
                Log.e("HijaiyahFragment", "Failed syncing letter progress: ${it.message}", it)
            }
            result.isSuccess
        } catch (e: Exception) {
            Log.e("HijaiyahFragment", "Error syncing letter progress: ${e.message}", e)
            false
        }
    }
    
    private fun isCompletedStatus(status: String?): Boolean {
        if (status.isNullOrBlank()) return false
        val normalized = status.lowercase()
        return normalized == "completed" ||
            normalized == "selesai" ||
            normalized == "done" ||
            normalized == "true" ||
            normalized == "1"
    }

    private suspend fun getActiveRoomId(token: String): Int? {
        roomPreferenceManager.preferredRoomId?.let { return it }
        return try {
            val roomsResult = apiService.getMyRooms(token)
            val roomId = roomsResult.getOrNull()
                ?.rooms
                ?.firstOrNull()
                ?.roomId
            roomId?.let { roomPreferenceManager.preferredRoomId = it }
            roomId
        } catch (e: Exception) {
            Log.e("HijaiyahFragment", "Failed to fetch rooms: ${e.message}", e)
            null
        }
    }
    
    private fun updateProgress() {
        val completedCount = displayedLetters.count { it.isCompleted }
        val totalCount = when {
            displayedLetters.isNotEmpty() -> displayedLetters.size
            categoryLetters.isNotEmpty() -> categoryLetters.size
            else -> masterLetters.size.takeIf { it > 0 } ?: 28
        }
        val percentage = if (totalCount > 0) (completedCount * 100) / totalCount else 0
        
        binding.tvProgress.text = "$completedCount / $totalCount Huruf"
        binding.progressBar.progress = percentage
        binding.tvPercentage.text = "$percentage%"
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun navigateToGestureRecognition(letter: HijaiyahLetter) {
        val bundle = Bundle().apply {
            putString("selectedLetter", letter.arabic)
            putString("letterName", letter.transliteration)
            putInt("letterPosition", letter.position)
            val diacritic = letter.diacritic ?: "hijaiyah"
            putString("letterType", diacritic)
            putString("diacritic", letter.diacritic)
        }
        findNavController().navigate(R.id.action_hijaiyah_to_camera, bundle)
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh progress when returning from gesture recognition
        loadLetters()
        updateProgress()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
