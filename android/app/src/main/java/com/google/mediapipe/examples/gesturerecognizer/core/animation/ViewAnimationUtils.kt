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
package com.google.mediapipe.examples.gesturerecognizer.core.animation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator

/**
 * Utility class for consistent animations across all activities
 * Ensures smooth entrance animations without clipping
 */
object ViewAnimationUtils {
    
    /**
     * Fade in entire screen - use at start of activity
     */
    fun fadeInScreen(view: View, duration: Long = 300) {
        ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
            this.duration = duration
            interpolator = DecelerateInterpolator()
            start()
        }
    }
    
    /**
     * Standard view entrance with fade, scale, and slide
     */
    fun animateViewEntrance(
        view: View,
        delay: Long = 0,
        duration: Long = 700,
        translationY: Float = 100f,
        rotationDegrees: Float = 0f,
        overshoot: Float = 1.5f
    ) {
        view.postDelayed({
            val animSet = AnimatorSet()
            
            val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
                this.duration = duration
                interpolator = DecelerateInterpolator()
            }
            
            val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.3f, 1f).apply {
                this.duration = duration
                interpolator = OvershootInterpolator(overshoot)
            }
            
            val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.3f, 1f).apply {
                this.duration = duration
                interpolator = OvershootInterpolator(overshoot)
            }
            
            val translateY = ObjectAnimator.ofFloat(view, "translationY", translationY, 0f).apply {
                this.duration = duration
                interpolator = OvershootInterpolator(overshoot * 0.7f)
            }
            
            val animators = mutableListOf<android.animation.Animator>(fadeIn, scaleX, scaleY, translateY)
            
            if (rotationDegrees != 0f) {
                val rotation = ObjectAnimator.ofFloat(view, "rotation", rotationDegrees, 0f).apply {
                    this.duration = duration + 100
                    interpolator = OvershootInterpolator(1.2f)
                }
                animators.add(rotation)
            }
            
            animSet.playTogether(animators)
            animSet.start()
        }, delay)
    }
    
    /**
     * Card entrance with dramatic effect
     */
    fun animateCardEntrance(
        view: View,
        delay: Long = 0
    ) {
        view.postDelayed({
            val animSet = AnimatorSet()
            
            val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
                duration = 800
                interpolator = DecelerateInterpolator()
            }
            
            val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.5f, 1.05f, 1f).apply {
                duration = 900
                interpolator = OvershootInterpolator(2.0f)
            }
            
            val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1.05f, 1f).apply {
                duration = 900
                interpolator = OvershootInterpolator(2.0f)
            }
            
            val translateY = ObjectAnimator.ofFloat(view, "translationY", 150f, -15f, 0f).apply {
                duration = 900
                interpolator = OvershootInterpolator(1.5f)
            }
            
            val rotation = ObjectAnimator.ofFloat(view, "rotation", 8f, -3f, 0f).apply {
                duration = 900
                interpolator = OvershootInterpolator(1.2f)
            }
            
            animSet.playTogether(fadeIn, scaleX, scaleY, translateY, rotation)
            animSet.start()
        }, delay)
    }
    
    /**
     * List item entrance (staggered animation for RecyclerView items)
     */
    fun animateListItem(
        view: View,
        position: Int,
        baseDelay: Long = 50
    ) {
        val delay = position * baseDelay
        view.postDelayed({
            val animSet = AnimatorSet()
            
            val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
                duration = 500
                interpolator = DecelerateInterpolator()
            }
            
            val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.8f, 1f).apply {
                duration = 600
                interpolator = OvershootInterpolator(1.3f)
            }
            
            val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.8f, 1f).apply {
                duration = 600
                interpolator = OvershootInterpolator(1.3f)
            }
            
            val translateX = ObjectAnimator.ofFloat(view, "translationX", -100f, 0f).apply {
                duration = 600
                interpolator = OvershootInterpolator(1.2f)
            }
            
            animSet.playTogether(fadeIn, scaleX, scaleY, translateX)
            animSet.start()
        }, delay)
    }
    
    /**
     * Button entrance with attention-grabbing effect
     */
    fun animateButtonEntrance(
        view: View,
        delay: Long = 0
    ) {
        view.postDelayed({
            val animSet = AnimatorSet()
            
            val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
                duration = 600
                interpolator = DecelerateInterpolator()
            }
            
            val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.3f, 1.2f, 0.95f, 1f).apply {
                duration = 1000
                interpolator = OvershootInterpolator(2.5f)
            }
            
            val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.3f, 1.2f, 0.95f, 1f).apply {
                duration = 1000
                interpolator = OvershootInterpolator(2.5f)
            }
            
            val translateY = ObjectAnimator.ofFloat(view, "translationY", 150f, -25f, 0f).apply {
                duration = 1000
                interpolator = OvershootInterpolator(1.8f)
            }
            
            animSet.playTogether(fadeIn, scaleX, scaleY, translateY)
            animSet.start()
        }, delay)
    }
    
    /**
     * Click animation for interactive elements
     */
    fun animateClick(view: View, action: () -> Unit) {
        val pressDown = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.92f).apply {
                    duration = 100
                    interpolator = DecelerateInterpolator()
                },
                ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.92f).apply {
                    duration = 100
                    interpolator = DecelerateInterpolator()
                },
                ObjectAnimator.ofFloat(view, "alpha", 1f, 0.8f).apply {
                    duration = 100
                }
            )
        }
        
        val springBack = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 0.92f, 1.05f, 1f).apply {
                    duration = 400
                    interpolator = OvershootInterpolator(2.0f)
                    startDelay = 100
                },
                ObjectAnimator.ofFloat(view, "scaleY", 0.92f, 1.05f, 1f).apply {
                    duration = 400
                    interpolator = OvershootInterpolator(2.0f)
                    startDelay = 100
                },
                ObjectAnimator.ofFloat(view, "alpha", 0.8f, 1f).apply {
                    duration = 200
                    startDelay = 100
                }
            )
        }
        
        pressDown.start()
        springBack.start()
        
        view.postDelayed(action, 300)
    }
    
    /**
     * Prepare view for entrance animation (set initial state)
     */
    fun prepareForAnimation(view: View, translationY: Float = 200f) {
        view.apply {
            alpha = 0f
            scaleX = 0.3f
            scaleY = 0.3f
            this.translationY = translationY
        }
    }
    
    /**
     * Prepare multiple views for animation
     */
    fun prepareViewsForAnimation(vararg views: View?, translationY: Float = 200f) {
        views.forEach { view ->
            view?.let { prepareForAnimation(it, translationY) }
        }
    }
}
