package com.google.mediapipe.examples.gesturerecognizer.ui.hijaiyahdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.mediapipe.examples.gesturerecognizer.supabase.supabaseClient
import com.google.mediapipe.examples.gesturerecognizer.supabase.SupabaseConnectionTest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class HijaiyahDBItem(
    val hijaiyah_id: Int,
    val latin_name: String,
    val arabic_char: String,
    val ordinal: Int
)

class HijaiyahDBListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            android.util.Log.d("HijaiyahDBActivity", "Starting HijaiyahDBListActivity")
            
            // Simple fallback: Create a basic view first
            setContent {
                MaterialTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        SafeHijaiyahListScreen()
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("HijaiyahDBActivity", "Critical error in onCreate", e)
            // If everything fails, just finish the activity
            try {
                finish()
            } catch (finishError: Exception) {
                android.util.Log.e("HijaiyahDBActivity", "Error finishing activity", finishError)
            }
        }
    }
}

@Composable
fun BasicErrorScreen(errorMessage: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Error: $errorMessage",
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun SafeHijaiyahListScreen() {
    var connectionState by remember { mutableStateOf("starting") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var letters by remember { mutableStateOf<List<HijaiyahDBItem>>(emptyList()) }
    
    // Start with simple UI first
    LaunchedEffect(Unit) {
        try {
            connectionState = "connecting"
            android.util.Log.d("SafeHijaiyahList", "Starting safe connection test")
            
            // Use a simpler approach without timeout complexities
            val result = try {
                supabaseClient.from("hijaiyah")
                    .select()
                    .decodeList<HijaiyahDBItem>()
            } catch (networkError: Exception) {
                android.util.Log.e("SafeHijaiyahList", "Network error", networkError)
                connectionState = "error"
                errorMessage = "Network error: ${networkError.message}"
                return@LaunchedEffect
            }
            
            connectionState = "success"
            letters = result
            android.util.Log.d("SafeHijaiyahList", "Successfully loaded ${result.size} items")
            
        } catch (e: Exception) {
            android.util.Log.e("SafeHijaiyahList", "General error", e)
            connectionState = "error"
            errorMessage = "Error: ${e.message}"
        }
    }
    
    // Simple UI based on state
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = errorMessage ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(onClick = {
                        connectionState = "starting"
                        errorMessage = null
                    }) {
                        Text("Retry")
                    }
                }
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
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = letter.arabic_char,
                                    style = MaterialTheme.typography.headlineMedium
                                )
                                Text(text = letter.latin_name)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HijaiyahListScreen() {
    var letters by remember { mutableStateOf<List<HijaiyahDBItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                android.util.Log.d("SupabaseTest", "Starting connection test to Supabase...")
                
                // Check network connectivity first
                android.util.Log.d("SupabaseTest", "Checking network connectivity...")
                
                // First test basic connection with timeout
                val (connectionSuccess, connectionMessage) = try {
                    kotlinx.coroutines.withTimeout(10000) { // 10 second timeout
                        SupabaseConnectionTest.testConnection()
                    }
                } catch (timeoutException: kotlinx.coroutines.TimeoutCancellationException) {
                    android.util.Log.e("SupabaseTest", "Connection timeout")
                    Pair(false, "❌ Connection timeout - Check your internet connection")
                }
                
                android.util.Log.d("SupabaseTest", "Connection test result: $connectionMessage")
                
                if (!connectionSuccess) {
                    errorMessage = connectionMessage
                    return@launch
                }
                
                // If connection is successful, try to fetch data with timeout
                val data = try {
                    kotlinx.coroutines.withTimeout(15000) { // 15 second timeout for data
                        supabaseClient.from("hijaiyah")
                            .select()
                            .decodeList<HijaiyahDBItem>()
                    }
                } catch (timeoutException: kotlinx.coroutines.TimeoutCancellationException) {
                    android.util.Log.e("SupabaseTest", "Data fetch timeout")
                    errorMessage = "❌ Data fetch timeout - Please try again"
                    return@launch
                }
                
                android.util.Log.d("SupabaseTest", "Successfully fetched ${data.size} items from Supabase")
                letters = data
                
            } catch (e: Exception) {
                android.util.Log.e("SupabaseTest", "Failed to connect to Supabase", e)
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
                android.util.Log.d("SupabaseTest", "Data loading completed. Loading: $isLoading, Error: $errorMessage")
            }
        }
    }

    when {
        isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading data from Supabase...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        errorMessage != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Column(
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Error: $errorMessage",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Button(
                        onClick = {
                            // Retry connection
                            errorMessage = null
                            isLoading = true
                            scope.launch {
                                // Trigger LaunchedEffect again by changing a key
                                android.util.Log.d("SupabaseTest", "Retrying connection...")
                            }
                        }
                    ) {
                        Text("Retry Connection")
                    }
                }
            }
        }
        letters.isEmpty() -> {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Text(
                        text = "✅ Connected to Supabase successfully!",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "No hijaiyah data found in database",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        else -> {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                item {
                    Text(
                        text = "✅ Successfully loaded ${letters.size} hijaiyah letters",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                items(letters) { letter ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = letter.arabic_char,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Latin: ${letter.latin_name}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "ID: ${letter.hijaiyah_id} • Order: ${letter.ordinal}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
