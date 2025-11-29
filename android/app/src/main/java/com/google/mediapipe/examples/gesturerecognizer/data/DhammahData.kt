package com.google.mediapipe.examples.gesturerecognizer.data

data class DhammahLetter(
    override val arabic: String,
    override val transliteration: String,
    override val position: Int,
    override var isCompleted: Boolean = false,
    override val gestureName: String? = null
) : ArabicLetter

object DhammahData {
    // Fallback hardcoded data (used if API not loaded)
    private val fallbackLetters = listOf(
        DhammahLetter("اُ", "U", 1, gestureName = "Alif"),
        DhammahLetter("بُ", "Bu", 2, gestureName = "Ba"),
        DhammahLetter("تُ", "Tu", 3, gestureName = "Ta"),
        DhammahLetter("ثُ", "Tsu", 4, gestureName = "Tsa"),
        DhammahLetter("جُ", "Ju", 5, gestureName = "Jim"),
        DhammahLetter("حُ", "Hu", 6, gestureName = "Ha"),
        DhammahLetter("خُ", "Khu", 7, gestureName = "Kha"),
        DhammahLetter("دُ", "Du", 8, gestureName = "Dal"),
        DhammahLetter("ذُ", "Dzu", 9, gestureName = "Dzal"),
        DhammahLetter("رُ", "Ru", 10, gestureName = "Ra"),
        DhammahLetter("زُ", "Zu", 11, gestureName = "Zai"),
        DhammahLetter("سُ", "Su", 12, gestureName = "Sin"),
        DhammahLetter("شُ", "Syu", 13, gestureName = "Syin"),
        DhammahLetter("صُ", "Shu", 14, gestureName = "Sad"),
        DhammahLetter("ضُ", "Dhu", 15, gestureName = "Dad"),
        DhammahLetter("طُ", "Thu", 16, gestureName = "Tha"),
        DhammahLetter("ظُ", "Dzhu", 17, gestureName = "Zha"),
        DhammahLetter("عُ", "U", 18, gestureName = "Ain"),
        DhammahLetter("غُ", "Ghu", 19, gestureName = "Gain"),
        DhammahLetter("فُ", "Fu", 20, gestureName = "Fa"),
        DhammahLetter("قُ", "Qu", 21, gestureName = "Qaf"),
        DhammahLetter("كُ", "Ku", 22, gestureName = "Kaf"),
        DhammahLetter("لُ", "Lu", 23, gestureName = "Lam"),
        DhammahLetter("مُ", "Mu", 24, gestureName = "Mim"),
        DhammahLetter("نُ", "Nu", 25, gestureName = "Nun"),
        DhammahLetter("وُ", "Wu", 26, gestureName = "Waw"),
        DhammahLetter("هُ", "Hu", 27, gestureName = "Ha Besar"),
        DhammahLetter("يُ", "Yu", 28, gestureName = "Ya")
    )
    
    // Get letters from HijaiyahData API (with diacritic filter) or fallback
    val letters: List<DhammahLetter>
        get() {
            val hijaiyahLetters = HijaiyahData.getLettersByDiacritic("dhammah")
            return if (hijaiyahLetters.isNotEmpty()) {
                // Convert from HijaiyahLetter to DhammahLetter
                hijaiyahLetters.mapIndexed { index, hijaiyah ->
                    DhammahLetter(
                        arabic = hijaiyah.arabic,
                        transliteration = hijaiyah.transliteration,
                        position = index + 1, // Sequential position for Dhammah
                        isCompleted = hijaiyah.isCompleted,
                        gestureName = hijaiyah.gestureName
                    )
                }
            } else {
                android.util.Log.w("DhammahData", "HijaiyahData not loaded, using fallback")
                fallbackLetters
            }
        }
    
    fun getAllLetters(): List<DhammahLetter> = letters
    
    fun getLetterByArabic(arabic: String): DhammahLetter? {
        return letters.find { it.arabic == arabic }
    }
    
    fun getLetterByPosition(position: Int): DhammahLetter? {
        return letters.find { it.position == position }
    }
}
