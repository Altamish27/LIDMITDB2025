package com.google.mediapipe.examples.gesturerecognizer.data.model

data class ExpandablePanduanHijaiyah(
    val huruf: String,
    val gesture: String,
    val namaLatin: String,
    val deskripsi: String = "",
    val tips: String = "",
    var isExpanded: Boolean = false
)
