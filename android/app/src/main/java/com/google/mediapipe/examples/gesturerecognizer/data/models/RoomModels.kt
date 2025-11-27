package com.google.mediapipe.examples.gesturerecognizer.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model untuk join room
 */
@Serializable
data class JoinRoomRequest(
    @SerialName("room_code")
    val roomCode: String,
    
    @SerialName("user_id")
    val userId: Int
)

/**
 * Response model untuk join room
 */
@Serializable
data class JoinRoomResponse(
    @SerialName("success")
    val success: Boolean,
    
    @SerialName("message")
    val message: String,
    
    @SerialName("room")
    val room: RoomInfo? = null
)

/**
 * Model untuk informasi room
 */
@Serializable
data class RoomInfo(
    @SerialName("room_id")
    val roomId: Int,
    
    @SerialName("name")
    val name: String,
    
    @SerialName("description")
    val description: String? = null,
    
    @SerialName("room_code")
    val roomCode: String? = null,
    
    @SerialName("created_by")
    val createdBy: String,
    
    @SerialName("created_by_name")
    val createdByName: String,
    
    @SerialName("created_at")
    val createdAt: String
)

/**
 * Response model untuk my rooms
 */
@Serializable
data class MyRoomsResponse(
    @SerialName("success")
    val success: Boolean? = null,
    
    @SerialName("rooms")
    val rooms: List<EnrolledRoom>
)

/**
 * Model untuk enrolled room
 */
@Serializable
data class EnrolledRoom(
    @SerialName("enrollment_id")
    val enrollmentId: Int,
    
    @SerialName("room_id")
    val roomId: Int,
    
    @SerialName("name")
    val name: String = "",
    
    @SerialName("description")
    val description: String? = null,
    
    @SerialName("room_code")
    val roomCode: String? = null,
    
    @SerialName("code")
    @Serializable(with = FlexibleStringSerializer::class)
    val code: String? = null,
    
    @SerialName("created_by_name")
    val createdByName: String = "",
    
    @SerialName("joined_at")
    val joinedAt: String = ""
)

/**
 * Response model untuk room members
 */
@Serializable
data class RoomMembersResponse(
    @SerialName("success")
    val success: Boolean? = null,
    
    @SerialName("members")
    val members: List<RoomMember>
)

/**
 * Model untuk room member
 */
@Serializable
data class RoomMember(
    @SerialName("user_id")
    @Serializable(with = FlexibleStringSerializer::class)
    val userId: String = "",
    
    @SerialName("name")
    val name: String = "",
    
    @SerialName("email")
    val email: String = "",
    
    @SerialName("role")
    val role: String = "",
    
    @SerialName("is_creator")
    val isCreator: Boolean = false,
    
    @SerialName("joined_at")
    val joinedAt: String = ""
)

/**
 * Response model untuk enrollments
 */
@Serializable
data class EnrollmentsResponse(
    @SerialName("success")
    val success: Boolean,
    
    @SerialName("enrollments")
    val enrollments: List<EnrolledRoom>
)

/**
 * Response model untuk simple rooms
 */
@Serializable
data class SimpleRoomsResponse(
    @SerialName("success")
    val success: Boolean,
    
    @SerialName("rooms")
    val rooms: List<SimpleRoom>
)

/**
 * Model untuk simple room
 */
@Serializable
data class SimpleRoom(
    @SerialName("room_id")
    val roomId: Int,
    
    @SerialName("name")
    val name: String,
    
    @SerialName("description")
    val description: String,
    
    @SerialName("created_by_name")
    val createdByName: String
)
