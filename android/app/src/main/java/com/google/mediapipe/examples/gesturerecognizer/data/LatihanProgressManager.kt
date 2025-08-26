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

class LatihanProgressManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("latihan_progress", Context.MODE_PRIVATE)
    
    companion object {
        private const val COMPLETED_EXERCISES_KEY = "completed_exercises"
        private const val EXERCISE_PROGRESS_KEY = "exercise_progress"
        private const val CURRENT_LEVEL_KEY = "current_level"
    }
    
    fun markExerciseCompleted(exerciseId: Int) {
        val completedExercises = getCompletedExercises().toMutableSet()
        completedExercises.add(exerciseId)
        
        sharedPreferences.edit()
            .putStringSet(COMPLETED_EXERCISES_KEY, completedExercises.map { it.toString() }.toSet())
            .apply()
    }
    
    fun isExerciseCompleted(exerciseId: Int): Boolean {
        return getCompletedExercises().contains(exerciseId)
    }
    
    fun getCompletedExercises(): Set<Int> {
        val stringSet = sharedPreferences.getStringSet(COMPLETED_EXERCISES_KEY, emptySet()) ?: emptySet()
        return stringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }
    
    fun setExerciseProgress(exerciseId: Int, progress: Int) {
        sharedPreferences.edit()
            .putInt("${EXERCISE_PROGRESS_KEY}_$exerciseId", progress)
            .apply()
    }
    
    fun getExerciseProgress(exerciseId: Int): Int {
        return sharedPreferences.getInt("${EXERCISE_PROGRESS_KEY}_$exerciseId", 0)
    }
    
    fun getCurrentLevel(): Int {
        return sharedPreferences.getInt(CURRENT_LEVEL_KEY, 1)
    }
    
    fun setCurrentLevel(level: Int) {
        sharedPreferences.edit()
            .putInt(CURRENT_LEVEL_KEY, level)
            .apply()
    }
    
    fun getExercisesWithProgress(): List<LatihanItem> {
        return LatihanData.getAllExercises().map { exercise ->
            exercise.copy(
                isCompleted = isExerciseCompleted(exercise.id),
                progress = getExerciseProgress(exercise.id)
            )
        }
    }
    
    fun resetAllProgress() {
        sharedPreferences.edit().clear().apply()
    }
    
    fun getTotalProgress(): Int {
        val completedCount = getCompletedExercises().size
        val totalExercises = LatihanData.getAllExercises().size
        return if (totalExercises > 0) (completedCount * 100) / totalExercises else 0
    }
    
    fun getUnlockedExercises(): List<LatihanItem> {
        val completedExercises = getCompletedExercises()
        val allExercises = getExercisesWithProgress()
        
        return allExercises.mapIndexed { index, exercise ->
            when {
                // First 3 exercises are always unlocked
                exercise.id <= 3 -> exercise
                // Exercise is already completed
                exercise.isCompleted -> exercise
                // Exercise is unlocked if previous exercise is completed
                index > 0 && completedExercises.contains(allExercises[index - 1].id) -> exercise
                // Otherwise locked
                else -> exercise.copy(progress = 0)
            }
        }
    }
}
