package com.google.mediapipe.examples.gesturerecognizer.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HijaiyahApiResponse(
    val letters: List<HijaiyahLetterApi> = emptyList()
)

@Serializable
data class HijaiyahLetterApi(
    @SerialName("hijaiyah_id")
    val hijaiyahId: Int,
    @SerialName("latin_name")
    val latinName: String,
    @SerialName("arabic_char")
    val arabicChar: String,
    val ordinal: Int,
    val assets: String? = null  // URL to image/video asset or null
)