package com.google.mediapipe.examples.gesturerecognizer.features.panduan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahData
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityPanduanHijaiyahBinding

class PanduanHijaiyahActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPanduanHijaiyahBinding
    private lateinit var adapter: HijaiyahGridAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Hide action bar
        supportActionBar?.hide()
        
        binding = ActivityPanduanHijaiyahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = HijaiyahGridAdapter(HijaiyahData.letters)
        binding.rvPanduanHijaiyah.apply {
            layoutManager = GridLayoutManager(this@PanduanHijaiyahActivity, 3) // 3 columns
            adapter = this@PanduanHijaiyahActivity.adapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
