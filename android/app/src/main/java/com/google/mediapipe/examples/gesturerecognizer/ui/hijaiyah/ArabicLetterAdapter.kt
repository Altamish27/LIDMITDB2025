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

package com.google.mediapipe.examples.gesturerecognizer.ui.hijaiyah

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.data.ArabicLetter

class ArabicLetterAdapter(
    private val onLetterClick: (ArabicLetter) -> Unit
) : RecyclerView.Adapter<ArabicLetterAdapter.LetterViewHolder>() {
    
    private var letters: List<ArabicLetter> = emptyList()
    
    fun updateLetters(newLetters: List<ArabicLetter>) {
        letters = newLetters
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hijaiyah_letter, parent, false)
        return LetterViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: LetterViewHolder, position: Int) {
        holder.bind(letters[position])
    }
    
    override fun getItemCount(): Int = letters.size
    
    inner class LetterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val arabicText: TextView = itemView.findViewById(R.id.arabicText)
        private val transliterationText: TextView = itemView.findViewById(R.id.transliterationText)
        private val container: View = itemView.findViewById(R.id.letterContainer)
        
        fun bind(letter: ArabicLetter) {
            arabicText.text = letter.arabic
            transliterationText.text = letter.transliteration
            
            // Set colors based on completion status
            val context = itemView.context
            if (letter.isCompleted) {
                // Orange/Yellow for completed letters with rounded corners (same as Latihan page)
                container.background = ContextCompat.getDrawable(context, R.drawable.letter_container_completed_orange)
                arabicText.setTextColor(ContextCompat.getColor(context, android.R.color.white))
                transliterationText.setTextColor(ContextCompat.getColor(context, android.R.color.white))
            } else {
                // Dark navy (#2C3E50) background with white text for not completed letters
                container.background = ContextCompat.getDrawable(context, R.drawable.letter_container_navy)
                arabicText.setTextColor(ContextCompat.getColor(context, android.R.color.white))
                transliterationText.setTextColor(ContextCompat.getColor(context, android.R.color.white))
            }
            
            itemView.setOnClickListener {
                onLetterClick(letter)
            }
        }
    }
}
