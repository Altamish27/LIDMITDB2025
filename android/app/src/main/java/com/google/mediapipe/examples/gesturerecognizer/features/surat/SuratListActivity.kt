package com.google.mediapipe.examples.gesturerecognizer.features.surat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.gesturerecognizer.R

class SuratListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surat_list)

        val rv = findViewById<RecyclerView>(R.id.rv_surat_list)
        rv.layoutManager = LinearLayoutManager(this)

        // Minimal list: Al-Fatihah + 10 contoh juz 30 (contoh entries)
        val items = mutableListOf<String>()
        items.add("Al-Fatihah")
        for (i in 1..10) items.add("Contoh Surat Juz 30 - $i")

        rv.adapter = SuratAdapter(items) { title ->
            val intent = Intent(this, SuratDetailActivity::class.java)
            intent.putExtra("surat_title", title)
            startActivity(intent)
        }
    }

    class SuratAdapter(private val items: List<String>, private val onClick: (String) -> Unit) : RecyclerView.Adapter<SuratAdapter.VH>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_surat, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val title = items[position]
            holder.tv.text = title
            holder.itemView.setOnClickListener { onClick(title) }
        }

        override fun getItemCount(): Int = items.size

        class VH(view: View) : RecyclerView.ViewHolder(view) {
            val tv: TextView = view.findViewById(R.id.tv_item_surat_title)
        }
    }
}
