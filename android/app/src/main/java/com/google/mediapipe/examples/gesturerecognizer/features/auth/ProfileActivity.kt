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
import com.google.mediapipe.examples.gesturerecognizer.core.animation.ViewAnimationUtils

class ProfileActivity : AppCompatActivity() {

    private lateinit var btnSettings: CardView
    private lateinit var ivProfilePhoto: ImageView
    private lateinit var imgKaligrafiProfile: ImageView
    private lateinit var profileCard: CardView
    private lateinit var tvUsername: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var progressQuiz: ProgressBar
    private lateinit var progressSurat: ProgressBar
    private lateinit var progressHijaiyah: ProgressBar
    private lateinit var btnLogout: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable hardware acceleration
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        
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
        
        // Start animations
        window.decorView.post {
            startEntranceAnimations()
        }
    }

    private fun initializeViews() {
        btnSettings = findViewById(R.id.btn_settings)
        ivProfilePhoto = findViewById(R.id.iv_profile_photo)
        imgKaligrafiProfile = findViewById(R.id.img_kaligrafi_profile)
        profileCard = findViewById(R.id.profile_card)
        tvUsername = findViewById(R.id.tv_username)
        tvUserEmail = findViewById(R.id.tv_user_email)
        progressQuiz = findViewById(R.id.progress_quiz)
        progressSurat = findViewById(R.id.progress_surat)
        progressHijaiyah = findViewById(R.id.progress_hijaiyah)
        btnLogout = findViewById(R.id.btn_logout)
        
        // Prepare for animation
        window.decorView.alpha = 0f
        ViewAnimationUtils.prepareForAnimation(imgKaligrafiProfile, -80f)
        ViewAnimationUtils.prepareForAnimation(profileCard, 120f)
        ViewAnimationUtils.prepareForAnimation(btnLogout, 100f)
    }
    
    private fun startEntranceAnimations() {
        // Fade in screen
        ViewAnimationUtils.fadeInScreen(window.decorView, 250)
        
        // Animate kaligrafi
        ViewAnimationUtils.animateViewEntrance(
            view = imgKaligrafiProfile,
            delay = 200,
            duration = 800,
            translationY = -80f,
            overshoot = 1.8f
        )
        
        // Animate profile card
        ViewAnimationUtils.animateCardEntrance(profileCard, 400)
        
        // Animate logout button
        ViewAnimationUtils.animateButtonEntrance(btnLogout, 700)
    }

    private fun setupClickListeners() {
        // Settings button
        btnSettings.setOnClickListener {
            ViewAnimationUtils.animateClick(it) {
                showSettingsDialog()
            }
        }

        // Profile photo - change photo
        profileCard.setOnClickListener {
            ViewAnimationUtils.animateClick(it) {
                showChangePhotoDialog()
            }
        }

        // Logout button
        btnLogout.setOnClickListener {
            ViewAnimationUtils.animateClick(it) {
                showLogoutConfirmation()
            }
        }
    }

    private fun loadUserData() {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "Altamish") ?: "Altamish"
        val userEmail = sharedPreferences.getString("user_email", "altamish@gmail.com") ?: "altamish@gmail.com"
        
        tvUsername.text = username
        tvUserEmail.text = userEmail
    }
    
    private fun showSettingsDialog() {
        val options = arrayOf("Edit Profile", "Ubah Password", "Notifikasi", "Tentang Aplikasi")
        
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("⚙️ Pengaturan")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> showEditProfileDialog()
                1 -> Toast.makeText(this, "Fitur Ubah Password - Coming Soon", Toast.LENGTH_SHORT).show()
                2 -> Toast.makeText(this, "Pengaturan Notifikasi - Coming Soon", Toast.LENGTH_SHORT).show()
                3 -> showAboutDialog()
            }
        }
        builder.setNegativeButton("Tutup") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }
    
    private fun showEditProfileDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("✏️ Edit Profile")
        
        val layout = android.widget.LinearLayout(this)
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.setPadding(50, 20, 50, 20)
        
        val inputName = android.widget.EditText(this)
        inputName.hint = "Nama"
        inputName.setText(tvUsername.text)
        layout.addView(inputName)
        
        val inputEmail = android.widget.EditText(this)
        inputEmail.hint = "Email"
        inputEmail.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        inputEmail.setText(tvUserEmail.text)
        layout.addView(inputEmail)
        
        builder.setView(layout)
        
        builder.setPositiveButton("Simpan") { dialog, _ ->
            val newName = inputName.text.toString()
            val newEmail = inputEmail.text.toString()
            
            if (newName.isNotEmpty()) {
                tvUsername.text = newName
                tvUserEmail.text = newEmail
                
                val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                sharedPreferences.edit().apply {
                    putString("username", newName)
                    putString("user_email", newEmail)
                    apply()
                }
                
                Toast.makeText(this, "✅ Profile berhasil diupdate!", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        
        builder.setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }
    
    private fun showChangePhotoDialog() {
        val options = arrayOf("📷 Ambil Foto", "🖼️ Pilih dari Galeri", "❌ Hapus Foto")
        
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Ubah Foto Profile")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> Toast.makeText(this, "Fitur Camera - Coming Soon", Toast.LENGTH_SHORT).show()
                1 -> Toast.makeText(this, "Fitur Galeri - Coming Soon", Toast.LENGTH_SHORT).show()
                2 -> {
                    ivProfilePhoto.setImageResource(R.drawable.profileust)
                    Toast.makeText(this, "Foto direset ke default", Toast.LENGTH_SHORT).show()
                }
            }
        }
        builder.setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }
    
    private fun showAboutDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("📱 Tentang Aplikasi")
        builder.setMessage("Aplikasi Belajar Huruf Hijaiyah\n\nVersi: 1.0.0\n\nDikembangkan dengan ❤️ menggunakan MediaPipe Gesture Recognition\n\n© 2025 Altamish")
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
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
