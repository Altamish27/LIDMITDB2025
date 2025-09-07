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

package com.google.mediapipe.examples.gesturerecognizer.ui.hijaiyah

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.data.ArabicLetter
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahData
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahLetter
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahProgressManager
import com.google.mediapipe.examples.gesturerecognizer.data.FathahData
import com.google.mediapipe.examples.gesturerecognizer.data.FathahLetter
import com.google.mediapipe.examples.gesturerecognizer.data.KasrahData
import com.google.mediapipe.examples.gesturerecognizer.data.DhammahData
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
    private var allLetters: List<ArabicLetter> = emptyList()
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
        
        // Check if default category is specified in arguments
        val defaultCategory = arguments?.getString("defaultCategory")
        currentCategory = when(defaultCategory) {
            "fathah" -> 1
            "kasrah" -> 2
            "dhammah" -> 3
            else -> 0 // default to Hijaiyah
        }
        
        setupUI()
        setupSpinner()
        setupRecyclerView()
        setupSearch()
        setupClickListeners()
        loadLetters()
        updateProgress()
    }

    private fun setupUI() {
        // Progress will be updated dynamically
        binding.tvCurrentLetter.text = "Belajar Hijaiyah"
    }
    
    private fun setupSpinner() {
        try {
            val categories = arrayOf("Hijaiyah", "Fathah", "Kasrah", "Dhammah")
            val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = spinnerAdapter
            
            // Set dropdown to show below spinner with extra margin
            binding.spinnerCategory.dropDownVerticalOffset = binding.spinnerCategory.height + 100
            
            binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    currentCategory = position
                    when (position) {
                        0 -> { // Hijaiyah
                            loadHijaiyahLetters()
                        }
                        1 -> { // Fathah
                            loadFathahLetters()
                        }
                        2 -> { // Kasrah
                            loadKasrahLetters()
                        }
                        3 -> { // Dhammah
                            loadDhammahLetters()
                        }
                    }
                }
                
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Do nothing
                }
            }
            
            // Set spinner to default category
            binding.spinnerCategory.setSelection(currentCategory, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun setupRecyclerView() {
        adapter = ArabicLetterAdapter { letter ->
            navigateToGestureRecognition(letter)
        }
        
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter
    }
    
    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                val filteredLetters = if (query.isEmpty()) {
                    allLetters
                } else {
                    allLetters.filter { letter ->
                        letter.arabic.contains(query, ignoreCase = true) ||
                        letter.transliteration.contains(query, ignoreCase = true) ||
                        (letter.gestureName?.contains(query, ignoreCase = true) == true)
                    }
                }
                adapter.updateLetters(filteredLetters)
            }
        })
    }
    
    private fun loadHijaiyahLetters() {
        val hijaiyahLetters = progressManager.getLettersWithProgress()
        allLetters = hijaiyahLetters.map { hijaiyahLetter ->
            object : ArabicLetter {
                override val arabic = hijaiyahLetter.arabic
                override val transliteration = hijaiyahLetter.transliteration
                override val gestureName = hijaiyahLetter.gestureName
                override val position = hijaiyahLetter.position
                override var isCompleted = hijaiyahLetter.isCompleted
            }
        }
        adapter.updateLetters(allLetters)
        updateProgress()
    }
    
    private fun loadFathahLetters() {
        val fathahLetters = FathahData.getAllLetters()
        allLetters = fathahLetters.map { fathahLetter ->
            object : ArabicLetter {
                override val arabic = fathahLetter.arabic
                override val transliteration = fathahLetter.transliteration
                override val gestureName: String? = null // Fathah uses position-based matching
                override val position = fathahLetter.position
                override var isCompleted = false // TODO: Implement progress tracking for fathah
            }
        }
        adapter.updateLetters(allLetters)
        updateProgress()
    }
    
    private fun loadKasrahLetters() {
        val kasrahLetters = KasrahData.getAllLetters()
        allLetters = kasrahLetters.map { kasrahLetter ->
            object : ArabicLetter {
                override val arabic = kasrahLetter.arabic
                override val transliteration = kasrahLetter.transliteration
                override val gestureName = kasrahLetter.gestureName
                override val position = kasrahLetter.position
                override var isCompleted = false // TODO: Implement progress tracking for kasrah
            }
        }
        adapter.updateLetters(allLetters)
        updateProgress()
    }
    
    private fun loadDhammahLetters() {
        val dhammahLetters = DhammahData.getAllLetters()
        allLetters = dhammahLetters.map { dhammahLetter ->
            object : ArabicLetter {
                override val arabic = dhammahLetter.arabic
                override val transliteration = dhammahLetter.transliteration
                override val gestureName = dhammahLetter.gestureName
                override val position = dhammahLetter.position
                override var isCompleted = false // TODO: Implement progress tracking for dhammah
            }
        }
        adapter.updateLetters(allLetters)
        updateProgress()
    }
    
    private fun loadLetters() {
        // Keep original method for backward compatibility
        loadHijaiyahLetters()
    }
    
    private fun updateProgress() {
        val completedCount = allLetters.count { it.isCompleted }
        val totalCount = allLetters.size.takeIf { it > 0 } ?: 28
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
    
    private fun navigateToGestureRecognition(letter: ArabicLetter) {
        val bundle = Bundle().apply {
            putString("selectedLetter", letter.arabic)
            putString("letterName", letter.transliteration)
            putInt("letterPosition", letter.position)
            putString("letterType", when (currentCategory) {
                0 -> "hijaiyah"
                1 -> "fathah"
                2 -> "kasrah"
                3 -> "dhammah"
                else -> "hijaiyah"
            })
            // Add diacritic parameter - null for Hijaiyah, specific diacritic for others
            putString("diacritic", when (currentCategory) {
                0 -> null // Hijaiyah without diacritics
                1 -> "fathah"
                2 -> "kasrah"
                3 -> "dhammah"
                else -> null
            })
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
