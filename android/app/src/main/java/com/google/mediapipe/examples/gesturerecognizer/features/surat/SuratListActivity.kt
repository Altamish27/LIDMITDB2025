package com.google.mediapipe.examples.gesturerecognizer.features.surat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.data.QuranData
import com.google.mediapipe.examples.gesturerecognizer.data.model.Surat

class SuratListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surat_list)

        // Hide action bar
        supportActionBar?.hide()

        // Back button
        findViewById<ImageView>(R.id.btn_back_list).setOnClickListener {
            finish()
        }

        // Get surat list from data
        val suratList = QuranData.getAllSuratJuz30()

        // Set up RecyclerView
        val rv = findViewById<RecyclerView>(R.id.rv_surat_list)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = SuratAdapter(suratList) { surat ->
            // Navigate to detail
            val intent = Intent(this, SuratDetailActivity::class.java)
            intent.putExtra("surat_nomor", surat.nomor)
            startActivity(intent)
        }
    }

    class SuratAdapter(
        private val items: List<Surat>, 
        private val onClick: (Surat) -> Unit
    ) : RecyclerView.Adapter<SuratAdapter.VH>() {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_surat, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val surat = items[position]
            holder.tvTitle.text = surat.nama
            holder.tvArabic.text = surat.namaArab
            holder.tvInfo.text = "${surat.jumlahAyat} Ayat • ${surat.tempatTurunIndonesia}"
            holder.tvNumber.text = surat.nomor.toString()
            
            holder.itemView.setOnClickListener { onClick(surat) }
        }

        override fun getItemCount(): Int = items.size

        class VH(view: View) : RecyclerView.ViewHolder(view) {
            val tvTitle: TextView = view.findViewById(R.id.tv_item_surat_title)
            val tvArabic: TextView = view.findViewById(R.id.tv_item_surat_arabic)
            val tvInfo: TextView = view.findViewById(R.id.tv_item_surat_info)
            val tvNumber: TextView = view.findViewById(R.id.tv_item_surat_number)
        }
    }
}
