package com.google.mediapipe.examples.gesturerecognizer.data.api

import com.google.mediapipe.examples.gesturerecognizer.data.manager.AuthManager
import com.google.mediapipe.examples.gesturerecognizer.data.models.HijaiyahApiResponse
import com.google.mediapipe.examples.gesturerecognizer.data.models.JilidApiResponse
import com.google.mediapipe.examples.gesturerecognizer.data.models.JilidPagesApiResponse
import com.google.mediapipe.examples.gesturerecognizer.data.models.JilidProgressListResponse
import com.google.mediapipe.examples.gesturerecognizer.data.models.PageDetailApiResponse
import com.google.mediapipe.examples.gesturerecognizer.data.models.HalamanProgressRequest
import com.google.mediapipe.examples.gesturerecognizer.data.models.HalamanProgressResponse
import com.google.mediapipe.examples.gesturerecognizer.data.models.HalamanProgressCheckResponse
import com.google.mediapipe.examples.gesturerecognizer.data.models.JoinRoomRequest
import com.google.mediapipe.examples.gesturerecognizer.data.models.JoinRoomResponse
import com.google.mediapipe.examples.gesturerecognizer.data.models.MyRoomsResponse
import com.google.mediapipe.examples.gesturerecognizer.data.models.RoomMembersResponse
import com.google.mediapipe.examples.gesturerecognizer.data.models.EnrollmentsResponse
import com.google.mediapipe.examples.gesturerecognizer.data.models.EnrolledRoom
import com.google.mediapipe.examples.gesturerecognizer.data.models.SimpleRoomsResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * API Service untuk mengakses SignQuran API
 */
class SignQuranApiService {
    
