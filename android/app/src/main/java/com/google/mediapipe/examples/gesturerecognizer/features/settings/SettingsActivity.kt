package com.google.mediapipe.examples.gesturerecognizer.features.settings

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.core.animation.ViewAnimationUtils

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var switchNotification: SwitchCompat
    private lateinit var switchReminder: SwitchCompat
    private lateinit var switchSoundEffects: SwitchCompat
    private lateinit var switchVoiceGuide: SwitchCompat
    private lateinit var switchDarkMode: SwitchCompat
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable hardware acceleration
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        
        setContentView(R.layout.activity_settings)

        // Hide action bar
        supportActionBar?.hide()

        // Initialize views
        initializeViews()
        
        // Setup click listeners
        setupClickListeners()

        // Start entrance animations
        window.decorView.post {
            startEntranceAnimations()
        }
    }

    private fun initializeViews() {
        switchNotification = findViewById(R.id.switch_notification)
        switchReminder = findViewById(R.id.switch_reminder)
        switchSoundEffects = findViewById(R.id.switch_sound_effects)
        switchVoiceGuide = findViewById(R.id.switch_voice_guide)
        switchDarkMode = findViewById(R.id.switch_dark_mode)
    }

    private fun setupClickListeners() {
        // Back button
        findViewById<android.widget.ImageView>(R.id.btn_back)?.setOnClickListener {
            finish()
        }

        // Notification switch
        switchNotification.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                this,
                if (isChecked) "Notifikasi diaktifkan" else "Notifikasi dinonaktifkan",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Reminder switch
        switchReminder.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                this,
                if (isChecked) "Pengingat diaktifkan" else "Pengingat dinonaktifkan",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Sound effects switch
        switchSoundEffects.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                this,
                if (isChecked) "Efek suara diaktifkan" else "Efek suara dinonaktifkan",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Voice guide switch
        switchVoiceGuide.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                this,
                if (isChecked) "Panduan suara diaktifkan" else "Panduan suara dinonaktifkan",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Dark mode switch
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                this,
                if (isChecked) "Mode gelap akan segera tersedia" else "Mode terang aktif",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Font size selector
        findViewById<android.widget.TextView>(R.id.tv_font_size)?.setOnClickListener {
            showFontSizeDialog()
        }

        // Clear cache button
        findViewById<android.view.View>(R.id.btn_clear_cache)?.setOnClickListener {
            showClearCacheDialog()
        }

        // Reset progress button
        findViewById<android.view.View>(R.id.btn_reset_progress)?.setOnClickListener {
            showResetProgressDialog()
        }
    }

    private fun showFontSizeDialog() {
        val options = arrayOf("Kecil", "Sedang", "Besar")
        var selectedOption = 1 // Default: Sedang

        AlertDialog.Builder(this)
            .setTitle("Pilih Ukuran Font")
            .setSingleChoiceItems(options, selectedOption) { _, which ->
                selectedOption = which
            }
            .setPositiveButton("OK") { dialog, _ ->
                findViewById<android.widget.TextView>(R.id.tv_font_size)?.text = options[selectedOption]
                Toast.makeText(this, "Ukuran font: ${options[selectedOption]}", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showClearCacheDialog() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Cache")
            .setMessage("Apakah Anda yakin ingin menghapus cache aplikasi?")
            .setPositiveButton("Ya") { dialog, _ ->
                Toast.makeText(this, "Cache berhasil dihapus", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showResetProgressDialog() {
        AlertDialog.Builder(this)
            .setTitle("Reset Progress")
            .setMessage("Apakah Anda yakin ingin mereset semua progress belajar? Tindakan ini tidak dapat dibatalkan.")
            .setPositiveButton("Ya") { dialog, _ ->
                Toast.makeText(this, "Progress berhasil direset", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
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
