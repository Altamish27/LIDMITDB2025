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

class LoginActivity : AppCompatActivity() {
    private lateinit var authManager: AuthManager
    private lateinit var authApiService: AuthApiService
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnMasuk: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable hardware acceleration
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        
        setContentView(R.layout.activity_login)

        authManager = AuthManager(this)
        authApiService = AuthApiService.getInstance()
        
        etEmail = findViewById(R.id.et_username) // Using the same field name as in layout
        etPassword = findViewById(R.id.et_password)
        btnMasuk = findViewById(R.id.btn_masuk)
        progressBar = findViewById(R.id.progress_bar_login)

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
                performLogin()
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
    
    private fun performLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        
        // Validate input
        when {
            email.isEmpty() -> {
                Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            password.isEmpty() -> {
                Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            else -> {
                // Show progress indicator
                progressBar.visibility = android.view.View.VISIBLE
                btnMasuk.isEnabled = false
                
                // Call login API
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val result = authApiService.login(email, password)
                        
                        runOnUiThread {
                            progressBar.visibility = android.view.View.GONE
                            btnMasuk.isEnabled = true
                            
                            if (result.isSuccess) {
                                val loginResponse = result.getOrNull()
                                if (loginResponse != null) {
                                    // Save user data and token
                                    authManager.saveUserData(loginResponse.user, loginResponse.token)
                                    
                                    Toast.makeText(this@LoginActivity, "Login berhasil!", Toast.LENGTH_SHORT).show()
                                    
                                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this@LoginActivity, "Login gagal: Data tidak valid", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // Extract error message if possible
                                val exception = result.exceptionOrNull()
                                if (exception?.message?.contains("401") == true) {
                                    Toast.makeText(this@LoginActivity, "Email atau password salah", Toast.LENGTH_SHORT).show()
                                } else if (exception?.message?.contains("403") == true) {
                                    Toast.makeText(this@LoginActivity, "Silakan verifikasi email terlebih dahulu", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this@LoginActivity, "Login gagal: ${exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            progressBar.visibility = android.view.View.GONE
                            btnMasuk.isEnabled = true
                            Toast.makeText(this@LoginActivity, "Login gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
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