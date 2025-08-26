package com.google.mediapipe.examples.gesturerecognizer.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.postgrest

val supabaseClient: SupabaseClient = createSupabaseClient(
    supabaseUrl = "https://edgkkycvwgolcmzutvok.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVkZ2treWN2d2dvbGNtenV0dm9rIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTYxMDM4MDYsImV4cCI6MjA3MTY3OTgwNn0.69HYX1ltdL2uIxVYbfHp2nd_KDtsz6HZv5d81uuhf3A"
) {
    install(io.github.jan.supabase.postgrest.Postgrest)
}
