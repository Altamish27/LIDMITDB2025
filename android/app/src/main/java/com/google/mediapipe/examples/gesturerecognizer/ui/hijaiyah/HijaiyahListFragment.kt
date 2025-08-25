package com.google.mediapipe.examples.gesturerecognizer.ui.hijaiyah

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

class HijaiyahListFragment : Fragment() {

    private var _binding: FragmentHijaiyahListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: HijaiyahListAdapter
    private lateinit var progressManager: HijaiyahProgressManager
    private var allLetters = HijaiyahData.letters
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
    }

    private fun setupStaticLetters() {
        // Setup click listeners for all 28 Hijaiyah letters
        binding.letterAlif.setOnClickListener { navigateToCamera("ا", "ALIF", 1) }
        binding.letterBa.setOnClickListener { navigateToCamera("ب", "BA", 2) }
        binding.letterTa.setOnClickListener { navigateToCamera("ت", "TA", 3) }
        binding.letterTsa.setOnClickListener { navigateToCamera("ث", "TSA", 4) }
        binding.letterJim.setOnClickListener { navigateToCamera("ج", "JIM", 5) }
        binding.letterHa.setOnClickListener { navigateToCamera("ح", "HA", 6) }
        binding.letterKha.setOnClickListener { navigateToCamera("خ", "KHA", 7) }
        binding.letterDal.setOnClickListener { navigateToCamera("د", "DAL", 8) }
        binding.letterDzal.setOnClickListener { navigateToCamera("ذ", "DZAL", 9) }
        binding.letterRa.setOnClickListener { navigateToCamera("ر", "RA", 10) }
        binding.letterZa.setOnClickListener { navigateToCamera("ز", "ZA", 11) }
        binding.letterSin.setOnClickListener { navigateToCamera("س", "SIN", 12) }
        binding.letterSyin.setOnClickListener { navigateToCamera("ش", "SYIN", 13) }
        binding.letterShad.setOnClickListener { navigateToCamera("ص", "SHAD", 14) }
        binding.letterDhad.setOnClickListener { navigateToCamera("ض", "DHAD", 15) }
        binding.letterTha.setOnClickListener { navigateToCamera("ط", "THA", 16) }
        binding.letterZha.setOnClickListener { navigateToCamera("ظ", "ZHA", 17) }
        binding.letterAin.setOnClickListener { navigateToCamera("ع", "AIN", 18) }
        binding.letterGhain.setOnClickListener { navigateToCamera("غ", "GHAIN", 19) }
        binding.letterFa.setOnClickListener { navigateToCamera("ف", "FA", 20) }
        binding.letterQaf.setOnClickListener { navigateToCamera("ق", "QAF", 21) }
        binding.letterKaf.setOnClickListener { navigateToCamera("ك", "KAF", 22) }
        binding.letterLam.setOnClickListener { navigateToCamera("ل", "LAM", 23) }
        binding.letterMim.setOnClickListener { navigateToCamera("م", "MIM", 24) }
        binding.letterNun.setOnClickListener { navigateToCamera("ن", "NUN", 25) }
        binding.letterWaw.setOnClickListener { navigateToCamera("و", "WAW", 26) }
        binding.letterHaAkhir.setOnClickListener { navigateToCamera("ه", "HHA", 27) }
        binding.letterYa.setOnClickListener { navigateToCamera("ي", "YA", 28) }
        
        // Update backgrounds based on completion status
        updateLetterBackgrounds()
    }
    
    private fun updateLetterBackgrounds() {
        val completedLetters = progressManager.getCompletedLetters()
        val completedDrawable = ContextCompat.getDrawable(requireContext(), 
            com.google.mediapipe.examples.gesturerecognizer.R.drawable.cr16lr270f1c40ff39c12)
        val incompleteDrawable = ContextCompat.getDrawable(requireContext(), 
            com.google.mediapipe.examples.gesturerecognizer.R.drawable.cr16bc4c4c4)
        
        // Update all 28 letter backgrounds
        binding.letterAlif.background = if (completedLetters.contains(1)) completedDrawable else incompleteDrawable
        binding.letterBa.background = if (completedLetters.contains(2)) completedDrawable else incompleteDrawable
        binding.letterTa.background = if (completedLetters.contains(3)) completedDrawable else incompleteDrawable
        binding.letterTsa.background = if (completedLetters.contains(4)) completedDrawable else incompleteDrawable
        binding.letterJim.background = if (completedLetters.contains(5)) completedDrawable else incompleteDrawable
        binding.letterHa.background = if (completedLetters.contains(6)) completedDrawable else incompleteDrawable
        binding.letterKha.background = if (completedLetters.contains(7)) completedDrawable else incompleteDrawable
        binding.letterDal.background = if (completedLetters.contains(8)) completedDrawable else incompleteDrawable
        binding.letterDzal.background = if (completedLetters.contains(9)) completedDrawable else incompleteDrawable
        binding.letterRa.background = if (completedLetters.contains(10)) completedDrawable else incompleteDrawable
        binding.letterZa.background = if (completedLetters.contains(11)) completedDrawable else incompleteDrawable
        binding.letterSin.background = if (completedLetters.contains(12)) completedDrawable else incompleteDrawable
        binding.letterSyin.background = if (completedLetters.contains(13)) completedDrawable else incompleteDrawable
        binding.letterShad.background = if (completedLetters.contains(14)) completedDrawable else incompleteDrawable
        binding.letterDhad.background = if (completedLetters.contains(15)) completedDrawable else incompleteDrawable
        binding.letterTha.background = if (completedLetters.contains(16)) completedDrawable else incompleteDrawable
        binding.letterZha.background = if (completedLetters.contains(17)) completedDrawable else incompleteDrawable
        binding.letterAin.background = if (completedLetters.contains(18)) completedDrawable else incompleteDrawable
        binding.letterGhain.background = if (completedLetters.contains(19)) completedDrawable else incompleteDrawable
        binding.letterFa.background = if (completedLetters.contains(20)) completedDrawable else incompleteDrawable
        binding.letterQaf.background = if (completedLetters.contains(21)) completedDrawable else incompleteDrawable
        binding.letterKaf.background = if (completedLetters.contains(22)) completedDrawable else incompleteDrawable
        binding.letterLam.background = if (completedLetters.contains(23)) completedDrawable else incompleteDrawable
        binding.letterMim.background = if (completedLetters.contains(24)) completedDrawable else incompleteDrawable
        binding.letterNun.background = if (completedLetters.contains(25)) completedDrawable else incompleteDrawable
        binding.letterWaw.background = if (completedLetters.contains(26)) completedDrawable else incompleteDrawable
        binding.letterHaAkhir.background = if (completedLetters.contains(27)) completedDrawable else incompleteDrawable
        binding.letterYa.background = if (completedLetters.contains(28)) completedDrawable else incompleteDrawable
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
        filteredLetters = if (query.isEmpty()) {
            allLetters
        } else {
            allLetters.filter { letter ->
                letter.arabic.contains(query, ignoreCase = true) ||
                letter.transliteration.contains(query, ignoreCase = true)
            }
        }
        adapter.updateLetters(filteredLetters)
    }

    private fun updateProgressDisplay() {
        val completedCount = progressManager.getCompletedLetters().size
        val totalCount = 28 // All 28 Hijaiyah letters now available
        val percentage = if (totalCount > 0) (completedCount * 100) / totalCount else 0

        binding.tvProgressSummary.text = "$completedCount / $totalCount Huruf Hijaiyah Lengkap"
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
