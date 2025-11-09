package com.google.mediapipe.examples.gesturerecognizer.features.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.core.animation.ViewAnimationUtils

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable hardware acceleration
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        
        setContentView(R.layout.activity_register)

        val etFullname = findViewById<EditText>(R.id.et_fullname)
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnDaftar = findViewById<Button>(R.id.btn_daftar)
        val tvLoginLink = findViewById<TextView>(R.id.tv_login_link)
        val registerContainer = findViewById<android.view.ViewGroup>(R.id.register_container)
        
        // Prepare for animation
        window.decorView.alpha = 0f
        ViewAnimationUtils.prepareForAnimation(registerContainer, 120f)
        
        // Start animations
        window.decorView.post {
            startEntranceAnimations(registerContainer)
        }

        btnDaftar.setOnClickListener {
            ViewAnimationUtils.animateClick(it) {
                val fullname = etFullname.text.toString()
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                
                // Validasi input
                when {
                    fullname.isEmpty() -> {
                        Toast.makeText(this, "Nama lengkap tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    }
                    email.isEmpty() -> {
                        Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    }
                    password.isEmpty() -> {
                        Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    }
                    password.length < 6 -> {
                        Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        // Registrasi berhasil (simpan data di sini jika diperlukan)
                        Toast.makeText(this, "Registrasi berhasil! Silakan login.", Toast.LENGTH_SHORT).show()
                        
                        // Redirect ke halaman login
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }

        tvLoginLink.setOnClickListener {
            ViewAnimationUtils.animateClick(it) {
                // Kembali ke halaman login
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
    
    private fun startEntranceAnimations(registerContainer: android.view.ViewGroup?) {
        // Fade in screen
        ViewAnimationUtils.fadeInScreen(window.decorView, 300)
        
        // Animate register container
        registerContainer?.let {
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
