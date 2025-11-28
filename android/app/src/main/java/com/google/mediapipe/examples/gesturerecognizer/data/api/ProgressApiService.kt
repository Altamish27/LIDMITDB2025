package com.google.mediapipe.examples.gesturerecognizer.data.api

import android.util.Log
import com.google.mediapipe.examples.gesturerecognizer.data.models.*
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
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull

/**
 * Progress API Service for updating user progress
 */
class ProgressApiService {
    
    companion object {
        private const val BASE_URL = "https://signquran.site/api"
        private const val TAG = "ProgressApiService"
        
        @Volatile
        private var INSTANCE: ProgressApiService? = null
        
        fun getInstance(): ProgressApiService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ProgressApiService().also { INSTANCE = it }
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
     * Update letter progress for the authenticated user
     * 
     * @param token Authentication token
     * @param hijaiyahId ID of the hijaiyah letter
     * @return Result containing the progress response or error
     */
    suspend fun updateLetterProgress(token: String, hijaiyahId: Int): Result<UpdateLetterProgressResponse> {
        return try {
            Log.d(TAG, "Updating letter progress for hijaiyahId: $hijaiyahId")
            
            val response = client.post("$BASE_URL/progress/letter") {
                headers.append("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(UpdateLetterProgressRequest(hijaiyahId))
            }
            
            Log.d(TAG, "Letter progress update response status: ${response.status}")
            
            if (response.status == HttpStatusCode.OK) {
                val body = response.body<UpdateLetterProgressResponse>()
                Log.d(TAG, "Letter progress updated successfully for hijaiyahId: $hijaiyahId")
                Result.success(body)
            } else {
                val errorMessage = response.extractErrorMessage()
                Log.e(TAG, "Letter progress update failed: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Letter progress update error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get letter progress for the authenticated user
     * 
     * @param token Authentication token
     * @return Result containing list of completed letters or error
     */
    suspend fun getLetterProgress(token: String): Result<LetterProgressResponse> {
        return try {
            Log.d(TAG, "Fetching letter progress from server")
            
            val response = client.get("$BASE_URL/progress/letter") {
                headers.append("Authorization", "Bearer $token")
            }
            
            Log.d(TAG, "Letter progress fetch response status: ${response.status}")
            
            if (response.status == HttpStatusCode.OK) {
                val body = response.body<LetterProgressResponse>()
                Log.d(TAG, "Successfully fetched ${body.progress.size} letter progress records")
                Result.success(body)
            } else {
                val errorMessage = response.extractErrorMessage()
                Log.e(TAG, "Letter progress fetch failed: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Letter progress fetch error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Cleanup resources
     */
    fun close() {
        client.close()
    }
    
    private suspend fun HttpResponse.extractErrorMessage(): String {
        return try {
            val raw = bodyAsText()
            if (raw.isBlank()) {
                "Request failed (${status.value})"
            } else {
                val jsonElement = Json.parseToJsonElement(raw)
                if (jsonElement is JsonObject) {
                    jsonElement["message"]?.jsonPrimitive?.contentOrNull
                        ?: jsonElement["error"]?.jsonPrimitive?.contentOrNull
                        ?: raw
                } else {
                    raw
                }
            }
        } catch (e: Exception) {
            "Request failed (${status.value})"
        }
    }
}
