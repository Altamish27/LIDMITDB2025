package com.google.mediapipe.examples.gesturerecognizer.data.api

import com.google.mediapipe.examples.gesturerecognizer.data.manager.AuthManager
import com.google.mediapipe.examples.gesturerecognizer.data.models.HijaiyahApiResponse
import com.google.mediapipe.examples.gesturerecognizer.data.models.JilidApiResponse
import com.google.mediapipe.examples.gesturerecognizer.data.models.PageDetailApiResponse
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
            val body = response.body<PageDetailApiResponse>()
            android.util.Log.d("SignQuranAPI", "Page success: ${body.pageDetail.size} items (${body.pageDetail.groupBy { it.baris }.size} baris)")
            Result.success(body)
        } catch (e: Exception) {
            android.util.Log.e("SignQuranAPI", "Page error: ${e.message}", e)
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
