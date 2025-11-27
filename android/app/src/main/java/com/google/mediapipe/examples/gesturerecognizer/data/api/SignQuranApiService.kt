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
import com.google.mediapipe.examples.gesturerecognizer.data.models.RoomMember
import com.google.mediapipe.examples.gesturerecognizer.data.models.EnrollmentsResponse
import com.google.mediapipe.examples.gesturerecognizer.data.models.EnrolledRoom
import com.google.mediapipe.examples.gesturerecognizer.data.models.SimpleRoomsResponse
import com.google.mediapipe.examples.gesturerecognizer.data.models.HalamanInfo
import com.google.mediapipe.examples.gesturerecognizer.data.models.LetterProgressEntry
import com.google.mediapipe.examples.gesturerecognizer.data.models.LetterProgressResponse
import com.google.mediapipe.examples.gesturerecognizer.data.models.LetterProgressSingleResponse
import com.google.mediapipe.examples.gesturerecognizer.data.models.PracticeProgressEntry
import com.google.mediapipe.examples.gesturerecognizer.data.models.PracticeProgressListResponse
import com.google.mediapipe.examples.gesturerecognizer.data.models.PracticeProgressSingleResponse
import com.google.mediapipe.examples.gesturerecognizer.data.models.UserJilidProgressResponse
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
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement

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
            if (body.pages.isNotEmpty()) {
                Result.success(body)
            } else {
                android.util.Log.w("SignQuranAPI", "Pages list empty, falling back to detail scanning")
                fetchPagesFromDetail(jilidId, authToken)
            }
        } catch (e: Exception) {
            android.util.Log.e("SignQuranAPI", "Pages error: ${e.message}", e)
            val fallback = fetchPagesFromDetail(jilidId, authToken)
            return if (fallback.isSuccess) fallback else Result.failure(e)
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
                    jsonFormatter.decodeFromString<PageDetailApiResponse>(responseText)
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
     * Fallback builder untuk daftar halaman ketika endpoint /jilid/{id}/pages tidak tersedia
     */
    private suspend fun fetchPagesFromDetail(jilidId: Int, authToken: String?): Result<JilidPagesApiResponse> {
        android.util.Log.d("SignQuranAPI", "Fallback: scanning halaman detail for jilid $jilidId")
        val fallbackPages = mutableListOf<HalamanInfo>()
        var consecutiveEmpty = 0
        val maxConsecutiveEmpty = 3
        val maxPagesToCheck = 30

        for (pageNum in 1..maxPagesToCheck) {
            val detailResult = getPageDetail(jilidId, pageNum, authToken)
            var found = false
            detailResult.onSuccess { response ->
                if (response.pageDetail.isNotEmpty()) {
                    val sample = response.pageDetail.first()
                    val halamanNumericId = sample.hijaiyahHalamanId
                    val description = "Halaman ${sample.nomorHalaman} - ${sample.latinName}"
                    fallbackPages.add(
                        HalamanInfo(
                            halamanId = halamanNumericId,
                            nomorHalaman = sample.nomorHalaman,
                            deskripsi = description,
                            isCompleted = false
                        )
                    )
                    consecutiveEmpty = 0
                    found = true
                }
            }

            if (!found) {
                consecutiveEmpty++
                if (consecutiveEmpty >= maxConsecutiveEmpty) {
                    android.util.Log.d("SignQuranAPI", "Stopping detail scan after $consecutiveEmpty empty pages")
                    break
                }
            }
        }

        return if (fallbackPages.isNotEmpty()) {
            android.util.Log.d("SignQuranAPI", "Fallback produced ${fallbackPages.size} halaman for jilid $jilidId")
            Result.success(JilidPagesApiResponse(success = true, pages = fallbackPages))
        } else {
            android.util.Log.e("SignQuranAPI", "Fallback failed to find pages for jilid $jilidId")
            Result.failure(IllegalStateException("Tidak menemukan halaman untuk jilid $jilidId"))
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
            Result.success(JilidProgressListResponse(progress = emptyList()))
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
                Result.success(HalamanProgressCheckResponse(completed = false))
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
            val numericUserId = userId.toIntOrNull()
                ?: throw IllegalArgumentException("User ID tidak valid")
            val response = client.post("$BASE_URL/progress/halaman") {
                headers.append("Authorization", "Bearer $authToken")
                contentType(ContentType.Application.Json)
                setBody(HalamanProgressRequest(halamanId.toInt(), numericUserId, status == 1))
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
     * Get letter progress overview untuk user saat ini
     */
    suspend fun getLetterProgress(
        authToken: String,
        roomId: String? = null,
        targetUserId: String? = null,
        status: String? = null
    ): Result<LetterProgressResponse> {
        return try {
            android.util.Log.d("SignQuranAPI", "Fetching letter progress from: $BASE_URL/progress/letter")
            val response = client.get("$BASE_URL/progress/letter") {
                headers.append("Authorization", "Bearer $authToken")
                roomId?.let { parameter("roomId", it) }
                targetUserId?.let { parameter("targetUserId", it) }
                status?.let { parameter("status", it) }
            }
            val body = response.body<LetterProgressResponse>()
            android.util.Log.d("SignQuranAPI", "Letter progress fetched: ${body.progress.size} entries")
            Result.success(body)
        } catch (e: Exception) {
            android.util.Log.e("SignQuranAPI", "Get letter progress error: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Submit or update letter progress for a hijaiyah in a room
     */
    suspend fun submitLetterProgress(
        roomId: Int,
        hijaiyahId: Int,
        status: String,
        authToken: String
    ): Result<LetterProgressEntry> {
        return try {
            android.util.Log.d("SignQuranAPI", "Submitting letter progress room=$roomId, hijaiyah=$hijaiyahId, status=$status")
            val response = client.post("$BASE_URL/progress/letter") {
                headers.append("Authorization", "Bearer $authToken")
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "roomId" to roomId,
                        "hijaiyahId" to hijaiyahId,
                        "status" to status
                    )
                )
            }
            val body = response.body<LetterProgressSingleResponse>()
            Result.success(body.progress)
        } catch (e: Exception) {
            android.util.Log.e("SignQuranAPI", "Submit letter progress error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get jilid progress overview untuk user saat ini
     */
    suspend fun getUserJilidProgress(
        authToken: String,
        roomId: String? = null,
        targetUserId: String? = null,
        status: String? = null
    ): Result<UserJilidProgressResponse> {
        return try {
            android.util.Log.d("SignQuranAPI", "Fetching jilid progress from: $BASE_URL/progress/jilid")
            val response = client.get("$BASE_URL/progress/jilid") {
                headers.append("Authorization", "Bearer $authToken")
                roomId?.let { parameter("roomId", it) }
                targetUserId?.let { parameter("targetUserId", it) }
                status?.let { parameter("status", it) }
            }
            val body = response.body<UserJilidProgressResponse>()
            android.util.Log.d("SignQuranAPI", "Jilid progress fetched: ${body.progress.size} entries")
            Result.success(body)
        } catch (e: Exception) {
            android.util.Log.e("SignQuranAPI", "Get jilid progress error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Submit practice progress for a specific hijaiyah
     */
    suspend fun submitPracticeProgress(
        hijaiyahId: Int,
        status: String,
        attempts: Int = 1,
        authToken: String
    ): Result<PracticeProgressEntry> {
        return try {
            android.util.Log.d("SignQuranAPI", "Submitting practice progress: hijaiyah=$hijaiyahId status=$status attempts=$attempts")
            val response = client.post("$BASE_URL/practice") {
                headers.append("Authorization", "Bearer $authToken")
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "hijaiyahId" to hijaiyahId,
                        "status" to status,
                        "attempts" to attempts
                    )
                )
            }
            val body = response.body<PracticeProgressSingleResponse>()
            android.util.Log.d("SignQuranAPI", "Practice progress saved with id ${body.practice.practiceId}")
            Result.success(body.practice)
        } catch (e: Exception) {
            android.util.Log.e("SignQuranAPI", "Submit practice progress error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get practice progress list for logged in user
     */
    suspend fun getPracticeProgress(
        authToken: String,
        status: String? = null
    ): Result<List<PracticeProgressEntry>> {
        return try {
            android.util.Log.d("SignQuranAPI", "Fetching practice progress")
            val response = client.get("$BASE_URL/practice") {
                headers.append("Authorization", "Bearer $authToken")
                status?.let { parameter("status", it) }
            }
            val body = response.body<PracticeProgressListResponse>()
            Result.success(body.practices)
        } catch (e: Exception) {
            android.util.Log.e("SignQuranAPI", "Get practice progress error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Join room by code
     */
    suspend fun joinRoom(code: String, authToken: String, userId: String): Result<JoinRoomResponse> {
        return try {
            android.util.Log.d("SignQuranAPI", "Joining room with code: $code")
            val numericUserId = userId.toIntOrNull()
                ?: throw IllegalArgumentException("User ID tidak valid")
            val response = client.post("$BASE_URL/enrollments/join") {
                headers.append("Authorization", "Bearer $authToken")
                contentType(ContentType.Application.Json)
<<<<<<< HEAD
                setBody(JoinRoomRequest(code))
=======
                setBody(JoinRoomRequest(code, numericUserId))
>>>>>>> origin/main
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
                jsonFormatter.decodeFromString<MyRoomsResponse>(responseText)
            } catch (parseError: Exception) {
                android.util.Log.e("SignQuranAPI", "JSON Parse error: ${parseError.message}", parseError)
                android.util.Log.e("SignQuranAPI", "Response text length: ${responseText.length}")
                android.util.Log.e("SignQuranAPI", "First 200 chars: ${responseText.take(200)}")
                android.util.Log.e("SignQuranAPI", "Response bytes: ${responseText.take(50).toByteArray().joinToString(",") { it.toString() }}")
                throw parseError
            }
            
            android.util.Log.d("SignQuranAPI", "Enrolled rooms: ${body.rooms.size}")
            for ((index, room) in body.rooms.withIndex()) {
                val displayCode = room.roomCode ?: room.code ?: "-"
                android.util.Log.d("SignQuranAPI", "Room $index: ${room.name}, Code: $displayCode, Creator: ${room.createdByName}")
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
    suspend fun getRoomMembers(roomId: Int, roomCode: String?, authToken: String): Result<RoomMembersResponse> {
        val url = "$BASE_URL/enrollments/room/$roomId/members"
        return try {
            android.util.Log.d("SignQuranAPI", "Fetching room members from enrollments endpoint: $url")
            val body = fetchRoomMembersFromUrl(url, authToken)
            Result.success(body)
        } catch (error: Exception) {
            android.util.Log.e("SignQuranAPI", "Get room members error: ${error.message}", error)
            Result.failure(error)
        }
    }
    
    private suspend fun fetchRoomMembersFromUrl(url: String, authToken: String): RoomMembersResponse {
        val response = client.get(url) {
            headers.append("Authorization", "Bearer $authToken")
        }
        val responseText = response.bodyAsText()
        android.util.Log.d("SignQuranAPI", "Room members raw response: ${responseText.take(200)}")
        
        val jsonElement = jsonFormatter.parseToJsonElement(responseText)
        val members = mutableListOf<RoomMember>()
        
        when (jsonElement) {
            is JsonArray -> members += parseRoomMembersArray(jsonElement, null, null)
            is JsonObject -> {
                extractMemberArrays(jsonElement, "teachers").forEach { array ->
                    members += parseRoomMembersArray(array, defaultRole = "guru", isCreatorDefault = true)
                }
                extractMemberArrays(jsonElement, "students").forEach { array ->
                    members += parseRoomMembersArray(array, defaultRole = "murid", isCreatorDefault = false)
                }
                if (members.isEmpty()) {
                    extractMemberArrays(jsonElement, "members").forEach { array ->
                        members += parseRoomMembersArray(array, defaultRole = null, isCreatorDefault = null)
                    }
                }
                if (members.isEmpty()) {
                    extractMemberArrays(jsonElement, "enrollments").forEach { array ->
                        members += parseRoomMembersArray(array, defaultRole = "murid", isCreatorDefault = false)
                    }
                }
                if (members.isEmpty()) {
                    (jsonElement["data"] as? JsonArray)?.let { dataArray ->
                        members += parseRoomMembersArray(dataArray, null, null)
                    }
                }
            }
            else -> android.util.Log.w("SignQuranAPI", "Unexpected room members payload type: ${jsonElement::class}")
        }
        
        val successFlag = (jsonElement as? JsonObject)?.get("success")?.jsonPrimitive?.booleanOrNull
        android.util.Log.d("SignQuranAPI", "Parsed ${members.size} members")
        return RoomMembersResponse(success = successFlag, members = members)
    }
    
    private fun parseRoomMembersArray(
        array: JsonArray,
        defaultRole: String?,
        isCreatorDefault: Boolean?
    ): List<RoomMember> {
        val members = mutableListOf<RoomMember>()
        for (element in array) {
            parseRoomMemberElement(element, defaultRole, isCreatorDefault)?.let { members.add(it) }
        }
        return members
    }
    
    private fun parseRoomMemberElement(
        element: JsonElement,
        defaultRole: String?,
        isCreatorDefault: Boolean?
    ): RoomMember? {
        val container = element as? JsonObject ?: return null
        val userObj = container["user"]?.jsonObject ?: container
        
        val userId = userObj["user_id"]?.jsonPrimitive?.contentOrNull
            ?: userObj["id"]?.jsonPrimitive?.contentOrNull
            ?: container["user_id"]?.jsonPrimitive?.contentOrNull
            ?: container["id"]?.jsonPrimitive?.contentOrNull
            ?: return null
        
        val name = userObj["name"]?.jsonPrimitive?.contentOrNull
            ?: container["name"]?.jsonPrimitive?.contentOrNull
            ?: "Pengguna"
        
        val email = userObj["email"]?.jsonPrimitive?.contentOrNull
            ?: container["email"]?.jsonPrimitive?.contentOrNull
            ?: ""
        
        val role = container["role"]?.jsonPrimitive?.contentOrNull
            ?: userObj["role"]?.jsonPrimitive?.contentOrNull
            ?: container["type"]?.jsonPrimitive?.contentOrNull
            ?: defaultRole
            ?: "murid"
        
        val isCreator = container["is_creator"]?.jsonPrimitive?.booleanOrNull
            ?: userObj["is_creator"]?.jsonPrimitive?.booleanOrNull
            ?: isCreatorDefault
            ?: role.equals("guru", ignoreCase = true)
        
        val joinedAt = container["joined_at"]?.jsonPrimitive?.contentOrNull
            ?: container["created_at"]?.jsonPrimitive?.contentOrNull
            ?: ""
        
        return RoomMember(
            userId = userId,
            name = name,
            email = email,
            role = role,
            isCreator = isCreator,
            joinedAt = joinedAt
        )
    }
    
    private fun extractMemberArrays(jsonObject: JsonObject, key: String): List<JsonArray> {
        val arrays = mutableListOf<JsonArray>()
        
        jsonObject[key]?.let { value ->
            (value as? JsonArray)?.let { arrays.add(it) }
        }
        
        jsonObject["room"]?.jsonObject?.get(key)?.let { value ->
            (value as? JsonArray)?.let { arrays.add(it) }
        }
        
        jsonObject["data"]?.jsonObject?.get(key)?.let { value ->
            (value as? JsonArray)?.let { arrays.add(it) }
        }
        
        return arrays
    }
    
    /**
     * Cleanup resources
     */
    fun close() {
        client.close()
    }
}
