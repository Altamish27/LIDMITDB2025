/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.mediapipe.examples.gesturerecognizer.ui.latihan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.gesturerecognizer.R

class HurufGridAdapter(
    private val onHurufClick: (HurufItem) -> Unit
) : RecyclerView.Adapter<HurufGridAdapter.HurufViewHolder>() {
    
    private var hurufList: List<HurufItem> = emptyList()
    
    fun updateHuruf(newHurufList: List<HurufItem>) {
        hurufList = newHurufList
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HurufViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_huruf_grid, parent, false)
        return HurufViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: HurufViewHolder, position: Int) {
        holder.bind(hurufList[position])
    }
    
    override fun getItemCount(): Int = hurufList.size
    
    inner class HurufViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val arabicText: TextView = itemView.findViewById(R.id.tvArabic)
        private val latinText: TextView = itemView.findViewById(R.id.tvLatin)
        private val container: View = itemView.findViewById(R.id.hurufContainer)
        
        fun bind(huruf: HurufItem) {
            arabicText.text = huruf.arabic
            latinText.text = huruf.latin
            
            val context = itemView.context
            
            when {
                huruf.isCompleted -> {
                    // Orange/Yellow for completed
                    container.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_orange_light))
                    arabicText.setTextColor(ContextCompat.getColor(context, android.R.color.white))
                    latinText.setTextColor(ContextCompat.getColor(context, android.R.color.white))
                }
                huruf.isActive -> {
                    // Highlighted for active
                    container.setBackgroundColor(ContextCompat.getColor(context, R.color.hijaiyah_yellow))
                    arabicText.setTextColor(ContextCompat.getColor(context, R.color.hijaiyah_navy))
                    latinText.setTextColor(ContextCompat.getColor(context, R.color.hijaiyah_navy))
                }
                else -> {
                    // Dark navy for default
                    container.setBackgroundColor(ContextCompat.getColor(context, R.color.hijaiyah_navy))
                    arabicText.setTextColor(ContextCompat.getColor(context, android.R.color.white))
                    latinText.setTextColor(ContextCompat.getColor(context, android.R.color.white))
                }
            }
            
            itemView.setOnClickListener {
                onHurufClick(huruf)
            }
        }
    }
}
