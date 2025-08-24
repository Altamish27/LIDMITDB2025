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

class HijaiyahProgressManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("hijaiyah_progress", Context.MODE_PRIVATE)
    
    fun markLetterCompleted(letterPosition: Int) {
        prefs.edit().putBoolean("letter_$letterPosition", true).apply()
    }
    
    fun isLetterCompleted(letterPosition: Int): Boolean {
        return prefs.getBoolean("letter_$letterPosition", false)
    }
    
    fun getCompletedCount(): Int {
        return (1..28).count { isLetterCompleted(it) }
    }
    
    fun resetProgress() {
        prefs.edit().clear().apply()
    }
    
    fun getLettersWithProgress(): List<HijaiyahLetter> {
        return HijaiyahData.getAllLetters().map { letter ->
            letter.copy(isCompleted = isLetterCompleted(letter.position))
        }
    }
}
