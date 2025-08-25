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
    val arabic: String,
    val transliteration: String,
    val pronunciation: String,
    val isCompleted: Boolean = false,
    val position: Int
)

object HijaiyahData {
    val letters = listOf(
        HijaiyahLetter("ا", "Alif", "A", false, 1),
        HijaiyahLetter("ب", "Ba", "B", false, 2),
        HijaiyahLetter("ت", "Ta", "T", false, 3),
        HijaiyahLetter("ث", "Tsa", "Ts", false, 4),
        HijaiyahLetter("ج", "Jim", "J", false, 5),
        HijaiyahLetter("ح", "Ha", "H", false, 6),
        HijaiyahLetter("خ", "Kho", "Kh", false, 7),
        HijaiyahLetter("د", "Dal", "D", false, 8),
        HijaiyahLetter("ذ", "Dzal", "Dz", false, 9),
        HijaiyahLetter("ر", "Ra", "R", false, 10),
        HijaiyahLetter("ز", "Zai", "Z", false, 11),
        HijaiyahLetter("س", "Sin", "S", false, 12),
        HijaiyahLetter("ش", "Syin", "Sy", false, 13),
        HijaiyahLetter("ص", "Shod", "Sh", false, 14),
        HijaiyahLetter("ض", "Dhod", "Dh", false, 15),
        HijaiyahLetter("ط", "Tho", "Th", false, 16),
        HijaiyahLetter("ظ", "Zho", "Zh", false, 17),
        HijaiyahLetter("ع", "Ain", "'", false, 18),
        HijaiyahLetter("غ", "Ghoin", "Gh", false, 19),
        HijaiyahLetter("ف", "Fa", "F", false, 20),
        HijaiyahLetter("ق", "Qof", "Q", false, 21),
        HijaiyahLetter("ك", "Kaf", "K", false, 22),
        HijaiyahLetter("ل", "Lam", "L", false, 23),
        HijaiyahLetter("م", "Mim", "M", false, 24),
        HijaiyahLetter("ن", "Nun", "N", false, 25),
        HijaiyahLetter("و", "Wau", "W", false, 26),
        HijaiyahLetter("ه", "Ha", "H", false, 27),
        HijaiyahLetter("ي", "Ya", "Y", false, 28)
    )
    
    fun getAllLetters(): List<HijaiyahLetter> {
        return letters
    }
    
    fun searchLetters(query: String, letters: List<HijaiyahLetter>): List<HijaiyahLetter> {
        if (query.isBlank()) return letters
        
        return letters.filter { letter ->
            letter.arabic.contains(query, ignoreCase = true) ||
            letter.transliteration.contains(query, ignoreCase = true) ||
            letter.pronunciation.contains(query, ignoreCase = true)
        }
    }
}
