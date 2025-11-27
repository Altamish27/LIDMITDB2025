package com.google.mediapipe.examples.gesturerecognizer.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Model untuk User
 */
@Serializable
data class User(
    @SerialName("user_id")
    @Serializable(with = FlexibleStringSerializer::class)
    val userId: String = "",
    
    @SerialName("name")
    val name: String = "",
    
    @SerialName("email")
    val email: String = "",
    
    @SerialName("role")
    val role: String = "",
    
    @SerialName("created_at")
    val createdAt: String? = null,
    
    @SerialName("updated_at")
    val updatedAt: String? = null
)

/**
 * Response model untuk user profile
 */
@Serializable
data class UserProfileResponse(
    @SerialName("success")
    val success: Boolean,
    
    @SerialName("user")
    val user: User? = null,
    
    @SerialName("message")
    val message: String? = null
)

/**
 * Request model untuk update user profile
 */
@Serializable
data class UpdateUserProfileRequest(
    @SerialName("name")
    val name: String,
    
    @SerialName("email")
    val email: String
)
