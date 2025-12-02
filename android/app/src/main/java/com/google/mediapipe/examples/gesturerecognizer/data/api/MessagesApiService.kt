package com.google.mediapipe.examples.gesturerecognizer.data.api

import com.google.mediapipe.examples.gesturerecognizer.data.models.ConversationsResponse
import com.google.mediapipe.examples.gesturerecognizer.data.models.MarkConversationReadRequest
import com.google.mediapipe.examples.gesturerecognizer.data.models.MarkConversationReadResponse
import com.google.mediapipe.examples.gesturerecognizer.data.models.MessagesResponse
import com.google.mediapipe.examples.gesturerecognizer.data.models.SendMessageRequest
import com.google.mediapipe.examples.gesturerecognizer.data.models.SendMessageResponse
import com.google.mediapipe.examples.gesturerecognizer.data.models.UnreadCountResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * API Service untuk Messages
 */
class MessagesApiService {
    
    companion object {
        private const val BASE_URL = "https://signquran.site/api/messages"
        private const val TAG = "MessagesApiService"
        
        @Volatile
        private var INSTANCE: MessagesApiService? = null
        
        fun getInstance(): MessagesApiService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MessagesApiService().also { INSTANCE = it }
            }
        }
    }
    
    private val jsonFormatter = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }
    
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(jsonFormatter)
        }
        
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }
    
    /**
     * Get all conversations for current user
     */
    suspend fun getConversations(authToken: String): Result<ConversationsResponse> {
        return try {
            android.util.Log.d(TAG, "Fetching conversations from: $BASE_URL/conversations")
            val response = client.get("$BASE_URL/conversations") {
                headers.append("Authorization", "Bearer $authToken")
            }
            val body = response.body<ConversationsResponse>()
            android.util.Log.d(TAG, "Conversations fetched: ${body.conversations.size} conversations")
            Result.success(body)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Get conversations error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get messages for a specific conversation
     */
    suspend fun getMessages(authToken: String, conversationWith: Int): Result<MessagesResponse> {
        return try {
            android.util.Log.d(TAG, "Fetching messages with user: $conversationWith")
            val response = client.get(BASE_URL) {
                headers.append("Authorization", "Bearer $authToken")
                parameter("conversation_with", conversationWith)
            }
            val body = response.body<MessagesResponse>()
            android.util.Log.d(TAG, "Messages fetched: ${body.messages.size} messages")
            Result.success(body)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Get messages error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Send a new message
     */
    suspend fun sendMessage(
        authToken: String,
        receiverId: Int,
        message: String
    ): Result<SendMessageResponse> {
        return try {
            android.util.Log.d(TAG, "Sending message to user: $receiverId")
            val response = client.post(BASE_URL) {
                headers.append("Authorization", "Bearer $authToken")
                contentType(ContentType.Application.Json)
                setBody(SendMessageRequest(receiverId, message))
            }
            val body = response.body<SendMessageResponse>()
            android.util.Log.d(TAG, "Message sent successfully: ${body.data.messageId}")
            Result.success(body)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Send message error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Mark all messages from a sender as read
     */
    suspend fun markConversationAsRead(
        authToken: String,
        senderId: Int
    ): Result<MarkConversationReadResponse> {
        return try {
            android.util.Log.d(TAG, "Marking conversation as read from sender: $senderId")
            val response = client.put("$BASE_URL/mark-conversation-read") {
                headers.append("Authorization", "Bearer $authToken")
                contentType(ContentType.Application.Json)
                setBody(MarkConversationReadRequest(senderId))
            }
            val body = response.body<MarkConversationReadResponse>()
            android.util.Log.d(TAG, "Conversation marked as read: ${body.updatedCount} messages")
            Result.success(body)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Mark conversation as read error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get total unread message count
     */
    suspend fun getUnreadCount(authToken: String): Result<UnreadCountResponse> {
        return try {
            android.util.Log.d(TAG, "Fetching unread count")
            val response = client.get("$BASE_URL/unread-count") {
                headers.append("Authorization", "Bearer $authToken")
            }
            val body = response.body<UnreadCountResponse>()
            android.util.Log.d(TAG, "Unread count: ${body.unreadCount}")
            Result.success(body)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Get unread count error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Cleanup resources
     */
    fun close() {
        client.close()
    }
}
