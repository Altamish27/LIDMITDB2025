package com.google.mediapipe.examples.gesturerecognizer.features.messages

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.mediapipe.examples.gesturerecognizer.data.api.MessagesApiService
import com.google.mediapipe.examples.gesturerecognizer.data.manager.AuthManager
import com.google.mediapipe.examples.gesturerecognizer.data.models.Conversation
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityMessagesListBinding
import kotlinx.coroutines.launch

class MessagesListActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMessagesListBinding
    private val apiService = MessagesApiService.getInstance()
    private lateinit var authManager: AuthManager
    private lateinit var conversationsAdapter: ConversationsAdapter
    
    // Polling untuk unread count
    private val handler = Handler(Looper.getMainLooper())
    private val pollRunnable = object : Runnable {
        override fun run() {
            loadConversations()
            handler.postDelayed(this, 30000) // Poll every 30 seconds
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessagesListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        authManager = AuthManager(this)
        
        setupUI()
        setupRecyclerView()
        setupClickListeners()
        loadConversations()
    }
    
    override fun onResume() {
        super.onResume()
        // Start polling when activity is visible
        handler.post(pollRunnable)
    }
    
    override fun onPause() {
        super.onPause()
        // Stop polling when activity is not visible
        handler.removeCallbacks(pollRunnable)
    }
    
    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Pesan"
        }
    }
    
    private fun setupRecyclerView() {
        conversationsAdapter = ConversationsAdapter { conversation ->
            // Navigate to chat activity
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("user_id", conversation.userId)
            intent.putExtra("user_name", conversation.name)
            intent.putExtra("user_role", conversation.role)
            startActivity(intent)
        }
        
        binding.rvConversations.apply {
            layoutManager = LinearLayoutManager(this@MessagesListActivity)
            adapter = conversationsAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        
        binding.swipeRefresh.setOnRefreshListener {
            loadConversations()
        }
    }
    
    private fun loadConversations() {
        val authToken = authManager.authToken
        
        if (authToken.isEmpty()) {
            Toast.makeText(this, "Anda harus login terlebih dahulu", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        // Only show progress bar if not refreshing
        if (!binding.swipeRefresh.isRefreshing) {
            binding.progressBar.visibility = View.VISIBLE
        }
        binding.tvEmpty.visibility = View.GONE
        
        lifecycleScope.launch {
            val result = apiService.getConversations(authToken)
            
            binding.progressBar.visibility = View.GONE
            binding.swipeRefresh.isRefreshing = false
            
            result.onSuccess { response ->
                if (response.conversations.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.rvConversations.visibility = View.GONE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvConversations.visibility = View.VISIBLE
                    conversationsAdapter.submitList(response.conversations)
                }
            }.onFailure { error ->
                Toast.makeText(
                    this@MessagesListActivity,
                    "Gagal memuat pesan: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
                binding.tvEmpty.visibility = View.VISIBLE
                binding.tvEmpty.text = "Error: ${error.message}"
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
