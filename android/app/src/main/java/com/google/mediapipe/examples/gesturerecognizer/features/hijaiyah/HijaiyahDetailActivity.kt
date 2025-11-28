package com.google.mediapipe.examples.gesturerecognizer.features.hijaiyah

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.request.ErrorResult
import coil.request.ImageRequest
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.LoadControl
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahLetter
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahData
import java.io.File

/**
 * Activity to display detailed information about a hijaiyah letter, including assets (image/video)
 */
class HijaiyahDetailActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "HijaiyahDetailActivity"
        const val EXTRA_LETTER_ID = "letter_id"
    }
    
    private lateinit var imageView: ImageView
    private lateinit var videoView: com.google.android.exoplayer2.ui.PlayerView
    private lateinit var arabicLetterText: TextView
    private lateinit var latinNameText: TextView
    private lateinit var ordinalText: TextView
    private lateinit var loadingProgress: ProgressBar
    private lateinit var loadingProgressContainer: android.widget.LinearLayout
    private lateinit var tvLoadingPercentage: android.widget.TextView
    private lateinit var containerImage: FrameLayout
    private lateinit var containerVideo: FrameLayout
    
    private var exoPlayer: ExoPlayer? = null
    private var hijaiyahLetter: HijaiyahLetter? = null
    private var letterId: Int = -1
    private var videoLoadStartTime: Long = 0
    private val progressUpdateHandler = Handler(Looper.getMainLooper())
    private var progressUpdateRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hijaiyah_detail)
        
        letterId = intent.extras?.getInt(EXTRA_LETTER_ID, -1) ?: -1
        
        if (letterId == -1) {
            Toast.makeText(this, "Letter ID not provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        initViews()
        loadLetterData()
        setupUI()
        setupBackButton()
    }
    
    private fun initViews() {
        imageView = findViewById(R.id.imageView)
        videoView = findViewById(R.id.videoView)
        arabicLetterText = findViewById(R.id.text_arabic_letter)
        latinNameText = findViewById(R.id.text_latin_name)
        ordinalText = findViewById(R.id.text_ordinal)
        loadingProgress = findViewById(R.id.loading_progress)
        loadingProgressContainer = findViewById(R.id.loading_progress_container)
        tvLoadingPercentage = findViewById(R.id.tv_loading_percentage)
        containerImage = findViewById(R.id.container_image)
        containerVideo = findViewById(R.id.container_video)
    }
    
    private fun loadLetterData() {
        hijaiyahLetter = HijaiyahData.getAllLetters().find { it.position == letterId }
        
        if (hijaiyahLetter == null) {
            Toast.makeText(this, "Letter not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
    }
    
    private fun setupUI() {
        val letter = hijaiyahLetter ?: return
        
        // Set basic letter info
        arabicLetterText.text = letter.arabic
        latinNameText.text = letter.transliteration
        ordinalText.text = "Huruf ke-${letter.position}"
        
        // Process the assets based on the URL
        processAssets(letter.assets)
    }
    
    private fun processAssets(assetsUrl: String?) {
        // Hide both containers initially
        containerImage.visibility = View.GONE
        containerVideo.visibility = View.GONE
        loadingProgressContainer.visibility = View.GONE
        
        if (assetsUrl.isNullOrEmpty()) {
            // No assets to display
            Log.d(TAG, "No assets URL provided for letter")
            return
        }
        
        try {
            val uri = Uri.parse(assetsUrl)
            val fileExtension = getFileExtension(uri.toString())
            
            when (fileExtension?.lowercase()) {
                "jpg", "jpeg", "png", "gif" -> {
                    // Show image
                    displayImage(assetsUrl)
                }
                "mp4", "m4v", "mov", "avi", "mkv" -> {
                    // Show video
                    displayVideo(assetsUrl)
                }
                else -> {
                    // Unknown file type, skip display
                    Log.w(TAG, "Unsupported file type for: $assetsUrl")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing assets URL: $assetsUrl", e)
        }
    }
    
    private fun displayImage(imageUrl: String) {
        containerImage.visibility = View.VISIBLE
        loadingProgressContainer.visibility = View.GONE // Hide video loading, image doesn't need it
        
        val request = ImageRequest.Builder(this)
            .data(imageUrl)
            .target(
                onSuccess = { drawable ->
                    imageView.setImageDrawable(drawable)
                },
                onError = { _ ->
                    Log.e(TAG, "Error loading image from: $imageUrl")
                    containerImage.visibility = View.GONE
                }
            )
            .build()
        
        // Execute the request
        imageView.context.applicationContext.let { 
            ImageRequest.Builder(it)
                .data(imageUrl)
                .target(
                    onSuccess = { drawable ->
                        imageView.setImageDrawable(drawable)
                    },
                    onError = { _ ->
                        Log.e(TAG, "Error loading image from: $imageUrl")
                        containerImage.visibility = View.GONE
                    }
                )
                .build()
        }.also { request ->
            imageView.load(request)
        }
    }
    
    private fun displayVideo(videoUrl: String) {
        containerVideo.visibility = View.VISIBLE
        loadingProgressContainer.visibility = View.VISIBLE
        loadingProgress.progress = 0
        tvLoadingPercentage.text = "0%"
        
        // Ensure PlayerView is visible and properly configured
        videoView.visibility = View.VISIBLE
        videoView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        videoView.useController = false // Hide default controller
        
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
                    videoView.player = this
                    videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                    playWhenReady = true
                    repeatMode = Player.REPEAT_MODE_OFF
                }
            
            // Create media item from the video URL
            val mediaItem = MediaItem.fromUri(videoUrl)
            
            // Set up player listeners
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
                
                // Show loading progress until video is ready to play
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
                                loadingProgressContainer.visibility = View.VISIBLE
                                updateBufferingProgress()
                                val bufferingTime = System.currentTimeMillis() - videoLoadStartTime
                                Log.d(TAG, "Video buffering: $videoUrl (elapsed: ${bufferingTime}ms)")
                            }
                            Player.STATE_IDLE -> {
                                // Video sedang idle, tetap tunggu
                                loadingProgressContainer.visibility = View.VISIBLE
                                updateBufferingProgress()
                                Log.d(TAG, "Video idle: $videoUrl")
                            }
                        }
                        super.onPlaybackStateChanged(playbackState)
                    }
                    
                    override fun onRenderedFirstFrame() {
                        // Video frame pertama sudah di-render, sembunyikan loading
                        stopProgressUpdates()
                        loadingProgressContainer.visibility = View.GONE
                        loadingProgress.progress = 100
                        tvLoadingPercentage.text = "100%"
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
                loadingProgress.progress = progress
                tvLoadingPercentage.text = "$progress%"
            } else {
                // Jika duration belum diketahui, gunakan buffered position saja
                val bufferedPosition = player.bufferedPosition
                if (bufferedPosition > 0) {
                    // Estimasi progress berdasarkan buffered position (asumsi video ~5 detik)
                    val estimatedDuration = 5000L
                    val progress = ((bufferedPosition.toFloat() / estimatedDuration.toFloat()) * 100).toInt().coerceIn(0, 95)
                    loadingProgress.progress = progress
                    tvLoadingPercentage.text = "$progress%"
                } else {
                    // Progress minimal saat baru mulai, increment sedikit
                    val currentProgress = loadingProgress.progress
                    val newProgress = (currentProgress + 2).coerceIn(5, 20)
                    loadingProgress.progress = newProgress
                    tvLoadingPercentage.text = "$newProgress%"
                }
            }
        }
    }
    
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
    
    private fun setupBackButton() {
        val backButton = findViewById<ImageView>(R.id.button_back)
        backButton.setOnClickListener {
            finish()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Stop progress updates
        stopProgressUpdates()
        // Release ExoPlayer resources
        exoPlayer?.let { player ->
            player.release()
            videoView.player = null
            exoPlayer = null
        }
    }
    
    override fun onPause() {
        super.onPause()
        exoPlayer?.pause()
    }
    
    override fun onResume() {
        super.onResume()
        // Resume video playback if needed
        // exoPlayer?.playWhenReady = true
    }
}