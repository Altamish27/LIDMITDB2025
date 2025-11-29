package com.google.mediapipe.examples.gesturerecognizer.data

data class KasrahLetter(
    override val arabic: String,
    override val transliteration: String,
    override val position: Int,
    override var isCompleted: Boolean = false,
    override val gestureName: String? = null
) : ArabicLetter

object KasrahData {
    // Fallback hardcoded data (used if API not loaded)
    private val fallbackLetters = listOf(
        KasrahLetter("اِ", "I", 1, gestureName = "Alif"),
        KasrahLetter("بِ", "Bi", 2, gestureName = "Ba"),
        KasrahLetter("تِ", "Ti", 3, gestureName = "Ta"),
        KasrahLetter("ثِ", "Tsi", 4, gestureName = "Tsa"),
        KasrahLetter("جِ", "Ji", 5, gestureName = "Jim"),
        KasrahLetter("حِ", "Hi", 6, gestureName = "Ha"),
        KasrahLetter("خِ", "Khi", 7, gestureName = "Kha"),
        KasrahLetter("دِ", "Di", 8, gestureName = "Dal"),
        KasrahLetter("ذِ", "Dzi", 9, gestureName = "Dzal"),
        KasrahLetter("رِ", "Ri", 10, gestureName = "Ra"),
        KasrahLetter("زِ", "Zi", 11, gestureName = "Zai"),
        KasrahLetter("سِ", "Si", 12, gestureName = "Sin"),
        KasrahLetter("شِ", "Syi", 13, gestureName = "Syin"),
        KasrahLetter("صِ", "Shi", 14, gestureName = "Sad"),
        KasrahLetter("ضِ", "Dhi", 15, gestureName = "Dad"),
        KasrahLetter("طِ", "Thi", 16, gestureName = "Tha"),
        KasrahLetter("ظِ", "Dzhi", 17, gestureName = "Zha"),
        KasrahLetter("عِ", "I", 18, gestureName = "Ain"),
        KasrahLetter("غِ", "Ghi", 19, gestureName = "Gain"),
        KasrahLetter("فِ", "Fi", 20, gestureName = "Fa"),
        KasrahLetter("قِ", "Qi", 21, gestureName = "Qaf"),
        KasrahLetter("كِ", "Ki", 22, gestureName = "Kaf"),
        KasrahLetter("لِ", "Li", 23, gestureName = "Lam"),
        KasrahLetter("مِ", "Mi", 24, gestureName = "Mim"),
        KasrahLetter("نِ", "Ni", 25, gestureName = "Nun"),
        KasrahLetter("وِ", "Wi", 26, gestureName = "Waw"),
        KasrahLetter("هِ", "Hi", 27, gestureName = "Ha Besar"),
        KasrahLetter("يِ", "Yi", 28, gestureName = "Ya")
    )
    
    // Get letters from HijaiyahData API (with diacritic filter) or fallback
    val letters: List<KasrahLetter>
        get() {
            val hijaiyahLetters = HijaiyahData.getLettersByDiacritic("kasrah")
            return if (hijaiyahLetters.isNotEmpty()) {
                // Convert from HijaiyahLetter to KasrahLetter
                hijaiyahLetters.mapIndexed { index, hijaiyah ->
                    KasrahLetter(
                        arabic = hijaiyah.arabic,
                        transliteration = hijaiyah.transliteration,
                        position = index + 1, // Sequential position for Kasrah
                        isCompleted = hijaiyah.isCompleted,
                        gestureName = hijaiyah.gestureName
                    )
                }
            } else {
                android.util.Log.w("KasrahData", "HijaiyahData not loaded, using fallback")
                fallbackLetters
            }
        }
    
    fun getAllLetters(): List<KasrahLetter> = letters
    
    fun getLetterByArabic(arabic: String): KasrahLetter? {
        return letters.find { it.arabic == arabic }
    }
    
    fun getLetterByPosition(position: Int): KasrahLetter? {
        return letters.find { it.position == position }
    }
}
