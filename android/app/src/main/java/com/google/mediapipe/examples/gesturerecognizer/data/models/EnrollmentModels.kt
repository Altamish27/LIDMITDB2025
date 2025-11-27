package com.google.mediapipe.examples.gesturerecognizer.data.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class JoinRoomRequest(
    val code: String,
    val user_id: Int = 0
)

@Serializable
data class JoinRoomResponse(
    val message: String,
    val enrollment: Enrollment,
    val room: RoomInfo
)

@Serializable
data class Enrollment(
    @SerialName("enrollment_id") val enrollmentId: Int,
    @SerialName("user_id") val userId: Int,
    @SerialName("room_id") val roomId: Int,
    @SerialName("joined_at") val joinedAt: String
)

@Serializable
data class RoomInfo(
    @SerialName("room_id") val roomId: Int,
    val name: String,
    val description: String,
    val code: String,
    @SerialName("created_by") val createdBy: Int,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class MyRoomsResponse(
    val rooms: List<EnrolledRoom> = emptyList()
)

@Serializable
data class EnrolledRoom(
    @SerialName("enrollment_id") val enrollmentId: Int,
    @SerialName("joined_at") val joinedAt: String,
    @SerialName("room_id") val roomId: Int,
    val name: String,
    val description: String,
    val code: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("created_by_name") val createdByName: String,
    @SerialName("created_by") val createdBy: Int? = null
)

@Serializable
data class RoomMembersResponse(
    val members: List<RoomMember>
)

@Serializable
data class RoomMember(
    @SerialName("enrollment_id") val enrollmentId: Int,
    @SerialName("joined_at") val joinedAt: String,
    @SerialName("user_id") val userId: Int,
    val name: String,
    val email: String,
    val role: String,
    @SerialName("is_creator") val isCreator: Boolean
)

// Additional models for fallback support
@Serializable
data class EnrollmentsResponse(
    val enrollments: List<EnrollmentWithRoom>
)

@Serializable
data class EnrollmentWithRoom(
    @SerialName("enrollment_id") val enrollment_id: Int,
    @SerialName("user_id") val user_id: Int,
    @SerialName("room_id") val room_id: Int? = null,
    @SerialName("joined_at") val joined_at: String,
    val name: String? = null,
    val email: String? = null,
    @SerialName("room_name") val room_name: String? = null
)

@Serializable
data class SimpleRoomsResponse(
    val rooms: List<SimpleRoom>
)

@Serializable
data class SimpleRoom(
    @SerialName("enrollment_id") val enrollmentId: Int,
    @SerialName("joined_at") val joinedAt: String,
    @SerialName("room_id") val roomId: Int,
    val name: String,
    val description: String? = null,
    val code: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("created_by_name") val createdByName: String? = null,
    @SerialName("created_by") val createdBy: Int? = null
)
