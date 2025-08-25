package com.google.mediapipe.examples.gesturerecognizer.ui.hijaiyah

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahData
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahProgressManager
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahLetter
import com.google.mediapipe.examples.gesturerecognizer.databinding.FragmentHijaiyahListBinding
import com.google.mediapipe.examples.gesturerecognizer.ui.adapter.HijaiyahListAdapter
import com.google.mediapipe.examples.gesturerecognizer.ui.panduan.PanduanHijaiyahActivity

class HijaiyahListFragment : Fragment() {

    private var _binding: FragmentHijaiyahListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: HijaiyahListAdapter
    private lateinit var progressManager: HijaiyahProgressManager
    private var allLetters = HijaiyahData.getAllLetters()
    private var filteredLetters = allLetters

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHijaiyahListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressManager = HijaiyahProgressManager(requireContext())
        setupStaticLetters()
        setupSearch()
        updateProgressDisplay()
        setupPanduanButton()
    }

    private fun setupStaticLetters() {
        // Setup click listeners for static letters
        binding.letterAlif.setOnClickListener {
            navigateToCamera("ا", "ALIF", 1)
        }
        
        binding.letterBa.setOnClickListener {
            navigateToCamera("ب", "BA", 2)
        }
        
        binding.letterTa.setOnClickListener {
            navigateToCamera("ت", "TA", 3)
        }
        
        binding.letterTsa.setOnClickListener {
            navigateToCamera("ث", "TSA", 4)
        }
        
        binding.letterJim.setOnClickListener {
            navigateToCamera("ج", "JIM", 5)
        }
        
        binding.letterHa.setOnClickListener {
            navigateToCamera("ح", "HA", 6)
        }
        
        // Update backgrounds based on completion status
        updateLetterBackgrounds()
    }
    
    private fun updateLetterBackgrounds() {
        val completedLetters = progressManager.getCompletedLetters()
        
        // Update Alif background
        if (completedLetters.contains(1)) {
            binding.letterAlif.background = ContextCompat.getDrawable(requireContext(), 
                com.google.mediapipe.examples.gesturerecognizer.R.drawable.cr16lr270f1c40ff39c12)
        }
        
        // Update other letters backgrounds similarly
        // For now keeping them as grey (incomplete)
    }

    private fun setupRecyclerView() {
        // Keep this for compatibility but hide the RecyclerView
        binding.rvHijaiyahLetters.visibility = View.GONE
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterLetters(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterLetters(query: String) {
        filteredLetters = HijaiyahData.searchLetters(query, allLetters)
        if (::adapter.isInitialized) {
            adapter.updateLetters(filteredLetters)
        }
    }

    private fun updateProgressDisplay() {
        val completedCount = progressManager.getCompletedLetters().size
        val totalCount = allLetters.size
        val percentage = if (totalCount > 0) (completedCount * 100) / totalCount else 0

        binding.tvProgressSummary.text = "$completedCount / $totalCount Huruf"
        binding.tvOverallPercentage.text = "$percentage%"
        binding.overallProgress.progress = percentage
    }

    private fun navigateToCamera(arabic: String, transliteration: String, position: Int) {
        val bundle = Bundle()
        bundle.putString("selectedLetter", arabic)
        bundle.putString("letterName", transliteration)
        bundle.putInt("letterPosition", position)
        findNavController().navigate(
            com.google.mediapipe.examples.gesturerecognizer.R.id.camera_fragment,
            bundle
        )
    }

    private fun navigateToCamera(letter: HijaiyahLetter, position: Int) {
        navigateToCamera(letter.arabic, letter.transliteration, position)
    }
    
    private fun setupPanduanButton() {
        binding.btnLihatSemuaTabel.setOnClickListener {
            val intent = Intent(requireContext(), PanduanHijaiyahActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh progress when returning from camera
        updateProgressDisplay()
        updateLetterBackgrounds()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
