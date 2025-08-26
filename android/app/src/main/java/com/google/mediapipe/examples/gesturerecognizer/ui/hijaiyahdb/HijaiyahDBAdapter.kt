package com.google.mediapipe.examples.gesturerecognizer.ui.hijaiyahdb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.ui.hijaiyahdb.model.HijaiyahDbItem

class HijaiyahAdapter(private val list: List<HijaiyahDbItem>) :
    RecyclerView.Adapter<HijaiyahAdapter.ViewHolder>() {
        
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvArabic: TextView = itemView.findViewById(R.id.tvArabicChar)
        val tvLatin: TextView = itemView.findViewById(R.id.tvLatinName)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hijaiyah, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvLatin.text = item.latin_name
        holder.tvArabic.text = item.arabic_char

    }

    override fun getItemCount(): Int = list.size
}
