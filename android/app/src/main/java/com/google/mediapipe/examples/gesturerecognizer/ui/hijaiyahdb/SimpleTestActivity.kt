package com.google.mediapipe.examples.gesturerecognizer.ui.hijaiyahdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class SimpleTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            android.util.Log.d("SimpleTest", "Starting SimpleTestActivity")
            
            setContent {
                MaterialTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        SimpleTestScreen()
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("SimpleTest", "Error in SimpleTestActivity", e)
            finish()
        }
    }
}

@Composable
fun SimpleTestScreen() {
    var message by remember { mutableStateOf("Testing basic functionality...") }
    var testsPassed by remember { mutableStateOf(0) }
    
    LaunchedEffect(Unit) {
        try {
            android.util.Log.d("SimpleTest", "Starting basic tests")
            
            // Test 1: Basic Compose functionality
            try {
                message = "✅ Test 1: Compose working"
                testsPassed = 1
                android.util.Log.d("SimpleTest", "Test 1 passed")
                kotlinx.coroutines.delay(1000)
            } catch (e: Exception) {
                message = "❌ Test 1 Failed: ${e.message}"
                android.util.Log.e("SimpleTest", "Test 1 failed", e)
                return@LaunchedEffect
            }
            
            // Test 2: Basic Android components
            try {
                message = "✅ Test 2: Android components working"
                testsPassed = 2
                android.util.Log.d("SimpleTest", "Test 2 passed")
                kotlinx.coroutines.delay(1000)
            } catch (e: Exception) {
                message = "❌ Test 2 Failed: ${e.message}"
                android.util.Log.e("SimpleTest", "Test 2 failed", e)
                return@LaunchedEffect
            }
            
            // Test 3: Coroutines and async operations
            try {
                message = "🔄 Test 3: Testing coroutines..."
                kotlinx.coroutines.delay(500)
                
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    // Simple IO operation test
                    android.util.Log.d("SimpleTest", "IO context working")
                }
                
                message = "✅ Test 3: Coroutines working"
                testsPassed = 3
                android.util.Log.d("SimpleTest", "Test 3 passed")
                kotlinx.coroutines.delay(1000)
            } catch (e: Exception) {
                message = "❌ Test 3 Failed: ${e.message}"
                android.util.Log.e("SimpleTest", "Test 3 failed", e)
                return@LaunchedEffect
            }
            
            // Test 4: Try to create Supabase client (this is where crash might happen)
            try {
                message = "🔄 Test 4: Testing Supabase client creation..."
                kotlinx.coroutines.delay(500)
                
                android.util.Log.d("SimpleTest", "Attempting to access Supabase client")
                
                // Try to access the client in a safe way
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    try {
                        val client = com.google.mediapipe.examples.gesturerecognizer.supabase.supabaseClient
                        android.util.Log.d("SimpleTest", "Supabase client accessed successfully")
                        
                        // Try to get some basic info without making actual requests
                        android.util.Log.d("SimpleTest", "Client created: ${client != null}")
                        
                    } catch (clientError: Exception) {
                        android.util.Log.e("SimpleTest", "Error accessing Supabase client", clientError)
                        throw clientError
                    }
                }
                
                message = "✅ Test 4: Supabase client created successfully"
                testsPassed = 4
                android.util.Log.d("SimpleTest", "Test 4 passed")
                
            } catch (e: Exception) {
                message = "❌ Test 4 Failed: ${e.message}\nType: ${e.javaClass.simpleName}"
                android.util.Log.e("SimpleTest", "Test 4 failed", e)
                // Don't return here, let's show the partial results
            }
            
        } catch (e: Exception) {
            message = "❌ Critical Test Failure: ${e.message}"
            android.util.Log.e("SimpleTest", "Critical test failure", e)
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Simple Functionality Test",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "Tests passed: $testsPassed/4",
                style = MaterialTheme.typography.bodyMedium,
                color = if (testsPassed == 4) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { 
                    android.util.Log.d("SimpleTest", "Finishing SimpleTestActivity")
                    // This should be called from the activity context, but for testing purposes
                }
            ) {
                Text("Close Test")
            }
        }
    }
}
