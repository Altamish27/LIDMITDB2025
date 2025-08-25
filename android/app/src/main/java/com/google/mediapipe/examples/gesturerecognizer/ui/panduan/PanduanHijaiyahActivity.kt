package com.google.mediapipe.examples.gesturerecognizer.ui.panduan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahData
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityPanduanHijaiyahBinding
import com.google.mediapipe.examples.gesturerecognizer.ui.adapter.PanduanHijaiyahAdapter

class PanduanHijaiyahActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPanduanHijaiyahBinding
    private lateinit var adapter: PanduanHijaiyahAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPanduanHijaiyahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = PanduanHijaiyahAdapter(HijaiyahData.letters)
        binding.rvPanduanHijaiyah.apply {
            layoutManager = LinearLayoutManager(this@PanduanHijaiyahActivity)
            adapter = this@PanduanHijaiyahActivity.adapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
