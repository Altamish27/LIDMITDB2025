package com.google.mediapipe.examples.gesturerecognizer.features.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahProgressManager
import com.google.mediapipe.examples.gesturerecognizer.data.LatihanProgressManager

class ProfileActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnSettings: ImageView
    private lateinit var ivProfilePhoto: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var tvUserStatus: TextView
    private lateinit var progressQuiz: ProgressBar
    private lateinit var progressSurat: ProgressBar
    private lateinit var progressHijaiyah: ProgressBar
    private lateinit var btnLogout: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Hide action bar
        supportActionBar?.hide()

        // Initialize views
        initializeViews()

        // Setup click listeners
        setupClickListeners()

        // Load user data
        loadUserData()

        // Load progress data
        loadProgressData()
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btn_back)
        btnSettings = findViewById(R.id.btn_settings)
        ivProfilePhoto = findViewById(R.id.iv_profile_photo)
        tvUsername = findViewById(R.id.tv_username)
        tvUserStatus = findViewById(R.id.tv_user_status)
        progressQuiz = findViewById(R.id.progress_quiz)
        progressSurat = findViewById(R.id.progress_surat)
        progressHijaiyah = findViewById(R.id.progress_hijaiyah)
        btnLogout = findViewById(R.id.btn_logout)
    }

    private fun setupClickListeners() {
        // Back button
        btnBack.setOnClickListener {
            onBackPressed()
        }

        // Settings button
        btnSettings.setOnClickListener {
            Toast.makeText(this, "Pengaturan - Akan segera tersedia", Toast.LENGTH_SHORT).show()
        }

        // Logout button
        btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun loadUserData() {
        // TODO: Load user data from SharedPreferences or Database
        // For now, using default values
        
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "Altamish") ?: "Altamish"
        val userStatus = sharedPreferences.getString("user_status", "Belajar Hijaiyah") ?: "Belajar Hijaiyah"
        
        tvUsername.text = username
        tvUserStatus.text = userStatus
    }

    private fun loadProgressData() {
        // Initialize progress managers
        val hijaiyahProgressManager = HijaiyahProgressManager(this)
        val latihanProgressManager = LatihanProgressManager(this)
        
        // Get Hijaiyah progress
        val hijaiyahProgress = hijaiyahProgressManager.getTotalProgress()
        val hijaiyahPercentage = if (hijaiyahProgress.second > 0) {
            (hijaiyahProgress.first.toFloat() / hijaiyahProgress.second.toFloat() * 100).toInt()
        } else {
            0
        }
        
        // Get Latihan (Quiz) progress
        val latihanProgress = latihanProgressManager.getTotalProgress()
        val latihanPercentage = if (latihanProgress.second > 0) {
            (latihanProgress.first.toFloat() / latihanProgress.second.toFloat() * 100).toInt()
        } else {
            0
        }
        
        // Get Surat progress (from SharedPreferences)
        val sharedPreferences = getSharedPreferences("SuratPrefs", MODE_PRIVATE)
        val suratCompleted = sharedPreferences.getInt("surat_completed", 2)
        val suratTotal = sharedPreferences.getInt("surat_total", 10)
        val suratPercentage = if (suratTotal > 0) {
            (suratCompleted.toFloat() / suratTotal.toFloat() * 100).toInt()
        } else {
            0
        }
        
        // Set progress bars
        progressQuiz.progress = latihanPercentage
        progressSurat.progress = suratPercentage
        progressHijaiyah.progress = hijaiyahPercentage
    }

    private fun showLogoutConfirmation() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Keluar")
        builder.setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
        
        builder.setPositiveButton("Ya") { dialog, _ ->
            performLogout()
            dialog.dismiss()
        }
        
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }
        
        val dialog = builder.create()
        dialog.show()
    }

    private fun performLogout() {
        // Clear user session
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        
        // Navigate to login screen
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        
        Toast.makeText(this, "Berhasil keluar", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
