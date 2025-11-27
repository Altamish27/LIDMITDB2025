package com.google.mediapipe.examples.gesturerecognizer.data.model

data class Surat(
    val nomor: Int,
    val nama: String,
    val namaArab: String,
    val jumlahAyat: Int,
    val tempatTurun: String, // "Makkah" atau "Madinah"
    val juz: Int,
    val ayatList: List<Ayat> = emptyList()
) {
    val tempatTurunIndonesia: String
        get() = if (tempatTurun == "Makkah") "Makiyah" else "Madaniyah"
}

data class Ayat(
    val nomor: Int,
    val teksArab: String,
    val terjemahan: String
)
