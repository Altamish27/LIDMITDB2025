package com.google.mediapipe.examples.gesturerecognizer.features.panduan

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahData
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahLetter
import com.google.mediapipe.examples.gesturerecognizer.databinding.ActivityPanduanHijaiyahBinding
import kotlinx.coroutines.launch

class PanduanHijaiyahActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPanduanHijaiyahBinding
    private lateinit var adapter: HijaiyahGridAdapter
    private var allLetters: List<HijaiyahLetter> = emptyList()
    private var currentCategory = 0 // 0 = hijaiyah dasar, 1 = fathah, 2 = kasrah, 3 = dhammah

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Hide action bar
        supportActionBar?.hide()
        
        binding = ActivityPanduanHijaiyahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupCategoryButtons()
        loadDataFromApi()
        setupClickListeners()
    }
    
    private fun loadDataFromApi() {
        lifecycleScope.launch {
            if (HijaiyahData.letters.isEmpty()) {
                HijaiyahData.loadFromApi(this@PanduanHijaiyahActivity)
            }
            allLetters = HijaiyahData.letters.sortedBy { it.position }
            applyCategoryFilter(forceUpdateTitle = true)
        }
    }

    private fun setupRecyclerView() {
        adapter = HijaiyahGridAdapter()
        binding.rvPanduanHijaiyah.apply {
            layoutManager = GridLayoutManager(this@PanduanHijaiyahActivity, 3) // 3 columns
            adapter = this@PanduanHijaiyahActivity.adapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupCategoryButtons() {
        setActiveCategoryButton(currentCategory)
        binding.btnCategoryHijaiyah.setOnClickListener { onCategorySelected(0) }
        binding.btnCategoryFathah.setOnClickListener { onCategorySelected(1) }
        binding.btnCategoryKasrah.setOnClickListener { onCategorySelected(2) }
        binding.btnCategoryDhommah.setOnClickListener { onCategorySelected(3) }
    }
    
    private fun onCategorySelected(category: Int) {
        if (currentCategory == category) return
        currentCategory = category
        setActiveCategoryButton(category)
        applyCategoryFilter(forceUpdateTitle = true)
    }
    
    private fun setActiveCategoryButton(category: Int) {
        setButtonState(binding.btnCategoryHijaiyah, category == 0)
        setButtonState(binding.btnCategoryFathah, category == 1)
        setButtonState(binding.btnCategoryKasrah, category == 2)
        setButtonState(binding.btnCategoryDhommah, category == 3)
    }
    
    private fun setButtonState(button: MaterialButton, isActive: Boolean) {
        if (isActive) {
            button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.hijaiyah_green))
            button.setTextColor(ContextCompat.getColor(this, R.color.white))
            button.strokeWidth = 0
        } else {
            button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
            button.setTextColor(ContextCompat.getColor(this, R.color.hijaiyah_navy))
            button.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.hijaiyah_green))
            button.strokeWidth = (2 * resources.displayMetrics.density).toInt()
        }
    }
    
    private fun applyCategoryFilter(forceUpdateTitle: Boolean = false) {
        if (allLetters.isEmpty()) {
            adapter.updateLetters(emptyList())
            return
        }
        
        val filtered = when (currentCategory) {
            1 -> allLetters.filter { it.diacritic == "fathah" }
            2 -> allLetters.filter { it.diacritic == "kasrah" }
            3 -> allLetters.filter { it.diacritic == "dhammah" }
            else -> allLetters.filter { it.diacritic.isNullOrEmpty() }
        }
        
        adapter.updateLetters(filtered)
        if (forceUpdateTitle) {
            binding.tvMulaiBelajar.text = when (currentCategory) {
                1 -> getString(R.string.title_panduan_fathah)
                2 -> getString(R.string.title_panduan_kasrah)
                3 -> getString(R.string.title_panduan_dhammah)
                else -> getString(R.string.title_panduan_hijaiyah_dasar)
            }
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
