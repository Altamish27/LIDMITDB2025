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
    override var isCompleted: Boolean = false
) : ArabicLetter {
    override val gestureName: String? = null
}

object FathahData {
    val letters = listOf(
        FathahLetter("أَ", "A", 1),
        FathahLetter("بَ", "Ba", 2),
        FathahLetter("تَ", "Ta", 3),
        FathahLetter("ثَ", "Tsa", 4),
        FathahLetter("جَ", "Ja", 5),
        FathahLetter("حَ", "Ha", 6),
        FathahLetter("خَ", "Kho", 7),
        FathahLetter("دَ", "Da", 8),
        FathahLetter("ذَ", "Dza", 9),
        FathahLetter("رَ", "Ra", 10),
        FathahLetter("زَ", "Za", 11),
        FathahLetter("سَ", "Sa", 12),
        FathahLetter("شَ", "Sya", 13),
        FathahLetter("صَ", "Sha", 14),
        FathahLetter("ضَ", "Dha", 15),
        FathahLetter("طَ", "Tha", 16),
        FathahLetter("ظَ", "Dzha", 17),
        FathahLetter("عَ", "A", 18),
        FathahLetter("غَ", "Gha", 19),
        FathahLetter("فَ", "Fa", 20),
        FathahLetter("قَ", "Qa", 21),
        FathahLetter("كَ", "Ka", 22),
        FathahLetter("لَ", "La", 23),
        FathahLetter("مَ", "Ma", 24),
        FathahLetter("نَ", "Na", 25),
        FathahLetter("وَ", "Wa", 26),
        FathahLetter("هَ", "Ha", 27),
        FathahLetter("يَ", "Ya", 28)
    )
    
    fun getAllLetters(): List<FathahLetter> = letters
    
    fun getLetterByArabic(arabic: String): FathahLetter? {
        return letters.find { it.arabic == arabic }
    }
    
    fun getLetterByPosition(position: Int): FathahLetter? {
        return letters.find { it.position == position }
    }
}
