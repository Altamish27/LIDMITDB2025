package com.google.mediapipe.examples.gesturerecognizer.features.room

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.mediapipe.examples.gesturerecognizer.data.api.SignQuranApiService
import com.google.mediapipe.examples.gesturerecognizer.data.manager.AuthManager
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityJoinRoomBinding
import kotlinx.coroutines.launch

class JoinRoomActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityJoinRoomBinding
    private val apiService = SignQuranApiService.getInstance()
    private lateinit var authManager: AuthManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        authManager = AuthManager(this)
        
        setupUI()
        setupClickListeners()
    }
    
    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Join Room"
        }
    }
    
    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        
        binding.btnJoinRoom.setOnClickListener {
            val code = binding.etRoomCode.text.toString().trim()
            
            if (code.isEmpty()) {
                binding.etRoomCode.error = "Kode room tidak boleh kosong"
                return@setOnClickListener
            }
            
            if (code.length != 6) {
                binding.etRoomCode.error = "Kode room harus 6 karakter"
                return@setOnClickListener
            }
            
            joinRoom(code)
        }
    }
    
    private fun joinRoom(code: String) {
        val authToken = authManager.authToken
        
        if (authToken.isEmpty()) {
            Toast.makeText(this, "Anda harus login terlebih dahulu", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        // Show loading
        binding.btnJoinRoom.isEnabled = false
        binding.btnJoinRoom.text = "Bergabung..."
        
        lifecycleScope.launch {
            val result = apiService.joinRoom(code.uppercase(), authToken)
            
            result.onSuccess { response ->
                Toast.makeText(
                    this@JoinRoomActivity,
                    "Berhasil bergabung ke ${response.room.name}",
                    Toast.LENGTH_LONG
                ).show()
                
                // Return to previous activity
                setResult(RESULT_OK)
                finish()
            }.onFailure { error ->
                val errorMessage = when {
                    error.message?.contains("404") == true -> "Kode room tidak ditemukan"
                    error.message?.contains("400") == true -> "Anda sudah terdaftar di room ini"
                    else -> "Gagal bergabung: ${error.message}"
                }
                
                Toast.makeText(this@JoinRoomActivity, errorMessage, Toast.LENGTH_LONG).show()
                
                // Reset button
                binding.btnJoinRoom.isEnabled = true
                binding.btnJoinRoom.text = "Bergabung"
            }
        }
    }
}
