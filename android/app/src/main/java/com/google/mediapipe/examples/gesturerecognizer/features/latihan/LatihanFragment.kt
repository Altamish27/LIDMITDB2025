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
    private lateinit var halamanAdapter: HalamanListAdapter

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
        binding.rvHalamanPages.apply {
            visibility = View.GONE
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = halamanAdapter
        }
    }

    private fun loadPagesFromApi() {
        lifecycleScope.launch {
            try {
                setLoadingState(true)
                binding.tvSubtitle?.text = "Memuat halaman..."
                binding.tvEmptyState.visibility = View.GONE
                
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
                    
                    updateProgress()
                    
                    val completedCount = availablePages.count { it.isCompleted }
                    binding.tvSubtitle?.text = "$completedCount / ${availablePages.size} Halaman Selesai"
                    
                    if (availablePages.isEmpty()) {
                        showEmptyState("Halaman latihan belum tersedia.")
                    } else {
                        binding.tvEmptyState.visibility = View.GONE
                        binding.rvHalamanPages.visibility = View.VISIBLE
                        halamanAdapter.submitList(availablePages)
                    }
                    
                    setLoadingState(false)
                }
                
                pagesResult.onFailure { error ->
                    android.util.Log.e("LatihanFragment", "Error loading pages: ${error.message}")
                    binding.tvSubtitle?.text = "Gagal memuat halaman"
                    showEmptyState("Gagal memuat halaman.\nTarik untuk memuat ulang.")
                    setLoadingState(false)
                }
            } catch (e: Exception) {
                android.util.Log.e("LatihanFragment", "Exception loading pages: ${e.message}", e)
                binding.tvSubtitle?.text = "Error"
                showEmptyState("Terjadi kesalahan.\nSilakan coba lagi.")
                setLoadingState(false)
            }
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
    
    private fun setLoadingState(isLoading: Boolean) {
        binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        if (isLoading) {
            binding.rvHalamanPages.visibility = View.GONE
        }
    }

    private fun showEmptyState(message: String) {
        binding.rvHalamanPages.visibility = View.GONE
        binding.tvEmptyState.apply {
            text = message
            visibility = View.VISIBLE
        }
    }

    private fun updateProgress() {
        val totalPages = availablePages.size.takeIf { it > 0 } ?: 0
        val completedCount = availablePages.count { it.isCompleted }
        val percentage = if (totalPages > 0) (completedCount * 100) / totalPages else 0
        
        binding.tvProgress.text = "$completedCount / $totalPages Halaman"
        binding.progressBar.progress = percentage
    }

    override fun onResume() {
        super.onResume()
        // Reload data from API to refresh completion status
        loadPagesFromApi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
