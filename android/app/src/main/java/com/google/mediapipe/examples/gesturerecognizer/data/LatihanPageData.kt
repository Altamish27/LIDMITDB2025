package com.google.mediapipe.examples.gesturerecognizer.data

import android.util.Log
import com.google.mediapipe.examples.gesturerecognizer.data.api.SignQuranApiService
import com.google.mediapipe.examples.gesturerecognizer.data.models.JilidApi
import com.google.mediapipe.examples.gesturerecognizer.data.models.PageDetailItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    val progress: Int = 0
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
    val progress: Int = 0
)

/**
 * Object untuk mengelola data latihan dari API
 * 100% API - NO HARDCODE
 */
object LatihanPageData {
    
    private const val TAG = "LatihanPageData"
    
    // Cache untuk data dari API
    private var cachedJilidList: List<LatihanJilid>? = null
    private val cachedHalamanMap = mutableMapOf<String, LatihanHalaman>()
    
    // Mapping dari latin name API ke gesture name untuk deteksi kamera
    private val latinToGestureMap = mapOf(
        "Alif" to "alif",
        "Ba" to "ba",
        "Ta" to "ta",
        "Tsa" to "tsa",
        "Jim" to "jim",
        "Ha" to "ha",
        "Kha" to "kha",
        "Dal" to "dal",
        "Dzal" to "dzal",
        "Ra" to "ra",
        "Za" to "za",
        "Sin" to "sin",
        "Syin" to "syin",
        "Shod" to "shad",
        "Dhod" to "dhad",
        "Tho" to "tha",
        "Zho" to "zha",
        "Ain" to "ain",
        "Ghoin" to "ghain",
        "Fa" to "fa",
        "Qof" to "qaf",
        "Kaf" to "kaf",
        "Lam" to "lam",
        "Mim" to "mim",
        "Nun" to "nun",
        "Wau" to "waw",
        "Ha'" to "ha'",
        "Ya" to "ya"
    )
    
