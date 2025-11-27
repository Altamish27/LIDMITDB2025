package com.google.mediapipe.examples.gesturerecognizer.features.surat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.data.QuranData
import com.google.mediapipe.examples.gesturerecognizer.data.model.Ayat
import com.google.mediapipe.examples.gesturerecognizer.data.model.Surat

class SuratDetailActivity : AppCompatActivity() {
    
    private lateinit var currentSurat: Surat
    private val allSuratList = QuranData.getAllSuratJuz30()
    private var currentIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surat_detail)

        // Hide action bar
        supportActionBar?.hide()

        // Get surat data
        val suratNomor = intent.getIntExtra("surat_nomor", 30) // Default Al-Maun (nomor 30 dalam list)
        currentSurat = allSuratList.find { it.nomor == suratNomor } ?: allSuratList[29] // Default Al-Maun

        // Find current index in list
        currentIndex = allSuratList.indexOfFirst { it.nomor == currentSurat.nomor }

        setupViews()
        setupRecyclerView()
        setupNavigation()
    }

    private fun setupViews() {
        // Back button
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        // Header info
        findViewById<TextView>(R.id.tv_surat_title).text = currentSurat.nama
        findViewById<TextView>(R.id.tv_surat_title_arab).text = currentSurat.namaArab
        findViewById<TextView>(R.id.tv_surat_info).text = currentSurat.tempatTurunIndonesia
        findViewById<TextView>(R.id.tv_surat_sub).text = "${currentSurat.jumlahAyat} Ayat • Juz ${currentSurat.juz}"

        // Filter buttons - show previous and next surat names
        val btnFilterPrev = findViewById<TextView>(R.id.btn_filter_prev)
        val btnFilterCurrent = findViewById<TextView>(R.id.btn_filter_current)
        val btnFilterNext = findViewById<TextView>(R.id.btn_filter_next)

        btnFilterCurrent.text = currentSurat.nama

        if (currentIndex > 0) {
            btnFilterPrev.text = allSuratList[currentIndex - 1].nama
            btnFilterPrev.setOnClickListener {
                navigateToSurat(allSuratList[currentIndex - 1].nomor)
            }
        } else {
            btnFilterPrev.visibility = View.INVISIBLE
        }

        if (currentIndex < allSuratList.size - 1) {
            btnFilterNext.text = allSuratList[currentIndex + 1].nama
            btnFilterNext.setOnClickListener {
                navigateToSurat(allSuratList[currentIndex + 1].nomor)
            }
        } else {
            btnFilterNext.visibility = View.INVISIBLE
        }
    }

    private fun setupRecyclerView() {
        val rv = findViewById<RecyclerView>(R.id.rv_ayat_list)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = AyatAdapter(currentSurat.ayatList)
    }

    private fun setupNavigation() {
        val btnPrevious = findViewById<TextView>(R.id.btn_previous_surat)
        val btnNext = findViewById<TextView>(R.id.btn_next_surat)

        // Previous button
        if (currentIndex > 0) {
            btnPrevious.setOnClickListener {
                navigateToSurat(allSuratList[currentIndex - 1].nomor)
            }
        } else {
            btnPrevious.alpha = 0.5f
            btnPrevious.isEnabled = false
        }

        // Next button
        if (currentIndex < allSuratList.size - 1) {
            btnNext.setOnClickListener {
                navigateToSurat(allSuratList[currentIndex + 1].nomor)
            }
        } else {
            btnNext.alpha = 0.5f
            btnNext.isEnabled = false
        }
    }

    private fun navigateToSurat(suratNomor: Int) {
        val intent = Intent(this, SuratDetailActivity::class.java)
        intent.putExtra("surat_nomor", suratNomor)
        startActivity(intent)
        finish() // Close current activity to prevent stack buildup
    }

    inner class AyatAdapter(private val items: List<Ayat>) : RecyclerView.Adapter<AyatAdapter.VH>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_ayat, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val ayat = items[position]
            holder.tvNumber.text = ayat.nomor.toString()
            holder.tvLabel.text = "Ayat ${ayat.nomor}"
            holder.tvArab.text = ayat.teksArab
            holder.tvTerjemah.text = ayat.terjemahan
            
            // Generate isyarat representation (simplified - show Arabic with lighter style)
            val isyaratText = generateIsyaratText(ayat.teksArab)
            if (isyaratText.isNotEmpty()) {
                holder.tvIsyarat.text = isyaratText
                holder.tvIsyarat.visibility = View.VISIBLE
            } else {
                holder.tvIsyarat.visibility = View.GONE
            }
            
            // Hide check icon by default
            holder.ivCheck.visibility = View.GONE
        }

        override fun getItemCount(): Int = items.size

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val tvNumber: TextView = view.findViewById(R.id.tv_ayat_number)
            val tvLabel: TextView = view.findViewById(R.id.tv_ayat_label)
            val tvArab: TextView = view.findViewById(R.id.tv_arab)
            val tvTerjemah: TextView = view.findViewById(R.id.tv_terjemahan)
            val tvIsyarat: TextView = view.findViewById(R.id.tv_isyarat)
            val ivCheck: ImageView = view.findViewById(R.id.iv_check)
        }
    }

    private fun generateIsyaratText(arabicText: String): String {
        // Map of common Arabic letters to hijaiyah representations
        val gestureMap = mapOf(
            'ا' to "ا", 'أ' to "ا", 'إ' to "ا", 'آ' to "ا",
            'ب' to "ب", 'ت' to "ت", 'ث' to "ث", 'ج' to "ج",
            'ح' to "ح", 'خ' to "خ", 'د' to "د", 'ذ' to "ذ", 
            'ر' to "ر", 'ز' to "ز", 'س' to "س", 'ش' to "ش", 
            'ص' to "ص", 'ض' to "ض", 'ط' to "ط", 'ظ' to "ظ", 
            'ع' to "ع", 'غ' to "غ", 'ف' to "ف", 'ق' to "ق", 
            'ك' to "ك", 'ل' to "ل", 'م' to "م", 'ن' to "ن",
            'و' to "و", 'ؤ' to "و", 'ه' to "ه", 'ة' to "ه",
            'ي' to "ي", 'ئ' to "ي", 'ى' to "ي"
        )

        // Remove diacritics and normalize
        val normalized = arabicText.replace(Regex("[\u064B-\u065F\u0610-\u061A\u06D6-\u06ED]"), "")
        
        val isyaratBuilder = StringBuilder()
        for (ch in normalized) {
            val glyph = gestureMap[ch]
            if (glyph != null) {
                isyaratBuilder.append(glyph)
                isyaratBuilder.append(' ')
            }
        }
        
        return if (isyaratBuilder.isNotEmpty()) {
            isyaratBuilder.toString().trim()
        } else {
            ""
        }
    }
}
