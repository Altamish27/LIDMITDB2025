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

    // Mapping posisi huruf ke emoji isyarat
    private val gestureEmojis = mapOf(
        1 to "👆", // Alif
        2 to "✊", // Ba
        3 to "✌️", // Ta
        4 to "🤟", // Tsa
        5 to "🤞", // Jim
        6 to "✋", // Ha
        7 to "🖖", // Kho
        8 to "☝️", // Dal
        9 to "👍", // Dzal
        10 to "🤏", // Ra
        11 to "🤌", // Zai
        12 to "🖐️", // Sin
        13 to "🤚", // Syin
        14 to "👊", // Shod
        15 to "🖕", // Dhod
        16 to "🫵", // Tho
        17 to "🫶", // Zho
        18 to "👌", // Ain
        19 to "🤙", // Ghoin
        20 to "🫰", // Fa
        21 to "👎", // Qof
        22 to "🤛", // Kaf
        23 to "🤜", // Lam
        24 to "🤝", // Mim
        25 to "🙏", // Nun
        26 to "👋", // Wau
        27 to "🫷", // Ha
        28 to "🫸"  // Ya
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
        holder.tvIsyarat.text = gestureEmojis[letter.position] ?: "🤷"
        
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
