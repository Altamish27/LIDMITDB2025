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
    override val gestureName: String,
    override val position: Int,
    override var isCompleted: Boolean = false
) : ArabicLetter

object FathahData {
    val letters = listOf(
        FathahLetter("أَ", "A", "alif", 0),
        FathahLetter("بَ", "Ba", "ba", 1),
        FathahLetter("تَ", "Ta", "ta", 2),
        FathahLetter("ثَ", "Tsa", "tsa", 3),
        FathahLetter("جَ", "Ja", "jim", 4),
        FathahLetter("حَ", "Ha", "ha", 5),
        FathahLetter("خَ", "Kha", "kha", 6),
        FathahLetter("دَ", "Da", "dal", 7),
        FathahLetter("ذَ", "Dza", "dzal", 8),
        FathahLetter("رَ", "Ra", "ra", 9),
        FathahLetter("زَ", "Za", "zai", 10),
        FathahLetter("سَ", "Sa", "sin", 11),
        FathahLetter("شَ", "Sya", "syin", 12),
        FathahLetter("صَ", "Sha", "shod", 13),
        FathahLetter("ضَ", "Dha", "dhod", 14),
        FathahLetter("طَ", "Tha", "tho", 15),
        FathahLetter("ظَ", "Dzha", "dzho", 16),
        FathahLetter("عَ", "A", "ain", 17),
        FathahLetter("غَ", "Gha", "ghoin", 18),
        FathahLetter("فَ", "Fa", "fa", 19),
        FathahLetter("قَ", "Qa", "qof", 20),
        FathahLetter("كَ", "Ka", "kaf", 21),
        FathahLetter("لَ", "La", "lam", 22),
        FathahLetter("مَ", "Ma", "mim", 23),
        FathahLetter("نَ", "Na", "nun", 24),
        FathahLetter("وَ", "Wa", "waw", 25),
        FathahLetter("هَ", "Ha", "ha_end", 26),
        FathahLetter("يَ", "Ya", "ya", 27)
    )
    
    fun getAllLetters(): List<FathahLetter> = letters
    
    fun getLetterByArabic(arabic: String): FathahLetter? {
        return letters.find { it.arabic == arabic }
    }
    
    fun getLetterByPosition(position: Int): FathahLetter? {
        return letters.find { it.position == position }
    }
}
