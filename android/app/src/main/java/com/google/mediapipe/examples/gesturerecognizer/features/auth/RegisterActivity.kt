package com.google.mediapipe.examples.gesturerecognizer.features.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.data.api.AuthApiService
import com.google.mediapipe.examples.gesturerecognizer.data.manager.AuthManager
import com.google.mediapipe.examples.gesturerecognizer.features.home.HomeActivity
import com.google.mediapipe.examples.gesturerecognizer.core.animation.ViewAnimationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var authManager: AuthManager
    private lateinit var authApiService: AuthApiService
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnDaftar: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable hardware acceleration
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        
        setContentView(R.layout.activity_register)

        authManager = AuthManager(this)
        authApiService = AuthApiService.getInstance()
        
        etName = findViewById(R.id.et_fullname)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        // Note: et_confirm_password doesn't exist in layout, so we'll skip validation
        btnDaftar = findViewById(R.id.btn_daftar)
        progressBar = findViewById(R.id.progress_bar_register)

        val tvLoginLink = findViewById<android.widget.TextView>(R.id.tv_login_link)
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
                performRegister()
            }
        }

        tvLoginLink.setOnClickListener {
            ViewAnimationUtils.animateClick(it) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
    
    private fun performRegister() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        
        // Validate input
        when {
            name.isEmpty() -> {
                Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            email.isEmpty() -> {
                Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            password.isEmpty() -> {
                Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            password.length < 6 -> {
                Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                return
            }
            else -> {
                // Show progress indicator
                progressBar.visibility = android.view.View.VISIBLE
                btnDaftar.isEnabled = false
                
                // Call register API with "murid" role
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val result = authApiService.register(name, email, password, "murid")
                        
                        runOnUiThread {
                            progressBar.visibility = android.view.View.GONE
                            btnDaftar.isEnabled = true
                            
                            if (result.isSuccess) {
                                val registerResponse = result.getOrNull()
                                val message = registerResponse?.message
                                    ?: "Registrasi berhasil! Silakan periksa email Anda untuk verifikasi."
                                
                                Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_LONG).show()
                                
                                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                val errorMessage = result.exceptionOrNull()?.message
                                    ?: "Registrasi gagal. Silakan coba lagi."
                                Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            progressBar.visibility = android.view.View.GONE
                            btnDaftar.isEnabled = true
                            Toast.makeText(this@RegisterActivity, 
                                "Registrasi gagal: ${e.message}", 
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
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