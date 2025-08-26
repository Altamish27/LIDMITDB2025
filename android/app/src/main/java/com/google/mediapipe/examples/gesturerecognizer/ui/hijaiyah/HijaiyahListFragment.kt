package com.google.mediapipe.examples.gesturerecognizer.ui.hijaiyah

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.databinding.FragmentHijaiyahListBinding

class HijaiyahListFragment : Fragment() {

    private var _binding: FragmentHijaiyahListBinding? = null
    private val binding get() = _binding!!

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
        
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Setup click listeners for each letter container
        setupLetterClickListener(binding.letterAlif, "ا", "Alif", 1)
        setupLetterClickListener(binding.letterBa, "ب", "Ba", 2)
        setupLetterClickListener(binding.letterTa, "ت", "Ta", 3)
        setupLetterClickListener(binding.letterTsa, "ث", "Tsa", 4)
        setupLetterClickListener(binding.letterJim, "ج", "Jim", 5)
        setupLetterClickListener(binding.letterHa, "ح", "Ha", 6)
        setupLetterClickListener(binding.letterKho, "خ", "Kho", 7)
        setupLetterClickListener(binding.letterDal, "د", "Dal", 8)
        setupLetterClickListener(binding.letterDzal, "ذ", "Dzal", 9)
        setupLetterClickListener(binding.letterRa, "ر", "Ra", 10)
        setupLetterClickListener(binding.letterZai, "ز", "Zai", 11)
        setupLetterClickListener(binding.letterSin, "س", "Sin", 12)
        setupLetterClickListener(binding.letterSyin, "ش", "Syin", 13)
        setupLetterClickListener(binding.letterShad, "ص", "Shad", 14)
        setupLetterClickListener(binding.letterDhad, "ض", "Dhad", 15)
        setupLetterClickListener(binding.letterTha, "ط", "Tha", 16)
        setupLetterClickListener(binding.letterZha, "ظ", "Zha", 17)
        setupLetterClickListener(binding.letterAin, "ع", "Ain", 18)
        setupLetterClickListener(binding.letterGhain, "غ", "Ghain", 19)
        setupLetterClickListener(binding.letterFa, "ف", "Fa", 20)
        setupLetterClickListener(binding.letterQaf, "ق", "Qaf", 21)
        setupLetterClickListener(binding.letterKaf, "ك", "Kaf", 22)
        setupLetterClickListener(binding.letterLam, "ل", "Lam", 23)
        setupLetterClickListener(binding.letterMim, "م", "Mim", 24)
        setupLetterClickListener(binding.letterNun, "ن", "Nun", 25)
        setupLetterClickListener(binding.letterWaw, "و", "Waw", 26)
        setupLetterClickListener(binding.letterHaFinal, "ه", "Ha", 27)
        setupLetterClickListener(binding.letterYa, "ي", "Ya", 28)
    }

    private fun setupLetterClickListener(
        letterContainer: View,
        arabicLetter: String,
        transliteration: String,
        position: Int
    ) {
        letterContainer.setOnClickListener {
            navigateToGestureRecognition(arabicLetter, transliteration, position)
        }
    }

    private fun navigateToGestureRecognition(
        arabicLetter: String,
        transliteration: String,
        position: Int
    ) {
        val bundle = Bundle().apply {
            putString("selectedLetter", arabicLetter)
            putString("letterName", transliteration)
            putInt("letterPosition", position)
        }
        findNavController().navigate(R.id.action_hijaiyah_list_to_camera, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}