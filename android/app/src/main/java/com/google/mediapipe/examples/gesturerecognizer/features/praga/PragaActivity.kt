package com.google.mediapipe.examples.gesturerecognizer.features.praga

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.core.main.MainActivity
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahData
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityTutorialHijaiyahBinding
import kotlinx.coroutines.launch

class PragaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTutorialHijaiyahBinding
    private var hurufArab: String = ""
    private var hurufLatin: String = ""
    private var gestureName: String = ""
    private var currentIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialHijaiyahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide action bar
        supportActionBar?.hide()

        // Get data from intent
        hurufArab = intent.getStringExtra("huruf_arab") ?: "ا"
        hurufLatin = intent.getStringExtra("huruf_latin") ?: "Alif"
        gestureName = intent.getStringExtra("gesture_name") ?: ""

        // Load data dan setup UI
        loadDataAndSetup()
    }
    
    private fun loadDataAndSetup() {
        lifecycleScope.launch {
            // Load data hijaiyah dari API
            HijaiyahData.loadFromApi()
            
            // Find current index
            currentIndex = HijaiyahData.letters.indexOfFirst { it.transliteration == hurufLatin }
            if (currentIndex == -1) currentIndex = 0

            setupViews()
            setupClickListeners()
            updateNavigationButtons()
        }
    }

    private fun setupViews() {
        // Set huruf latin di header
        binding.tvHurufLatin.text = hurufLatin
        
        // Set huruf center (card atas)
        binding.tvHurufArabCenter.text = hurufArab
        
        // Display Arabic letter with hijaiyah_font_family (same as home table's Isyarat column)
        binding.tvGestureName.text = hurufArab

        // Set description based on letter
        binding.tvDescription.text = getDescription(hurufLatin)

        // Set image based on letter
        val imageResId = getImageResource(hurufLatin)
        if (imageResId != 0) {
            binding.ivTutorialImage.setImageResource(imageResId)
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnOpenCamera.setOnClickListener {
            // Navigate to camera with the selected letter for detection
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("target_letter", gestureName)
                putExtra("huruf_latin", hurufLatin)
            }
            startActivity(intent)
        }

        binding.btnPrevious.setOnClickListener {
            navigateToPrevious()
        }

        binding.btnNext.setOnClickListener {
            navigateToNext()
        }
    }

    private fun navigateToPrevious() {
        if (currentIndex > 0) {
            currentIndex--
            loadLetterAtIndex(currentIndex)
        }
    }

    private fun navigateToNext() {
        if (currentIndex < HijaiyahData.letters.size - 1) {
            currentIndex++
            loadLetterAtIndex(currentIndex)
        }
    }

    private fun loadLetterAtIndex(index: Int) {
        val letter = HijaiyahData.letters[index]
        hurufArab = letter.arabic
        hurufLatin = letter.transliteration
        gestureName = letter.gestureName ?: ""
        
        setupViews()
        updateNavigationButtons()
    }

    private fun updateNavigationButtons() {
        // Disable previous button if at first letter
        binding.btnPrevious.isEnabled = currentIndex > 0
        binding.btnPrevious.alpha = if (currentIndex > 0) 1.0f else 0.5f

        // Disable next button if at last letter
        binding.btnNext.isEnabled = currentIndex < HijaiyahData.letters.size - 1
        binding.btnNext.alpha = if (currentIndex < HijaiyahData.letters.size - 1) 1.0f else 0.5f
    }

    private fun getDescription(latin: String): String {
        return when (latin) {
            "Alif" -> "Telapak tangan menghadap ke luar. Jari-jari rapat dan lurus menunjuk ke atas. Jari-jari menggambarkan gigi-gigi pada huruf sin."
            "Ba" -> "Tangan membentuk kepalan dengan ibu jari di dalam. Posisikan kepalan di depan dada."
            "Ta" -> "Tangan membentuk kepalan dengan ibu jari di luar. Posisikan kepalan di depan dada."
            "Tsa" -> "Tangan membentuk huruf 'O' dengan semua jari bertemu di satu titik."
            else -> "Pelajari dengan seksama bentuk gesture untuk huruf $latin ini."
        }
    }

    private fun getImageResource(latin: String): Int {
        return when (latin) {
            "Alif" -> R.drawable.praga_alif
            "Ba" -> R.drawable.praga_ba
            "Ta" -> R.drawable.praga_ta
            "Tsa" -> R.drawable.praga_tsa
            else -> R.drawable.praga_alif
        }
    }
}
