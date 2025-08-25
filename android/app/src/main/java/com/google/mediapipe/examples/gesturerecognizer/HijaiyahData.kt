package com.google.mediapipe.examples.gesturerecognizer

object HijaiyahData {
    data class HijaiyahLetter(
        val id: Int,
        val arabic: String,
        val transliteration: String,
        val gestureName: String
    )

    val letters = listOf(
        HijaiyahLetter(1, "ا", "Alif", "alif"),
        HijaiyahLetter(2, "ب", "Ba", "ba"),
        HijaiyahLetter(3, "ت", "Ta", "ta"),
        HijaiyahLetter(4, "ث", "Tsa", "tsa"),
        HijaiyahLetter(5, "ج", "Jim", "jim"),
        HijaiyahLetter(6, "ح", "Ha", "ha"),
        HijaiyahLetter(7, "خ", "Kha", "kha"),
        HijaiyahLetter(8, "د", "Dal", "dal"),
        HijaiyahLetter(9, "ذ", "Dzal", "dzal"),
        HijaiyahLetter(10, "ر", "Ra", "ra"),
        HijaiyahLetter(11, "ز", "Zai", "zai"),
        HijaiyahLetter(12, "س", "Sin", "sin"),
        HijaiyahLetter(13, "ش", "Syin", "syin"),
        HijaiyahLetter(14, "ص", "Shod", "shod"),
        HijaiyahLetter(15, "ض", "Dhod", "dhod"),
        HijaiyahLetter(16, "ط", "Tho", "tho"),
        HijaiyahLetter(17, "ظ", "Dzho", "dzho"),
        HijaiyahLetter(18, "ع", "Ain", "ain"),
        HijaiyahLetter(19, "غ", "Ghoin", "ghoin"),
        HijaiyahLetter(20, "ف", "Fa", "fa"),
        HijaiyahLetter(21, "ق", "Qof", "qof"),
        HijaiyahLetter(22, "ك", "Kaf", "kaf"),
        HijaiyahLetter(23, "ل", "Lam", "lam"),
        HijaiyahLetter(24, "م", "Mim", "mim"),
        HijaiyahLetter(25, "ن", "Nun", "nun"),
        HijaiyahLetter(26, "و", "Waw", "waw"),
        HijaiyahLetter(27, "ه", "Ha", "ha_end"),
        HijaiyahLetter(28, "ي", "Ya", "ya")
    )

    // Fungsi untuk mendapatkan huruf berdasarkan gesture name
    fun getLetterByGesture(gestureName: String): HijaiyahLetter? {
        return letters.find { it.gestureName.equals(gestureName, ignoreCase = true) }
    }

    // Fungsi untuk mendapatkan huruf berdasarkan ID
    fun getLetterById(id: Int): HijaiyahLetter? {
        return letters.find { it.id == id }
    }

    // Fungsi untuk menyimpan dan mengambil status completed letters
    // (dalam implementasi nyata, ini bisa menggunakan SharedPreferences atau database)
    private val completedLettersSet = mutableSetOf<Int>()

    fun markLetterCompleted(letterId: Int) {
        completedLettersSet.add(letterId)
    }

    fun getCompletedLetters(): Set<Int> {
        return completedLettersSet.toSet()
    }

    fun isLetterCompleted(letterId: Int): Boolean {
        return completedLettersSet.contains(letterId)
    }
}
