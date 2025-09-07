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

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.data.LatihanHuruf

/**
 * Adapter untuk menampilkan grid huruf dalam baris
 */
class LatihanHurufGridAdapter(
    private val onHurufClick: (LatihanHuruf) -> Unit
) : RecyclerView.Adapter<LatihanHurufGridAdapter.HurufViewHolder>() {

    private var hurufList = listOf<LatihanHuruf>()
    private var completedPositions = setOf<Int>()

    fun updateHuruf(newHurufList: List<LatihanHuruf>) {
        hurufList = newHurufList
        notifyDataSetChanged()
    }

    fun updateCompletedPositions(completed: Set<Int>) {
        completedPositions = completed
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
        private val cardView: CardView = itemView.findViewById(R.id.cardHuruf)
        private val hurufContainer: LinearLayout = itemView.findViewById(R.id.hurufContainer)
        private val tvArabic: TextView = itemView.findViewById(R.id.tvArabic)
        private val tvLatin: TextView = itemView.findViewById(R.id.tvLatin)

        fun bind(huruf: LatihanHuruf) {
            tvArabic.text = huruf.arabic
            tvLatin.text = huruf.latin

            val context = itemView.context
            val isCompleted = completedPositions.contains(huruf.position)

            // Set warna berdasarkan status - ubah background container dengan drawable
            if (isCompleted) {
                // Orange untuk huruf yang sudah selesai/benar
                hurufContainer.background = ContextCompat.getDrawable(context, R.drawable.letter_container_orange)
                // Text tetap putih
                tvArabic.setTextColor(Color.WHITE)
                tvLatin.setTextColor(Color.WHITE)
            } else {
                // Biru untuk huruf yang belum selesai
                hurufContainer.background = ContextCompat.getDrawable(context, R.drawable.letter_container_navy)
                tvArabic.setTextColor(Color.WHITE)
                tvLatin.setTextColor(Color.WHITE)
            }

            // Set click listener
            itemView.setOnClickListener {
                onHurufClick(huruf)
            }
        }
    }
}
