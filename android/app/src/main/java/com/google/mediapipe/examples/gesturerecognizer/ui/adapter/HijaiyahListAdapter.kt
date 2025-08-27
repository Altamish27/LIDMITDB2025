package com.google.mediapipe.examples.gesturerecognizer.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.data.HijaiyahLetter

class HijaiyahListAdapter(
    private var letters: List<HijaiyahLetter>,
    private var completedLetters: Set<Int>,
    private val onLetterClick: (HijaiyahLetter, Int) -> Unit
) : RecyclerView.Adapter<HijaiyahListAdapter.LetterViewHolder>() {

    class LetterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val arabicLetter: TextView = itemView.findViewById(R.id.tv_arabic_letter)
        val romanLetter: TextView = itemView.findViewById(R.id.tv_roman_letter)
        val container: View = itemView.findViewById(R.id.letter_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hijaiyah_letter_grid, parent, false)
        return LetterViewHolder(view)
    }

    override fun onBindViewHolder(holder: LetterViewHolder, position: Int) {
        val letter = letters[position]
        val isCompleted = completedLetters.contains(position)

        holder.arabicLetter.text = letter.arabic
        holder.romanLetter.text = letter.transliteration

        // Set background based on completion status with rounded corners (same as Latihan page)
        if (isCompleted) {
            holder.container.background = ContextCompat.getDrawable(
                holder.itemView.context, 
                R.drawable.letter_container_completed_orange
            )
            holder.arabicLetter.setTextColor(ContextCompat.getColor(
                holder.itemView.context, 
                android.R.color.white
            ))
            holder.romanLetter.setTextColor(ContextCompat.getColor(
                holder.itemView.context, 
                android.R.color.white
            ))
        } else {
            holder.container.background = ContextCompat.getDrawable(
                holder.itemView.context, 
                R.drawable.letter_container_navy
            )
            holder.arabicLetter.setTextColor(ContextCompat.getColor(
                holder.itemView.context, 
                android.R.color.white
            ))
            holder.romanLetter.setTextColor(ContextCompat.getColor(
                holder.itemView.context, 
                android.R.color.white
            ))
        }

        holder.itemView.setOnClickListener {
            onLetterClick(letter, position)
        }
    }

    override fun getItemCount() = letters.size

    fun updateLetters(newLetters: List<HijaiyahLetter>) {
        letters = newLetters
        notifyDataSetChanged()
    }

    fun updateCompletedLetters(newCompletedLetters: Set<Int>) {
        completedLetters = newCompletedLetters
        notifyDataSetChanged()
    }
}
