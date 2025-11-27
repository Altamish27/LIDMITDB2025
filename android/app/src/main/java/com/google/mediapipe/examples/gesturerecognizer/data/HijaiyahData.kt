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

import com.google.mediapipe.examples.gesturerecognizer.data.api.SignQuranApiService
import com.google.mediapipe.examples.gesturerecognizer.data.models.HijaiyahLetterApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class HijaiyahLetter(
    override val arabic: String,
    override val transliteration: String,
    override val gestureName: String?,
    override var isCompleted: Boolean = false,
    override val position: Int,
    val assets: String? = null,  // URL to image/video asset or null
    val diacritic: String? = null
) : ArabicLetter

object HijaiyahData {
    // Mapping dari latin name API ke gesture name
    private val latinToGestureMap = mapOf(
        "Alif" to "Alif",
        "Ba" to "Ba",
        "Ta" to "Ta",
        "Tsa" to "Tsa",
        "Jim" to "Jim",
        "Ha" to "Ha",
        "Kha" to "Kha",
        "Dal" to "Dal",
        "Dzal" to "Dzal",
        "Ra" to "Ra",
        "Za" to "Zai",
        "Sin" to "Sin",
        "Syin" to "Syin",
        "Shod" to "Sad",
        "Dhod" to "Dad",
        "Tho" to "Tha",
        "Zho" to "Zha",
        "Ain" to "Ain",
        "Ghoin" to "Gain",
        "Fa" to "Fa",
        "Qof" to "Qaf",
        "Kaf" to "Kaf",
        "Lam" to "Lam",
        "Mim" to "Mim",
        "Nun" to "Nun",
        "Wau" to "Waw",
        "Ha'" to "Ha Besar",
        "Ya" to "Ya",
        "Shad" to "Sad",
        "Dhad" to "Dad",
        "Zaa" to "Zai",
        "Ghain" to "Gain",
        "Kho" to "Kha",
        "Hamzah" to "Ha Besar"
    )
    
    // Cache untuk menyimpan data dari API
    private var cachedLetters: List<HijaiyahLetter>? = null
    
    // Property untuk backward compatibility - HANYA dari API
    val letters: List<HijaiyahLetter>
        get() = cachedLetters ?: emptyList()
    
    /**
     * Load data dari API
     */
    suspend fun loadFromApi(context: android.content.Context? = null): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val apiService = SignQuranApiService.getInstance()
                
                // Get auth token if context is provided
                val token = if (context != null) {
                    val authManager = com.google.mediapipe.examples.gesturerecognizer.data.manager.AuthManager(context)
                    if (authManager.isLoggedIn) authManager.authToken else null
                } else null
                
                val result = apiService.getHijaiyahLetters(token)
                
                result.onSuccess { response ->
                    cachedLetters = response.letters.map { apiLetter ->
                        mapApiLetterToHijaiyahLetter(apiLetter)
                    }
                    return@withContext true
                }
                
                result.onFailure { error ->
                    android.util.Log.e("HijaiyahData", "API Failed: ${error.message}", error)
                    cachedLetters = emptyList()
                }
                
                return@withContext false
            } catch (e: Exception) {
                android.util.Log.e("HijaiyahData", "API Error: ${e.message}", e)
                cachedLetters = emptyList()
                return@withContext false
            }
        }
    }
    
    /**
     * Mapping dari API letter ke HijaiyahLetter
     */
    private fun mapApiLetterToHijaiyahLetter(apiLetter: HijaiyahLetterApi): HijaiyahLetter {
        val (baseLatinName, diacriticType) = extractBaseNameAndDiacritic(apiLetter.latinName)
        val gestureName = latinToGestureMap[baseLatinName] ?: baseLatinName
        return HijaiyahLetter(
            arabic = apiLetter.arabicChar,
            transliteration = apiLetter.latinName,
            gestureName = gestureName,
            isCompleted = false,
            position = apiLetter.ordinal,
            assets = apiLetter.assets,
            diacritic = diacriticType
        )
    }
    
    private fun extractBaseNameAndDiacritic(rawName: String): Pair<String, String?> {
        val lower = rawName.lowercase()
        val diacritic = when {
            lower.contains("fathah") -> "fathah"
            lower.contains("kasrah") -> "kasrah"
            lower.contains("dhammah") || lower.contains("dhommah") || lower.contains("dhomah") -> "dhammah"
            lower.contains("dammah") -> "dhammah"
            else -> null
        }
        
        if (diacritic == null) {
            return rawName to null
        }
        
        val base = rawName.replace("Fathah", "", ignoreCase = true)
            .replace("Kasrah", "", ignoreCase = true)
            .replace("Dhammah", "", ignoreCase = true)
            .replace("Dhommah", "", ignoreCase = true)
            .replace("Dhomah", "", ignoreCase = true)
            .replace("Tanwin", "", ignoreCase = true)
            .trim()
        
        return base.ifEmpty { rawName } to diacritic
    }
    
    fun getAllLetters(): List<HijaiyahLetter> {
        return letters
    }
    
    fun searchLetters(query: String, letters: List<HijaiyahLetter>): List<HijaiyahLetter> {
        if (query.isBlank()) return letters
        
        return letters.filter { letter ->
            letter.arabic.contains(query, ignoreCase = true) ||
            letter.transliteration.contains(query, ignoreCase = true) ||
            letter.gestureName?.contains(query, ignoreCase = true) == true
        }
    }
    
    // Fungsi untuk mendapatkan huruf berdasarkan gesture name
    fun getLetterByGesture(gestureName: String): HijaiyahLetter? {
        return letters.find { it.gestureName?.equals(gestureName, ignoreCase = true) == true }
    }

    // Fungsi untuk mendapatkan huruf berdasarkan ID
    fun getLetterById(id: Int): HijaiyahLetter? {
        return letters.find { it.position == id }
    }
    
    // Fungsi untuk mendapatkan huruf berdasarkan position
    fun getLetterByPosition(position: Int): HijaiyahLetter? {
        return letters.find { it.position == position }
    }

    // Fungsi untuk menyimpan dan mengambil status completed letters
    private val completedLettersSet = mutableSetOf<Int>()

    fun markLetterCompleted(letterId: Int) {
        completedLettersSet.add(letterId)
    }

    fun getCompletedLetters(): Set<Int> {
        return completedLettersSet.toSet()
    }

    fun isLetterCompleted(letterId: Int): Boolean {
        return completedLettersSet.contains(letterId)
    }
}
