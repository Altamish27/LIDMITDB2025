package com.google.mediapipe.examples.gesturerecognizer.data.models

import kotlinx.serialization.Serializable

@Serializable
data class AladhanTimingsResponse(
    val code: Int,
    val status: String,
    val data: AladhanTimingsData
)

@Serializable
data class AladhanTimingsData(
    val timings: PrayerTimings,
    val date: DateInfo? = null,
    val meta: MetaInfo? = null
)

@Serializable
data class PrayerTimings(
    val Fajr: String = "",
    val Sunrise: String = "",
    val Dhuhr: String = "",
    val Asr: String = "",
    val Sunset: String = "",
    val Maghrib: String = "",
    val Isha: String = "",
    val Imsak: String = ""
) {
    val fajr: String get() = Fajr
    val dhuhr: String get() = Dhuhr
    val asr: String get() = Asr
    val maghrib: String get() = Maghrib
    val isha: String get() = Isha
}

@Serializable
data class DateInfo(
    val readable: String = "",
    val timestamp: Long = 0,
    val gregorian: DateDetail? = null,
    val hijri: DateDetail? = null
)

@Serializable
data class DateDetail(
    val date: String = "",
    val timestamp: Long = 0,
    val month: MonthInfo? = null,
    val year: String = ""
)

@Serializable
data class MonthInfo(
    val number: Int = 0,
    val en: String = "",
    val ar: String = ""
)

@Serializable
data class MetaInfo(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timezone: String = "",
    val method: MethodInfo? = null
)

@Serializable
data class MethodInfo(
    val id: Int = 0,
    val name: String = "",
    val params: MethodParams? = null
)

@Serializable
data class MethodParams(
    val Fajr: Double = 0.0,
    val Isha: Double = 0.0
)
