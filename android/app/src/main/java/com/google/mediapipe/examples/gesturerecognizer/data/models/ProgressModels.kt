package com.google.mediapipe.examples.gesturerecognizer.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model untuk halaman progress
 */
@Serializable
data class HalamanProgressRequest(
    @SerialName("halaman_id")
    val halamanId: Int,
    
    @SerialName("user_id")
    val userId: Int,
    
    @SerialName("completed")
    val completed: Boolean = true
)

/**
 * Response model untuk halaman progress
 */
@Serializable
data class HalamanProgressResponse(
    @SerialName("success")
    val success: Boolean,
    
    @SerialName("message")
    val message: String,
    
    @SerialName("progress_id")
    val progressId: Int? = null
)

/**
 * Response model untuk check halaman progress
 */
@Serializable
data class HalamanProgressCheckResponse(
    @SerialName("success")
    val success: Boolean,
    
    @SerialName("completed")
    val completed: Boolean,
    
    @SerialName("message")
    val message: String? = null
)

/**
 * Response model untuk list progress jilid
 */
@Serializable
data class JilidProgressListResponse(
    @SerialName("success")
    val success: Boolean,
    
    @SerialName("progress")
    val progress: List<JilidProgress>
)

/**
 * Model untuk progress jilid
 */
@Serializable
data class JilidProgress(
    @SerialName("halaman_id")
    val halamanId: Int,
    
    @SerialName("nomor_halaman")
    val nomorHalaman: Int,
    
    @SerialName("deskripsi")
    val deskripsi: String,
    
    @SerialName("completed")
    val completed: Boolean,
    
    @SerialName("completed_at")
    val completedAt: String? = null
)

/**
 * Response model untuk pages jilid
 */
@Serializable
data class JilidPagesApiResponse(
    @SerialName("success")
    val success: Boolean,
    
    @SerialName("pages")
    val pages: List<HalamanInfo>
)

/**
 * Model untuk informasi halaman
 */
@Serializable
data class HalamanInfo(
    @SerialName("halaman_id")
    val halamanId: Int,
    
    @SerialName("nomor_halaman")
    val nomorHalaman: Int,
    
    @SerialName("deskripsi")
    val deskripsi: String,
    
    @SerialName("is_completed")
    var isCompleted: Boolean = false
)

/**
 * Response model untuk progress huruf per user
 */
@Serializable
data class LetterProgressResponse(
    @SerialName("progress")
    val progress: List<LetterProgressEntry> = emptyList()
)

@Serializable
data class LetterProgressSingleResponse(
    @SerialName("progress")
    val progress: LetterProgressEntry
)

/**
 * Model detail progress huruf
 */
@Serializable
data class LetterProgressEntry(
    @SerialName("progress_id")
    val progressId: Int,
    
    @SerialName("user_id")
    val userId: Int,
    
    @SerialName("room_id")
    val roomId: Int? = null,
    
    @SerialName("hijaiyah_id")
    val hijaiyahId: Int? = null,
    
    @SerialName("status")
    @Serializable(with = FlexibleStringSerializer::class)
    val status: String = "",
    
    @SerialName("latin_name")
    val latinName: String? = null,
    
    @SerialName("arabic_char")
    val arabicChar: String? = null,
    
    @SerialName("user_name")
    val userName: String? = null
)

/**
 * Response model untuk practice progress list
 */
@Serializable
data class PracticeProgressListResponse(
    @SerialName("practices")
    val practices: List<PracticeProgressEntry> = emptyList()
)

/**
 * Response model untuk practice progress single item
 */
@Serializable
data class PracticeProgressSingleResponse(
    @SerialName("practice")
    val practice: PracticeProgressEntry
)

/**
 * Practice progress entry detail
 */
@Serializable
data class PracticeProgressEntry(
    @SerialName("practice_id")
    val practiceId: Int? = null,
    
    @SerialName("user_id")
    val userId: Int,
    
    @SerialName("hijaiyah_id")
    val hijaiyahId: Int,
    
    @SerialName("status")
    val status: String? = null,
    
    @SerialName("attempts")
    val attempts: Int? = null,
    
    @SerialName("last_update")
    val lastUpdate: String? = null,
    
    @SerialName("latin_name")
    val latinName: String? = null,
    
    @SerialName("arabic_char")
    val arabicChar: String? = null,
    
    @SerialName("user_name")
    val userName: String? = null
)

/**
 * Response model untuk progress jilid per user
 */
@Serializable
data class UserJilidProgressResponse(
    @SerialName("progress")
    val progress: List<UserJilidProgressEntry> = emptyList()
)

/**
 * Model detail progress jilid
 */
@Serializable
data class UserJilidProgressEntry(
    @SerialName("user_jilid_id")
    val userJilidId: Int,
    
    @SerialName("user_id")
    val userId: Int,
    
    @SerialName("room_id")
    val roomId: Int? = null,
    
    @SerialName("jilid_id")
    val jilidId: Int? = null,
    
    @SerialName("status")
    @Serializable(with = FlexibleStringSerializer::class)
    val status: String = "",
    
    @SerialName("jilid_name")
    val jilidName: String? = null,
    
    @SerialName("description")
    val description: String? = null,
    
    @SerialName("user_name")
    val userName: String? = null
)

/**
 * Request model for updating letter progress
 */
@Serializable
data class UpdateLetterProgressRequest(
    @SerialName("hijaiyahId")
    val hijaiyahId: Int
)

/**
 * Letter progress data from API response
 */
@Serializable
data class LetterProgressData(
    @SerialName("progress_id")
    val progressId: Int? = null,
    
    @SerialName("user_id")
    val userId: Int,
    
    @SerialName("hijaiyah_id")
    val hijaiyahId: Int,
    
    @SerialName("last_update")
    val lastUpdate: String
)

/**
 * Response model for letter progress update
 */
@Serializable
data class UpdateLetterProgressResponse(
    @SerialName("progress")
    val progress: LetterProgressData
)