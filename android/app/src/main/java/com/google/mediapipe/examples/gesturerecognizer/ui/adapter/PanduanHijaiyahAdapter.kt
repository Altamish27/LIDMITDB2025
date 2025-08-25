package com.google.mediapipe.examples.gesturerecognizer.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahLetter

class PanduanHijaiyahAdapter(
    private val letters: List<HijaiyahLetter>
) : RecyclerView.Adapter<PanduanHijaiyahAdapter.ViewHolder>() {

    // Mapping posisi huruf ke huruf Hijaiyah asli untuk isyarat
    private val gestureHijaiyah = mapOf(
        1 to "ا", // Alif
        2 to "ب", // Ba
        3 to "ت", // Ta
        4 to "ث", // Tsa
        5 to "ج", // Jim
        6 to "ح", // Ha
        7 to "خ", // Kho
        8 to "د", // Dal
        9 to "ذ", // Dzal
        10 to "ر", // Ra
        11 to "ز", // Zai
        12 to "س", // Sin
        13 to "ش", // Syin
        14 to "ص", // Shod
        15 to "ض", // Dhod
        16 to "ط", // Tho
        17 to "ظ", // Zho
        18 to "ع", // Ain
        19 to "غ", // Ghoin
        20 to "ف", // Fa
        21 to "ق", // Qof
        22 to "ك", // Kaf
        23 to "ل", // Lam
        24 to "م", // Mim
        25 to "ن", // Nun
        26 to "و", // Wau
        27 to "ه", // Ha
        28 to "ي"  // Ya
    )

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHuruf: TextView = itemView.findViewById(R.id.tv_huruf)
        val tvIsyarat: TextView = itemView.findViewById(R.id.tv_isyarat)
        val tvLatin: TextView = itemView.findViewById(R.id.tv_latin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_panduan_hijaiyah, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val letter = letters[position]
        
        holder.tvHuruf.text = letter.arabic
        holder.tvLatin.text = letter.transliteration
        holder.tvIsyarat.text = gestureHijaiyah[letter.position] ?: "ا"
        
        // Alternate row colors
        val context = holder.itemView.context
        val backgroundColor = if (position % 2 == 0) {
            androidx.core.content.ContextCompat.getColor(context, android.R.color.white)
        } else {
            androidx.core.content.ContextCompat.getColor(context, R.color.hijaiyah_grey)
        }
        holder.itemView.setBackgroundColor(backgroundColor)
    }

    override fun getItemCount(): Int = letters.size
}
