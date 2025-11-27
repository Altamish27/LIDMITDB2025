package com.google.mediapipe.examples.gesturerecognizer.features.latihan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.mediapipe.examples.gesturerecognizer.databinding.FragmentLatihanBinding
import com.google.mediapipe.examples.gesturerecognizer.data.api.SignQuranApiService
import com.google.mediapipe.examples.gesturerecognizer.data.manager.AuthManager
import com.google.mediapipe.examples.gesturerecognizer.data.models.HalamanInfo
import kotlinx.coroutines.launch

class LatihanFragment : Fragment() {

    private var _binding: FragmentLatihanBinding? = null
    private val binding get() = _binding!!
    
    private var jilidId: Int = 1
    private var jilidTitle: String = "Jilid 1"
    private var availablePages: List<HalamanInfo> = emptyList()
    
    // Dynamic adapter for pages
    private lateinit var halamanAdapter: HalamanListAdapter
    private var useDynamicAdapter = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLatihanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Get arguments
        jilidId = arguments?.getInt("jilidId", 1) ?: 1
        jilidTitle = arguments?.getString("jilidTitle", "Jilid 1") ?: "Jilid 1"
        
        // Update UI with jilid info
        binding.tvTitle.text = jilidTitle
        
        // Setup dynamic adapter
        setupHalamanAdapter()
        
