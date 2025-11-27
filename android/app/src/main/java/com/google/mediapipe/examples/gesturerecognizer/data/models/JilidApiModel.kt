package com.google.mediapipe.examples.gesturerecognizer.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model untuk API Jilid
 */
@Serializable
data class JilidApiResponse(
    @SerialName("jilid")
    val jilid: List<JilidApi>
)

/**
 * Model untuk setiap jilid dari API
 */
@Serializable
data class JilidApi(
    @SerialName("jilid_id")
    val jilidId: Int,
    
    @SerialName("jilid_name")
    val jilidName: String,
    
    @SerialName("description")
    val description: String
)
