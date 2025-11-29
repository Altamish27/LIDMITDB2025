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

data class FathahLetter(
    override val arabic: String,
    override val transliteration: String,
    override val position: Int,
    override var isCompleted: Boolean = false,
    override val gestureName: String? = null
) : ArabicLetter

object FathahData {
    // Fallback hardcoded data (used if API not loaded)
    private val fallbackLetters = listOf(
        FathahLetter("اَ", "A", 1, gestureName = "Alif"),
        FathahLetter("بَ", "Ba", 2, gestureName = "Ba"),
        FathahLetter("تَ", "Ta", 3, gestureName = "Ta"),
        FathahLetter("ثَ", "Tsa", 4, gestureName = "Tsa"),
        FathahLetter("جَ", "Ja", 5, gestureName = "Jim"),
        FathahLetter("حَ", "Ha", 6, gestureName = "Ha"),
        FathahLetter("خَ", "Kho", 7, gestureName = "Kha"),
        FathahLetter("دَ", "Da", 8, gestureName = "Dal"),
        FathahLetter("ذَ", "Dza", 9, gestureName = "Dzal"),
        FathahLetter("رَ", "Ra", 10, gestureName = "Ra"),
        FathahLetter("زَ", "Za", 11, gestureName = "Zai"),
        FathahLetter("سَ", "Sa", 12, gestureName = "Sin"),
        FathahLetter("شَ", "Sya", 13, gestureName = "Syin"),
        FathahLetter("صَ", "Sha", 14, gestureName = "Sad"),
        FathahLetter("ضَ", "Dha", 15, gestureName = "Dad"),
        FathahLetter("طَ", "Tha", 16, gestureName = "Tha"),
        FathahLetter("ظَ", "Dzha", 17, gestureName = "Zha"),
        FathahLetter("عَ", "A", 18, gestureName = "Ain"),
        FathahLetter("غَ", "Gha", 19, gestureName = "Gain"),
        FathahLetter("فَ", "Fa", 20, gestureName = "Fa"),
        FathahLetter("قَ", "Qa", 21, gestureName = "Qaf"),
        FathahLetter("كَ", "Ka", 22, gestureName = "Kaf"),
        FathahLetter("لَ", "La", 23, gestureName = "Lam"),
        FathahLetter("مَ", "Ma", 24, gestureName = "Mim"),
        FathahLetter("نَ", "Na", 25, gestureName = "Nun"),
        FathahLetter("وَ", "Wa", 26, gestureName = "Waw"),
        FathahLetter("هَ", "Ha", 27, gestureName = "Ha Besar"),
        FathahLetter("يَ", "Ya", 28, gestureName = "Ya")
    )
    
    // Get letters from HijaiyahData API (with diacritic filter) or fallback
    val letters: List<FathahLetter>
        get() {
            val hijaiyahLetters = HijaiyahData.getLettersByDiacritic("fathah")
            return if (hijaiyahLetters.isNotEmpty()) {
                // Convert from HijaiyahLetter to FathahLetter
                hijaiyahLetters.mapIndexed { index, hijaiyah ->
                    FathahLetter(
                        arabic = hijaiyah.arabic,
                        transliteration = hijaiyah.transliteration,
                        position = index + 1, // Sequential position for Fathah
                        isCompleted = hijaiyah.isCompleted,
                        gestureName = hijaiyah.gestureName
                    )
                }
            } else {
                android.util.Log.w("FathahData", "HijaiyahData not loaded, using fallback")
                fallbackLetters
            }
        }
    
    fun getAllLetters(): List<FathahLetter> = letters
    
    fun getLetterByArabic(arabic: String): FathahLetter? {
        return letters.find { it.arabic == arabic }
    }
    
    fun getLetterByPosition(position: Int): FathahLetter? {
        return letters.find { it.position == position }
    }
}
