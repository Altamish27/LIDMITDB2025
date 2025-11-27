package com.google.mediapipe.examples.gesturerecognizer.features.panduan

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahLetter
import com.google.mediapipe.examples.gesturerecognizer.features.praga.PragaActivity

class HijaiyahGridAdapter(
    private val letters: List<HijaiyahLetter>
) : RecyclerView.Adapter<HijaiyahGridAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_huruf_hijaiyah, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val letter = letters[position]
        holder.bind(letter)
    }

    override fun getItemCount(): Int = letters.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvHuruf: TextView = view.findViewById(R.id.tv_huruf)
        private val tvGesture: TextView = view.findViewById(R.id.tv_gesture)
        private val tvNamaLatin: TextView = view.findViewById(R.id.tv_nama_latin)

        fun bind(letter: HijaiyahLetter) {
            tvHuruf.text = letter.arabic
            tvGesture.text = letter.arabic // Menggunakan huruf Arab untuk ditampilkan dengan font gesture
            tvNamaLatin.text = letter.transliteration

            itemView.setOnClickListener {
                // Buka halaman Praga dengan data huruf ini
                val context = itemView.context
                val intent = Intent(context, PragaActivity::class.java).apply {
                    putExtra("huruf_arab", letter.arabic)
                    putExtra("huruf_latin", letter.transliteration)
                    putExtra("gesture_name", letter.gestureName ?: "")
                }
                context.startActivity(intent)
            }
        }
    }
}
