package com.google.mediapipe.examples.gesturerecognizer.data.api

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
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import io.ktor.client.statement.bodyAsText

/**
 * Authentication API Service for the SignQuran app
 */
class AuthApiService {
    
    companion object {
        private const val BASE_URL = "https://signquran.site/api"
        
        @Volatile
        private var INSTANCE: AuthApiService? = null
        
        fun getInstance(): AuthApiService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthApiService().also { INSTANCE = it }
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
     * Register a new user
     */
    suspend fun register(name: String, email: String, password: String, role: String = "murid"): Result<RegisterResponse> {
        return try {
            val response = client.post("$BASE_URL/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequest(name, email, password, role))
            }
            
            if (response.status.value in 200..299) {
                val body = response.body<RegisterResponse>()
                Result.success(body)
            } else {
                val errorMessage = response.extractErrorMessage()
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthApiService", "Registration error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Login user
     */
    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            android.util.Log.d("AuthApiService", "Attempting login for: $email")
            
            val response = client.post("$BASE_URL/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password))
            }
            
            android.util.Log.d("AuthApiService", "Login response status: ${response.status}")
            
            if (response.status == HttpStatusCode.OK) {
                val body = response.body<LoginResponse>()
                android.util.Log.d("AuthApiService", "Login successful for: $email")
                Result.success(body)
            } else {
                val errorMessage = response.extractErrorMessage()
                android.util.Log.e("AuthApiService", "Login failed: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthApiService", "Login error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Verify user email using token
     */
    suspend fun verifyEmail(token: String): Result<VerifyEmailResponse> {
        return try {
            val response = client.get("$BASE_URL/auth/verify-email") {
                parameter("token", token)
            }
            val body = response.body<VerifyEmailResponse>()
            Result.success(body)
        } catch (e: Exception) {
            android.util.Log.e("AuthApiService", "Email verification error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Resend verification email
     */
    suspend fun resendVerification(email: String): Result<ResendVerificationResponse> {
        return try {
            val response = client.post("$BASE_URL/auth/resend-verification") {
                contentType(ContentType.Application.Json)
                setBody(ResendVerificationRequest(email))
            }
            val body = response.body<ResendVerificationResponse>()
            Result.success(body)
        } catch (e: Exception) {
            android.util.Log.e("AuthApiService", "Resend verification error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get current user info
     */
    suspend fun getCurrentUser(token: String): Result<GetUserResponse> {
        return try {
            val response = client.get("$BASE_URL/auth/me") {
                headers.append("Authorization", "Bearer $token")
            }
            val body = response.body<GetUserResponse>()
            Result.success(body)
        } catch (e: Exception) {
            android.util.Log.e("AuthApiService", "Get user error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Logout user
     */
    suspend fun logout(token: String): Result<LogoutResponse> {
        return try {
            val response = client.post("$BASE_URL/auth/logout") {
                headers.append("Authorization", "Bearer $token")
            }
            val body = response.body<LogoutResponse>()
            Result.success(body)
        } catch (e: Exception) {
            android.util.Log.e("AuthApiService", "Logout error: ${e.message}", e)
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
                "Permintaan gagal (${status.value})"
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
            "Permintaan gagal (${status.value})"
        }
    }
}