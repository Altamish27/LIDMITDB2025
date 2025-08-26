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

data class LatihanItem(
    val id: Int,
    val title: String,
    val description: String,
    val level: String,
    val totalQuestions: Int,
    val isCompleted: Boolean = false,
    val progress: Int = 0, // Progress in percentage
    val icon: String = "ic_exercise" // Icon identifier
)

object LatihanData {
    val exercises = listOf(
        LatihanItem(
            id = 1,
            title = "Latihan 1",
            description = "Dasar - 25 Halaman",
            level = "Dasar",
            totalQuestions = 25,
            isCompleted = false,
            progress = 0,
            icon = "1"
        ),
        LatihanItem(
            id = 2,
            title = "Latihan 2",
            description = "Lanjutan - 28 Halaman",
            level = "Lanjutan", 
            totalQuestions = 28,
            isCompleted = false,
            progress = 0,
            icon = "2"
        ),
        LatihanItem(
            id = 3,
            title = "Latihan 3",
            description = "Sedang dipelajari - 30 Halaman",
            level = "Menengah",
            totalQuestions = 30,
            isCompleted = false,
            progress = 0,
            icon = "3"
        ),
        LatihanItem(
            id = 4,
            title = "Latihan 4",
            description = "Terkunci - 32 Halaman",
            level = "Lanjut",
            totalQuestions = 32,
            isCompleted = false,
            progress = 0,
            icon = "4"
        ),
        LatihanItem(
            id = 5,
            title = "Latihan 5",
            description = "Terkunci - 35 Halaman",
            level = "Mahir",
            totalQuestions = 35,
            isCompleted = false,
            progress = 0,
            icon = "5"
        ),
        LatihanItem(
            id = 6,
            title = "Latihan 6", 
            description = "Terkunci - 38 Halaman",
            level = "Expert",
            totalQuestions = 38,
            isCompleted = false,
            progress = 0,
            icon = "6"
        )
    )
    
    fun getAllExercises(): List<LatihanItem> {
        return exercises
    }
    
    fun searchExercises(query: String, exercises: List<LatihanItem>): List<LatihanItem> {
        if (query.isBlank()) return exercises
        
        return exercises.filter { exercise ->
            exercise.title.contains(query, ignoreCase = true) ||
            exercise.description.contains(query, ignoreCase = true) ||
            exercise.level.contains(query, ignoreCase = true)
        }
    }
    
    fun getExerciseById(id: Int): LatihanItem? {
        return exercises.find { it.id == id }
    }
}
