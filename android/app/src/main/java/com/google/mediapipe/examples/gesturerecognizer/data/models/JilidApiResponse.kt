package com.google.mediapipe.examples.gesturerecognizer.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JilidApiResponse(
    val jilid: List<JilidApi> = emptyList()
)

@Serializable
data class JilidApi(
    @SerialName("jilid_id")
    val jilidId: Int,
    @SerialName("jilid_name")
    val jilidName: String,
    val description: String
)

@Serializable
data class PageDetailApiResponse(
    @SerialName("pageDetail")
    val pageDetail: List<PageDetailItem> = emptyList()
)

@Serializable
data class PageDetailItem(
    // Primary fields
    @SerialName("jilid_id") val jilidId: Int,
    @SerialName("jilid_name") val jilidName: String,
    @SerialName("halaman_id") val halamanId: String,
    @SerialName("nomor_halaman") val nomorHalaman: Int,
    @SerialName("hijaiyah_halaman_id") val hijaiyahHalamanId: Int,
    @SerialName("hijaiyah_id") val hijaiyahId: Int,
    
    // Letter information
    @SerialName("latin_name") val hurufLatin: String,
    @SerialName("arabic_char") val hurufArab: String,
    
    // Position information
    val baris: Int,
    val urutan: Int,
    
    // Optional fields with defaults (for backward compatibility)
    val transliterasi: String = "",
    val arti: String = "",
    @SerialName("contoh_pembelajaran") val contohPembelajaran: String = "",
    val penjelasan: String = ""
)

@Serializable
data class JilidPagesApiResponse(
    val pages: List<HalamanInfo>
)

@Serializable
data class HalamanInfo(
    @SerialName("halaman_id") val halamanId: String,
    @SerialName("nomor_halaman") val nomorHalaman: Int,
    val deskripsi: String,
    @SerialName("jilid_id") val jilidId: Int,
    var isCompleted: Boolean = false
)

@Serializable
data class HalamanProgressRequest(
    @SerialName("halamanId") val halamanId: String,
    val status: Int
)

@Serializable
data class HalamanProgressResponse(
    val message: String,
    val progress: HalamanProgressData? = null
)

@Serializable
data class HalamanProgressData(
    @SerialName("user_halaman_id") val userHalamanId: Int,
    @SerialName("user_id") val userId: Int,
    @SerialName("halaman_id") val halamanId: String,
    val status: Int,
    @SerialName("last_update") val lastUpdate: String
)

@Serializable
data class HalamanProgressCheckResponse(
    val completed: Boolean
)

@Serializable
data class JilidProgressListResponse(
    val progress: List<JilidHalamanProgress>
)

@Serializable
data class JilidHalamanProgress(
    @SerialName("halaman_id") val halamanId: String,
    val status: Int,
    @SerialName("nomor_halaman") val nomorHalaman: Int
)
