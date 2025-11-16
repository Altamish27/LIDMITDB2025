package com.google.mediapipe.examples.gesturerecognizer.features.latihan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.data.LatihanJilid
import com.google.mediapipe.examples.gesturerecognizer.databinding.ItemJilidBinding

class JilidListAdapter(
    private val onJilidClick: (LatihanJilid) -> Unit
) : ListAdapter<LatihanJilid, JilidListAdapter.JilidViewHolder>(JilidDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JilidViewHolder {
        val binding = ItemJilidBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return JilidViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JilidViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class JilidViewHolder(
        private val binding: ItemJilidBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(jilid: LatihanJilid) {
            binding.apply {
                // Set nomor jilid
                tvJilidNumber.text = jilid.id.toString()
                
                // Set title dan description
                tvJilidTitle.text = jilid.title
                tvJilidDescription.text = jilid.description
                
                // Set status (locked atau available)
                val isLocked = jilid.halamanList.isEmpty()
                
                if (isLocked) {
                    // Jilid terkunci
                    cardJilid.setCardBackgroundColor(
                        itemView.context.getColor(R.color.gray_light)
                    )
                    tvJilidNumber.setBackgroundResource(R.drawable.circle_gray_background)
                    ivLockIcon.visibility = android.view.View.VISIBLE
                    tvJilidStatus.apply {
                        text = "Terkunci"
                        setTextColor(itemView.context.getColor(R.color.gray_dark))
                    }
                } else {
                    // Jilid tersedia
                    cardJilid.setCardBackgroundColor(
                        itemView.context.getColor(R.color.hijaiyah_navy)
                    )
                    tvJilidNumber.setBackgroundResource(R.drawable.circle_yellow_background)
                    ivLockIcon.visibility = android.view.View.GONE
                    
                    // Tampilkan progress
                    val totalHalaman = jilid.halamanList.size
                    tvJilidStatus.apply {
                        text = "$totalHalaman Halaman"
                        setTextColor(itemView.context.getColor(R.color.hijaiyah_yellow))
                    }
                }
                
                // Set click listener
                root.setOnClickListener {
                    onJilidClick(jilid)
                }
            }
        }
    }

    private class JilidDiffCallback : DiffUtil.ItemCallback<LatihanJilid>() {
        override fun areItemsTheSame(oldItem: LatihanJilid, newItem: LatihanJilid): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LatihanJilid, newItem: LatihanJilid): Boolean {
            return oldItem == newItem
        }
    }
}
