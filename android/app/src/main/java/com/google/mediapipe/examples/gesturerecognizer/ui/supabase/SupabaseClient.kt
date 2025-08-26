package com.google.mediapipe.examples.gesturerecognizer.supabase

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.android.Android

val supabaseClient: SupabaseClient by lazy {
    try {
        Log.d("SupabaseClient", "Initializing Supabase client...")
        
        val client = createSupabaseClient(
            supabaseUrl = "https://mrwjqcegksdqyzgcvtrq.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1yd2pxY2Vna3NkcXl6Z2N2dHJxIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc1NjIwNzk3MCwiZXhwIjoyMDcxNzgzOTcwfQ.JaxvW0I2hkMaNy5-AfA5paK-ElOeVZWa_r7G99Bi-2g"
        ) {
            install(Postgrest)
        }
        
        Log.d("SupabaseClient", "Supabase client initialized successfully")
        client
        
    } catch (e: Exception) {
        Log.e("SupabaseClient", "Failed to initialize Supabase client", e)
        Log.e("SupabaseClient", "Error details:", e)
        // Return a dummy client or rethrow with more info
        throw RuntimeException("Supabase client initialization failed: ${e.javaClass.simpleName} - ${e.message}", e)
    }
}
