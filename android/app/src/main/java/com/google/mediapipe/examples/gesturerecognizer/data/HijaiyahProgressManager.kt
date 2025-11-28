/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.mediapipe.examples.gesturerecognizer.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.mediapipe.examples.gesturerecognizer.data.api.ProgressApiService
import com.google.mediapipe.examples.gesturerecognizer.data.manager.AuthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HijaiyahProgressManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("hijaiyah_progress", Context.MODE_PRIVATE)
    private val authManager = AuthManager(context)
    private val progressApiService = ProgressApiService.getInstance()
    
    companion object {
        private const val TAG = "HijaiyahProgressMgr"
    }
    
    fun markLetterCompleted(letterPosition: Int) {
        // Save locally first
        setLetterCompletion(letterPosition, true)
        
        // Sync to server in background
        syncLetterProgressToServer(letterPosition)
    }
    
    /**
     * Sync letter progress to backend API
     * This runs in the background and doesn't block the UI
     */
    private fun syncLetterProgressToServer(letterPosition: Int) {
        // Only sync if user is logged in
        if (!authManager.isLoggedIn) {
            Log.d(TAG, "User not logged in, skipping server sync for position $letterPosition")
            return
        }
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Get the letter data to find hijaiyahId
                val letter = HijaiyahData.getLetterByPosition(letterPosition)
                if (letter == null) {
                    Log.w(TAG, "Letter not found for position $letterPosition, cannot sync")
                    return@launch
                }
                
                // Get hijaiyahId from the letter
                // The letter.position corresponds to ordinal, which should match hijaiyahId
                val hijaiyahId = letter.position
                
                val token = authManager.authToken
                if (token == null) {
                    Log.w(TAG, "No auth token available, cannot sync letter progress")
                    return@launch
                }
                
                Log.d(TAG, "Syncing letter progress to server: position=$letterPosition, hijaiyahId=$hijaiyahId")
                
                val result = progressApiService.updateLetterProgress(token, hijaiyahId)
                
                result.onSuccess { response ->
                    Log.d(TAG, "Successfully synced letter progress: hijaiyahId=$hijaiyahId, progressId=${response.progress.progressId}")
                }
                
                result.onFailure { error ->
                    Log.e(TAG, "Failed to sync letter progress: ${error.message}", error)
                    // Don't throw error - we don't want to disrupt user experience
                    // Local progress is already saved
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing letter progress: ${e.message}", e)
                // Silently fail - local progress is already saved
            }
        }
    }
    
    fun isLetterCompleted(letterPosition: Int): Boolean {
        return prefs.getBoolean("letter_$letterPosition", false)
    }
    
    fun getCompletedCount(): Int {
        return (1..28).count { isLetterCompleted(it) }
    }
    
    fun getTotalProgress(): Pair<Int, Int> {
        return Pair(getCompletedCount(), 28)
    }
    
    fun getCompletedLetters(): Set<Int> {
        return (1..28).filter { isLetterCompleted(it) }.toSet()
    }
    
    fun resetProgress() {
        prefs.edit().clear().apply()
    }
    
    fun getLettersWithProgress(): List<HijaiyahLetter> {
        return HijaiyahData.getAllLetters().map { letter ->
            letter.copy(isCompleted = isLetterCompleted(letter.position))
        }
    }

    fun setLetterCompletion(letterPosition: Int, isCompleted: Boolean) {
        prefs.edit().putBoolean("letter_$letterPosition", isCompleted).apply()
    }
    
    fun replaceCompletedLetters(completedPositions: Set<Int>) {
        val editor = prefs.edit()
        // Clear existing flags
        prefs.all.keys
            .filter { it.startsWith("letter_") }
            .forEach { editor.remove(it) }
        // Apply new snapshot
        completedPositions.forEach { position ->
            editor.putBoolean("letter_$position", true)
        }
        editor.apply()
    }
    
    /**
     * Sync progress from server to local cache
     * Call this when the app starts or when user logs in
     */
    suspend fun syncProgressFromServer(): Boolean {
        if (!authManager.isLoggedIn) {
            Log.d(TAG, "User not logged in, skipping progress sync")
            return false
        }
        
        return try {
            val token = authManager.authToken ?: return false
            
            Log.d(TAG, "Syncing progress from server...")
            val result = progressApiService.getLetterProgress(token)
            
            result.onSuccess { response ->
                // Extract hijaiyah_ids from the response
                val completedHijaiyahIds = response.progress.mapNotNull { it.hijaiyahId }.toSet()
                
                Log.d(TAG, "Synced ${completedHijaiyahIds.size} completed letters from server")
                
                // Update local cache with server data
                replaceCompletedLetters(completedHijaiyahIds)
            }
            
            result.onFailure { error ->
                Log.e(TAG, "Failed to sync progress from server: ${error.message}", error)
            }
            
            result.isSuccess
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing progress from server: ${e.message}", e)
            false
        }
    }
}
