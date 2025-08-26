package com.google.mediapipe.examples.gesturerecognizer.ui.hijaiyahdb

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.google.mediapipe.examples.gesturerecognizer.supabase.supabaseClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable

@Serializable
data class HijaiyahItem(
    val hijaiyah_id: Int,
    val latin_name: String,
    val arabic_char: String,
    val ordinal: Int
)

class HijaiyahDbFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return try {
            android.util.Log.d("HijaiyahDBFragment", "Creating HijaiyahDbFragment view")
            
            ComposeView(requireContext()).apply {
                setContent {
                    MaterialTheme {
                        Surface(modifier = Modifier.fillMaxSize()) {
                            SafeHijaiyahDbScreen()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("HijaiyahDBFragment", "Critical error creating fragment view", e)
            // Return a completely basic view
            ComposeView(requireContext()).apply {
                setContent {
                    Text(
                        text = "Critical error: ${e.message}",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SafeHijaiyahDbScreen() {
    var connectionState by remember { mutableStateOf("starting") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var letters by remember { mutableStateOf<List<HijaiyahItem>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        try {
            connectionState = "connecting"
            android.util.Log.d("SafeHijaiyahDbFragment", "Starting safe connection test")
            
            val result = withContext(Dispatchers.IO) {
                try {
                    supabaseClient.from("hijaiyah")
                        .select()
                        .decodeList<HijaiyahItem>()
                } catch (networkError: Exception) {
                    android.util.Log.e("SafeHijaiyahDbFragment", "Network error", networkError)
                    throw networkError
                }
            }
            
            connectionState = "success"
            letters = result
            android.util.Log.d("SafeHijaiyahDbFragment", "Successfully loaded ${result.size} items")
            
        } catch (e: Exception) {
            android.util.Log.e("SafeHijaiyahDbFragment", "General error", e)
            connectionState = "error"
            errorMessage = "Error: ${e.message}"
        }
    }
    
    when (connectionState) {
        "starting" -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Initializing...")
            }
        }
        "connecting" -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Connecting to database...")
                }
            }
        }
        "error" -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = errorMessage ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        "success" -> {
            if (letters.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Connected successfully, but no data found")
                }
            } else {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    item {
                        Text(
                            text = "✅ Loaded ${letters.size} letters",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    items(letters) { letter ->
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = letter.arabic_char,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(text = letter.latin_name)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HijaiyahDbScreen() {
    var letters by remember { mutableStateOf<List<HijaiyahItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            android.util.Log.d("SupabaseTest", "Fragment: Starting connection test to Supabase...")
            
            val data = withContext(Dispatchers.IO) {
                try {
                    kotlinx.coroutines.withTimeout(15000) { // 15 second timeout
                        supabaseClient.from("hijaiyah")
                            .select()
                            .decodeList<HijaiyahItem>()
                    }
                } catch (timeoutException: kotlinx.coroutines.TimeoutCancellationException) {
                    android.util.Log.e("SupabaseTest", "Fragment: Data fetch timeout")
                    throw Exception("Data fetch timeout - Please try again")
                }
            }
            
            android.util.Log.d("SupabaseTest", "Fragment: Successfully fetched ${data.size} items from Supabase")
            letters = data
            
        } catch (e: Exception) {
            android.util.Log.e("SupabaseTest", "Fragment: Failed to connect to Supabase", e)
            val errorDetail = when (e) {
                is java.net.UnknownHostException -> "No internet connection or DNS issue"
                is java.net.ConnectException -> "Connection refused - Check server status"
                is java.net.SocketTimeoutException -> "Connection timeout - Slow network"
                is kotlinx.serialization.SerializationException -> "Data format error - Database schema mismatch"
                else -> "Unknown error: ${e.message}"
            }
            errorMessage = "❌ Connection failed: $errorDetail\n\nError type: ${e.javaClass.simpleName}"
        } finally {
            isLoading = false
            android.util.Log.d("SupabaseTest", "Fragment: Data loading completed. Loading: $isLoading, Error: $errorMessage")
        }
    }

    when {
        isLoading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        errorMessage != null -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Error: $errorMessage", color = MaterialTheme.colorScheme.error)
        }
        else -> LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(letters) { letter ->
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Arab: ${letter.arabic_char}",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Latin: ${letter.latin_name}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
