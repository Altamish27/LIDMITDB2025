package com.google.mediapipe.examples.gesturerecognizer.features.room

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.mediapipe.examples.gesturerecognizer.data.api.SignQuranApiService
import com.google.mediapipe.examples.gesturerecognizer.data.manager.AuthManager
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityRoomDetailBinding
import kotlinx.coroutines.launch

class RoomDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityRoomDetailBinding
    private val apiService = SignQuranApiService.getInstance()
    private lateinit var authManager: AuthManager
    private lateinit var membersAdapter: RoomMembersAdapter
    private var roomId: Int = 0
    private var roomName: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        authManager = AuthManager(this)
        
        roomId = intent.getIntExtra("room_id", 0)
        roomName = intent.getStringExtra("room_name") ?: "Room Detail"
        
        setupUI()
        setupRecyclerView()
        loadRoomMembers()
    }
    
    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = roomName
        }
        
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        membersAdapter = RoomMembersAdapter()
        
        binding.rvMembers.apply {
            layoutManager = LinearLayoutManager(this@RoomDetailActivity)
            adapter = membersAdapter
        }
    }
    
    private fun loadRoomMembers() {
        val authToken = authManager.authToken
        
        if (authToken.isEmpty()) {
            Toast.makeText(this, "Anda harus login terlebih dahulu", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            val result = apiService.getRoomMembers(roomId, authToken)
            
            binding.progressBar.visibility = View.GONE
            
            result.onSuccess { response ->
                val guru = response.members.filter { it.role == "guru" }
                val murid = response.members.filter { it.role == "murid" }
                
                binding.tvGuruCount.text = "${guru.size} Guru"
                binding.tvMuridCount.text = "${murid.size} Murid"
                binding.tvTotalMembers.text = "Total ${response.members.size} anggota"
                
                membersAdapter.submitList(response.members)
            }.onFailure { error ->
                Toast.makeText(
                    this@RoomDetailActivity,
                    "Gagal memuat anggota: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
