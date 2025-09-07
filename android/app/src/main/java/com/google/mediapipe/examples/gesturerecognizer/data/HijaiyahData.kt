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

data class HijaiyahLetter(
    override val arabic: String,
    override val transliteration: String,
    override val gestureName: String?,
    override var isCompleted: Boolean = false,
    override val position: Int
) : ArabicLetter

object HijaiyahData {
    val letters = listOf(
        HijaiyahLetter("ا", "Alif", "alif", false, 1),
        HijaiyahLetter("ب", "Ba", "ba", false, 2),
        HijaiyahLetter("ت", "Ta", "ta", false, 3),
        HijaiyahLetter("ث", "Tsa", "tsa", false, 4),
        HijaiyahLetter("ج", "Jim", "jim", false, 5),
        HijaiyahLetter("ح", "Ha", "ha", false, 6),
        HijaiyahLetter("خ", "Kha", "kho", false, 7),
        HijaiyahLetter("د", "Dal", "dal", false, 8),
        HijaiyahLetter("ذ", "Dzal", "dzal", false, 9),
        HijaiyahLetter("ر", "Ra", "ra", false, 10),
        HijaiyahLetter("ز", "Za", "za", false, 11),
        HijaiyahLetter("س", "Sin", "sin", false, 12),
        HijaiyahLetter("ش", "Syin", "syin", false, 13),
        HijaiyahLetter("ص", "Shod", "shod", false, 14),
        HijaiyahLetter("ض", "Dhod", "dhod", false, 15),
        HijaiyahLetter("ط", "Tho", "tho", false, 16),
        HijaiyahLetter("ظ", "Zho", "dzho", false, 17),
        HijaiyahLetter("ع", "Ain", "ain", false, 18),
        HijaiyahLetter("غ", "Ghoin", "ghoin", false, 19),
        HijaiyahLetter("ف", "Fa", "fa", false, 20),
        HijaiyahLetter("ق", "Qof", "qof", false, 21),
        HijaiyahLetter("ك", "Kaf", "kaf", false, 22),
        HijaiyahLetter("ل", "Lam", "lam", false, 23),
        HijaiyahLetter("م", "Mim", "mim", false, 24),
        HijaiyahLetter("ن", "Nun", "nun", false, 25),
        HijaiyahLetter("و", "Wau", "waw", false, 26),
        HijaiyahLetter("ه", "Ha'", "ha'", false, 27),
        HijaiyahLetter("ي", "Ya", "ya", false, 28)
    )
    
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
