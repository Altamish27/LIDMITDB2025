package com.google.mediapipe.examples.gesturerecognizer.features.about

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.core.animation.ViewAnimationUtils

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable hardware acceleration
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        
        setContentView(R.layout.activity_about)

        // Hide action bar
        supportActionBar?.hide()

        // Setup back button
        findViewById<android.widget.ImageView>(R.id.btn_back)?.setOnClickListener {
            finish()
        }

        // Start entrance animations
        window.decorView.post {
            startEntranceAnimations()
        }
    }

    private fun startEntranceAnimations() {
        val contentContainer = findViewById<android.view.ViewGroup>(R.id.content_container)
        
        // Fade in screen
        ViewAnimationUtils.fadeInScreen(window.decorView, 300)
        
        // Animate content container
        contentContainer?.let {
            ViewAnimationUtils.animateViewEntrance(
                view = it,
                delay = 250,
                duration = 800,
                translationY = 80f,
                overshoot = 1.3f
            )
        }
    }
}
