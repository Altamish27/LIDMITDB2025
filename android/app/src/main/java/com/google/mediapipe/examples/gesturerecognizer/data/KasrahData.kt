package com.google.mediapipe.examples.gesturerecognizer.data

data class KasrahLetter(
    override val arabic: String,
    override val transliteration: String,
    override val position: Int,
    override var isCompleted: Boolean = false
) : ArabicLetter {
    override val gestureName: String? = null
}

object KasrahData {
    val letters = listOf(
        KasrahLetter("أِ", "Hi", 1),
        KasrahLetter("بِ", "Bi", 2),
        KasrahLetter("تِ", "Ti", 3),
        KasrahLetter("ثِ", "Tsi", 4),
        KasrahLetter("جِ", "Ji", 5),
        KasrahLetter("حِ", "Hi", 6),
        KasrahLetter("خِ", "Khi", 7),
        KasrahLetter("دِ", "Di", 8),
        KasrahLetter("ذِ", "Dzi", 9),
        KasrahLetter("رِ", "Ri", 10),
        KasrahLetter("زِ", "Zi", 11),
        KasrahLetter("سِ", "Si", 12),
        KasrahLetter("شِ", "Syi", 13),
        KasrahLetter("صِ", "Shi", 14),
        KasrahLetter("ضِ", "Dhi", 15),
        KasrahLetter("طِ", "Thi", 16),
        KasrahLetter("ظِ", "Dzhi", 17),
        KasrahLetter("عِ", "I", 18),
        KasrahLetter("غِ", "Ghi", 19),
        KasrahLetter("فِ", "Fi", 20),
        KasrahLetter("قِ", "Qi", 21),
        KasrahLetter("كِ", "Ki", 22),
        KasrahLetter("لِ", "Li", 23),
        KasrahLetter("مِ", "Mi", 24),
        KasrahLetter("نِ", "Ni", 25),
        KasrahLetter("وِ", "Wi", 26),
        KasrahLetter("هِ", "Hi", 27),
        KasrahLetter("يِ", "Yi", 28)
    )
    
    fun getAllLetters(): List<KasrahLetter> = letters
    
    fun getLetterByArabic(arabic: String): KasrahLetter? {
        return letters.find { it.arabic == arabic }
    }
    
    fun getLetterByPosition(position: Int): KasrahLetter? {
        return letters.find { it.position == position }
    }
}
