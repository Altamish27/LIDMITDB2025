package com.google.mediapipe.examples.gesturerecognizer.features.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.data.models.Message
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter untuk RecyclerView chat messages
 */
class MessagesAdapter(
    private val currentUserId: Int
) : ListAdapter<MessagesAdapter.MessageItem, RecyclerView.ViewHolder>(MessageDiffCallback()) {
    
    /**
     * Sealed class untuk message items (bisa message atau date divider)
     */
    sealed class MessageItem {
        data class DateDivider(val date: String) : MessageItem()
        data class MessageData(val message: Message) : MessageItem()
    }
    
    companion object {
        private const val VIEW_TYPE_DATE_DIVIDER = 0
        private const val VIEW_TYPE_MESSAGE_SENT = 1
        private const val VIEW_TYPE_MESSAGE_RECEIVED = 2
        
        /**
         * Convert list of messages to list of message items with date dividers
         */
        fun messagesToItems(messages: List<Message>): List<MessageItem> {
            val items = mutableListOf<MessageItem>()
            var lastDate: String? = null
            
            for (message in messages) {
                val messageDate = getDateString(message.createdAt)
                
                // Add date divider if date changed
                if (messageDate != lastDate) {
                    items.add(MessageItem.DateDivider(messageDate))
                    lastDate = messageDate
                }
                
                items.add(MessageItem.MessageData(message))
            }
            
            return items
        }
        
        private fun getDateString(timestamp: String): String {
            return try {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                val date = sdf.parse(timestamp) ?: return timestamp
                
                val now = Calendar.getInstance()
                val messageDate = Calendar.getInstance().apply { time = date }
                
                when {
                    isSameDay(now, messageDate) -> "Hari ini"
                    isYesterday(now, messageDate) -> "Kemarin"
                    else -> {
                        val displayFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id"))
                        displayFormat.format(date)
                    }
                }
            } catch (e: Exception) {
                timestamp
            }
        }
        
        fun formatMessageTime(timestamp: String): String {
            return try {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                val date = sdf.parse(timestamp) ?: return timestamp
                
                val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
                timeFormat.format(date)
            } catch (e: Exception) {
                timestamp
            }
        }
        
        private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
        }
        
        private fun isYesterday(now: Calendar, date: Calendar): Boolean {
            val yesterday = now.clone() as Calendar
            yesterday.add(Calendar.DAY_OF_YEAR, -1)
            return isSameDay(yesterday, date)
        }
    }
    
    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is MessageItem.DateDivider -> VIEW_TYPE_DATE_DIVIDER
            is MessageItem.MessageData -> {
                if (item.message.senderId == currentUserId) {
                    VIEW_TYPE_MESSAGE_SENT
                } else {
                    VIEW_TYPE_MESSAGE_RECEIVED
                }
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DATE_DIVIDER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_date_divider, parent, false)
                DateDividerViewHolder(view)
            }
            VIEW_TYPE_MESSAGE_SENT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_sent, parent, false)
                MessageSentViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_received, parent, false)
                MessageReceivedViewHolder(view)
            }
        }
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is MessageItem.DateDivider -> {
                (holder as DateDividerViewHolder).bind(item.date)
            }
            is MessageItem.MessageData -> {
                when (holder) {
                    is MessageSentViewHolder -> holder.bind(item.message)
                    is MessageReceivedViewHolder -> holder.bind(item.message)
                }
            }
        }
    }
    
    /**
     * ViewHolder untuk date divider
     */
    class DateDividerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        
        fun bind(date: String) {
            tvDate.text = date
        }
    }
    
    /**
     * ViewHolder untuk sent message
     */
    class MessageSentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tv_message)
        private val tvTimestamp: TextView = itemView.findViewById(R.id.tv_timestamp)
        
        fun bind(message: Message) {
            tvMessage.text = message.message
            tvTimestamp.text = MessagesAdapter.formatMessageTime(message.createdAt)
        }
    }
    
    /**
     * ViewHolder untuk received message
     */
    class MessageReceivedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tv_message)
        private val tvTimestamp: TextView = itemView.findViewById(R.id.tv_timestamp)
        
        fun bind(message: Message) {
            tvMessage.text = message.message
            tvTimestamp.text = MessagesAdapter.formatMessageTime(message.createdAt)
        }
    }
    
    class MessageDiffCallback : DiffUtil.ItemCallback<MessageItem>() {
        override fun areItemsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
            return when {
                oldItem is MessageItem.DateDivider && newItem is MessageItem.DateDivider -> {
                    oldItem.date == newItem.date
                }
                oldItem is MessageItem.MessageData && newItem is MessageItem.MessageData -> {
                    oldItem.message.messageId == newItem.message.messageId
                }
                else -> false
            }
        }
        
        override fun areContentsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
            return oldItem == newItem
        }
    }
}
