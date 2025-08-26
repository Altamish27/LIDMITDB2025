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
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        HijaiyahDbScreen()
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
            val data = withContext(Dispatchers.IO) {
                supabaseClient.from("hijaiyah")
                    .select()
                    .decodeList<HijaiyahItem>()
            }
            letters = data
        } catch (e: Exception) {
            errorMessage = "Failed to load data: ${e.message}"
        } finally {
            isLoading = false
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
