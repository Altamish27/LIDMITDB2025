package com.google.mediapipe.examples.gesturerecognizer.features.room

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.gesturerecognizer.data.models.RoomMember
import com.google.mediapipe.examples.gesturerecognizer.databinding.ItemMemberBinding

class RoomMembersAdapter : ListAdapter<RoomMember, RoomMembersAdapter.MemberViewHolder>(MemberDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MemberViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class MemberViewHolder(
        private val binding: ItemMemberBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(member: RoomMember) {
            binding.tvMemberName.text = member.name
            binding.tvMemberEmail.text = member.email
            
            // Show role badge
            val roleText = when {
                member.isCreator -> "Guru (Pembuat)"
                member.role == "guru" -> "Guru"
                else -> "Murid"
            }
            binding.tvMemberRole.text = roleText
            
            // Different color for guru and creator
            val backgroundColor = when {
                member.isCreator -> android.graphics.Color.parseColor("#FFD700") // Gold
                member.role == "guru" -> android.graphics.Color.parseColor("#4CAF50") // Green
                else -> android.graphics.Color.parseColor("#2196F3") // Blue
            }
            binding.tvMemberRole.setBackgroundColor(backgroundColor)
        }
    }
    
    class MemberDiffCallback : DiffUtil.ItemCallback<RoomMember>() {
        override fun areItemsTheSame(oldItem: RoomMember, newItem: RoomMember): Boolean {
            return oldItem.userId == newItem.userId
        }
        
        override fun areContentsTheSame(oldItem: RoomMember, newItem: RoomMember): Boolean {
            return oldItem == newItem
        }
    }
}
