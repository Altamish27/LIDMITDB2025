package com.google.mediapipe.examples.gesturerecognizer.features.hijaiyah

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahLetter
import com.google.mediapipe.examples.gesturerecognizer.features.guide.LetterGuideActivity

class HijaiyahAdapter(
    private val onLetterClick: (HijaiyahLetter) -> Unit
) : RecyclerView.Adapter<HijaiyahAdapter.LetterViewHolder>() {
    
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
                // Open guide activity first, not directly camera
                val context = itemView.context
                val intent = Intent(context, LetterGuideActivity::class.java).apply {
                    putExtra("selectedLetter", letter.arabic)
                    putExtra("target_letter", letter.arabic)
                    putExtra("letterName", letter.transliteration)
                    putExtra("target_letter_name", letter.transliteration)
                    putExtra("letterType", "hijaiyah")
                    putExtra("letterPosition", letter.position)
                }
                context.startActivity(intent)
            }
        }
    }
}
