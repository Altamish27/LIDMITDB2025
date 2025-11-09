package com.google.mediapipe.examples.gesturerecognizer.features.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.features.home.HomeActivity
import com.google.mediapipe.examples.gesturerecognizer.core.animation.ViewAnimationUtils

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable hardware acceleration
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        
        setContentView(R.layout.activity_login)

        val btnMasuk = findViewById<Button>(R.id.btn_masuk)
        val btnTanpa = findViewById<Button>(R.id.btn_tanpa_login)
        val tvRegisterLink = findViewById<android.widget.TextView>(R.id.tv_register_link)
        val loginContainer = findViewById<android.view.ViewGroup>(R.id.login_container)
        
        // Prepare for animation
        window.decorView.alpha = 0f
        ViewAnimationUtils.prepareForAnimation(loginContainer, 120f)
        
        // Start animations
        window.decorView.post {
            startEntranceAnimations(loginContainer)
        }

        btnMasuk.setOnClickListener {
            ViewAnimationUtils.animateClick(it) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        btnTanpa.setOnClickListener {
            ViewAnimationUtils.animateClick(it) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        tvRegisterLink.setOnClickListener {
            ViewAnimationUtils.animateClick(it) {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }
    
    private fun startEntranceAnimations(loginContainer: android.view.ViewGroup?) {
        // Fade in screen
        ViewAnimationUtils.fadeInScreen(window.decorView, 300)
        
        // Animate login container
        loginContainer?.let {
            ViewAnimationUtils.animateViewEntrance(
                view = it,
                delay = 250,
                duration = 800,
                translationY = 120f,
                overshoot = 1.5f
            )
        }
    }
}
