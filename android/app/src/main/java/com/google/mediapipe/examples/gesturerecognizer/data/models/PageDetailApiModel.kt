package com.google.mediapipe.examples.gesturerecognizer.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model untuk API Page Detail
 */
@Serializable
data class PageDetailApiResponse(
    @SerialName("pageDetail")
    val pageDetail: List<PageDetailItem>
)

/**
 * Model untuk setiap item detail halaman dari API
 */
@Serializable
data class PageDetailItem(
    @SerialName("jilid_id")
    val jilidId: Int,
    
    @SerialName("jilid_name")
    val jilidName: String,
    
    @SerialName("halaman_id")
    val halamanId: String,
    
    @SerialName("nomor_halaman")
    val nomorHalaman: Int,
    
    @SerialName("hijaiyah_halaman_id")
    val hijaiyahHalamanId: Int,
    
    @SerialName("hijaiyah_id")
    val hijaiyahId: Int,
    
    @SerialName("latin_name")
    val latinName: String,
    
    @SerialName("arabic_char")
    val arabicChar: String,
    
    @SerialName("baris")
    val baris: Int,
    
    @SerialName("urutan")
    val urutan: Int
)
