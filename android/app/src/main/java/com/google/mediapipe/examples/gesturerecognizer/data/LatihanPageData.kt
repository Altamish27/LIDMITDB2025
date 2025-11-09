/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.mediapipe.examples.gesturerecognizer.data

/**
 * Data class untuk setiap huruf dalam latihan
 */
data class LatihanHuruf(
    val arabic: String,
    val latin: String,
    val gestureName: String? = null,
    val position: Int,
    val isCompleted: Boolean = false,
    val isActive: Boolean = false
)

/**
 * Data class untuk setiap baris dalam halaman
 */
data class LatihanBaris(
    val id: Int,
    val hurufList: List<LatihanHuruf>,
    val isCompleted: Boolean = false
)

/**
 * Data class untuk setiap halaman dalam jilid
 */
data class LatihanHalaman(
    val id: Int,
    val title: String,
    val description: String,
    val barisList: List<LatihanBaris>,
    val isCompleted: Boolean = false,
    val progress: Int = 0 // Progress dalam persentase
)

/**
 * Data class untuk setiap jilid/volume latihan
 */
data class LatihanJilid(
    val id: Int,
    val title: String,
    val description: String,
    val halamanList: List<LatihanHalaman>,
    val isCompleted: Boolean = false,
    val progress: Int = 0 // Progress dalam persentase
)

/**
 * Object untuk menyimpan data struktur latihan berdasarkan gambar
 */
object LatihanPageData {
    
    /**
     * Data untuk halaman pertama sesuai dengan gambar yang diberikan
     * Terdiri dari 3 baris dengan 6 huruf per baris
     */
    fun getHalaman1(): LatihanHalaman {
        return LatihanHalaman(
            id = 1,
            title = "Halaman 1",
            description = "Pengenalan Huruf Hijaiyah Dasar",
            barisList = listOf(
                // Baris 1
                LatihanBaris(
                    id = 1,
                    hurufList = listOf(
                        LatihanHuruf("ا", "ALIF", "alif", 1),
                        LatihanHuruf("ب", "BA", "ba", 2),
                        LatihanHuruf("ب", "BA", "ba", 3),
                        LatihanHuruf("ا", "ALIF", "alif", 4),
                        LatihanHuruf("ب", "BA", "ba", 5),
                        LatihanHuruf("ا", "ALIF", "alif", 6)
                    )
                ),
                // Baris 2
                LatihanBaris(
                    id = 2,
                    hurufList = listOf(
                        LatihanHuruf("ب", "BA", "ba", 7),
                        LatihanHuruf("ا", "ALIF", "alif", 8),
                        LatihanHuruf("ا", "ALIF", "alif", 9),
                        LatihanHuruf("ب", "BA", "ba", 10),
                        LatihanHuruf("ب", "BA", "ba", 11),
                        LatihanHuruf("ا", "ALIF", "alif", 12)
                    )
                ),
                // Baris 3
                LatihanBaris(
                    id = 3,
                    hurufList = listOf(
                        LatihanHuruf("ب", "BA", "ba", 13),
                        LatihanHuruf("ا", "ALIF", "alif", 14),
                        LatihanHuruf("ا", "ALIF", "alif", 15),
                        LatihanHuruf("ا", "ALIF", "alif", 16),
                        LatihanHuruf("ا", "ALIF", "alif", 17),
                        LatihanHuruf("ب", "BA", "ba", 18)
                    )
                )
            )
        )
    }

