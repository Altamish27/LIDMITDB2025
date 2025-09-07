package com.google.mediapipe.examples.gesturerecognizer.data

data class DhammahLetter(
    override val arabic: String,
    override val transliteration: String,
    override val position: Int,
    override var isCompleted: Boolean = false
) : ArabicLetter {
    override val gestureName: String? = null
}

object DhammahData {
    val letters = listOf(
        DhammahLetter("أُ", "Hu", 1),
        DhammahLetter("بُ", "Bu", 2),
        DhammahLetter("تُ", "Tu", 3),
        DhammahLetter("ثُ", "Tsu", 4),
        DhammahLetter("جُ", "Ju", 5),
        DhammahLetter("حُ", "Hu", 6),
        DhammahLetter("خُ", "Khu", 7),
        DhammahLetter("دُ", "Du", 8),
        DhammahLetter("ذُ", "Dzu", 9),
        DhammahLetter("رُ", "Ru", 10),
        DhammahLetter("زُ", "Zu", 11),
        DhammahLetter("سُ", "Su", 12),
        DhammahLetter("شُ", "Syu", 13),
        DhammahLetter("صُ", "Shu", 14),
        DhammahLetter("ضُ", "Dhu", 15),
        DhammahLetter("طُ", "Thu", 16),
        DhammahLetter("ظُ", "Dzhu", 17),
        DhammahLetter("عُ", "U", 18),
        DhammahLetter("غُ", "Ghu", 19),
        DhammahLetter("فُ", "Fu", 20),
        DhammahLetter("قُ", "Qu", 21),
        DhammahLetter("كُ", "Ku", 22),
        DhammahLetter("لُ", "Lu", 23),
        DhammahLetter("مُ", "Mu", 24),
        DhammahLetter("نُ", "Nu", 25),
        DhammahLetter("وُ", "Wu", 26),
        DhammahLetter("هُ", "Hu", 27),
        DhammahLetter("يُ", "Yu", 28)
    )
    
    fun getAllLetters(): List<DhammahLetter> = letters
    
    fun getLetterByArabic(arabic: String): DhammahLetter? {
        return letters.find { it.arabic == arabic }
    }
    
    fun getLetterByPosition(position: Int): DhammahLetter? {
        return letters.find { it.position == position }
    }
}
