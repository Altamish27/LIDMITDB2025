package com.google.mediapipe.examples.gesturerecognizer.features.room

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.mediapipe.examples.gesturerecognizer.data.api.SignQuranApiService
import com.google.mediapipe.examples.gesturerecognizer.data.manager.AuthManager
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityMyRoomsBinding
import kotlinx.coroutines.launch

class MyRoomsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMyRoomsBinding
    private val apiService = SignQuranApiService.getInstance()
    private lateinit var authManager: AuthManager
    private lateinit var roomsAdapter: MyRoomsAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyRoomsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        authManager = AuthManager(this)
        
        setupUI()
        setupRecyclerView()
        setupClickListeners()
        loadMyRooms()
    }
    
    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Room Saya"
        }
    }
    
    private fun setupRecyclerView() {
        roomsAdapter = MyRoomsAdapter { room ->
            // Click room untuk melihat detail
            val intent = Intent(this, RoomDetailActivity::class.java)
            intent.putExtra("room_id", room.roomId)
            intent.putExtra("room_name", room.name)
            val identifier = room.code ?: ""
            intent.putExtra("room_code", identifier)
            intent.putExtra("room_creator_name", room.createdByName)
            startActivity(intent)
        }
        
        binding.rvRooms.apply {
            layoutManager = LinearLayoutManager(this@MyRoomsActivity)
            adapter = roomsAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        
        binding.fabJoinRoom.setOnClickListener {
            val intent = Intent(this, JoinRoomActivity::class.java)
            startActivityForResult(intent, REQUEST_JOIN_ROOM)
        }
    }
    
    private fun loadMyRooms() {
        val authToken = authManager.authToken
        
        if (authToken.isEmpty()) {
            Toast.makeText(this, "Anda harus login terlebih dahulu", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        android.util.Log.d("MyRoomsActivity", "Loading rooms with token: ${authToken.take(20)}...")
        
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE
        
        lifecycleScope.launch {
            val result = apiService.getMyRooms(authToken)
            
            binding.progressBar.visibility = View.GONE
            
            result.onSuccess { response ->
                android.util.Log.d("MyRoomsActivity", "Rooms loaded successfully: ${response.rooms.size} rooms")
                response.rooms.forEach { room ->
                    val displayCode = room.code ?: "-"
                    android.util.Log.d("MyRoomsActivity", "Room: ${room.name} ($displayCode)")
                }
                
                if (response.rooms.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.rvRooms.visibility = View.GONE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvRooms.visibility = View.VISIBLE
                    roomsAdapter.submitList(response.rooms)
                }
            }.onFailure { error ->
                android.util.Log.e("MyRoomsActivity", "Failed to load rooms", error)
                android.util.Log.e("MyRoomsActivity", "Error message: ${error.message}")
                android.util.Log.e("MyRoomsActivity", "Error stack trace:", error)
                
                // Check if it's a token error
                val errorMessage = error.message ?: ""
                if (errorMessage.contains("Invalid token", ignoreCase = true) || 
                    errorMessage.contains("Token expired", ignoreCase = true) ||
                    errorMessage.contains("Unauthorized", ignoreCase = true)) {
                    
                    Toast.makeText(
                        this@MyRoomsActivity,
                        "Sesi anda telah berakhir. Silakan login kembali.",
                        Toast.LENGTH_LONG
                    ).show()
                    
                    // Clear auth and redirect to login
                    authManager.clearAuthData()
                    val intent = Intent(this@MyRoomsActivity, Class.forName("com.google.mediapipe.examples.gesturerecognizer.AuthActivity"))
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@MyRoomsActivity,
                        "Gagal memuat room: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.tvEmpty.text = "Error: ${error.message}"
                }
            }
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_JOIN_ROOM && resultCode == RESULT_OK) {
            // Reload rooms setelah join room baru
            loadMyRooms()
        }
    }
    
    companion object {
        private const val REQUEST_JOIN_ROOM = 100
    }
}
