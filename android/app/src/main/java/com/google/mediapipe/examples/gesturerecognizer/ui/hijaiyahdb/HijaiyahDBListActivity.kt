package com.google.mediapipe.examples.gesturerecognizer.ui.hijaiyahdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.mediapipe.examples.gesturerecognizer.supabase.supabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

data class HijaiyahDBItem(
    val hijaiyah_id: Int,
    val latin_name: String,
    val arabic_char: String,
    val ordinal: Int
)

class HijaiyahDBListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HijaiyahListScreen()
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
                val data = supabaseClient.from("hijaiyah")
                    .select()
                    .decodeList<HijaiyahDBItem>()
                letters = data
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    when {
        isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        errorMessage != null -> {
            Text(
                text = "Error: $errorMessage",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }
        else -> {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(letters) { letter ->
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Arab: ${letter.arabic_char}",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(text = "Latin: ${letter.latin_name}")
                    }
                }
            }
        }
    }
}