    /**
     * Data untuk halaman kedua sesuai dengan gambar yang diberikan
     * Baris 1 & 2: semua Jim, Baris 3: Jim, Ta, Ta, Ba, Jim, Alif
     */
    fun getHalaman2(): LatihanHalaman {
        return LatihanHalaman(
            id = 2,
            title = "Halaman 2",
            description = "Latihan Huruf Jim, Ta, dan Ba",
            barisList = listOf(
                // Baris 1 - Jim, Jim, Jim, Jim, Jim, Jim
                LatihanBaris(
                    id = 1,
                    hurufList = listOf(
                        LatihanHuruf("ج", "JIM", "jim", 19),
                        LatihanHuruf("ج", "JIM", "jim", 20),
                        LatihanHuruf("ج", "JIM", "jim", 21),
                        LatihanHuruf("ج", "JIM", "jim", 22),
                        LatihanHuruf("ج", "JIM", "jim", 23),
                        LatihanHuruf("ج", "JIM", "jim", 24)
                    )
                ),
                // Baris 2 - Jim, Jim, Jim, Jim, Jim, Jim
                LatihanBaris(
                    id = 2,
                    hurufList = listOf(
                        LatihanHuruf("ج", "JIM", "jim", 25),
                        LatihanHuruf("ج", "JIM", "jim", 26),
                        LatihanHuruf("ج", "JIM", "jim", 27),
                        LatihanHuruf("ج", "JIM", "jim", 28),
                        LatihanHuruf("ج", "JIM", "jim", 29),
                        LatihanHuruf("ج", "JIM", "jim", 30)
                    )
                ),
                // Baris 3 - Jim, Ta, Ta, Ba, Jim, Alif (sesuai gambar)
                LatihanBaris(
                    id = 3,
                    hurufList = listOf(
                        LatihanHuruf("ج", "JIM", "jim", 31),
                        LatihanHuruf("ت", "TA", "ta", 32),
                        LatihanHuruf("ت", "TA", "ta", 33),
                        LatihanHuruf("ب", "BA", "ba", 34),
                        LatihanHuruf("ج", "JIM", "jim", 35),
                        LatihanHuruf("ا", "ALIF", "alif", 36)
                    )
                )
            )
        )
    }

    /**
     * Data untuk halaman ketiga
     * Latihan huruf Ta dan Tsa
     */
    fun getHalaman3(): LatihanHalaman {
        return LatihanHalaman(
            id = 3,
            title = "Halaman 3",
            description = "Latihan Huruf Ta dan Tsa",
            barisList = listOf(
                // Baris 1
                LatihanBaris(
                    id = 1,
                    hurufList = listOf(
                        LatihanHuruf("ت", "TA", "ta", 37),
                        LatihanHuruf("ت", "TA", "ta", 38),
                        LatihanHuruf("ت", "TA", "ta", 39),
                        LatihanHuruf("ث", "TSA", "tsa", 40),
                        LatihanHuruf("ث", "TSA", "tsa", 41),
                        LatihanHuruf("ث", "TSA", "tsa", 42)
                    )
                ),
                // Baris 2
                LatihanBaris(
                    id = 2,
                    hurufList = listOf(
                        LatihanHuruf("ت", "TA", "ta", 43),
                        LatihanHuruf("ث", "TSA", "tsa", 44),
                        LatihanHuruf("ت", "TA", "ta", 45),
                        LatihanHuruf("ث", "TSA", "tsa", 46),
                        LatihanHuruf("ت", "TA", "ta", 47),
                        LatihanHuruf("ث", "TSA", "tsa", 48)
                    )
                ),
                // Baris 3
                LatihanBaris(
                    id = 3,
                    hurufList = listOf(
                        LatihanHuruf("ث", "TSA", "tsa", 49),
                        LatihanHuruf("ث", "TSA", "tsa", 50),
                        LatihanHuruf("ت", "TA", "ta", 51),
                        LatihanHuruf("ت", "TA", "ta", 52),
                        LatihanHuruf("ث", "TSA", "tsa", 53),
                        LatihanHuruf("ت", "TA", "ta", 54)
                    )
                )
            )
        )
    }

