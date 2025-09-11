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
        HijaiyahLetter("ا", "Alif", "01_alif", false, 1),
        HijaiyahLetter("ب", "Ba", "02_ba", false, 2),
        HijaiyahLetter("ت", "Ta", "03_ta", false, 3),
        HijaiyahLetter("ث", "Tsa", "04_tsa", false, 4),
        HijaiyahLetter("ج", "Jim", "05_jim", false, 5),
        HijaiyahLetter("ح", "Ha", "06_ha", false, 6),
        HijaiyahLetter("خ", "Kha", "07_kha", false, 7),
        HijaiyahLetter("د", "Dal", "08_dal", false, 8),
        HijaiyahLetter("ذ", "Dzal", "09_dzal", false, 9),
        HijaiyahLetter("ر", "Ra", "10_ra", false, 10),
        HijaiyahLetter("ز", "Za", "11_za", false, 11),
        HijaiyahLetter("س", "Sin", "12_sin", false, 12),
        HijaiyahLetter("ش", "Syin", "13_syin", false, 13),
        HijaiyahLetter("ص", "Shod", "14_shad", false, 14),
        HijaiyahLetter("ض", "Dhod", "15_dhad", false, 15),
        HijaiyahLetter("ط", "Tho", "16_tha", false, 16),
        HijaiyahLetter("ظ", "Zho", "17_zha", false, 17),
        HijaiyahLetter("ع", "Ain", "18_ain", false, 18),
        HijaiyahLetter("غ", "Ghoin", "19_ghain", false, 19),
        HijaiyahLetter("ف", "Fa", "20_fa", false, 20),
        HijaiyahLetter("ق", "Qof", "21_qaf", false, 21),
        HijaiyahLetter("ك", "Kaf", "22_kaf", false, 22),
        HijaiyahLetter("ل", "Lam", "23_lam", false, 23),
        HijaiyahLetter("م", "Mim", "24_mim", false, 24),
        HijaiyahLetter("ن", "Nun", "25_nun", false, 25),
        HijaiyahLetter("و", "Wau", "26_waw", false, 26),
        HijaiyahLetter("ه", "Ha'", "27_ha'", false, 27),
        HijaiyahLetter("ي", "Ya", "28_ya", false, 28)
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
