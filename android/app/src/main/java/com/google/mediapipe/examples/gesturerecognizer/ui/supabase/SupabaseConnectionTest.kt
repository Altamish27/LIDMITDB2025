package com.google.mediapipe.examples.gesturerecognizer.supabase

import android.util.Log
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Serializable
data class TestHijaiyahItem(
    val hijaiyah_id: Int,
    val latin_name: String,
    val arabic_char: String,
    val ordinal: Int
)

object SupabaseConnectionTest {
    
    /**
     * Test basic connectivity to Supabase
     */
    suspend fun testConnection(): Pair<Boolean, String> {
        return try {
            withContext(Dispatchers.IO) {
                Log.d("SupabaseConnectionTest", "Testing Supabase connection...")
                
                // Try to access a simple table or make a basic query
                val data = supabaseClient.from("hijaiyah")
                    .select()
                    .decodeList<TestHijaiyahItem>()
                
                Log.d("SupabaseConnectionTest", "Successfully connected! Fetched ${data.size} items")
                
                Pair(true, "✅ Connection successful! Fetched ${data.size} items from hijaiyah table")
            }
        } catch (e: Exception) {
            Log.e("SupabaseConnectionTest", "Connection test failed", e)
            val errorMsg = "❌ Connection failed: ${e.message}\n" +
                          "Error type: ${e.javaClass.simpleName}\n" +
                          "Cause: ${e.cause?.message ?: "Unknown"}"
            Pair(false, errorMsg)
        }
    }
    
    /**
     * Test data retrieval from hijaiyah table with specific count
     */
    suspend fun testDataRetrieval(): Pair<Boolean, String> {
        return try {
            withContext(Dispatchers.IO) {
                Log.d("SupabaseConnectionTest", "Testing data retrieval...")
                
                // Try to fetch data from hijaiyah table
                val data = supabaseClient.from("hijaiyah")
                    .select()
                    .decodeList<TestHijaiyahItem>()
                
                Log.d("SupabaseConnectionTest", "Data retrieval successful! Got ${data.size} items")
                
                if (data.isNotEmpty()) {
                    val firstItem = data.first()
                    Log.d("SupabaseConnectionTest", "First item: ${firstItem.latin_name} (${firstItem.arabic_char})")
                    Pair(true, "✅ Data retrieval successful! Got ${data.size} items\nFirst item: ${firstItem.latin_name} (${firstItem.arabic_char})")
                } else {
                    Pair(true, "✅ Connection successful but no data found in hijaiyah table")
                }
            }
        } catch (e: Exception) {
            Log.e("SupabaseConnectionTest", "Data retrieval test failed", e)
            val errorMsg = "❌ Data retrieval failed: ${e.message}\n" +
                          "Error type: ${e.javaClass.simpleName}"
            Pair(false, errorMsg)
        }
    }
}
