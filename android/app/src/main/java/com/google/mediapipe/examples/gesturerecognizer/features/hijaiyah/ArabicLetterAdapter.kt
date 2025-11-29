package com.google.mediapipe.examples.gesturerecognizer.features.hijaiyah

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahLetter

class ArabicLetterAdapter(
    private val onLetterClick: (HijaiyahLetter) -> Unit
) : RecyclerView.Adapter<ArabicLetterAdapter.LetterViewHolder>() {
    
    companion object {
        // Letters that don't have gesture recognition (skip camera, go to panduan directly)
        private val LETTERS_WITHOUT_GESTURE = setOf(
            "hamzah", "lam alif", "lamalif", "lam-alif"
        )
        
        /**
         * Check if a letter should skip camera and go directly to panduan
         */
        fun shouldSkipCamera(transliteration: String): Boolean {
            return LETTERS_WITHOUT_GESTURE.any { 
                transliteration.lowercase().contains(it) 
            }
        }
    }
    
    private var letters: List<HijaiyahLetter> = emptyList()
    
    fun updateLetters(newLetters: List<HijaiyahLetter>) {
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
        
        fun bind(letter: HijaiyahLetter) {
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
                // Let the callback handle all navigation logic
                onLetterClick(letter)
            }
        }
    }
}
