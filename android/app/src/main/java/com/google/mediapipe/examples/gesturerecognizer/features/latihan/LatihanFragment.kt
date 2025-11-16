package com.google.mediapipe.examples.gesturerecognizer.features.latihan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
        
        // Load pages from API
        loadPagesFromApi()
    }

    private fun loadPagesFromApi() {
        lifecycleScope.launch {
            try {
                binding.tvSubtitle?.text = "Memuat halaman..."
                
                val apiService = SignQuranApiService.getInstance()
                val authManager = AuthManager(requireContext())
                val token = if (authManager.isLoggedIn) authManager.authToken else null
                
                val result = apiService.getJilidPages(jilidId, token)
                
                result.onSuccess { response ->
                    availablePages = response.pages
                    android.util.Log.d("LatihanFragment", "Loaded ${availablePages.size} pages")
                    
                    // Setup click listeners with real data
                    setupClickListeners()
                    updateProgress()
                    
                    binding.tvSubtitle?.text = "${availablePages.size} Halaman Tersedia"
                }
                
                result.onFailure { error ->
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
        // Setup untuk 5 halaman pertama (sesuai layout)
        if (availablePages.isNotEmpty()) {
            // Halaman 1
            if (availablePages.size >= 1) {
                val page1 = availablePages[0]
                binding.cardHalaman1.setOnClickListener {
                    navigateToHalaman(page1.halamanId, page1.nomorHalaman, "Halaman ${page1.nomorHalaman}", page1.deskripsi)
                }
                // Update deskripsi jika ada field untuk itu di layout
                // Untuk saat ini kita skip update deskripsi ke UI karena layout belum punya TextView-nya
            } else {
                binding.cardHalaman1.setOnClickListener {
                    showLockedMessage()
                }
            }
            
            // Halaman 2
            if (availablePages.size >= 2) {
                val page2 = availablePages[1]
                binding.cardHalaman2.setOnClickListener {
                    navigateToHalaman(page2.halamanId, page2.nomorHalaman, "Halaman ${page2.nomorHalaman}", page2.deskripsi)
                }
            } else {
                binding.cardHalaman2.setOnClickListener {
                    showLockedMessage()
                }
            }
            
            // Halaman 3
            if (availablePages.size >= 3) {
                val page3 = availablePages[2]
                binding.cardHalaman3.setOnClickListener {
                    navigateToHalaman(page3.halamanId, page3.nomorHalaman, "Halaman ${page3.nomorHalaman}", page3.deskripsi)
                }
            } else {
                binding.cardHalaman3.setOnClickListener {
                    showLockedMessage()
                }
            }
            
            // Halaman 4
            if (availablePages.size >= 4) {
                val page4 = availablePages[3]
                binding.cardHalaman4.setOnClickListener {
                    navigateToHalaman(page4.halamanId, page4.nomorHalaman, "Halaman ${page4.nomorHalaman}", page4.deskripsi)
                }
            } else {
                binding.cardHalaman4.setOnClickListener {
                    showLockedMessage()
                }
            }
            
            // Halaman 5
            if (availablePages.size >= 5) {
                val page5 = availablePages[4]
                binding.cardHalaman5.setOnClickListener {
                    navigateToHalaman(page5.halamanId, page5.nomorHalaman, "Halaman ${page5.nomorHalaman}", page5.deskripsi)
                }
            } else {
                binding.cardHalaman5.setOnClickListener {
                    showLockedMessage()
                }
            }
        } else {
            // Fallback hardcoded jika API belum load atau error
            binding.cardHalaman1.setOnClickListener {
                navigateToHalaman("$jilidId-1", 1, "Halaman 1", "Pengenalan Huruf Hijaiyah Dasar")
            }
            
            binding.cardHalaman2.setOnClickListener {
                showLockedMessage()
            }
            
            binding.cardHalaman3.setOnClickListener {
                showLockedMessage()
            }
            
            binding.cardHalaman4.setOnClickListener {
                showLockedMessage()
            }
            
            binding.cardHalaman5.setOnClickListener {
                showLockedMessage()
            }
        }
    }
    
    private fun navigateToHalaman(halamanId: String, nomorHalaman: Int, title: String, description: String) {
        val intent = Intent(requireContext(), LatihanPracticeActivity::class.java).apply {
            putExtra("exerciseId", jilidId)
            putExtra("exerciseTitle", jilidTitle)
            putExtra("jilidId", jilidId)
            putExtra("halamanId", nomorHalaman)
            putExtra("realHalamanId", halamanId)  // Pass the real halaman_id like "1-1"
        }
        startActivity(intent)
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
        val completedCount = 0 // TODO: implement progress tracking from API
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
