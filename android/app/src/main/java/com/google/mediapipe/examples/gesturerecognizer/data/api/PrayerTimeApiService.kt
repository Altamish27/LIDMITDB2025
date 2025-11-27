package com.google.mediapipe.examples.gesturerecognizer.data.api

import com.google.mediapipe.examples.gesturerecognizer.data.models.AladhanTimingsData
import com.google.mediapipe.examples.gesturerecognizer.data.models.AladhanTimingsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class PrayerTimeApiService {

    companion object {
        private const val BASE_URL = "https://api.aladhan.com/v1"
        private const val DEFAULT_METHOD = 20 // Majelis Ulama Indonesia

        @Volatile
        private var INSTANCE: PrayerTimeApiService? = null

        fun getInstance(): PrayerTimeApiService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PrayerTimeApiService().also { INSTANCE = it }
            }
        }
    }

    private val jsonFormatter = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(jsonFormatter)
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    suspend fun getTimings(latitude: Double, longitude: Double): Result<AladhanTimingsData> {
        return try {
            val response = client.get("$BASE_URL/timings") {
                parameter("latitude", latitude)
                parameter("longitude", longitude)
                parameter("method", DEFAULT_METHOD)
                parameter("school", 0)
            }
            val body = response.body<AladhanTimingsResponse>()
            Result.success(body.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

