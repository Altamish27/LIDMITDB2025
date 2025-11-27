package com.google.mediapipe.examples.gesturerecognizer.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model untuk register
 */
@Serializable
data class RegisterRequest(
    @SerialName("name")
    val name: String,
    
    @SerialName("email")
    val email: String,
    
    @SerialName("password")
    val password: String,
    
    @SerialName("role")
    val role: String = "student"
)

/**
 * Response model untuk register
 */
@Serializable
data class RegisterResponse(
    @SerialName("success")
    val success: Boolean? = null,
    
    @SerialName("message")
    val message: String? = null,
    
    @SerialName("user")
    val user: User? = null,
    
    @SerialName("token")
    val token: String? = null,
    
    @SerialName("user_id")
    @Serializable(with = FlexibleStringSerializer::class)
    val userId: String? = null,
    
    @SerialName("email_sent")
    val emailSent: Boolean? = null
)

/**
 * Request model untuk login
 */
@Serializable
data class LoginRequest(
    @SerialName("email")
    val email: String,
    
    @SerialName("password")
    val password: String
)

/**
 * Response model untuk login
 */
@Serializable
data class LoginResponse(
    @SerialName("success")
    val success: Boolean? = null,
    
    @SerialName("message")
    val message: String? = null,
    
    @SerialName("user")
    val user: User? = null,
    
    @SerialName("token")
    val token: String? = null
)

/**
 * Response model untuk verify email
 */
@Serializable
data class VerifyEmailResponse(
    @SerialName("success")
    val success: Boolean? = null,
    
    @SerialName("message")
    val message: String? = null
)

/**
 * Request model untuk resend verification
 */
@Serializable
data class ResendVerificationRequest(
    @SerialName("email")
    val email: String
)

/**
 * Response model untuk resend verification
 */
@Serializable
data class ResendVerificationResponse(
    @SerialName("success")
    val success: Boolean? = null,
    
    @SerialName("message")
    val message: String? = null
)

/**
 * Response model untuk get user
 */
@Serializable
data class GetUserResponse(
    @SerialName("success")
    val success: Boolean? = null,
    
    @SerialName("user")
    val user: User? = null,
    
    @SerialName("message")
    val message: String? = null
)

/**
 * Response model untuk logout
 */
@Serializable
data class LogoutResponse(
    @SerialName("success")
    val success: Boolean? = null,
    
    @SerialName("message")
    val message: String? = null
)
