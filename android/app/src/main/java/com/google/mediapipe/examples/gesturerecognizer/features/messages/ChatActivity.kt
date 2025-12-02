package com.google.mediapipe.examples.gesturerecognizer.features.messages

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
import com.google.mediapipe.examples.gesturerecognizer.data.models.Message
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityChatBinding
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityChatBinding
    private val apiService = MessagesApiService.getInstance()
    private lateinit var authManager: AuthManager
    private lateinit var messagesAdapter: MessagesAdapter
    
    private var otherUserId: Int = 0
    private var otherUserName: String = ""
    private var currentUserId: Int = 0
    private var isSending: Boolean = false
    
    // Polling untuk new messages
    private val handler = Handler(Looper.getMainLooper())
    private val pollRunnable = object : Runnable {
        override fun run() {
            fetchMessages(silent = true)
            handler.postDelayed(this, 5000) // Poll every 5 seconds
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        authManager = AuthManager(this)
        currentUserId = authManager.userId.toIntOrNull() ?: 0
        
        // Get data from intent
        otherUserId = intent.getIntExtra("user_id", 0)
        otherUserName = intent.getStringExtra("user_name") ?: "User"
        val otherUserRole = intent.getStringExtra("user_role") ?: ""
        
        if (otherUserId == 0) {
            Toast.makeText(this, "User ID tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        setupUI(otherUserRole)
        setupRecyclerView()
        setupClickListeners()
        fetchMessages()
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
    
    private fun setupUI(role: String) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = otherUserName
            subtitle = if (role == "guru") "Guru" else "Murid"
        }
    }
    
    private fun setupRecyclerView() {
        messagesAdapter = MessagesAdapter(currentUserId)
        
        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true // Start from bottom
            }
            adapter = messagesAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        
        binding.btnSend.setOnClickListener {
            sendMessage()
        }
        
        // Enable/disable send button based on input
        binding.etMessage.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnSend.isEnabled = !s.isNullOrBlank() && !isSending
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }
    
    private fun fetchMessages(silent: Boolean = false) {
        val authToken = authManager.authToken
        
        if (authToken.isEmpty()) {
            Toast.makeText(this, "Anda harus login terlebih dahulu", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        if (!silent) {
            binding.progressBar.visibility = View.VISIBLE
        }
        
        lifecycleScope.launch {
            val result = apiService.getMessages(authToken, otherUserId)
            
            if (!silent) {
                binding.progressBar.visibility = View.GONE
            }
            
            result.onSuccess { response ->
                // Convert messages to items with date dividers
                val items = MessagesAdapter.messagesToItems(response.messages)
                messagesAdapter.submitList(items) {
                    // Scroll to bottom after list is updated
                    if (items.isNotEmpty()) {
                        binding.rvMessages.scrollToPosition(items.size - 1)
                    }
                }
                
                // Mark conversation as read
                markConversationAsRead(authToken)
            }.onFailure { error ->
                if (!silent) {
                    Toast.makeText(
                        this@ChatActivity,
                        "Gagal memuat pesan: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
    
    private fun sendMessage() {
        val messageText = binding.etMessage.text.toString().trim()
        if (messageText.isEmpty() || isSending) return
        
        val authToken = authManager.authToken
        if (authToken.isEmpty()) {
            Toast.makeText(this, "Anda harus login terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }
        
        isSending = true
        binding.btnSend.isEnabled = false
        
        lifecycleScope.launch {
            val result = apiService.sendMessage(authToken, otherUserId, messageText)
            
            isSending = false
            binding.btnSend.isEnabled = binding.etMessage.text.isNotBlank()
            
            result.onSuccess { response ->
                // Clear input
                binding.etMessage.text.clear()
                
                // Refresh messages to show the new one
                fetchMessages(silent = true)
            }.onFailure { error ->
                Toast.makeText(
                    this@ChatActivity,
                    "Gagal mengirim pesan: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    private fun markConversationAsRead(authToken: String) {
        lifecycleScope.launch {
            apiService.markConversationAsRead(authToken, otherUserId)
            // Silently mark as read, no need to handle response
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
