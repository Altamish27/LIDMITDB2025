package com.google.mediapipe.examples.gesturerecognizer.features.hijaiyah

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.request.ErrorResult
import coil.request.ImageRequest
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
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
    private lateinit var containerImage: FrameLayout
    private lateinit var containerVideo: FrameLayout
    
    private var exoPlayer: ExoPlayer? = null
    private var hijaiyahLetter: HijaiyahLetter? = null
    private var letterId: Int = -1

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
        loadingProgress.visibility = View.GONE
        
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
        loadingProgress.visibility = View.VISIBLE
        
        val request = ImageRequest.Builder(this)
            .data(imageUrl)
            .target(
                onSuccess = { drawable ->
                    imageView.setImageDrawable(drawable)
                    loadingProgress.visibility = View.GONE
                },
                onError = { _ ->
                    Log.e(TAG, "Error loading image from: $imageUrl")
                    loadingProgress.visibility = View.GONE
                    // Optionally show a placeholder or error message
                    Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show()
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
                        loadingProgress.visibility = View.GONE
                    },
                    onError = { _ ->
                        Log.e(TAG, "Error loading image from: $imageUrl")
                        loadingProgress.visibility = View.GONE
                        Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show()
                    }
                )
                .build()
        }.also { request ->
            imageView.load(request)
        }
    }
    
    private fun displayVideo(videoUrl: String) {
        containerVideo.visibility = View.VISIBLE
        loadingProgress.visibility = View.VISIBLE
        
        try {
            // Initialize ExoPlayer if not already initialized
            if (exoPlayer == null) {
                exoPlayer = ExoPlayer.Builder(this).build().apply {
                    videoView.player = this
                }
            }
            
            // Create media item from the video URL
            val mediaItem = MediaItem.fromUri(videoUrl)
            
            // Set up player listeners
            exoPlayer?.apply {
                setMediaItem(mediaItem)
                prepare()
                
                // Show loading progress until video is ready to play
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_READY) {
                            loadingProgress.visibility = View.GONE
                        }
                        super.onPlaybackStateChanged(playbackState)
                    }
                    
                    override fun onPlayerError(error: com.google.android.exoplayer2.PlaybackException) {
                        Log.e(TAG, "Error playing video: $videoUrl", error)
                        loadingProgress.visibility = View.GONE
                        Toast.makeText(this@HijaiyahDetailActivity, "Gagal memutar video", Toast.LENGTH_SHORT).show()
                        super.onPlayerError(error)
                    }
                })
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up video player for: $videoUrl", e)
            loadingProgress.visibility = View.GONE
            Toast.makeText(this, "Gagal memutar video", Toast.LENGTH_SHORT).show()
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