    /**
     * Data untuk halaman keempat
     * Latihan huruf Ha dan Kho
     */
    fun getHalaman4(): LatihanHalaman {
        return LatihanHalaman(
            id = 4,
            title = "Halaman 4",
            description = "Latihan Huruf Ha dan Kho",
            barisList = listOf(
                // Baris 1
                LatihanBaris(
                    id = 1,
                    hurufList = listOf(
                        LatihanHuruf("ح", "HA", "ha", 55),
                        LatihanHuruf("ح", "HA", "ha", 56),
                        LatihanHuruf("ح", "HA", "ha", 57),
                        LatihanHuruf("خ", "KHO", "kho", 58),
                        LatihanHuruf("خ", "KHO", "kho", 59),
                        LatihanHuruf("خ", "KHO", "kho", 60)
                    )
                ),
                // Baris 2
                LatihanBaris(
                    id = 2,
                    hurufList = listOf(
                        LatihanHuruf("ح", "HA", "ha", 61),
                        LatihanHuruf("خ", "KHO", "kho", 62),
                        LatihanHuruf("ح", "HA", "ha", 63),
                        LatihanHuruf("خ", "KHO", "kho", 64),
                        LatihanHuruf("ح", "HA", "ha", 65),
                        LatihanHuruf("خ", "KHO", "kho", 66)
                    )
                ),
                // Baris 3
                LatihanBaris(
                    id = 3,
                    hurufList = listOf(
                        LatihanHuruf("خ", "KHO", "kho", 67),
                        LatihanHuruf("خ", "KHO", "kho", 68),
                        LatihanHuruf("ح", "HA", "ha", 69),
                        LatihanHuruf("ح", "HA", "ha", 70),
                        LatihanHuruf("خ", "KHO", "kho", 71),
                        LatihanHuruf("ح", "HA", "ha", 72)
                    )
                )
            )
        )
    }

    /**
     * Data untuk halaman kelima
     * Latihan huruf Dal, Dzal, dan Ra
     */
    fun getHalaman5(): LatihanHalaman {
        return LatihanHalaman(
            id = 5,
            title = "Halaman 5",
            description = "Latihan Huruf Dal, Dzal, dan Ra",
            barisList = listOf(
                // Baris 1
                LatihanBaris(
                    id = 1,
                    hurufList = listOf(
                        LatihanHuruf("د", "DAL", "dal", 73),
                        LatihanHuruf("د", "DAL", "dal", 74),
                        LatihanHuruf("ذ", "DZAL", "dzal", 75),
                        LatihanHuruf("ذ", "DZAL", "dzal", 76),
                        LatihanHuruf("ر", "RA", "ra", 77),
                        LatihanHuruf("ر", "RA", "ra", 78)
                    )
                ),
                // Baris 2
                LatihanBaris(
                    id = 2,
                    hurufList = listOf(
                        LatihanHuruf("د", "DAL", "dal", 79),
                        LatihanHuruf("ذ", "DZAL", "dzal", 80),
                        LatihanHuruf("ر", "RA", "ra", 81),
                        LatihanHuruf("د", "DAL", "dal", 82),
                        LatihanHuruf("ذ", "DZAL", "dzal", 83),
                        LatihanHuruf("ر", "RA", "ra", 84)
                    )
                ),
                // Baris 3
                LatihanBaris(
                    id = 3,
                    hurufList = listOf(
                        LatihanHuruf("ر", "RA", "ra", 85),
                        LatihanHuruf("ر", "RA", "ra", 86),
                        LatihanHuruf("ذ", "DZAL", "dzal", 87),
                        LatihanHuruf("د", "DAL", "dal", 88),
                        LatihanHuruf("ر", "RA", "ra", 89),
                        LatihanHuruf("د", "DAL", "dal", 90)
                    )
                )
            )
        )
    }

    /**
     * Data untuk jilid pertama (bisa dikembangkan untuk menambah halaman)
     */
    fun getJilid1(): LatihanJilid {
        return LatihanJilid(
            id = 1,
            title = "Jilid 1 - Pengenalan Huruf",
            description = "Belajar pengenalan huruf Hijaiyah dasar",
            halamanList = listOf(
                getHalaman1(),
                getHalaman2(),
                getHalaman3(),
                getHalaman4(),
                getHalaman5()
            )
        )
    }

