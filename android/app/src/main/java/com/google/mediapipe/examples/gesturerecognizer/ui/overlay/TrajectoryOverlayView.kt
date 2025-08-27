package com.google.mediapipe.examples.gesturerecognizer.ui.overlay

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Size
import android.view.View
import androidx.annotation.MainThread
import kotlin.math.*

/**
 * Custom View untuk menggambar trajectory ujung jari telunjuk secara real-time
 * Optimized untuk performance dengan object reuse dan minimal allocations
 */
class TrajectoryOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "TrajectoryOverlayView"
        private const val MAX_ALPHA = 255
        private const val MIN_ALPHA = 50
        private const val STROKE_WIDTH = 8f
        private const val TIP_RADIUS = 12f
        private const val ARROW_SIZE = 20f
        private const val DIRECTION_TEXT_SIZE = 48f
    }

    // Reusable objects untuk menghindari alokasi di onDraw
    private val trajectoryPath = Path()
    private val arrowPath = Path()
    private val tempMatrix = Matrix()
    private val tempRectF = RectF()
    private val tempPointF = PointF()
    
    // Paint objects (reused)
    private val trajectoryPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = STROKE_WIDTH
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    
    private val tipPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.RED
    }
    
    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
    }
    
    private val directionTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = DIRECTION_TEXT_SIZE
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        setShadowLayer(4f, 2f, 2f, Color.BLACK)
    }

    // Data trajectory
    private var trajectoryPoints: List<PointF> = emptyList()
    private var globalDirection: String = ""
    private var isRecording: Boolean = false

    // Callback listeners
    private var onTrajectorySegmented: ((startIdx: Int, endIdx: Int) -> Unit)? = null
    private var onDirectionChanged: ((direction: String) -> Unit)? = null

    /**
     * Update trajectory points dan trigger redraw
     * HARUS dipanggil dari main thread
     */
    @MainThread
    fun updateTrajectory(
        points: List<PointF>,
        viewSize: Size,
        imageSize: Size,
        isFrontCamera: Boolean,
        rotationDegrees: Int
    ) {
        // Map normalized coordinates ke view coordinates
        trajectoryPoints = points.map { point ->
            CoordinateMapper.mapNormalizedToView(
                point.x, point.y,
                imageSize, Size(width, height),
                rotationDegrees, isFrontCamera
            )
        }
        
        // Update global direction
        if (trajectoryPoints.size >= 2) {
            val start = trajectoryPoints.first()
            val end = trajectoryPoints.last()
            globalDirection = calculateGlobalDirection(start, end)
        }
        
        // Trigger redraw
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        if (trajectoryPoints.size < 2) return
        
        // Draw trajectory path dengan alpha gradient
        drawTrajectoryPath(canvas)
        
        // Draw tip point (titik terbaru)
        drawTipPoint(canvas)
        
        // Draw arrow head untuk arah
        drawArrowHead(canvas)
        
        // Draw direction text
        drawDirectionText(canvas)
        
        // Draw recording indicator
        if (isRecording) {
            drawRecordingIndicator(canvas)
        }
    }

    private fun drawTrajectoryPath(canvas: Canvas) {
        trajectoryPath.reset()
        
        if (trajectoryPoints.isEmpty()) return
        
        val pointCount = trajectoryPoints.size
        
        // Create smooth path
        trajectoryPath.moveTo(trajectoryPoints[0].x, trajectoryPoints[0].y)
        
        for (i in 1 until pointCount) {
            val currentPoint = trajectoryPoints[i]
            val prevPoint = trajectoryPoints[i - 1]
            
            // Smooth curve menggunakan quadratic bezier
            val midX = (prevPoint.x + currentPoint.x) / 2
            val midY = (prevPoint.y + currentPoint.y) / 2
            
            if (i == 1) {
                trajectoryPath.lineTo(midX, midY)
            } else {
                trajectoryPath.quadTo(prevPoint.x, prevPoint.y, midX, midY)
            }
            
            // Calculate alpha berdasarkan posisi dalam trajectory
            val alpha = calculateAlpha(i, pointCount)
            trajectoryPaint.alpha = alpha
            trajectoryPaint.color = Color.argb(alpha, 0, 255, 255) // Cyan dengan fade
            
            // Draw segment
            val segmentPath = Path()
            if (i == 1) {
                segmentPath.moveTo(trajectoryPoints[0].x, trajectoryPoints[0].y)
                segmentPath.lineTo(midX, midY)
            } else {
                val prevMidX = (trajectoryPoints[i - 2].x + prevPoint.x) / 2
                val prevMidY = (trajectoryPoints[i - 2].y + prevPoint.y) / 2
                segmentPath.moveTo(prevMidX, prevMidY)
                segmentPath.quadTo(prevPoint.x, prevPoint.y, midX, midY)
            }
            
            canvas.drawPath(segmentPath, trajectoryPaint)
        }
    }

    private fun drawTipPoint(canvas: Canvas) {
        if (trajectoryPoints.isNotEmpty()) {
            val tip = trajectoryPoints.last()
            tipPaint.alpha = MAX_ALPHA
            canvas.drawCircle(tip.x, tip.y, TIP_RADIUS, tipPaint)
            
            // Inner circle
            tipPaint.color = Color.WHITE
            canvas.drawCircle(tip.x, tip.y, TIP_RADIUS * 0.5f, tipPaint)
            tipPaint.color = Color.RED
        }
    }

    private fun drawArrowHead(canvas: Canvas) {
        if (trajectoryPoints.size < 2) return
        
        val lastPoint = trajectoryPoints.last()
        val secondLastPoint = trajectoryPoints[trajectoryPoints.size - 2]
        
        // Calculate arrow direction
        val dx = lastPoint.x - secondLastPoint.x
        val dy = lastPoint.y - secondLastPoint.y
        val angle = atan2(dy, dx)
        
        // Create arrow head path
        arrowPath.reset()
        arrowPath.moveTo(
            lastPoint.x + cos(angle + PI - PI/6).toFloat() * ARROW_SIZE,
            lastPoint.y + sin(angle + PI - PI/6).toFloat() * ARROW_SIZE
        )
        arrowPath.lineTo(lastPoint.x, lastPoint.y)
        arrowPath.lineTo(
            lastPoint.x + cos(angle + PI + PI/6).toFloat() * ARROW_SIZE,
            lastPoint.y + sin(angle + PI + PI/6).toFloat() * ARROW_SIZE
        )
        
        arrowPaint.alpha = MAX_ALPHA
        canvas.drawPath(arrowPath, arrowPaint)
    }

    private fun drawDirectionText(canvas: Canvas) {
        if (globalDirection.isNotEmpty()) {
            val x = width * 0.1f
            val y = height * 0.1f
            canvas.drawText(globalDirection, x, y, directionTextPaint)
        }
    }

    private fun drawRecordingIndicator(canvas: Canvas) {
        val radius = 20f
        val x = width - radius * 2
        val y = radius * 2
        
        tipPaint.color = Color.RED
        tipPaint.alpha = if ((System.currentTimeMillis() / 500) % 2 == 0L) 255 else 128
        canvas.drawCircle(x, y, radius, tipPaint)
        
        directionTextPaint.textSize = 32f
        canvas.drawText("REC", x, y + 50f, directionTextPaint)
        directionTextPaint.textSize = DIRECTION_TEXT_SIZE
    }

    private fun calculateAlpha(index: Int, totalPoints: Int): Int {
        val ratio = index.toFloat() / totalPoints.toFloat()
        return (MIN_ALPHA + (MAX_ALPHA - MIN_ALPHA) * ratio).toInt()
    }

    private fun calculateGlobalDirection(start: PointF, end: PointF): String {
        val dx = end.x - start.x
        val dy = end.y - start.y
        
        if (abs(dx) < 20 && abs(dy) < 20) return "●" // Static
        
        val angle = atan2(dy, dx) * 180 / PI
        
        return when {
            angle >= -22.5 && angle < 22.5 -> "→"
            angle >= 22.5 && angle < 67.5 -> "↘"
            angle >= 67.5 && angle < 112.5 -> "↓"
            angle >= 112.5 && angle < 157.5 -> "↙"
            angle >= 157.5 || angle < -157.5 -> "←"
            angle >= -157.5 && angle < -112.5 -> "↖"
            angle >= -112.5 && angle < -67.5 -> "↑"
            angle >= -67.5 && angle < -22.5 -> "↗"
            else -> "?"
        }
    }

    // Public API methods
    fun setRecording(recording: Boolean) {
        isRecording = recording
        invalidate()
    }

    fun clearTrajectory() {
        trajectoryPoints = emptyList()
        globalDirection = ""
        invalidate()
    }

    fun setOnTrajectorySegmentedListener(listener: (startIdx: Int, endIdx: Int) -> Unit) {
        onTrajectorySegmented = listener
    }

    fun setOnDirectionChangedListener(listener: (direction: String) -> Unit) {
        onDirectionChanged = listener
    }

    fun getCurrentDirection(): String = globalDirection
    
    fun getTrajectoryPointCount(): Int = trajectoryPoints.size
    
    // Additional configuration methods
    fun setTrajectoryWidth(width: Float) {
        trajectoryPaint.strokeWidth = width
        invalidate()
    }
    
    fun setTrajectoryColor(color: Int) {
        trajectoryPaint.color = color
        invalidate()
    }
    
    fun setFadeEnabled(enabled: Boolean) {
        isFadeEnabled = enabled
        invalidate()
    }
    
    fun setArrowEnabled(enabled: Boolean) {
        isArrowEnabled = enabled
        invalidate()
    }
    
    private var isFadeEnabled = true
    private var isArrowEnabled = true
}
