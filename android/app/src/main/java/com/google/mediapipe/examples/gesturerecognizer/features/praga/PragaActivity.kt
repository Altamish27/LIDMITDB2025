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
import android.os.Handler
import android.os.Looper
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.LoadControl
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.video.VideoSize
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
    private var videoLoadStartTime: Long = 0
    private val progressUpdateHandler = Handler(Looper.getMainLooper())
    private var progressUpdateRunnable: Runnable? = null

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


    /**
     * Process assets berdasarkan URL dari API
     * Menentukan apakah akan menampilkan gambar, video, atau fallback
     */
    private fun processAssets(assetsUrl: String?) {
        // Sembunyikan semua container asset terlebih dahulu
        hideAllAssetContainers()
        
        if (assetsUrl.isNullOrEmpty()) {
            // Jika assets null/kosong, sembunyikan semua container
            Log.d(TAG, "No assets URL for letter $hurufLatin")
            hideAllAssetContainers()
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
                    // File format tidak didukung, sembunyikan container
                    Log.w(TAG, "Unsupported file type for: $assetsUrl")
                    hideAllAssetContainers()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing assets URL: $assetsUrl", e)
            hideAllAssetContainers()
        }
    }
    
    /**
     * Sembunyikan semua container asset
     */
    private fun hideAllAssetContainers() {
        binding.cardImageContainer.visibility = View.GONE
        binding.cardVideoContainer.visibility = View.GONE
        binding.loadingProgressContainer.visibility = View.GONE
    }
    
    /**
     * Tampilkan gambar dari network menggunakan Coil
     */
    private fun displayNetworkImage(imageUrl: String) {
        binding.cardImageContainer.visibility = View.VISIBLE
        binding.loadingProgressContainer.visibility = View.GONE // Hide video loading, image doesn't need it
        
        // Load image menggunakan Coil
        binding.ivTutorialImage.load(imageUrl) {
            listener(
                onSuccess = { _, _ ->
                    Log.d(TAG, "Successfully loaded image: $imageUrl")
                },
                onError = { _, error ->
                    Log.e(TAG, "Error loading image from: $imageUrl", error.throwable)
                    binding.cardImageContainer.visibility = View.GONE
                }
            )
        }
    }
    
    /**
     * Tampilkan video menggunakan ExoPlayer
     */
    private fun displayVideo(videoUrl: String) {
        binding.cardVideoContainer.visibility = View.VISIBLE
        binding.loadingProgressContainer.visibility = View.VISIBLE
        binding.loadingProgress.progress = 0
        binding.tvLoadingPercentage.text = "0%"
        
        // Ensure PlayerView is visible and properly configured
        binding.playerViewTutorial.visibility = View.VISIBLE
        binding.playerViewTutorial.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        binding.playerViewTutorial.useController = false // Hide default controller
        
        // Track loading time
        videoLoadStartTime = System.currentTimeMillis()
        Log.d(TAG, "Displaying video: $videoUrl")
        
        try {
            // Release previous player if exists
            exoPlayer?.release()
            exoPlayer = null
            
            // Configure LoadControl untuk buffering yang lebih cepat dan efektif
            // Mengurangi minimum buffer agar video bisa mulai play lebih cepat
            val loadControl: LoadControl = DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    300,   // minBufferMs - minimum buffer sebelum mulai play (0.3 detik) - LEBIH CEPAT
                    15000, // maxBufferMs - maximum buffer (15 detik) - LEBIH BESAR untuk smooth playback
                    300,   // bufferForPlaybackMs - buffer untuk playback (0.3 detik) - LEBIH CEPAT
                    500    // bufferForPlaybackAfterRebufferMs - buffer setelah rebuffer (0.5 detik) - LEBIH CEPAT
                )
                .setTargetBufferBytes(-1) // Unlimited target buffer
                .setPrioritizeTimeOverSizeThresholds(true) // Prioritaskan waktu daripada ukuran
                .setBackBuffer(3000, true) // Back buffer 3 detik - dikurangi untuk lebih cepat
                .build()
            
            // Initialize new ExoPlayer dengan LoadControl yang dioptimasi
            exoPlayer = ExoPlayer.Builder(this)
                .setLoadControl(loadControl)
                .build()
                .apply {
                    binding.playerViewTutorial.player = this
                    videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                    playWhenReady = true
                    repeatMode = Player.REPEAT_MODE_OFF
                }
            
            // Create media item dari video URL
            val mediaItem = MediaItem.fromUri(videoUrl)
            
            // Setup player listeners
            exoPlayer?.apply {
                // Clear previous media items
                clearMediaItems()
                setMediaItem(mediaItem)
                
                // Set playWhenReady to true so video auto-plays when ready
                playWhenReady = true
                
                // Prepare the player
                prepare()
                
                // Start updating progress periodically
                startProgressUpdates()
                
                // Listener untuk handle loading dan error
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_READY -> {
                                val readyTime = System.currentTimeMillis() - videoLoadStartTime
                                Log.d(TAG, "Video STATE_READY: $videoUrl (took ${readyTime}ms), isPlaying: $isPlaying, playWhenReady: $playWhenReady")
                                // Ensure video starts playing
                                if (!isPlaying && playWhenReady) {
                                    play()
                                }
                                // Don't hide loading yet, wait for first frame
                            }
                            Player.STATE_BUFFERING -> {
                                // Video sedang loading, tetap tampilkan loading progress
                                binding.loadingProgressContainer.visibility = View.VISIBLE
                                updateBufferingProgress()
                                val bufferingTime = System.currentTimeMillis() - videoLoadStartTime
                                Log.d(TAG, "Video buffering: $videoUrl (elapsed: ${bufferingTime}ms)")
                            }
                            Player.STATE_IDLE -> {
                                // Video sedang idle, tetap tunggu
                                binding.loadingProgressContainer.visibility = View.VISIBLE
                                updateBufferingProgress()
                                Log.d(TAG, "Video idle: $videoUrl")
                            }
                        }
                        super.onPlaybackStateChanged(playbackState)
                    }
                    
                    override fun onRenderedFirstFrame() {
                        // Video frame pertama sudah di-render, sembunyikan loading
                        stopProgressUpdates()
                        binding.loadingProgressContainer.visibility = View.GONE
                        binding.loadingProgress.progress = 100
                        binding.tvLoadingPercentage.text = "100%"
                        val loadTime = System.currentTimeMillis() - videoLoadStartTime
                        Log.d(TAG, "First frame rendered for: $videoUrl (took ${loadTime}ms)")
                        super.onRenderedFirstFrame()
                    }
                    
                    override fun onIsLoadingChanged(isLoading: Boolean) {
                        if (isLoading) {
                            startProgressUpdates()
                        } else {
                            stopProgressUpdates()
                        }
                        super.onIsLoadingChanged(isLoading)
                    }
                    
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        Log.d(TAG, "Video isPlaying changed: $isPlaying for: $videoUrl")
                        super.onIsPlayingChanged(isPlaying)
                    }
                    
                    override fun onVideoSizeChanged(videoSize: com.google.android.exoplayer2.video.VideoSize) {
                        Log.d(TAG, "Video size changed: width=${videoSize.width}, height=${videoSize.height}, pixelWidthHeightRatio=${videoSize.pixelWidthHeightRatio} for: $videoUrl")
                        super.onVideoSizeChanged(videoSize)
                    }
                    
                    override fun onPlayerError(error: com.google.android.exoplayer2.PlaybackException) {
                        // Log error dengan detail lebih lengkap
                        Log.e(TAG, "Error playing video: $videoUrl", error)
                        Log.e(TAG, "Error type: ${error.errorCode}, message: ${error.message}")
                        // Jangan sembunyikan loading progress, biarkan user tahu masih loading
                        super.onPlayerError(error)
                    }
                })
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up video player for: $videoUrl", e)
            // Jangan tampilkan error, biarkan loading tetap berjalan
        }
    }
    
    
    /**
     * Start updating progress periodically
     */
    private fun startProgressUpdates() {
        stopProgressUpdates() // Stop any existing updates
        progressUpdateRunnable = object : Runnable {
            override fun run() {
                updateBufferingProgress()
                progressUpdateHandler.postDelayed(this, 100) // Update every 100ms
            }
        }
        progressUpdateHandler.post(progressUpdateRunnable!!)
    }
    
    /**
     * Stop updating progress
     */
    private fun stopProgressUpdates() {
        progressUpdateRunnable?.let {
            progressUpdateHandler.removeCallbacks(it)
            progressUpdateRunnable = null
        }
    }
    
    /**
     * Update buffering progress berdasarkan buffered position
     */
    private fun updateBufferingProgress() {
        exoPlayer?.let { player ->
            val duration = player.duration
            if (duration > 0) {
                val bufferedPosition = player.bufferedPosition
                val progress = ((bufferedPosition.toFloat() / duration.toFloat()) * 100).toInt().coerceIn(0, 100)
                binding.loadingProgress.progress = progress
                binding.tvLoadingPercentage.text = "$progress%"
            } else {
                // Jika duration belum diketahui, gunakan buffered position saja
                val bufferedPosition = player.bufferedPosition
                if (bufferedPosition > 0) {
                    // Estimasi progress berdasarkan buffered position (asumsi video ~5 detik)
                    val estimatedDuration = 5000L
                    val progress = ((bufferedPosition.toFloat() / estimatedDuration.toFloat()) * 100).toInt().coerceIn(0, 95)
                    binding.loadingProgress.progress = progress
                    binding.tvLoadingPercentage.text = "$progress%"
                } else {
                    // Progress minimal saat baru mulai, increment sedikit
                    val currentProgress = binding.loadingProgress.progress
                    val newProgress = (currentProgress + 2).coerceIn(5, 20)
                    binding.loadingProgress.progress = newProgress
                    binding.tvLoadingPercentage.text = "$newProgress%"
                }
            }
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
        // Stop progress updates
        stopProgressUpdates()
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
    
    override fun onResume() {
        super.onResume()
        // Resume video playback if player is ready
        exoPlayer?.let {
            if (it.playbackState == Player.STATE_READY) {
                it.play()
            }
        }
    }
}
