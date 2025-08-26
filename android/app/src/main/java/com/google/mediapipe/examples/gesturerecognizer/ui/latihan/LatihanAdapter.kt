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

package com.google.mediapipe.examples.gesturerecognizer.ui.latihan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.gesturerecognizer.data.LatihanItem
import com.google.mediapipe.examples.gesturerecognizer.databinding.ItemLatihanExerciseBinding

class LatihanAdapter(
    private val onExerciseClick: (LatihanItem) -> Unit
) : RecyclerView.Adapter<LatihanAdapter.ExerciseViewHolder>() {

    private var exercises = listOf<LatihanItem>()

    fun updateExercises(newExercises: List<LatihanItem>) {
        exercises = newExercises
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemLatihanExerciseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(exercises[position])
    }

    override fun getItemCount(): Int = exercises.size

    inner class ExerciseViewHolder(
        private val binding: ItemLatihanExerciseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(exercise: LatihanItem) {
            binding.tvExerciseTitle.text = exercise.title
            binding.tvExerciseDescription.text = exercise.description
            
            // Set completion status
            if (exercise.isCompleted) {
                binding.ivCheckIcon.visibility = android.view.View.VISIBLE
                binding.ivLockIcon.visibility = android.view.View.GONE
                binding.tvStatus.text = "Selesai"
            } else {
                binding.ivCheckIcon.visibility = android.view.View.GONE
                binding.ivLockIcon.visibility = android.view.View.VISIBLE
                binding.tvStatus.text = "Belum Selesai"
            }
            
            binding.root.setOnClickListener {
                onExerciseClick(exercise)
            }
        }
    }
}