    /**
     * Mendapatkan semua jilid yang tersedia
     */
    fun getAllJilid(): List<LatihanJilid> {
        return listOf(
            getJilid1()
            // Jilid lain bisa ditambah di sini
        )
    }

    /**
     * Mendapatkan jilid berdasarkan ID
     */
    fun getJilidById(id: Int): LatihanJilid? {
        return getAllJilid().find { it.id == id }
    }

    /**
     * Mendapatkan halaman berdasarkan jilid ID dan halaman ID
     */
    fun getHalamanById(jilidId: Int, halamanId: Int): LatihanHalaman? {
        return getJilidById(jilidId)?.halamanList?.find { it.id == halamanId }
    }

    /**
     * Mendapatkan baris berdasarkan jilid ID, halaman ID, dan baris ID
     */
    fun getBarisById(jilidId: Int, halamanId: Int, barisId: Int): LatihanBaris? {
        return getHalamanById(jilidId, halamanId)?.barisList?.find { it.id == barisId }
    }

    /**
     * Mendapatkan huruf berdasarkan position
     */
    fun getHurufByPosition(jilidId: Int, halamanId: Int, position: Int): LatihanHuruf? {
        val halaman = getHalamanById(jilidId, halamanId) ?: return null
        for (baris in halaman.barisList) {
            val huruf = baris.hurufList.find { it.position == position }
            if (huruf != null) return huruf
        }
        return null
    }

    /**
     * Mendapatkan total huruf dalam satu halaman
     */
    fun getTotalHurufInHalaman(jilidId: Int, halamanId: Int): Int {
        val halaman = getHalamanById(jilidId, halamanId) ?: return 0
        return halaman.barisList.sumOf { it.hurufList.size }
    }

    /**
     * Mendapatkan huruf berikutnya dalam sequence
     */
    fun getNextHuruf(jilidId: Int, halamanId: Int, currentPosition: Int): LatihanHuruf? {
        val halaman = getHalamanById(jilidId, halamanId) ?: return null
        for (baris in halaman.barisList) {
            for (huruf in baris.hurufList) {
                if (huruf.position == currentPosition + 1) {
                    return huruf
                }
            }
        }
        return null
    }

    /**
     * Mendapatkan huruf sebelumnya dalam sequence
     */
    fun getPreviousHuruf(jilidId: Int, halamanId: Int, currentPosition: Int): LatihanHuruf? {
        val halaman = getHalamanById(jilidId, halamanId) ?: return null
        for (baris in halaman.barisList) {
            for (huruf in baris.hurufList) {
                if (huruf.position == currentPosition - 1) {
                    return huruf
                }
            }
        }
        return null
    }

    /**
     * Mengecek apakah satu baris sudah selesai
     */
    fun isBarisCompleted(jilidId: Int, halamanId: Int, barisId: Int, completedPositions: Set<Int>): Boolean {
        val baris = getBarisById(jilidId, halamanId, barisId) ?: return false
        return baris.hurufList.all { completedPositions.contains(it.position) }
    }

    /**
     * Mengecek apakah satu halaman sudah selesai
     */
    fun isHalamanCompleted(jilidId: Int, halamanId: Int, completedPositions: Set<Int>): Boolean {
        val halaman = getHalamanById(jilidId, halamanId) ?: return false
        return halaman.barisList.all { baris ->
            baris.hurufList.all { completedPositions.contains(it.position) }
        }
    }

    /**
     * Mendapatkan progress halaman dalam persentase
     */
    fun getHalamanProgress(jilidId: Int, halamanId: Int, completedPositions: Set<Int>): Int {
        val totalHuruf = getTotalHurufInHalaman(jilidId, halamanId)
        if (totalHuruf == 0) return 0
        
        val completedHuruf = completedPositions.size
        return (completedHuruf * 100) / totalHuruf
    }
}
