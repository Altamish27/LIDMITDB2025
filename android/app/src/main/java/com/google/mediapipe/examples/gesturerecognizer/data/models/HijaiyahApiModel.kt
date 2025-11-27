package com.google.mediapipe.examples.gesturerecognizer.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model untuk API Hijaiyah
 */
@Serializable
data class HijaiyahApiResponse(
    @SerialName("letters")
    val letters: List<HijaiyahLetterApi>
)

/**
 * Model untuk setiap huruf dari API
 */
@Serializable
data class HijaiyahLetterApi(
    @SerialName("hijaiyah_id")
    val hijaiyahId: Int,
    
    @SerialName("latin_name")
    val latinName: String,
    
    @SerialName("arabic_char")
    val arabicChar: String,
    
    @SerialName("ordinal")
    val ordinal: Int,
    
    @SerialName("assets")
    val assets: String? = null
)
