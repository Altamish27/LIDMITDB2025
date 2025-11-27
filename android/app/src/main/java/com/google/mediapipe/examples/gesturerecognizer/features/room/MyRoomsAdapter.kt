package com.google.mediapipe.examples.gesturerecognizer.features.room

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.gesturerecognizer.data.models.EnrolledRoom
import com.google.mediapipe.examples.gesturerecognizer.databinding.ItemRoomBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyRoomsAdapter(
    private val onRoomClick: (EnrolledRoom) -> Unit
) : ListAdapter<EnrolledRoom, MyRoomsAdapter.RoomViewHolder>(RoomDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemRoomBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RoomViewHolder(binding, onRoomClick)
    }
    
    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class RoomViewHolder(
        private val binding: ItemRoomBinding,
        private val onRoomClick: (EnrolledRoom) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(room: EnrolledRoom) {
            binding.tvRoomName.text = room.name
            binding.tvRoomDescription.text = room.description ?: "Tidak ada deskripsi"
            val displayCode = room.roomCode?.takeIf { it.isNotBlank() }
                ?: room.code?.takeIf { it.isNotBlank() }
                ?: "-"
            binding.tvRoomCode.text = "Kode: $displayCode"
            binding.tvGuruName.text = "Guru: ${room.createdByName}"
            
            // Format joined date
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
                val date = inputFormat.parse(room.joinedAt)
                binding.tvJoinedDate.text = "Bergabung: ${date?.let { outputFormat.format(it) } ?: room.joinedAt}"
            } catch (e: Exception) {
                binding.tvJoinedDate.text = "Bergabung: ${room.joinedAt.take(10)}"
            }
            
            binding.root.setOnClickListener {
                onRoomClick(room)
            }
        }
    }
    
    class RoomDiffCallback : DiffUtil.ItemCallback<EnrolledRoom>() {
        override fun areItemsTheSame(oldItem: EnrolledRoom, newItem: EnrolledRoom): Boolean {
            return oldItem.enrollmentId == newItem.enrollmentId
        }
        
        override fun areContentsTheSame(oldItem: EnrolledRoom, newItem: EnrolledRoom): Boolean {
            return oldItem == newItem
        }
    }
}
