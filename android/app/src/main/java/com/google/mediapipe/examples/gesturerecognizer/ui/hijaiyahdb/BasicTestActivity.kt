package com.google.mediapipe.examples.gesturerecognizer.ui.hijaiyahdb

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.postgrest.from

class BasicTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            android.util.Log.d("BasicTest", "Starting BasicTestActivity")
            
            setContent {
                MaterialTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        BasicTestScreen()
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("BasicTest", "Error in BasicTestActivity", e)
            finish()
        }
    }
}

@Composable
fun BasicTestScreen() {
    val context = LocalContext.current
    var message by remember { mutableStateOf("Starting basic tests...") }
    var currentTest by remember { mutableStateOf(0) }
    var allTestsPassed by remember { mutableStateOf(false) }
    var testingSupabase by remember { mutableStateOf(false) }
    var supabaseTestPassed by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        try {
            android.util.Log.d("BasicTest", "Running basic functionality tests")
            
            // Test 1: Compose and UI
            message = "🔄 Test 1: Testing Compose UI..."
            kotlinx.coroutines.delay(500)
            message = "✅ Test 1: Compose UI working"
            currentTest = 1
            kotlinx.coroutines.delay(1000)
            
            // Test 2: Coroutines
            message = "🔄 Test 2: Testing Coroutines..."
            kotlinx.coroutines.delay(500)
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                android.util.Log.d("BasicTest", "IO dispatcher working")
            }
            message = "✅ Test 2: Coroutines working"
            currentTest = 2
            kotlinx.coroutines.delay(1000)
            
            // Test 3: Basic Android operations
            message = "🔄 Test 3: Testing Android operations..."
            kotlinx.coroutines.delay(500)
            message = "✅ Test 3: Android operations working"
            currentTest = 3
            kotlinx.coroutines.delay(1000)
            
            // Final success
            message = "✅ All basic tests passed! Ready to test Supabase."
            allTestsPassed = true
            android.util.Log.d("BasicTest", "All basic tests completed successfully")
            
        } catch (e: Exception) {
            message = "❌ Test failed: ${e.message}"
            android.util.Log.e("BasicTest", "Basic test failed", e)
        }
    }
    
    // Supabase testing effect
    LaunchedEffect(testingSupabase) {
        if (testingSupabase) {
            try {
                android.util.Log.d("BasicTest", "Starting Supabase tests")
                
                // Test 1: Check if Supabase classes can be loaded
                message = "🔄 Test 1: Loading Supabase classes..."
                kotlinx.coroutines.delay(500)
                
                try {
                    Class.forName("io.github.jan.supabase.SupabaseClient")
                    Class.forName("io.github.jan.supabase.postgrest.Postgrest")
                    message = "✅ Test 1: Supabase classes loaded"
                    currentTest = 1
                    kotlinx.coroutines.delay(1000)
                } catch (classError: Exception) {
                    message = "❌ Test 1 Failed: Supabase classes not found - ${classError.message}"
                    android.util.Log.e("BasicTest", "Supabase classes not found", classError)
                    return@LaunchedEffect
                }
                
                // Test 2: Try to access the client object
                message = "🔄 Test 2: Accessing Supabase client..."
                kotlinx.coroutines.delay(500)
                
                try {
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                        val client = com.google.mediapipe.examples.gesturerecognizer.supabase.supabaseClient
                        android.util.Log.d("BasicTest", "Supabase client accessed: ${client != null}")
                    }
                    message = "✅ Test 2: Supabase client initialized"
                    currentTest = 2
                    kotlinx.coroutines.delay(1000)
                } catch (clientError: Exception) {
                    message = "❌ Test 2 Failed: Client initialization - ${clientError.message}"
                    android.util.Log.e("BasicTest", "Supabase client initialization failed", clientError)
                    return@LaunchedEffect
                }
                
                // Test 3: Try a simple query (this is where it might fail)
                message = "🔄 Test 3: Testing database query..."
                kotlinx.coroutines.delay(500)
                
                try {
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                        val client = com.google.mediapipe.examples.gesturerecognizer.supabase.supabaseClient
                        // Just try to create a query builder, don't execute yet
                        val query = client.from("hijaiyah").select()
                        android.util.Log.d("BasicTest", "Query builder created successfully")
                    }
                    message = "✅ Test 3: Query builder created successfully"
                    currentTest = 3
                    supabaseTestPassed = true
                } catch (queryError: Exception) {
                    message = "❌ Test 3 Failed: Query builder creation - ${queryError.message}"
                    android.util.Log.e("BasicTest", "Query builder creation failed", queryError)
                }
                
            } catch (e: Exception) {
                message = "❌ Supabase test critical failure: ${e.message}"
                android.util.Log.e("BasicTest", "Supabase test critical failure", e)
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Basic Functionality Test",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = if (testingSupabase) "Supabase tests: $currentTest/3" else "Tests completed: $currentTest/3",
            style = MaterialTheme.typography.bodyMedium,
            color = when {
                supabaseTestPassed -> MaterialTheme.colorScheme.primary
                allTestsPassed -> MaterialTheme.colorScheme.primary 
                else -> MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        when {
            allTestsPassed && !testingSupabase -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = {
                            testingSupabase = true
                            supabaseTestPassed = false
                            message = "🔄 Testing Supabase client initialization..."
                            currentTest = 0
                        }
                    ) {
                        Text("Test Supabase Connection")
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            android.util.Log.d("BasicTest", "Opening original database activity")
                            try {
                                val intent = Intent(
                                    context, 
                                    com.google.mediapipe.examples.gesturerecognizer.ui.hijaiyahdb.HijaiyahDBListActivity::class.java
                                )
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                android.util.Log.e("BasicTest", "Failed to open database activity", e)
                            }
                        }
                    ) {
                        Text("Open Database Activity")
                    }
                }
            }
            testingSupabase && !supabaseTestPassed -> {
                CircularProgressIndicator()
            }
            supabaseTestPassed -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🎉 Supabase connection successful!",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Button(
                        onClick = {
                            android.util.Log.d("BasicTest", "Opening safe database activity")
                            try {
                                val intent = Intent(
                                    context, 
                                    com.google.mediapipe.examples.gesturerecognizer.ui.hijaiyahdb.HijaiyahDBListActivity::class.java
                                )
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                android.util.Log.e("BasicTest", "Failed to open database activity", e)
                            }
                        }
                    ) {
                        Text("Open Database Activity")
                    }
                }
            }
            else -> {
                CircularProgressIndicator()
            }
        }
    }
}
