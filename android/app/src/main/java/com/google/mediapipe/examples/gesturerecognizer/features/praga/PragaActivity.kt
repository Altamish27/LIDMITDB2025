package com.google.mediapipe.examples.gesturerecognizer.features.praga

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.request.ImageRequest
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.core.main.MainActivity
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahData
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityTutorialHijaiyahBinding
import kotlinx.coroutines.launch

class PragaActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "PragaActivity"
    }

    private lateinit var binding: ActivityTutorialHijaiyahBinding
    private var hurufArab: String = ""
    private var hurufLatin: String = ""
    private var gestureName: String = ""
    private var currentIndex: Int = 0
    private var exoPlayer: ExoPlayer? = null

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
        val currentLetter = HijaiyahData.letters.getOrNull(currentIndex)
        
        // Set huruf latin di header
        binding.tvHurufLatin.text = hurufLatin
        
        // Set huruf center (card atas)
        binding.tvHurufArabCenter.text = hurufArab
        
        // Display Arabic letter with hijaiyah_font_family (same as home table's Isyarat column)
        binding.tvGestureName.text = hurufArab

        // Set description based on letter
        binding.tvDescription.text = getDescription(hurufLatin)

        // Process assets dinamis berdasarkan data dari API
        currentLetter?.let { letter ->
            processAssets(letter.assets)
        } ?: run {
            // Fallback ke gambar hardcoded jika data tidak tersedia
            showFallbackImage()
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

    /**
     * Process assets berdasarkan URL dari API
     * Menentukan apakah akan menampilkan gambar, video, atau fallback
     */
    private fun processAssets(assetsUrl: String?) {
        // Sembunyikan semua container asset terlebih dahulu
        hideAllAssetContainers()
        
        if (assetsUrl.isNullOrEmpty()) {
            // Jika assets null/kosong, gunakan fallback image
            Log.d(TAG, "No assets URL for letter $hurufLatin, using fallback")
            showFallbackImage()
            return
        }
        
        try {
            val fileExtension = getFileExtension(assetsUrl)
            
            when (fileExtension?.lowercase()) {
                "jpg", "jpeg", "png", "gif" -> {
                    // Tampilkan gambar dari network
                    Log.d(TAG, "Displaying image asset: $assetsUrl")
                    displayNetworkImage(assetsUrl)
                }
                "mp4", "m4v", "mov", "avi", "mkv" -> {
                    // Tampilkan video
                    Log.d(TAG, "Displaying video asset: $assetsUrl")
                    displayVideo(assetsUrl)
                }
                else -> {
                    // File format tidak didukung, gunakan fallback
                    Log.w(TAG, "Unsupported file type for: $assetsUrl")
                    showFallbackImage()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing assets URL: $assetsUrl", e)
            showFallbackImage()
        }
    }
    
    /**
     * Sembunyikan semua container asset
     */
    private fun hideAllAssetContainers() {
        binding.cardImageContainer.visibility = View.GONE
        binding.cardVideoContainer.visibility = View.GONE
        binding.loadingProgress.visibility = View.GONE
    }
    
    /**
     * Tampilkan gambar dari network menggunakan Coil
     */
    private fun displayNetworkImage(imageUrl: String) {
        binding.cardImageContainer.visibility = View.VISIBLE
        binding.loadingProgress.visibility = View.VISIBLE
        
        // Load image menggunakan Coil
        binding.ivTutorialImage.load(imageUrl) {
            listener(
                onSuccess = { _, _ ->
                    binding.loadingProgress.visibility = View.GONE
                    Log.d(TAG, "Successfully loaded image: $imageUrl")
                },
                onError = { _, error ->
                    Log.e(TAG, "Error loading image from: $imageUrl", error.throwable)
                    binding.loadingProgress.visibility = View.GONE
                    showFallbackImage()
                    Toast.makeText(this@PragaActivity, "Gagal memuat gambar", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
    
    /**
     * Tampilkan video menggunakan ExoPlayer
     */
    private fun displayVideo(videoUrl: String) {
        binding.cardVideoContainer.visibility = View.VISIBLE
        binding.loadingProgress.visibility = View.VISIBLE
        
        try {
            // Initialize ExoPlayer jika belum ada
            if (exoPlayer == null) {
                exoPlayer = ExoPlayer.Builder(this).build().apply {
                    binding.playerViewTutorial.player = this
                }
            }
            
            // Create media item dari video URL
            val mediaItem = MediaItem.fromUri(videoUrl)
            
            // Setup player listeners
            exoPlayer?.apply {
                setMediaItem(mediaItem)
                prepare()
                
                // Listener untuk handle loading dan error
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_READY) {
                            binding.loadingProgress.visibility = View.GONE
                            Log.d(TAG, "Video ready to play: $videoUrl")
                        }
                        super.onPlaybackStateChanged(playbackState)
                    }
                    
                    override fun onPlayerError(error: com.google.android.exoplayer2.PlaybackException) {
                        Log.e(TAG, "Error playing video: $videoUrl", error)
                        binding.loadingProgress.visibility = View.GONE
                        showFallbackImage()
                        Toast.makeText(this@PragaActivity, "Gagal memutar video", Toast.LENGTH_SHORT).show()
                        super.onPlayerError(error)
                    }
                })
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up video player for: $videoUrl", e)
            binding.loadingProgress.visibility = View.GONE
            showFallbackImage()
            Toast.makeText(this, "Gagal memutar video", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Tampilkan gambar fallback (hardcoded drawable) jika asset tidak tersedia
     */
    private fun showFallbackImage() {
        binding.cardImageContainer.visibility = View.VISIBLE
        binding.loadingProgress.visibility = View.GONE
        
        val imageResId = getImageResource(hurufLatin)
        if (imageResId != 0) {
            binding.ivTutorialImage.setImageResource(imageResId)
            Log.d(TAG, "Showing fallback image for: $hurufLatin")
        }
    }
    
    /**
     * Ekstrak ekstensi file dari URL
     */
    private fun getFileExtension(url: String): String? {
        return try {
            val lastDotIndex = url.lastIndexOf('.')
            if (lastDotIndex > 0 && lastDotIndex < url.length - 1) {
                url.substring(lastDotIndex + 1)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting file extension for: $url", e)
            null
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Release ExoPlayer resources
        exoPlayer?.let { player ->
            player.release()
            binding.playerViewTutorial.player = null
            exoPlayer = null
        }
    }
    
    override fun onPause() {
        super.onPause()
        exoPlayer?.pause()
    }
}
