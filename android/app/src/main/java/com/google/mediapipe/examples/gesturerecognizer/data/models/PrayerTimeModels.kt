package com.google.mediapipe.examples.gesturerecognizer.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AladhanTimingsResponse(
    @SerialName("code")
    val code: Int,
    @SerialName("status")
    val status: String,
    @SerialName("data")
    val data: AladhanTimingsData
)

@Serializable
data class AladhanTimingsData(
    @SerialName("timings")
    val timings: AladhanTimings,
    @SerialName("date")
    val date: AladhanDate,
    @SerialName("meta")
    val meta: AladhanMeta
)

@Serializable
data class AladhanTimings(
    @SerialName("Fajr")
    val fajr: String,
    @SerialName("Sunrise")
    val sunrise: String,
    @SerialName("Dhuhr")
    val dhuhr: String,
    @SerialName("Asr")
    val asr: String,
    @SerialName("Maghrib")
    val maghrib: String,
    @SerialName("Isha")
    val isha: String
)

@Serializable
data class AladhanDate(
    @SerialName("readable")
    val readable: String,
    @SerialName("timestamp")
    val timestamp: String
)

@Serializable
data class AladhanMeta(
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("timezone")
    val timezone: String,
    @SerialName("method")
    val method: AladhanMethod
)

@Serializable
data class AladhanMethod(
    @SerialName("name")
    val name: String,
    @SerialName("id")
    val id: Int
)

