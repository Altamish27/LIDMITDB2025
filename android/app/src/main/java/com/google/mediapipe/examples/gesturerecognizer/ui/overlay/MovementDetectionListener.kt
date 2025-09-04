package com.google.mediapipe.examples.gesturerecognizer.ui.overlay

/**
 * Interface untuk komunikasi movement detection dari TrajectoryOverlayView
 * ke komponen lain yang membutuhkan informasi arah gerakan
 */
interface MovementDetectionListener {
    
    /**
     * Dipanggil ketika gerakan berubah (arah atau status static)
     * @param movementType Enum type untuk pattern matching
     * @param isStatic true jika tangan dalam keadaan static
     */
    fun onMovementDetected(movementType: MovementType, isStatic: Boolean)
    
    /**
     * Dipanggil ketika arah gerakan berubah
     * @param direction Arah gerakan dalam format symbol (→, ←, ↑, ↓, dll)
     * @param movementType Enum type untuk pattern matching
     */
    fun onMovementDirectionChanged(direction: String, movementType: MovementType)
    
    /**
     * Dipanggil ketika status static berubah
     * @param isStatic true jika tangan dalam keadaan static
     */
    fun onStaticStatusChanged(isStatic: Boolean)
    
    /**
     * Dipanggil ketika tangan tidak terdeteksi (hand lost)
     * Implementor harus clear movement history dan reset state
     */
    fun onHandLost()
}

/**
 * Enum untuk mapping symbol ke movement type
 * Digunakan untuk pattern matching (Fathah, Dhammah, dll)
 */
enum class MovementType {
    STATIC,
    LEFT,
    RIGHT, 
    UP,
    DOWN,
    DIAGONAL_UP_LEFT,
    DIAGONAL_UP_RIGHT,
    DIAGONAL_DOWN_LEFT,
    DIAGONAL_DOWN_RIGHT,
    UNKNOWN
}

/**
 * Utility object untuk konversi symbol ke MovementType
 */
object MovementConverter {
    fun toMovementType(symbol: String): MovementType {
        return when (symbol) {
            "●" -> MovementType.STATIC
            "→" -> MovementType.RIGHT
            "←" -> MovementType.LEFT
            "↑" -> MovementType.UP
            "↓" -> MovementType.DOWN
            "↗" -> MovementType.DIAGONAL_UP_RIGHT
            "↖" -> MovementType.DIAGONAL_UP_LEFT
            "↘" -> MovementType.DIAGONAL_DOWN_RIGHT
            "↙" -> MovementType.DIAGONAL_DOWN_LEFT
            "STATIC" -> MovementType.STATIC
            else -> MovementType.UNKNOWN
        }
    }
    
    fun toSymbol(type: MovementType): String {
        return when (type) {
            MovementType.STATIC -> "●"
            MovementType.RIGHT -> "→"
            MovementType.LEFT -> "←"
            MovementType.UP -> "↑"
            MovementType.DOWN -> "↓"
            MovementType.DIAGONAL_UP_RIGHT -> "↗"
            MovementType.DIAGONAL_UP_LEFT -> "↖"
            MovementType.DIAGONAL_DOWN_RIGHT -> "↘"
            MovementType.DIAGONAL_DOWN_LEFT -> "↙"
            MovementType.UNKNOWN -> "?"
        }
    }
}
