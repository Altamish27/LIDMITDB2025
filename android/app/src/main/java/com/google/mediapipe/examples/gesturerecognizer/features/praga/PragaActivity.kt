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
import com.google.android.exoplayer2.DefaultRenderersFactory
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
    private var videoTimeoutHandler: Runnable? = null
    private val videoHandler = Handler(Looper.getMainLooper())

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
            
            // Find current index using multiple matching strategies
            currentIndex = findLetterIndex()
            if (currentIndex == -1) currentIndex = 0

            setupViews()
            setupClickListeners()
            updateNavigationButtons()
        }
    }
    
    /**
     * Find letter index using multiple matching strategies:
     * 1. Exact transliteration match
     * 2. Arabic character match
     * 3. Case-insensitive transliteration match
     * 4. Base transliteration match (for diacritic letters like "Ba Fathah" -> match "Ba")
     */
    private fun findLetterIndex(): Int {
        // Strategy 1: Exact transliteration match
        var index = HijaiyahData.letters.indexOfFirst { it.transliteration == hurufLatin }
        if (index != -1) {
            Log.d(TAG, "Found letter by exact transliteration: $hurufLatin at index $index")
            return index
        }
        
        // Strategy 2: Arabic character match
        index = HijaiyahData.letters.indexOfFirst { it.arabic == hurufArab }
        if (index != -1) {
            Log.d(TAG, "Found letter by Arabic character: $hurufArab at index $index")
            return index
        }
        
        // Strategy 3: Case-insensitive transliteration match
        index = HijaiyahData.letters.indexOfFirst { 
            it.transliteration.equals(hurufLatin, ignoreCase = true) 
        }
        if (index != -1) {
            Log.d(TAG, "Found letter by case-insensitive transliteration: $hurufLatin at index $index")
            return index
        }
        
        // Strategy 4: Base transliteration match (for diacritic letters)
        // Extract base name without diacritic (e.g., "Ba Fathah" -> "Ba")
        val baseName = extractBaseName(hurufLatin)
        index = HijaiyahData.letters.indexOfFirst { 
            it.transliteration.equals(baseName, ignoreCase = true) ||
            extractBaseName(it.transliteration).equals(baseName, ignoreCase = true)
        }
        if (index != -1) {
            Log.d(TAG, "Found letter by base transliteration: $baseName at index $index")
            return index
        }
        
        Log.w(TAG, "Letter not found: arab='$hurufArab', latin='$hurufLatin'. Defaulting to index 0")
        return -1
    }
    
    /**
     * Extract base name from transliteration by removing diacritic suffixes
     */
    private fun extractBaseName(name: String): String {
        val lower = name.lowercase()
        return when {
            lower.contains(" fathah") -> name.replace(" Fathah", "", ignoreCase = true).replace(" fathah", "", ignoreCase = true)
            lower.contains(" kasrah") -> name.replace(" Kasrah", "", ignoreCase = true).replace(" kasrah", "", ignoreCase = true)
            lower.contains(" kasroh") -> name.replace(" Kasroh", "", ignoreCase = true).replace(" kasroh", "", ignoreCase = true)
            lower.contains(" dhammah") -> name.replace(" Dhammah", "", ignoreCase = true).replace(" dhammah", "", ignoreCase = true)
            lower.contains(" dhommah") -> name.replace(" Dhommah", "", ignoreCase = true).replace(" dhommah", "", ignoreCase = true)
            lower.contains(" dammah") -> name.replace(" Dammah", "", ignoreCase = true).replace(" dammah", "", ignoreCase = true)
            else -> name
        }.trim()
    }

    private fun setupViews() {
        val currentLetter = HijaiyahData.letters.getOrNull(currentIndex)
        
        // Set huruf latin di header
        binding.tvHurufLatin.text = hurufLatin
        
        // Set huruf center (card atas)
        binding.tvHurufArabCenter.text = hurufArab
        
        // Display Arabic letter with hijaiyah_font_family (same as home table's Isyarat column)
        binding.tvGestureName.text = hurufArab

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
        // Cancel video timeout
        videoTimeoutHandler?.let { videoHandler.removeCallbacks(it) }
        
        // Hide containers
        binding.cardImageContainer.visibility = View.GONE
        binding.cardVideoContainer.visibility = View.GONE
        binding.loadingProgressContainer.visibility = View.GONE
        
        // Properly release player untuk prevent frame nyangkut
        exoPlayer?.let { player ->
            player.stop()
            player.clearMediaItems()
            binding.playerViewTutorial.player = null
        }
        exoPlayer?.release()
        exoPlayer = null
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
     * Tampilkan video menggunakan ExoPlayer - OPTIMIZED FOR LARGE FILES (2-3MB)
     */
    private fun displayVideo(videoUrl: String) {
        // Show container and loading
        binding.cardVideoContainer.visibility = View.VISIBLE
        binding.loadingProgressContainer.visibility = View.VISIBLE
        binding.loadingProgress.progress = 0
        binding.tvLoadingPercentage.text = "Loading..."
        
        // Cancel any existing timeout
        videoTimeoutHandler?.let { videoHandler.removeCallbacks(it) }
        
        // Release any existing player properly
        exoPlayer?.stop()
        exoPlayer?.release()
        exoPlayer = null
        binding.playerViewTutorial.player = null
        
        // Set timeout untuk auto-skip video jika terlalu lama (20 detik)
        videoTimeoutHandler = Runnable {
            if (binding.loadingProgressContainer.visibility == View.VISIBLE) {
                handleVideoLoadFailure()
            }
        }
        videoHandler.postDelayed(videoTimeoutHandler!!, 20000) // 20 detik timeout
        
        try {
            // Configure LoadControl untuk cepat detect error
            // Buffer lebih kecil agar error cepat terdeteksi
            val loadControl = DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    1000,  // minBufferMs - 1 detik (lebih cepat)
                    10000, // maxBufferMs - 10 detik (cukup)
                    500,   // bufferForPlaybackMs - 0.5 detik (cepat start)
                    1000   // bufferForPlaybackAfterRebufferMs - 1 detik
                )
                .setTargetBufferBytes(3 * 1024 * 1024) // 3MB buffer target
                .setPrioritizeTimeOverSizeThresholds(true)
                .build()
            
            // Configure Renderers Factory untuk support semua codec
            // Gunakan software decoder jika hardware decoder gagal
            val renderersFactory = DefaultRenderersFactory(this).apply {
                // PREFER_EXTENSION_RENDERER akan coba extension (software) renderer jika platform (hardware) renderer gagal
                setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
                // Enable decoder fallback
                setEnableDecoderFallback(true)
            }
            
            // Create new player with LoadControl dan RenderersFactory
            exoPlayer = ExoPlayer.Builder(this, renderersFactory)
                .setLoadControl(loadControl)
                .build().also { player ->
                
                // Configure player FIRST sebelum bind
                player.playWhenReady = true
                player.repeatMode = Player.REPEAT_MODE_ALL
                player.volume = 1f
                player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                
                // Bind to view
                binding.playerViewTutorial.player = player
                
                // Set media item dengan configuration
                val mediaItem = MediaItem.Builder()
                    .setUri(videoUrl)
                    .build()
                    
                player.setMediaItem(mediaItem)
                
                // Add listener
                player.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_BUFFERING -> {
                                // Show buffering progress
                                binding.loadingProgressContainer.visibility = View.VISIBLE
                                val duration = player.duration
                                val buffered = player.bufferedPosition
                                if (duration > 0) {
                                    val percent = ((buffered * 100) / duration).toInt()
                                    binding.loadingProgress.progress = percent
                                    binding.tvLoadingPercentage.text = "$percent%"
                                }
                            }
                            Player.STATE_READY -> {
                                // Force play jika belum playing
                                if (!player.isPlaying) {
                                    player.playWhenReady = true
                                    player.play()
                                }
                            }
                            Player.STATE_ENDED -> {
                                // Loop video
                                player.seekTo(0)
                                player.play()
                            }
                        }
                    }
                    
                    override fun onRenderedFirstFrame() {
                        // Cancel timeout - video berhasil render
                        videoTimeoutHandler?.let { videoHandler.removeCallbacks(it) }
                        
                        // Hide loading - video is now visible
                        runOnUiThread {
                            binding.loadingProgressContainer.visibility = View.GONE
                            binding.loadingProgress.progress = 100
                        }
                        
                        // Ensure video playing
                        if (!player.isPlaying) {
                            player.play()
                        }
                    }
                    
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        // Auto-restart if stopped unexpectedly
                        if (!isPlaying && player.playWhenReady && player.playbackState == Player.STATE_READY) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                if (!player.isPlaying && player.playWhenReady && player.playbackState == Player.STATE_READY) {
                                    player.play()
                                }
                            }, 200)
                        }
                        super.onIsPlayingChanged(isPlaying)
                    }
                    
                    override fun onPlayerError(error: com.google.android.exoplayer2.PlaybackException) {
                        // Silent skip - hide video container tanpa notifikasi
                        runOnUiThread {
                            handleVideoLoadFailure()
                        }
                    }
                })
                
                // Prepare AFTER everything is set up
                player.prepare()
                
                // Force play setelah prepare jika perlu
                Handler(Looper.getMainLooper()).postDelayed({
                    if (!player.isPlaying && player.playWhenReady && player.playbackState != Player.STATE_IDLE) {
                        player.play()
                    }
                }, 2000)
            }
            
        } catch (e: Exception) {
            // Silent skip on exception
            runOnUiThread {
                handleVideoLoadFailure()
            }
        }
    }
    
    /**
     * Handle video load failure - silent skip, hide video container
     */
    private fun handleVideoLoadFailure() {
        // Cancel timeout
        videoTimeoutHandler?.let { videoHandler.removeCallbacks(it) }
        
        // Stop and release player
        exoPlayer?.stop()
        exoPlayer?.release()
        exoPlayer = null
        binding.playerViewTutorial.player = null
        
        // Hide loading dan video container (silent, no notification)
        binding.loadingProgressContainer.visibility = View.GONE
        binding.cardVideoContainer.visibility = View.GONE
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
        // Cancel timeout
        videoTimeoutHandler?.let { videoHandler.removeCallbacks(it) }
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