    companion object {
        private const val BASE_URL = "https://signquran.site/api"
        
        @Volatile
        private var INSTANCE: SignQuranApiService? = null
        
        fun getInstance(): SignQuranApiService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SignQuranApiService().also { INSTANCE = it }
            }
        }
    }
    
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            })
        }
        
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }
    
    /**
     * Fetch data hijaiyah dari API
     */
    suspend fun getHijaiyahLetters(authToken: String? = null): Result<HijaiyahApiResponse> {
        return try {
            android.util.Log.d("SignQuranAPI", "Fetching hijaiyah from: $BASE_URL/hijaiyah")
            val response = client.get("$BASE_URL/hijaiyah") {
                if (!authToken.isNullOrEmpty()) {
                    headers.append("Authorization", "Bearer $authToken")
                }
            }
            val body = response.body<HijaiyahApiResponse>()
            android.util.Log.d("SignQuranAPI", "Hijaiyah success: ${body.letters.size} letters")
            Result.success(body)
        } catch (e: Exception) {
            android.util.Log.e("SignQuranAPI", "Hijaiyah error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Fetch data jilid dari API
     */
    suspend fun getJilidList(authToken: String? = null): Result<JilidApiResponse> {
        return try {
            android.util.Log.d("SignQuranAPI", "========================================")
            android.util.Log.d("SignQuranAPI", "Fetching jilid from: $BASE_URL/jilid")
            android.util.Log.d("SignQuranAPI", "Auth token provided: ${!authToken.isNullOrEmpty()}")
            if (!authToken.isNullOrEmpty()) {
                android.util.Log.d("SignQuranAPI", "Token: ${authToken.take(20)}...")
            }
            
            val response = client.get("$BASE_URL/jilid") {
                if (!authToken.isNullOrEmpty()) {
                    headers.append("Authorization", "Bearer $authToken")
                    android.util.Log.d("SignQuranAPI", "Authorization header added")
                }
            }
            
            android.util.Log.d("SignQuranAPI", "Response status: ${response.status}")
            val body = response.body<JilidApiResponse>()
            android.util.Log.d("SignQuranAPI", "Jilid success: ${body.jilid.size} jilid found")
            Result.success(body)
        } catch (e: Exception) {
            android.util.Log.e("SignQuranAPI", "Jilid error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Fetch daftar halaman dalam jilid
     */
    suspend fun getJilidPages(jilidId: Int, authToken: String? = null): Result<JilidPagesApiResponse> {
        return try {
            android.util.Log.d("SignQuranAPI", "Fetching pages for jilid: $jilidId")
            val response = client.get("$BASE_URL/jilid/$jilidId/pages") {
                if (!authToken.isNullOrEmpty()) {
                    headers.append("Authorization", "Bearer $authToken")
                }
            }
            val body = response.body<JilidPagesApiResponse>()
            android.util.Log.d("SignQuranAPI", "Pages success: ${body.pages.size} pages found")
            Result.success(body)
        } catch (e: Exception) {
            android.util.Log.e("SignQuranAPI", "Pages error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Fetch detail halaman berdasarkan jilid_id dan nomor_halaman
     */
    suspend fun getPageDetail(jilidId: Int, nomorHalaman: Int, authToken: String? = null): Result<PageDetailApiResponse> {
        return try {
            val url = "$BASE_URL/pages/detail?jilid_id=$jilidId&nomor_halaman=$nomorHalaman"
            android.util.Log.d("SignQuranAPI", "Fetching page from: $url")
            val response = client.get("$BASE_URL/pages/detail") {
                if (!authToken.isNullOrEmpty()) {
                    headers.append("Authorization", "Bearer $authToken")
                }
                parameter("jilid_id", jilidId)
                parameter("nomor_halaman", nomorHalaman)
            }
            
            // Log raw response text for debugging
            val responseText = response.bodyAsText()
            android.util.Log.d("SignQuranAPI", "Raw response: ${responseText.take(300)}...")
            android.util.Log.d("SignQuranAPI", "Response status: ${response.status}")
            android.util.Log.d("SignQuranAPI", "Response headers: ${response.headers}")
            
            // Try to parse
            val body = try {
                kotlinx.serialization.json.Json.decodeFromString<PageDetailApiResponse>(responseText)
            } catch (e: Exception) {
                android.util.Log.e("SignQuranAPI", "JSON parsing error: ${e.message}")
                android.util.Log.e("SignQuranAPI", "Failed to parse: $responseText")
                throw e
            }
            
            android.util.Log.d("SignQuranAPI", "Page success: ${body.pageDetail.size} items (${body.pageDetail.groupBy { it.baris }.size} baris)")
            Result.success(body)
        } catch (e: Exception) {
            android.util.Log.e("SignQuranAPI", "Page error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get progress untuk semua halaman dalam jilid (untuk user yang login)
     * Digunakan untuk menampilkan status completed di daftar halaman
     */
    suspend fun getJilidProgress(jilidId: Int, authToken: String): Result<JilidProgressListResponse> {
        return try {
            android.util.Log.d("SignQuranAPI", "Fetching jilid progress for jilid: $jilidId")
            val response = client.get("$BASE_URL/progress/halaman/by-jilid/$jilidId") {
                headers.append("Authorization", "Bearer $authToken")
            }
            val body = response.body<JilidProgressListResponse>()
            android.util.Log.d("SignQuranAPI", "Progress fetched: ${body.progress.size} completed pages")
            Result.success(body)
        } catch (e: Exception) {
            android.util.Log.e("SignQuranAPI", "Get jilid progress error: ${e.message}", e)
            // Return empty list on error (user might not have any progress yet)
            Result.success(JilidProgressListResponse(success = true, progress = emptyList()))
        }
    }
    
    /**
     * Check halaman progress untuk user yang sedang login
     * Mengecek apakah halaman sudah diselesaikan atau belum
     */
    suspend fun checkHalamanProgress(halamanId: String, authToken: String): Result<HalamanProgressCheckResponse> {
        return try {
            android.util.Log.d("SignQuranAPI", "Checking progress for halaman: $halamanId")
            val response = client.get("$BASE_URL/progress/halaman/by-page/$halamanId") {
                headers.append("Authorization", "Bearer $authToken")
            }
            val body = response.body<HalamanProgressCheckResponse>()
            android.util.Log.d("SignQuranAPI", "Progress check success - completed: ${body.completed}")
            Result.success(body)
        } catch (e: Exception) {
            // 404 berarti belum ada progress (belum dikerjakan)
            if (e.message?.contains("404") == true) {
                android.util.Log.d("SignQuranAPI", "No progress found (not started yet)")
                Result.success(HalamanProgressCheckResponse(success = true, completed = false))
            } else {
                android.util.Log.e("SignQuranAPI", "Progress check error: ${e.message}", e)
                Result.failure(e)
            }
        }
    }
    
    /**
     * Save atau update halaman progress
     * Dipanggil ketika user menyelesaikan halaman
     */
    suspend fun saveHalamanProgress(halamanId: String, status: Int, authToken: String, userId: String): Result<HalamanProgressResponse> {
        return try {
            android.util.Log.d("SignQuranAPI", "Saving progress - halaman: $halamanId, status: $status")
            val response = client.post("$BASE_URL/progress/halaman") {
                headers.append("Authorization", "Bearer $authToken")
                contentType(ContentType.Application.Json)
                setBody(HalamanProgressRequest(halamanId.toInt(), userId, status == 1))
            }
            val body = response.body<HalamanProgressResponse>()
            android.util.Log.d("SignQuranAPI", "Progress saved successfully")
            Result.success(body)
        } catch (e: Exception) {
            android.util.Log.e("SignQuranAPI", "Save progress error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Join room by code
     */
    suspend fun joinRoom(code: String, authToken: String, userId: String): Result<JoinRoomResponse> {
        return try {
            android.util.Log.d("SignQuranAPI", "Joining room with code: $code")
            val response = client.post("$BASE_URL/enrollments/join") {
                headers.append("Authorization", "Bearer $authToken")
                contentType(ContentType.Application.Json)
                setBody(JoinRoomRequest(code, userId))
            }
            val body = response.body<JoinRoomResponse>()
            android.util.Log.d("SignQuranAPI", "Successfully joined room: ${body.room?.name ?: "Unknown"}")
            Result.success(body)
        } catch (e: Exception) {
            android.util.Log.e("SignQuranAPI", "Join room error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get all rooms that current user has joined
     */
    suspend fun getMyRooms(authToken: String): Result<MyRoomsResponse> {
        return try {
            android.util.Log.d("SignQuranAPI", "Fetching my enrolled rooms")
            android.util.Log.d("SignQuranAPI", "Request URL: $BASE_URL/enrollments/my-rooms")
            
            val response = client.get("$BASE_URL/enrollments/my-rooms") {
                headers.append("Authorization", "Bearer $authToken")
            }
            android.util.Log.d("SignQuranAPI", "Response status: ${response.status}")
            
            var responseText = response.bodyAsText()
            android.util.Log.d("SignQuranAPI", "Raw response: $responseText")
            
            // Remove BOM if present (can cause "offset 2" JSON error)
            if (responseText.startsWith("\uFEFF")) {
                responseText = responseText.substring(1)
                android.util.Log.d("SignQuranAPI", "Removed BOM from response")
            }
            
            // Parse response directly into MyRoomsResponse
            val body = try {
                Json.decodeFromString<MyRoomsResponse>(responseText)
            } catch (parseError: Exception) {
                android.util.Log.e("SignQuranAPI", "JSON Parse error: ${parseError.message}", parseError)
                android.util.Log.e("SignQuranAPI", "Response text length: ${responseText.length}")
                android.util.Log.e("SignQuranAPI", "First 200 chars: ${responseText.take(200)}")
                android.util.Log.e("SignQuranAPI", "Response bytes: ${responseText.take(50).toByteArray().joinToString(",") { it.toString() }}")
                throw parseError
            }
            
            android.util.Log.d("SignQuranAPI", "Enrolled rooms: ${body.rooms.size}")
            for ((index, room) in body.rooms.withIndex()) {
                android.util.Log.d("SignQuranAPI", "Room $index: ${room.name}, Code: ${room.roomCode}, Creator: ${room.createdByName}")
            }
            
            Result.success(body)
        } catch (e: Exception) {
            android.util.Log.e("SignQuranAPI", "Get my rooms error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get all members in a specific room
     */
    suspend fun getRoomMembers(roomId: Int, authToken: String): Result<RoomMembersResponse> {
        return try {
            android.util.Log.d("SignQuranAPI", "Fetching members for room: $roomId")
            val response = client.get("$BASE_URL/enrollments/room/$roomId/members") {
                headers.append("Authorization", "Bearer $authToken")
            }
            val body = response.body<RoomMembersResponse>()
            android.util.Log.d("SignQuranAPI", "Room members: ${body.members.size}")
            Result.success(body)
        } catch (e: Exception) {
            android.util.Log.e("SignQuranAPI", "Get room members error: ${e.message}", e)
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
