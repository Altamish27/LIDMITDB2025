package com.google.mediapipe.examples.gesturerecognizer.ui.fathah

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.databinding.FragmentFathahListBinding

class FathahListFragment : Fragment() {

    private var _binding: FragmentFathahListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFathahListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Setup click listeners for each fathah letter container
        setupLetterClickListener(binding.letterAlifFathah, "أَ", "A", 1)
        setupLetterClickListener(binding.letterBaFathah, "بَ", "Ba", 2)
        setupLetterClickListener(binding.letterTaFathah, "تَ", "Ta", 3)
        setupLetterClickListener(binding.letterTsaFathah, "ثَ", "Tsa", 4)
        setupLetterClickListener(binding.letterJimFathah, "جَ", "Ja", 5)
        setupLetterClickListener(binding.letterHaFathah, "حَ", "Ha", 6)
        setupLetterClickListener(binding.letterKhaFathah, "خَ", "Kha", 7)
        setupLetterClickListener(binding.letterDalFathah, "دَ", "Da", 8)
        setupLetterClickListener(binding.letterDzalFathah, "ذَ", "Dza", 9)
        setupLetterClickListener(binding.letterRaFathah, "رَ", "Ra", 10)
        setupLetterClickListener(binding.letterZaiFathah, "زَ", "Za", 11)
        setupLetterClickListener(binding.letterSinFathah, "سَ", "Sa", 12)
        setupLetterClickListener(binding.letterSyinFathah, "شَ", "Sya", 13)
        setupLetterClickListener(binding.letterShodFathah, "صَ", "Sha", 14)
        setupLetterClickListener(binding.letterDhodFathah, "ضَ", "Dha", 15)
        setupLetterClickListener(binding.letterThoFathah, "طَ", "Tha", 16)
        setupLetterClickListener(binding.letterDzhoFathah, "ظَ", "Dzha", 17)
        setupLetterClickListener(binding.letterAinFathah, "عَ", "A", 18)
        setupLetterClickListener(binding.letterGhoinFathah, "غَ", "Gha", 19)
        setupLetterClickListener(binding.letterFaFathah, "فَ", "Fa", 20)
        setupLetterClickListener(binding.letterQofFathah, "قَ", "Qa", 21)
        setupLetterClickListener(binding.letterKafFathah, "كَ", "Ka", 22)
        setupLetterClickListener(binding.letterLamFathah, "لَ", "La", 23)
        setupLetterClickListener(binding.letterMimFathah, "مَ", "Ma", 24)
        setupLetterClickListener(binding.letterNunFathah, "نَ", "Na", 25)
        setupLetterClickListener(binding.letterWawFathah, "وَ", "Wa", 26)
        setupLetterClickListener(binding.letterHaEndFathah, "هَ", "Ha", 27)
        setupLetterClickListener(binding.letterYaFathah, "يَ", "Ya", 28)
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
            putString("letterType", "fathah") // Tambahan untuk membedakan jenis huruf
        }
        findNavController().navigate(R.id.action_fathah_list_to_camera, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
