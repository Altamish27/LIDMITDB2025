package com.google.mediapipe.examples.gesturerecognizer.features.latihan

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.gesturerecognizer.databinding.ItemHalamanBinding
import com.google.mediapipe.examples.gesturerecognizer.data.models.HalamanInfo

class HalamanListAdapter(
    private val onHalamanClick: (HalamanInfo) -> Unit
) : ListAdapter<HalamanInfo, HalamanListAdapter.HalamanViewHolder>(HalamanDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HalamanViewHolder {
        val binding = ItemHalamanBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HalamanViewHolder(binding, onHalamanClick)
    }

    override fun onBindViewHolder(holder: HalamanViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HalamanViewHolder(
        private val binding: ItemHalamanBinding,
        private val onHalamanClick: (HalamanInfo) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(halaman: HalamanInfo) {
            binding.tvHalamanNumber.text = "Halaman ${halaman.nomorHalaman}"
            binding.tvHalamanDescription.text = halaman.deskripsi
            
            // Set card background color based on completion status
            val backgroundColor = if (halaman.isCompleted) {
                Color.parseColor("#4CAF50") // Green for completed
            } else {
                Color.parseColor("#FFFFFF") // White for uncompleted
            }
            binding.cardHalaman.setCardBackgroundColor(backgroundColor)
            
            // Set text color for better contrast
            val textColor = if (halaman.isCompleted) {
                Color.WHITE
            } else {
                Color.BLACK
            }
            binding.tvHalamanNumber.setTextColor(textColor)
            binding.tvHalamanDescription.setTextColor(textColor)
            
            // Add completion indicator
            binding.tvCompletionStatus.text = if (halaman.isCompleted) "✓ Selesai" else "Belum selesai"
            binding.tvCompletionStatus.setTextColor(textColor)
            
            binding.cardHalaman.setOnClickListener {
                onHalamanClick(halaman)
            }
        }
    }

    class HalamanDiffCallback : DiffUtil.ItemCallback<HalamanInfo>() {
        override fun areItemsTheSame(oldItem: HalamanInfo, newItem: HalamanInfo): Boolean {
            return oldItem.halamanId == newItem.halamanId
        }

        override fun areContentsTheSame(oldItem: HalamanInfo, newItem: HalamanInfo): Boolean {
            return oldItem == newItem
        }
    }
}