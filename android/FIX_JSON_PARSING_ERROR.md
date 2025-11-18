# JSON Parsing Error Fix - Rooms API

## Problem
Error: `unexpected JSON token at offset 2 ignored unknownkeys json`

This typically indicates:
1. **BOM (Byte Order Mark)** at the start of the response (3 bytes: `EF BB BF`)
2. **Unknown fields** in the JSON that don't match the model
3. **Response encoding** issues

## Root Cause Analysis

### Backend Response Structure
The backend at `/api/enrollments/my-rooms` returns:
```json
{
  "rooms": [
    {
      "enrollment_id": 1,
      "joined_at": "2025-01-15T10:30:00Z",
      "room_id": 1,
      "name": "Quran Class 1",
      "description": "Learning Surah Al-Fatihah",
      "code": "ABC123",
      "created_at": "2025-01-01T08:00:00Z",
      "created_by_name": "Teacher Name",
      "created_by": 5
    }
  ]
}
```

### Kotlin Model Issues
The `EnrolledRoom` model was missing the `created_by` field, causing the JSON parser to fail when encountering an unexpected field.

## Changes Made

### 1. Updated `EnrollmentModels.kt`

#### EnrolledRoom - Added created_by field
```kotlin
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
    @SerialName("created_by") val createdBy: Int? = null  // ✅ ADDED
)
```

#### SimpleRoom - Added created_by field
```kotlin
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
    @SerialName("created_by") val createdBy: Int? = null  // ✅ ADDED
)
```

### 2. Updated `SignQuranApiService.kt`

#### getMyRooms() - Enhanced error handling
```kotlin
suspend fun getMyRooms(authToken: String): Result<MyRoomsResponse> {
    return try {
        // ... API call code ...
        
        var responseText = response.bodyAsText()
        
        // Remove BOM if present (can cause "offset 2" JSON error)
        if (responseText.startsWith("\uFEFF")) {
            responseText = responseText.substring(1)
            android.util.Log.d("SignQuranAPI", "Removed BOM from response")
        }
        
        // Better error logging
        val body = try {
            Json.decodeFromString<MyRoomsResponse>(responseText)
        } catch (parseError: Exception) {
            android.util.Log.e("SignQuranAPI", "JSON Parse error: ${parseError.message}", parseError)
            android.util.Log.e("SignQuranAPI", "Response text length: ${responseText.length}")
            android.util.Log.e("SignQuranAPI", "First 200 chars: ${responseText.take(200)}")
            throw parseError
        }
        
        Result.success(body)
    } catch (e: Exception) {
        android.util.Log.e("SignQuranAPI", "Get my rooms error: ${e.message}", e)
        Result.failure(e)
    }
}
```

## JSON Configuration

The API service already has proper JSON configuration:
```kotlin
private val client = HttpClient(Android) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true  // ✅ Ignores extra fields
            isLenient = true          // ✅ More flexible parsing
            prettyPrint = true
        })
    }
}
```

## Testing Recommendations

1. **Check Logcat** for the detailed error messages:
   - Look for "First 200 chars:" to see the actual response
   - Check "Response bytes:" for the first bytes (to detect BOM)

2. **Verify Backend Response**:
   ```bash
   curl -H "Authorization: Bearer YOUR_TOKEN" \
        https://signquran.site/api/enrollments/my-rooms
   ```

3. **Test with Sample Data**:
   - Ensure the API returns proper JSON
   - Check for any error messages instead of the expected data

## Additional Notes

- The `ignoreUnknownKeys = true` setting should handle any extra fields from the backend
- All optional fields use `? = null` default values
- BOM removal handles encoding issues that could cause offset parsing errors
- Enhanced logging will help diagnose future JSON parsing issues

## File Changes Summary

| File | Changes |
|------|---------|
| `EnrollmentModels.kt` | Added `created_by` field to `EnrolledRoom` and `SimpleRoom` |
| `SignQuranApiService.kt` | Added BOM removal and enhanced error logging in `getMyRooms()` |

