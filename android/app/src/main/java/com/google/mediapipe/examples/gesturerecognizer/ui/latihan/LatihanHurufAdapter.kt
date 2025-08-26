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
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.gesturerecognizer.R

class LatihanHurufAdapter(
    private val onHurufClick: (LatihanHuruf) -> Unit
) : RecyclerView.Adapter<LatihanHurufAdapter.HurufViewHolder>() {
    
    private var allHurufList: List<LatihanHuruf> = emptyList()
    private var filteredHurufList: List<LatihanHuruf> = emptyList()
    
    fun updateHuruf(newHurufList: List<LatihanHuruf>) {
        allHurufList = newHurufList
        filteredHurufList = newHurufList
        notifyDataSetChanged()
    }
    
    fun filterByStatus(status: String) {
        filteredHurufList = if (status == "Semua") {
            allHurufList
        } else {
            allHurufList.filter { it.status == status }
        }
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HurufViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_latihan_huruf, parent, false)
        return HurufViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: HurufViewHolder, position: Int) {
        holder.bind(filteredHurufList[position])
    }
    
    override fun getItemCount(): Int = filteredHurufList.size
    
    inner class HurufViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val numberText: TextView = itemView.findViewById(R.id.tvNumber)
        private val titleText: TextView = itemView.findViewById(R.id.tvTitle)
        private val statusButton: TextView = itemView.findViewById(R.id.btnStatus)
        private val playIcon: ImageView = itemView.findViewById(R.id.ivPlayIcon)
        private val lockIcon: ImageView = itemView.findViewById(R.id.ivLockIcon)
        private val container: View = itemView.findViewById(R.id.hurufContainer)
        
        fun bind(huruf: LatihanHuruf) {
            numberText.text = huruf.number
            titleText.text = huruf.title
            
            val context = itemView.context
            
            when (huruf.status) {
                "Selesai" -> {
                    // Green background for completed
                    container.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_light))
                    statusButton.text = "Selesai"
                    statusButton.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
                    statusButton.setTextColor(ContextCompat.getColor(context, android.R.color.white))
                    playIcon.visibility = View.VISIBLE
                    lockIcon.visibility = View.GONE
                    itemView.isEnabled = true
                    itemView.alpha = 1.0f
                }
                "Aktif" -> {
                    // Orange background for active
                    container.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_orange_light))
                    statusButton.text = "Aktif"
                    statusButton.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                    statusButton.setTextColor(ContextCompat.getColor(context, android.R.color.white))
                    playIcon.visibility = View.VISIBLE
                    lockIcon.visibility = View.GONE
                    itemView.isEnabled = true
                    itemView.alpha = 1.0f
                }
                "Terkunci" -> {
                    // Gray background for locked
                    container.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray))
                    statusButton.text = "Terkunci"
                    statusButton.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray))
                    statusButton.setTextColor(ContextCompat.getColor(context, android.R.color.white))
                    playIcon.visibility = View.GONE
                    lockIcon.visibility = View.VISIBLE
                    itemView.isEnabled = false
                    itemView.alpha = 0.6f
                }
            }
            
            // Set click listener
            if (huruf.status != "Terkunci") {
                itemView.setOnClickListener {
                    onHurufClick(huruf)
                }
            } else {
                itemView.setOnClickListener(null)
            }
        }
    }
}
