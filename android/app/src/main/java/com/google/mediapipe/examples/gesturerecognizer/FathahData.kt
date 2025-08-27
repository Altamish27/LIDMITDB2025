package com.google.mediapipe.examples.gesturerecognizer

object FathahData {
    data class FathahLetter(
        val id: Int,
        val arabic: String,
        val transliteration: String,
        val gestureName: String,
        val baseHuruf: String // huruf hijaiyah yang menjadi base
    )

    val letters = listOf(
        FathahLetter(1, "أَ", "A", "alif_fathah", "ا"),
        FathahLetter(2, "بَ", "Ba", "ba_fathah", "ب"),
        FathahLetter(3, "تَ", "Ta", "ta_fathah", "ت"),
        FathahLetter(4, "ثَ", "Tsa", "tsa_fathah", "ث"),
        FathahLetter(5, "جَ", "Ja", "jim_fathah", "ج"),
        FathahLetter(6, "حَ", "Ha", "ha_fathah", "ح"),
        FathahLetter(7, "خَ", "Kha", "kha_fathah", "خ"),
        FathahLetter(8, "دَ", "Da", "dal_fathah", "د"),
        FathahLetter(9, "ذَ", "Dza", "dzal_fathah", "ذ"),
        FathahLetter(10, "رَ", "Ra", "ra_fathah", "ر"),
        FathahLetter(11, "زَ", "Za", "zai_fathah", "ز"),
        FathahLetter(12, "سَ", "Sa", "sin_fathah", "س"),
        FathahLetter(13, "شَ", "Sya", "syin_fathah", "ش"),
        FathahLetter(14, "صَ", "Sha", "shod_fathah", "ص"),
        FathahLetter(15, "ضَ", "Dha", "dhod_fathah", "ض"),
        FathahLetter(16, "طَ", "Tha", "tho_fathah", "ط"),
        FathahLetter(17, "ظَ", "Dzha", "dzho_fathah", "ظ"),
        FathahLetter(18, "عَ", "A", "ain_fathah", "ع"),
        FathahLetter(19, "غَ", "Gha", "ghoin_fathah", "غ"),
        FathahLetter(20, "فَ", "Fa", "fa_fathah", "ف"),
        FathahLetter(21, "قَ", "Qa", "qof_fathah", "ق"),
        FathahLetter(22, "كَ", "Ka", "kaf_fathah", "ك"),
        FathahLetter(23, "لَ", "La", "lam_fathah", "ل"),
        FathahLetter(24, "مَ", "Ma", "mim_fathah", "م"),
        FathahLetter(25, "نَ", "Na", "nun_fathah", "ن"),
        FathahLetter(26, "وَ", "Wa", "waw_fathah", "و"),
        FathahLetter(27, "هَ", "Ha", "ha_end_fathah", "ه"),
        FathahLetter(28, "يَ", "Ya", "ya_fathah", "ي")
    )

    // Fungsi untuk mendapatkan huruf berdasarkan gesture name
    fun getLetterByGesture(gestureName: String): FathahLetter? {
        return letters.find { it.gestureName.equals(gestureName, ignoreCase = true) }
    }

    // Fungsi untuk mendapatkan huruf berdasarkan ID
    fun getLetterById(id: Int): FathahLetter? {
        return letters.find { it.id == id }
    }

    // Fungsi untuk mendapatkan huruf berdasarkan base huruf hijaiyah
    fun getLetterByBaseHuruf(baseHuruf: String): FathahLetter? {
        return letters.find { it.baseHuruf == baseHuruf }
    }
}