        // Load pages from API
        loadPagesFromApi()
    }
    
    private fun setupHalamanAdapter() {
        halamanAdapter = HalamanListAdapter { halaman ->
            navigateToHalaman(halaman.halamanId.toString(), halaman.nomorHalaman, "Halaman ${halaman.nomorHalaman}", halaman.deskripsi)
        }
        
        // Check if we have a RecyclerView in the layout (for future dynamic implementation)
        try {
            val recyclerView = binding.rvHalamanPages
            useDynamicAdapter = true
            recyclerView.visibility = View.VISIBLE
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // 2 columns
            recyclerView.adapter = halamanAdapter
            android.util.Log.d("LatihanFragment", "Using dynamic RecyclerView adapter")
        } catch (e: Exception) {
            useDynamicAdapter = false
            android.util.Log.d("LatihanFragment", "RecyclerView not available, using hardcoded cards")
        }
    }

    private fun loadPagesFromApi() {
        lifecycleScope.launch {
            try {
                binding.tvSubtitle?.text = "Memuat halaman..."
                
                val apiService = SignQuranApiService.getInstance()
                val authManager = AuthManager(requireContext())
                val token = if (authManager.isLoggedIn) authManager.authToken else null
                
                // Load pages
                val pagesResult = apiService.getJilidPages(jilidId, token)
                
                pagesResult.onSuccess { response ->
                    availablePages = response.pages.toMutableList()
                    android.util.Log.d("LatihanFragment", "Loaded ${availablePages.size} pages")
                    
                    // Load progress if user is logged in
                    if (authManager.isLoggedIn && !token.isNullOrEmpty()) {
                        android.util.Log.d("LatihanFragment", "Loading progress for jilid $jilidId")
                        
                        val progressResult = apiService.getJilidProgress(jilidId, token)
                        progressResult.onSuccess { progressResponse ->
                            android.util.Log.d("LatihanFragment", "Progress loaded: ${progressResponse.progress.size} completed pages")
                            
                            // Create a map of halaman_id -> completed status
                            val progressMap = progressResponse.progress.associate { 
                                it.halamanId to it.completed 
                            }
                            
                            // Update pages with completion status
                            availablePages.forEach { page ->
                                page.isCompleted = progressMap[page.halamanId] ?: false
                                if (page.isCompleted) {
                                    android.util.Log.d("LatihanFragment", "Page ${page.halamanId} is completed")
                                }
                            }
                        }
                    }
                    
                    // Setup click listeners with real data
                    setupClickListeners()
                    updateProgress()
                    
                    val completedCount = availablePages.count { it.isCompleted }
                    binding.tvSubtitle?.text = "$completedCount / ${availablePages.size} Halaman Selesai"
                    
                    // Update adapter if using dynamic display
                    if (useDynamicAdapter) {
                        halamanAdapter.submitList(availablePages)
                        // Hide hardcoded cards if using RecyclerView
                        hideHardcodedCards()
                    }
                }
                
                pagesResult.onFailure { error ->
                    android.util.Log.e("LatihanFragment", "Error loading pages: ${error.message}")
                    binding.tvSubtitle?.text = "Gagal memuat halaman"
                    
                    // Fallback to hardcoded setup
                    setupClickListeners()
                }
            } catch (e: Exception) {
                android.util.Log.e("LatihanFragment", "Exception loading pages: ${e.message}", e)
                binding.tvSubtitle?.text = "Error"
            }
        }
    }

    private fun setupClickListeners() {
        // If using dynamic adapter, no need to setup hardcoded cards
        if (useDynamicAdapter) {
            return
        }
        
        // Fallback: Setup untuk hardcoded cards (maksimal 5 halaman sesuai layout)
        setupHardcodedCards()
    }
    
    private fun hideHardcodedCards() {
        // Hide the hardcoded cards when using RecyclerView
        try {
            binding.cardHalaman1?.visibility = View.GONE
            binding.cardHalaman2?.visibility = View.GONE
            binding.cardHalaman3?.visibility = View.GONE
            binding.cardHalaman4?.visibility = View.GONE
            binding.cardHalaman5?.visibility = View.GONE
        } catch (e: Exception) {
            // Ignore if cards don't exist
        }
    }
    
    private fun setupHardcodedCards() {
        val maxDisplayPages = 5 // Layout limitation
        
        for (i in 0 until maxDisplayPages) {
            val cardView = when (i) {
                0 -> binding.cardHalaman1
                1 -> binding.cardHalaman2
                2 -> binding.cardHalaman3
                3 -> binding.cardHalaman4
                4 -> binding.cardHalaman5
                else -> null
            }
            
            val descTextView = when (i) {
                0 -> binding.tvDescHalaman1
                1 -> binding.tvDescHalaman2
                2 -> binding.tvDescHalaman3
                3 -> binding.tvDescHalaman4
                4 -> binding.tvDescHalaman5
                else -> null
            }
            
            if (cardView != null && descTextView != null) {
                if (i < availablePages.size) {
                    val page = availablePages[i]
                    descTextView.text = page.deskripsi
                    cardView.setOnClickListener {
                        navigateToHalaman(page.halamanId.toString(), page.nomorHalaman, "Halaman ${page.nomorHalaman}", page.deskripsi)
                    }
                    
                    // Update card color if completed
                    if (page.isCompleted) {
                        cardView.setCardBackgroundColor(
                            android.graphics.Color.parseColor("#4CAF50") // Green for completed
                        )
                        android.util.Log.d("LatihanFragment", "✓ Halaman ${i + 1} marked as completed")
                    } else {
                        // Reset to default color
                        cardView.setCardBackgroundColor(
                            android.graphics.Color.parseColor("#FFFFFF") // White for uncompleted
                        )
                    }
                } else {
                    // No more pages available, disable card
                    descTextView.text = "Halaman belum tersedia"
                    cardView.setOnClickListener {
                        showLockedMessage()
                    }
                    cardView.setCardBackgroundColor(
                        android.graphics.Color.parseColor("#CCCCCC") // Gray for locked
                    )
                }
            }
        }
        
        // Show notification if there are more pages than can be displayed
        if (availablePages.size > maxDisplayPages) {
            android.util.Log.w("LatihanFragment", "Warning: ${availablePages.size} pages available, but only $maxDisplayPages can be displayed with current layout")
            // TODO: Show a message to user about additional pages
        }
    }
    
    private fun navigateToHalaman(halamanId: String, nomorHalaman: Int, title: String, description: String) {
        android.util.Log.d("LatihanFragment", "Navigating to halaman: halamanId=$halamanId, nomorHalaman=$nomorHalaman, title=$title")
        
        val intent = Intent(requireContext(), LatihanPracticeActivity::class.java).apply {
            putExtra("exerciseId", jilidId)
            putExtra("exerciseTitle", jilidTitle)
            putExtra("jilidId", jilidId)
            putExtra("halamanId", nomorHalaman)
            putExtra("realHalamanId", halamanId)  // Pass the real halaman_id like "1-1"
        }
        
        android.util.Log.d("LatihanFragment", "Intent extras: jilidId=$jilidId, halamanId=$nomorHalaman, realHalamanId=$halamanId")
        
        try {
            startActivity(intent)
        } catch (e: Exception) {
            android.util.Log.e("LatihanFragment", "Failed to start LatihanPracticeActivity: ${e.message}", e)
            Toast.makeText(requireContext(), "Gagal membuka halaman latihan: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showLockedMessage() {
        Toast.makeText(
            requireContext(),
            "Halaman ini belum tersedia atau masih terkunci",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateProgress() {
        val totalPages = availablePages.size.coerceAtLeast(5)
        val completedCount = availablePages.count { it.isCompleted }
        val percentage = if (totalPages > 0) (completedCount * 100) / totalPages else 0
        
        binding.tvProgress.text = "$completedCount / $totalPages Halaman"
        binding.progressBar.progress = percentage
    }

    override fun onResume() {
        super.onResume()
        updateProgress()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
