package com.google.mediapipe.examples.gesturerecognizer.features.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.data.models.Conversation
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter untuk RecyclerView conversations list
 */
class ConversationsAdapter(
    private val onConversationClick: (Conversation) -> Unit
) : ListAdapter<Conversation, ConversationsAdapter.ConversationViewHolder>(ConversationDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_conversation, parent, false)
        return ConversationViewHolder(view, onConversationClick)
    }
    
    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class ConversationViewHolder(
        itemView: View,
        private val onConversationClick: (Conversation) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val tvName: TextView = itemView.findViewById(R.id.tv_name)
        private val tvRole: TextView = itemView.findViewById(R.id.tv_role)
        private val tvLastMessage: TextView = itemView.findViewById(R.id.tv_last_message)
        private val tvTimestamp: TextView = itemView.findViewById(R.id.tv_timestamp)
        private val tvUnreadBadge: TextView = itemView.findViewById(R.id.tv_unread_badge)
        private val tvAvatar: TextView = itemView.findViewById(R.id.tv_avatar)
        
        fun bind(conversation: Conversation) {
            tvName.text = conversation.name
            tvRole.text = if (conversation.role == "guru") "Guru" else "Murid"
            tvLastMessage.text = conversation.lastMessage
            tvTimestamp.text = formatTime(conversation.lastMessageTime)
            
            // Avatar initial
            val initial = conversation.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
            tvAvatar.text = initial
            
            // Unread badge
            if (conversation.unreadCount > 0) {
                tvUnreadBadge.visibility = View.VISIBLE
                tvUnreadBadge.text = if (conversation.unreadCount > 99) {
                    "99+"
                } else {
                    conversation.unreadCount.toString()
                }
            } else {
                tvUnreadBadge.visibility = View.GONE
            }
            
            itemView.setOnClickListener {
                onConversationClick(conversation)
            }
        }
        
        private fun formatTime(timestamp: String): String {
            return try {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                val date = sdf.parse(timestamp) ?: return timestamp
                
                val now = Date()
                val diff = now.time - date.time
                
                when {
                    diff < 60000 -> "Baru saja"
                    diff < 3600000 -> "${diff / 60000} menit lalu"
                    diff < 86400000 -> "${diff / 3600000} jam lalu"
                    diff < 172800000 -> "Kemarin"
                    else -> {
                        val displayFormat = SimpleDateFormat("dd MMM", Locale("id"))
                        displayFormat.format(date)
                    }
                }
            } catch (e: Exception) {
                timestamp
            }
        }
    }
    
    class ConversationDiffCallback : DiffUtil.ItemCallback<Conversation>() {
        override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
            return oldItem.userId == newItem.userId
        }
        
        override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
            return oldItem == newItem
        }
    }
}
