package com.google.mediapipe.examples.gesturerecognizer.ui.overlay

import android.graphics.PointF
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import kotlin.math.*

/**
 * Ring buffer untuk menyimpan trajectory points dengan EMA smoothing
 * Thread-safe untuk concurrent access dari analyzer dan UI thread
 */
class TrajectoryRingBuffer(
    private val capacity: Int = 24,                 // Lebih pendek agar ekor cepat hilang
    private val smoothingAlpha: Float = 0.25f,      // Minimal smoothing (low speed)
    private val maxSmoothingAlpha: Float = 0.75f,   // Smoothing efektif saat kecepatan tinggi (lebih responsif)
    private val adaptiveSmoothing: Boolean = true   // Aktifkan adaptive smoothing
) {
    
    // Internal storage - menggunakan array untuk performance
    private val buffer = Array<PointF?>(capacity) { null }
    private var head = 0
    private var size = 0
    private var lastSmoothedPoint: PointF? = null
    
    // Velocity tracking untuk segmentation
    private val velocityBuffer = FloatArray(capacity)
    private var velocityHead = 0
    private var velocitySize = 0
    
    // Segmentation parameters
    private val velocityThreshold = 10f // pixels per frame
    private val minSegmentLength = 5
    
    // Synchronization
    private val lock = Any()
    
    /**
     * Add point dengan EMA smoothing
     * Thread-safe, dapat dipanggil dari analyzer thread
     */
    @WorkerThread
    fun pushPoint(x: Float, y: Float): PointF {
        synchronized(lock) {
            // Apply EMA smoothing
            val smoothedPoint = if (lastSmoothedPoint == null) {
                PointF(x, y)
            } else {
                val last = lastSmoothedPoint!!
                // Hitung kecepatan raw (berbasis koordinat normalized 0..1)
                val rawVelocity = sqrt((x - last.x).pow(2) + (y - last.y).pow(2))
                // Normalisasi kecepatan terhadap ambang (semakin besar semakin mendekati 1)
                val velocityScale = 0.15f // ~gerakan besar antar frame dalam ruang normalized
                val vNorm = (rawVelocity / velocityScale).coerceIn(0f, 1f)
                val alphaEff = if (adaptiveSmoothing) {
                    // Saat kecepatan tinggi kita ingin smoothing lebih agresif (alpha tinggi mendekati 1 artinya mengejar raw point lebih cepat)
                    smoothingAlpha + (maxSmoothingAlpha - smoothingAlpha) * vNorm
                } else smoothingAlpha
                PointF(
                    last.x + alphaEff * (x - last.x),
                    last.y + alphaEff * (y - last.y)
                )
            }
            
            // Calculate velocity untuk segmentation
            if (size > 0) {
                val prev = buffer[(head - 1 + capacity) % capacity]!!
                val velocity = sqrt(
                    (smoothedPoint.x - prev.x).pow(2) + 
                    (smoothedPoint.y - prev.y).pow(2)
                )
                pushVelocity(velocity)
            }
            
            // Store point in ring buffer
            buffer[head] = smoothedPoint
            head = (head + 1) % capacity
            
            if (size < capacity) {
                size++
            }
            
            lastSmoothedPoint = smoothedPoint
            return smoothedPoint
        }
    }
    
    /**
     * Get all points as list dalam urutan chronological
     * Thread-safe, biasanya dipanggil dari UI thread
     */
    @MainThread
    fun asList(): List<PointF> {
        synchronized(lock) {
            if (size == 0) return emptyList()
            
            val result = mutableListOf<PointF>()
            val startIdx = if (size < capacity) 0 else head
            
            for (i in 0 until size) {
                val idx = (startIdx + i) % capacity
                buffer[idx]?.let { result.add(PointF(it.x, it.y)) }
            }
            
            return result
        }
    }
    
    /**
     * Clear semua points
     */
    @MainThread
    fun clear() {
        synchronized(lock) {
            for (i in buffer.indices) {
                buffer[i] = null
            }
            head = 0
            size = 0
            velocityHead = 0
            velocitySize = 0
            lastSmoothedPoint = null
        }
    }
    
    /**
     * Get current size
     */
    fun getCurrentSize(): Int {
        synchronized(lock) {
            return size
        }
    }
    
    /**
     * Check if buffer is full
     */
    fun isFull(): Boolean {
        synchronized(lock) {
            return size >= capacity
        }
    }
    
    /**
     * Get latest point without removing
     */
    fun getLatestPoint(): PointF? {
        synchronized(lock) {
            if (size == 0) return null
            val latestIdx = (head - 1 + capacity) % capacity
            return buffer[latestIdx]?.let { PointF(it.x, it.y) }
        }
    }
    
    /**
     * Get points dalam rentang tertentu
     */
    fun getPointsInRange(startIdx: Int, endIdx: Int): List<PointF> {
        synchronized(lock) {
            if (size == 0 || startIdx < 0 || endIdx >= size || startIdx > endIdx) {
                return emptyList()
            }
            
            val result = mutableListOf<PointF>()
            val bufferStartIdx = if (size < capacity) 0 else head
            
            for (i in startIdx..endIdx) {
                val idx = (bufferStartIdx + i) % capacity
                buffer[idx]?.let { result.add(PointF(it.x, it.y)) }
            }
            
            return result
        }
    }
    
    /**
     * Detect trajectory segments berdasarkan velocity
     * Returns list of (startIdx, endIdx) pairs
     */
    fun detectSegments(): List<Pair<Int, Int>> {
        synchronized(lock) {
            if (velocitySize < minSegmentLength) return emptyList()
            
            val segments = mutableListOf<Pair<Int, Int>>()
            var segmentStart = 0
            var inLowVelocity = false
            
            for (i in 0 until velocitySize) {
                val velocity = velocityBuffer[(velocityHead - velocitySize + i + capacity) % capacity]
                
                if (velocity < velocityThreshold) {
                    if (!inLowVelocity && i - segmentStart >= minSegmentLength) {
                        // End of high velocity segment
                        segments.add(Pair(segmentStart, i - 1))
                    }
                    inLowVelocity = true
                } else {
                    if (inLowVelocity) {
                        // Start of new high velocity segment
                        segmentStart = i
                    }
                    inLowVelocity = false
                }
            }
            
            // Add final segment jika masih dalam high velocity
            if (!inLowVelocity && velocitySize - segmentStart >= minSegmentLength) {
                segments.add(Pair(segmentStart, velocitySize - 1))
            }
            
            return segments
        }
    }
    
    /**
     * Get average velocity dalam window tertentu
     */
    fun getAverageVelocity(windowSize: Int = 5): Float {
        synchronized(lock) {
            if (velocitySize == 0) return 0f
            
            val actualWindowSize = minOf(windowSize, velocitySize)
            var sum = 0f
            
            for (i in 0 until actualWindowSize) {
                val idx = (velocityHead - actualWindowSize + i + capacity) % capacity
                sum += velocityBuffer[idx]
            }
            
            return sum / actualWindowSize
        }
    }
    
    /**
     * Export trajectory ke JSON-friendly format
     */
    fun exportToJson(): TrajectoryData {
        synchronized(lock) {
            val points = asList()
            val velocities = if (velocitySize > 0) {
                FloatArray(velocitySize) { i ->
                    val idx = (velocityHead - velocitySize + i + capacity) % capacity
                    velocityBuffer[idx]
                }
            } else {
                floatArrayOf()
            }
            
            return TrajectoryData(
                points = points.map { Pair(it.x, it.y) },
                velocities = velocities.toList(),
                timestamp = System.currentTimeMillis(),
                capacity = capacity,
                smoothingAlpha = smoothingAlpha
            )
        }
    }
    
    /**
     * Restore dari JSON data
     */
    fun importFromJson(data: TrajectoryData) {
        synchronized(lock) {
            clear()
            
            data.points.forEach { (x, y) ->
                pushPoint(x, y)
            }
        }
    }
    
    private fun pushVelocity(velocity: Float) {
        velocityBuffer[velocityHead] = velocity
        velocityHead = (velocityHead + 1) % capacity
        
        if (velocitySize < capacity) {
            velocitySize++
        }
    }
}

/**
 * Data class untuk export/import trajectory
 */
data class TrajectoryData(
    val points: List<Pair<Float, Float>>,
    val velocities: List<Float>,
    val timestamp: Long,
    val capacity: Int,
    val smoothingAlpha: Float
)