    /**
     * Load data jilid dari API
     */
    suspend fun loadJilidFromApi(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Loading jilid list from API...")
                val apiService = SignQuranApiService.getInstance()
                val result = apiService.getJilidList()
                
                result.onSuccess { response ->
                    Log.d(TAG, "API Success: ${response.jilid.size} jilid loaded")
                    
                    // Map setiap jilid dan cek halaman yang tersedia
                    val jilidList = mutableListOf<LatihanJilid>()
                    for (apiJilid in response.jilid) {
                        val jilid = mapApiJilidToLatihanJilid(apiJilid)
                        
                        // Hanya tambahkan jilid yang punya minimal 1 halaman
                        if (jilid.halamanList.isNotEmpty()) {
                            jilidList.add(jilid)
                            Log.d(TAG, "Added ${jilid.title} with ${jilid.halamanList.size} pages")
                        } else {
                            Log.w(TAG, "Skipped ${jilid.title} - no pages available")
                        }
                    }
                    
                    cachedJilidList = jilidList
                    return@withContext jilidList.isNotEmpty()
                }
                
                result.onFailure { error ->
                    Log.e(TAG, "API Jilid Failed: ${error.message}", error)
                    cachedJilidList = emptyList()
                }
                
                return@withContext false
            } catch (e: Exception) {
                Log.e(TAG, "API Jilid Error: ${e.message}", e)
                cachedJilidList = emptyList()
                return@withContext false
            }
        }
    }
    
    /**
     * Load halaman detail dari API
     */
    suspend fun loadHalamanFromApi(jilidId: Int, nomorHalaman: Int): LatihanHalaman? {
        return withContext(Dispatchers.IO) {
            val cacheKey = "$jilidId-$nomorHalaman"
            
            // Cek cache dulu
            if (cachedHalamanMap.containsKey(cacheKey)) {
                Log.d(TAG, "Using cached page: $cacheKey")
                return@withContext cachedHalamanMap[cacheKey]
            }
            
            try {
                Log.d(TAG, "Loading page from API: jilid=$jilidId, halaman=$nomorHalaman")
                val apiService = SignQuranApiService.getInstance()
                val result = apiService.getPageDetail(jilidId, nomorHalaman)
                
                result.onSuccess { response ->
                    if (response.pageDetail.isNotEmpty()) {
                        Log.d(TAG, "API Success: ${response.pageDetail.size} items loaded")
                        val halaman = mapApiPageToLatihanHalaman(response.pageDetail, jilidId, nomorHalaman)
                        cachedHalamanMap[cacheKey] = halaman
                        return@withContext halaman
                    } else {
                        Log.w(TAG, "API returned empty pageDetail")
                    }
                }
                
                result.onFailure { error ->
                    Log.e(TAG, "API Halaman Failed: ${error.message}", error)
                }
                
                return@withContext null
            } catch (e: Exception) {
                Log.e(TAG, "API Halaman Error: ${e.message}", e)
                return@withContext null
            }
        }
    }
    
    /**
     * Mapping dari API page detail ke LatihanHalaman
     */
    private fun mapApiPageToLatihanHalaman(
        pageDetails: List<PageDetailItem>,
        jilidId: Int,
        nomorHalaman: Int
    ): LatihanHalaman {
        Log.d(TAG, "Mapping ${pageDetails.size} items to LatihanHalaman")
        
        // Group by baris
        val groupedByBaris = pageDetails.groupBy { it.baris }
        Log.d(TAG, "Grouped into ${groupedByBaris.size} baris")
        
        // Convert ke LatihanBaris
        val barisList = groupedByBaris.map { (barisId, items) ->
            val hurufList = items.sortedBy { it.urutan }.mapIndexed { index, item ->
                val gestureName = latinToGestureMap[item.latinName] ?: item.latinName.lowercase()
                
                // Position calculation: untuk setiap baris, position mulai dari (barisId-1)*6 + urutan
                val position = item.hijaiyahHalamanId
                
                Log.d(TAG, "Baris $barisId, Urutan ${item.urutan}: ${item.latinName} (${item.arabicChar}) -> gesture: $gestureName, pos: $position")
                
                LatihanHuruf(
                    arabic = item.arabicChar,
                    latin = item.latinName.uppercase(),
                    gestureName = gestureName,
                    position = position,
                    isCompleted = false,
                    isActive = false
                )
            }
            
            LatihanBaris(
                id = barisId,
                hurufList = hurufList,
                isCompleted = false
            )
        }.sortedBy { it.id }
        
        val jilidName = pageDetails.firstOrNull()?.jilidName ?: "Jilid $jilidId"
        
        return LatihanHalaman(
            id = nomorHalaman,
            title = "Halaman $nomorHalaman",
            description = "Latihan Halaman $nomorHalaman dari $jilidName",
            barisList = barisList,
            isCompleted = false,
            progress = 0
        )
    }
    
    /**
     * Mapping dari API jilid ke LatihanJilid
     * HANYA MENAMPILKAN HALAMAN YANG ADA DATANYA DI API
     */
    private suspend fun mapApiJilidToLatihanJilid(apiJilid: JilidApi): LatihanJilid {
        Log.d(TAG, "Checking available pages for jilid ${apiJilid.jilidId}...")
        
        val apiService = SignQuranApiService.getInstance()
        val availablePages = mutableListOf<LatihanHalaman>()
        
        // Cek maksimal 50 halaman untuk setiap jilid
        val maxPagesToCheck = 50
        var consecutiveEmptyCount = 0
        val maxConsecutiveEmpty = 3  // Stop jika 3 halaman berturut-turut kosong
        
        for (pageNum in 1..maxPagesToCheck) {
            try {
                val result = apiService.getPageDetail(apiJilid.jilidId, pageNum)
                
                var pageFound = false
                result.onSuccess { response ->
                    if (response.pageDetail.isNotEmpty()) {
                        // Halaman ini ada datanya, buat placeholder
                        Log.d(TAG, "✓ Jilid ${apiJilid.jilidId} - Halaman $pageNum: ${response.pageDetail.size} items found")
                        availablePages.add(
                            LatihanHalaman(
                                id = pageNum,
                                title = "Halaman $pageNum",
                                description = "Klik untuk memuat halaman",
                                barisList = emptyList(),
                                isCompleted = false,
                                progress = 0
                            )
                        )
                        consecutiveEmptyCount = 0
                        pageFound = true
                    }
                }
                
                if (!pageFound) {
                    consecutiveEmptyCount++
                    Log.d(TAG, "✗ Jilid ${apiJilid.jilidId} - Halaman $pageNum: Empty/Not found (${consecutiveEmptyCount}/${maxConsecutiveEmpty})")
                    
                    if (consecutiveEmptyCount >= maxConsecutiveEmpty) {
                        Log.d(TAG, "Stopping check - ${maxConsecutiveEmpty} consecutive empty pages")
                        return@mapApiJilidToLatihanJilid LatihanJilid(
                            id = apiJilid.jilidId,
                            title = apiJilid.jilidName,
                            description = apiJilid.description,
                            halamanList = availablePages,
                            isCompleted = false,
                            progress = 0
                        )
                    }
                }
            } catch (e: Exception) {
                consecutiveEmptyCount++
                Log.e(TAG, "Error checking page $pageNum: ${e.message}")
                
                if (consecutiveEmptyCount >= maxConsecutiveEmpty) {
                    Log.d(TAG, "Stopping check due to errors")
                    return@mapApiJilidToLatihanJilid LatihanJilid(
                        id = apiJilid.jilidId,
                        title = apiJilid.jilidName,
                        description = apiJilid.description,
                        halamanList = availablePages,
                        isCompleted = false,
                        progress = 0
                    )
                }
            }
        }
        
        Log.d(TAG, "Jilid ${apiJilid.jilidId}: Found ${availablePages.size} available pages")
        
        return LatihanJilid(
            id = apiJilid.jilidId,
            title = apiJilid.jilidName,
            description = apiJilid.description,
            halamanList = availablePages,
            isCompleted = false,
            progress = 0
        )
    }
    
    /**
     * Mendapatkan semua jilid
     */
    fun getAllJilid(): List<LatihanJilid> {
        return cachedJilidList ?: emptyList()
    }

    /**
     * Mendapatkan jilid berdasarkan ID
     */
    fun getJilidById(id: Int): LatihanJilid? {
        return getAllJilid().find { it.id == id }
    }

    /**
     * Mendapatkan halaman berdasarkan jilid ID dan halaman ID
     * Untuk placeholder, gunakan loadHalamanFromApi() untuk data sebenarnya
     */
    fun getHalamanById(jilidId: Int, halamanId: Int): LatihanHalaman? {
        // Cek cache dulu
        val cacheKey = "$jilidId-$halamanId"
        return cachedHalamanMap[cacheKey] ?: getJilidById(jilidId)?.halamanList?.find { it.id == halamanId }
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
    
    /**
     * Clear cache - untuk refresh data
     */
    fun clearCache() {
        cachedJilidList = null
        cachedHalamanMap.clear()
        Log.d(TAG, "Cache cleared")
    }
}
