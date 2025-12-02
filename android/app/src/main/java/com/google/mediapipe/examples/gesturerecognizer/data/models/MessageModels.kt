package com.google.mediapipe.examples.gesturerecognizer.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Model untuk single message
 */
@Serializable
data class Message(
    @SerialName("message_id")
    val messageId: Int,
    
    @SerialName("sender_id")
    val senderId: Int,
    
    @SerialName("receiver_id")
    val receiverId: Int,
    
    @SerialName("message")
    val message: String,
    
    @SerialName("created_at")
    val createdAt: String,
    
    @SerialName("is_read")
    val isRead: Boolean,
    
    @SerialName("sender_name")
    val senderName: String? = null,
    
    @SerialName("sender_email")
    val senderEmail: String? = null,
    
    @SerialName("receiver_name")
    val receiverName: String? = null,
    
    @SerialName("receiver_email")
    val receiverEmail: String? = null
)

/**
 * Model untuk conversation list item
 */
@Serializable
data class Conversation(
    @SerialName("user_id")
    val userId: Int,
    
    @SerialName("name")
    val name: String,
    
    @SerialName("email")
    val email: String,
    
    @SerialName("role")
    val role: String,
    
    @SerialName("last_message")
    val lastMessage: String,
    
    @SerialName("last_message_time")
    val lastMessageTime: String,
    
    @SerialName("unread_count")
    val unreadCount: Int
)

/**
 * Response model untuk get messages
 */
@Serializable
data class MessagesResponse(
    @SerialName("messages")
    val messages: List<Message>,
    
    @SerialName("count")
    val count: Int
)

/**
 * Response model untuk conversations list
 */
@Serializable
data class ConversationsResponse(
    @SerialName("conversations")
    val conversations: List<Conversation>,
    
    @SerialName("count")
    val count: Int
)

/**
 * Request model untuk send message
 */
@Serializable
data class SendMessageRequest(
    @SerialName("receiver_id")
    val receiverId: Int,
    
    @SerialName("message")
    val message: String
)

/**
 * Response model untuk send message
 */
@Serializable
data class SendMessageResponse(
    @SerialName("message")
    val message: String,
    
    @SerialName("data")
    val data: Message
)

/**
 * Request model untuk mark conversation as read
 */
@Serializable
data class MarkConversationReadRequest(
    @SerialName("sender_id")
    val senderId: Int
)

/**
 * Response model untuk mark conversation as read
 */
@Serializable
data class MarkConversationReadResponse(
    @SerialName("message")
    val message: String,
    
    @SerialName("updated_count")
    val updatedCount: Int
)

/**
 * Response model untuk unread count
 */
@Serializable
data class UnreadCountResponse(
    @SerialName("unread_count")
    val unreadCount: Int
)
