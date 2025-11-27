package com.google.mediapipe.examples.gesturerecognizer.features.guide

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahData
import com.google.mediapipe.examples.gesturerecognizer.data.FathahData
import com.google.mediapipe.examples.gesturerecognizer.data.KasrahData
import com.google.mediapipe.examples.gesturerecognizer.data.DhammahData
import com.google.mediapipe.examples.gesturerecognizer.core.main.MainActivity

class LetterGuideActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "LetterGuideActivity"
    }
    
    private lateinit var textTargetLetter: TextView
    private lateinit var textLetterName: TextView
    private lateinit var textDiacriticMode: TextView
    private lateinit var imageGestureGuide: ImageView
    private lateinit var textInstructions: TextView
    private lateinit var textDiacriticInstructions: TextView
    private lateinit var buttonStartPractice: Button
    private lateinit var buttonBack: ImageView
    
    private var targetLetter: String? = null
    private var targetLetterName: String? = null
    private var letterType: String? = null
    private var diacritic: String? = null
    private var letterPosition: Int = -1
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_letter_guide)
        
        // Get data from intent
        targetLetter = intent.getStringExtra("selectedLetter") ?: intent.getStringExtra("target_letter")
        targetLetterName = intent.getStringExtra("letterName") ?: intent.getStringExtra("target_letter_name")
        letterType = intent.getStringExtra("letterType")
        diacritic = intent.getStringExtra("diacritic")
        letterPosition = intent.getIntExtra("letterPosition", -1)
        
        Log.d(TAG, "Received data - Letter: $targetLetter, Name: $targetLetterName, Type: $letterType, Diacritic: $diacritic, Position: $letterPosition")
        
        initViews()
        setupUI()
        setupListeners()
    }
    
    private fun initViews() {
        textTargetLetter = findViewById(R.id.textTargetLetter)
        textLetterName = findViewById(R.id.textLetterName)
        textDiacriticMode = findViewById(R.id.textDiacriticMode)
        imageGestureGuide = findViewById(R.id.imageGestureGuide)
        textInstructions = findViewById(R.id.textInstructions)
        textDiacriticInstructions = findViewById(R.id.textDiacriticInstructions)
        buttonStartPractice = findViewById(R.id.buttonStartPractice)
        buttonBack = findViewById(R.id.buttonBack)
    }
    
    private fun setupUI() {
        // Set letter display
        textTargetLetter.text = targetLetter ?: "ا"
        textLetterName.text = targetLetterName ?: "Alif"
        
        // Load gesture image (praga_xxx.jpg)
        val gestureName = when {
            diacritic == "fathah" -> {
                val fathahLetter = if (targetLetter != null) {
                    FathahData.getLetterByArabic(targetLetter!!)
                } else {
                    FathahData.getAllLetters().find { it.transliteration.equals(targetLetterName, ignoreCase = true) }
                }
                val baseHijaiyah = fathahLetter?.let { HijaiyahData.getLetterById(it.position) }
                baseHijaiyah?.gestureName
            }
            diacritic == "kasrah" -> {
                val kasrahLetter = if (targetLetter != null) {
                    KasrahData.getLetterByArabic(targetLetter!!)
                } else {
                    KasrahData.getAllLetters().find { it.transliteration.equals(targetLetterName, ignoreCase = true) }
                }
                val baseHijaiyah = kasrahLetter?.let { HijaiyahData.getLetterByPosition(it.position) }
                baseHijaiyah?.gestureName
            }
            diacritic == "dhammah" -> {
                val dhammahLetter = if (targetLetter != null) {
                    DhammahData.getLetterByArabic(targetLetter!!)
                } else {
                    DhammahData.getAllLetters().find { it.transliteration.equals(targetLetterName, ignoreCase = true) }
                }
                val baseHijaiyah = dhammahLetter?.let { HijaiyahData.getLetterByPosition(it.position) }
                baseHijaiyah?.gestureName
            }
            else -> {
                val hijaiyahLetter = if (targetLetter != null) {
                    HijaiyahData.letters.find { it.arabic == targetLetter }
                } else {
                    HijaiyahData.letters.find { it.transliteration.equals(targetLetterName, ignoreCase = true) }
                }
                hijaiyahLetter?.gestureName
            }
        }
        
        // Set gesture image - convert "01_alif" to "praga_alif"
        if (gestureName != null) {
            val imageResName = "praga_" + gestureName.substring(3) // Remove "01_" prefix
            val imageResId = resources.getIdentifier(imageResName, "drawable", packageName)
            if (imageResId != 0) {
                imageGestureGuide.setImageResource(imageResId)
                Log.d(TAG, "Loaded gesture image: $imageResName")
            } else {
                // Try fallback to placeholder
                val placeholderResId = resources.getIdentifier("placeholder_gesture", "drawable", packageName)
                if (placeholderResId != 0) {
                    imageGestureGuide.setImageResource(placeholderResId)
                }
                Log.w(TAG, "Gesture image not found: $imageResName, using placeholder")
            }
        }
        
        // Set instructions based on mode
        when (diacritic) {
            "fathah" -> {
                textDiacriticMode.visibility = View.VISIBLE
                textDiacriticMode.text = "(Mode: Fathah)"
                textDiacriticInstructions.visibility = View.VISIBLE
                textDiacriticInstructions.text = "MODE FATHAH:\n• Tunjukkan gesture huruf $targetLetterName\n• Tahan tangan diam selama 1 detik\n• Setelah terdeteksi, gerakkan tangan ke KIRI\n• Pola: DIAM → KIRI → DIAM"
                textInstructions.text = "1. Posisikan tangan di depan kamera\n2. Bentuk gesture sesuai gambar di atas\n3. Tahan posisi tetap stabil\n4. Tunggu hingga huruf terdeteksi"
            }
            "kasrah" -> {
                textDiacriticMode.visibility = View.VISIBLE
                textDiacriticMode.text = "(Mode: Kasrah)"
                textDiacriticInstructions.visibility = View.VISIBLE
                textDiacriticInstructions.text = "MODE KASRAH:\n• Tunjukkan gesture huruf $targetLetterName\n• Tahan tangan diam selama 1 detik\n• Setelah terdeteksi, gerakkan tangan ke BAWAH\n• Pola: DIAM → BAWAH → DIAM"
                textInstructions.text = "1. Posisikan tangan di depan kamera\n2. Bentuk gesture sesuai gambar di atas\n3. Tahan posisi tetap stabil\n4. Tunggu hingga huruf terdeteksi"
            }
            "dhammah" -> {
                textDiacriticMode.visibility = View.VISIBLE
                textDiacriticMode.text = "(Mode: Dhammah)"
                textDiacriticInstructions.visibility = View.VISIBLE
                textDiacriticInstructions.text = "MODE DHAMMAH:\n• Tunjukkan gesture huruf $targetLetterName\n• Tahan tangan diam selama 1 detik\n• Lakukan gerakan melingkar: BAWAH → diagonal → KIRI → diagonal → ATAS\n• Pola: Gerakan melingkar ke atas"
                textInstructions.text = "1. Posisikan tangan di depan kamera\n2. Bentuk gesture sesuai gambar di atas\n3. Tahan posisi tetap stabil\n4. Tunggu hingga huruf terdeteksi"
            }
            else -> {
                textDiacriticMode.visibility = View.GONE
                textDiacriticInstructions.visibility = View.GONE
                textInstructions.text = "1. Tunjukkan gesture huruf $targetLetterName\n2. Tahan posisi tangan tetap diam selama 1 detik\n3. Tunggu hingga progress bar penuh\n4. Gesture berhasil terdeteksi!"
            }
        }
    }
    
    private fun setupListeners() {
        buttonBack.setOnClickListener {
            finish()
        }
        
        buttonStartPractice.setOnClickListener {
            // Open MainActivity with camera fragment and pass parameters via bundle
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("openCamera", true)
                putExtra("selectedLetter", targetLetter)
                putExtra("target_letter", targetLetter)
                putExtra("letterName", targetLetterName)
                putExtra("target_letter_name", targetLetterName)
                putExtra("letterType", letterType)
                putExtra("diacritic", diacritic)
                putExtra("letterPosition", letterPosition)
            }
            startActivity(intent)
            // Close guide activity so back button from camera goes to previous screen
            finish()
        }
    }
}
