package com.google.mediapipe.examples.gesturerecognizer.data

import android.util.Log
import com.google.mediapipe.examples.gesturerecognizer.data.api.SignQuranApiService
import com.google.mediapipe.examples.gesturerecognizer.data.models.JilidApi
import com.google.mediapipe.examples.gesturerecognizer.data.models.PageDetailEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.engine.android.*

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
        "Alif" to "01_alif",
        "Ba" to "02_ba",
        "Ta" to "03_ta",
        "Tsa" to "04_tsa",
        "Jim" to "05_jim",
        "Ha" to "06_ha",
        "Kha" to "07_kha",
        "Dal" to "08_dal",
        "Dzal" to "09_dzal",
        "Ra" to "10_ra",
        "Za" to "11_za",
        "Sin" to "12_sin",
        "Syin" to "13_syin",
        "Shod" to "14_shad",
        "Dhod" to "15_dhad",
        "Tho" to "16_tha",
        "Zho" to "17_zha",
        "Ain" to "18_ain",
        "Ghoin" to "19_ghain",
        "Fa" to "20_fa",
        "Qof" to "21_qaf",
        "Kaf" to "22_kaf",
        "Lam" to "23_lam",
        "Mim" to "24_mim",
        "Nun" to "25_nun",
        "Wau" to "26_waw",
        "Ha'" to "27_ha'",
        "Ya" to "28_ya"
    )
    
    /**
     * Load data jilid dari API
     */
    suspend fun loadJilidFromApi(context: android.content.Context? = null): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "========================================")
                Log.d(TAG, "Loading jilid list from API...")
                Log.d(TAG, "Context provided: ${context != null}")
                
                val apiService = SignQuranApiService.getInstance()
                
                // Get auth token if context is provided
                val token = if (context != null) {
                    val authManager = com.google.mediapipe.examples.gesturerecognizer.data.manager.AuthManager(context)
                    Log.d(TAG, "AuthManager isLoggedIn: ${authManager.isLoggedIn}")
                    Log.d(TAG, "AuthManager token: ${if (authManager.authToken.isNotEmpty()) "EXISTS (${authManager.authToken.take(20)}...)" else "EMPTY"}")
                    if (authManager.isLoggedIn) authManager.authToken else null
                } else {
                    Log.w(TAG, "No context provided, will call API without token")
                    null
                }
                
                Log.d(TAG, "Token to be used: ${if (token != null) "EXISTS" else "NULL"}")
                
                val result = apiService.getJilidList(token)
                
                result.onSuccess { response ->
                    Log.d(TAG, "API Success: ${response.jilid.size} jilid loaded")
                    
                    // Map setiap jilid - LANGSUNG TAMPILKAN, TIDAK PERLU CEK HALAMAN DULU
                    val jilidList = response.jilid.map { apiJilid ->
                        Log.d(TAG, "Mapping jilid: ${apiJilid.jilidName} (ID: ${apiJilid.jilidId})")
                        
                        // Buat placeholder - halaman akan di-load on-demand
                        LatihanJilid(
                            id = apiJilid.jilidId,
                            title = apiJilid.jilidName,
                            description = apiJilid.description,
                            halamanList = emptyList(), // Will be loaded on demand
                            isCompleted = false,
                            progress = 0
                        )
                    }
                    
                    cachedJilidList = jilidList
                    Log.d(TAG, "✓ Successfully loaded ${jilidList.size} jilid")
                    return@withContext true
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
    suspend fun loadHalamanFromApi(jilidId: Int, nomorHalaman: Int, context: android.content.Context? = null): LatihanHalaman? {
        return try {
            withContext(Dispatchers.IO) {
                val cacheKey = "$jilidId-$nomorHalaman"
                
                Log.d(TAG, "==== API CALL DEBUG ====")
                Log.d(TAG, "Entry point reached successfully")
                Log.d(TAG, "Request: jilid=$jilidId, halaman=$nomorHalaman")
                
                // Cek cache dulu
                if (cachedHalamanMap.containsKey(cacheKey)) {
                    Log.d(TAG, "Using cached page: $cacheKey")
                    return@withContext cachedHalamanMap[cacheKey]
                }
                
                try {
                    // Test connectivity first
                    Log.d(TAG, "Testing basic connectivity...")
                    val testUrl = "https://signquran.site/api/jilid"
                    val testClient = io.ktor.client.HttpClient(io.ktor.client.engine.android.Android)
                    try {
                        val testResponse = testClient.get(testUrl)
                        Log.d(TAG, "✓ Basic connectivity test passed: ${testResponse.status}")
                        testClient.close()
                    } catch (e: Exception) {
                        Log.e(TAG, "✗ Basic connectivity test failed: ${e.message}")
                        testClient.close()
                        // Continue anyway, maybe the specific endpoint works
                    }
                    
                    Log.d(TAG, "Creating API service instance...")
                    val apiService = SignQuranApiService.getInstance()
                    Log.d(TAG, "✓ API service created successfully")
                    
                    // Get auth token if context is provided
                    val token = if (context != null) {
                        Log.d(TAG, "Getting auth token from context...")
                        try {
                            val authManager = com.google.mediapipe.examples.gesturerecognizer.data.manager.AuthManager(context)
                            val isLoggedIn = authManager.isLoggedIn
                            val authToken = if (isLoggedIn) authManager.authToken else null
                            Log.d(TAG, "Auth Status: logged_in=$isLoggedIn, token_length=${authToken?.length ?: 0}")
                            authToken
                        } catch (e: Exception) {
                            Log.e(TAG, "Error getting auth token: ${e.message}")
                            null
                        }
                    } else {
                        Log.d(TAG, "No context provided - no auth token")
                        null
                    }
                    
                    Log.d(TAG, "About to call API with:")
                    Log.d(TAG, "  - URL: https://signquran.site/api/pages/detail")
                    Log.d(TAG, "  - jilid_id: $jilidId")
                    Log.d(TAG, "  - nomor_halaman: $nomorHalaman")
                    Log.d(TAG, "  - token: ${if (token != null) "YES (${token.length} chars)" else "NO"}")
                    
                    val result = apiService.getPageDetail(jilidId, nomorHalaman, token)
                    
                    result.onSuccess { response ->
                        Log.d(TAG, "✓ API SUCCESS!")
                        Log.d(TAG, "  - Response pageDetail size: ${response.pageDetail.size}")
                        if (response.pageDetail.isNotEmpty()) {
                            val firstItem = response.pageDetail[0]
                            Log.d(TAG, "  - First item sample: baris=${firstItem.baris}, urutan=${firstItem.urutan}, latin='${firstItem.latinName}', arab='${firstItem.arabicChar}'")
                            Log.d(TAG, "  - Baris groups: ${response.pageDetail.groupBy { it.baris }.keys}")
                            
                            Log.d(TAG, "Starting data mapping...")
                            val halaman = mapApiPageToLatihanHalaman(response.pageDetail, jilidId, nomorHalaman)
                            cachedHalamanMap[cacheKey] = halaman
                            Log.d(TAG, "✓ Mapping complete, baris count: ${halaman.barisList.size}")
                            return@withContext halaman
                        } else {
                            Log.w(TAG, "✗ API returned empty pageDetail array")
                        }
                    }
                    
                    result.onFailure { error ->
                        Log.e(TAG, "✗ API FAILED: ${error.message}")
                        Log.e(TAG, "  - Error type: ${error.javaClass.simpleName}")
                        Log.e(TAG, "  - URL would be: https://signquran.site/api/pages/detail?jilid_id=$jilidId&nomor_halaman=$nomorHalaman")
                        error.printStackTrace()
                    }
                    
                    Log.d(TAG, "========================")
                    return@withContext null
                } catch (e: Exception) {
                    Log.e(TAG, "✗ INNER EXCEPTION in loadHalamanFromApi: ${e.message}", e)
                    return@withContext null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "✗ OUTER EXCEPTION in loadHalamanFromApi: ${e.message}", e)
            return null
        }
    }
    
    /**
     * Mapping dari API page detail ke LatihanHalaman
     */
    private fun mapApiPageToLatihanHalaman(
        pageDetails: List<PageDetailEntry>,
        jilidId: Int,
        nomorHalaman: Int
    ): LatihanHalaman {
        Log.d(TAG, "==== MAPPING DEBUG ====")
        Log.d(TAG, "Input: ${pageDetails.size} items to map")
        
        // Group by baris
        val groupedByBaris = pageDetails.groupBy { it.baris }
        Log.d(TAG, "Grouped into ${groupedByBaris.size} baris: ${groupedByBaris.keys.sorted()}")
        
        // Convert ke LatihanBaris
        val barisList = groupedByBaris.map { (barisId, items) ->
            Log.d(TAG, "Processing baris $barisId with ${items.size} items")
            
            val sortedItems = items.sortedBy { it.urutan }
            Log.d(TAG, "  - Urutan sequence: ${sortedItems.map { it.urutan }}")
            
            val hurufList = sortedItems.mapIndexed { index, item ->
                val gestureName = latinToGestureMap[item.latinName] ?: item.latinName.lowercase()
                
                // Position calculation: for this context we'll use a combination of barisId and urutan
                val position = (barisId * 100) + item.urutan  // Simple position calculation
                
                Log.d(TAG, "    [$index] Urutan ${item.urutan}: latin='${item.latinName}' -> gesture='$gestureName', arab='${item.arabicChar}', pos=$position")
                
                LatihanHuruf(
                    arabic = item.arabicChar,
                    latin = item.latinName.uppercase(),
                    gestureName = gestureName,
                    position = position,
                    isCompleted = false,
                    isActive = false
                )
            }
            
            Log.d(TAG, "  - Created baris $barisId with ${hurufList.size} huruf")
            
            LatihanBaris(
                id = barisId,
                hurufList = hurufList,
                isCompleted = false
            )
        }.sortedBy { it.id }
        
        val jilidName = "Jilid $jilidId"
        
        val finalHalaman = LatihanHalaman(
            id = nomorHalaman,
            title = "Halaman $nomorHalaman",
            description = "Latihan Halaman $nomorHalaman dari $jilidName",
            barisList = barisList,
            isCompleted = false,
            progress = 0
        )
        
        Log.d(TAG, "✓ Final result: ${finalHalaman.barisList.size} baris total")
        Log.d(TAG, "======================")
        
        return finalHalaman
    }
    
    /**
     * Mapping dari API jilid ke LatihanJilid
     * HANYA MENAMPILKAN HALAMAN YANG ADA DATANYA DI API
     */
    private suspend fun mapApiJilidToLatihanJilid(apiJilid: JilidApi, context: android.content.Context? = null): LatihanJilid {
        Log.d(TAG, "Checking available pages for jilid ${apiJilid.jilidId}...")
        
        val apiService = SignQuranApiService.getInstance()
        
        // Get auth token if context is provided
        val token = if (context != null) {
            val authManager = com.google.mediapipe.examples.gesturerecognizer.data.manager.AuthManager(context)
            if (authManager.isLoggedIn) authManager.authToken else null
        } else null
        
        val availablePages = mutableListOf<LatihanHalaman>()
        
        // Cek maksimal 50 halaman untuk setiap jilid
        val maxPagesToCheck = 50
        var consecutiveEmptyCount = 0
        val maxConsecutiveEmpty = 3  // Stop jika 3 halaman berturut-turut kosong
        
        for (pageNum in 1..maxPagesToCheck) {
            try {
                val result = apiService.getPageDetail(apiJilid.jilidId, pageNum, token)
                
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
     * Load daftar halaman yang tersedia untuk sebuah jilid
     * Fungsi ini lebih ringan - hanya mengecek halaman yang ada tanpa load detail
     */
    suspend fun loadAvailablePagesForJilid(jilidId: Int, context: android.content.Context? = null): List<Int> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Checking available pages for jilid $jilidId...")
                val apiService = SignQuranApiService.getInstance()
                
                // Get auth token if context is provided (untuk progress nanti)
                val token = if (context != null) {
                    val authManager = com.google.mediapipe.examples.gesturerecognizer.data.manager.AuthManager(context)
                    if (authManager.isLoggedIn) authManager.authToken else null
                } else null
                
                val availablePages = mutableListOf<Int>()
                
                // Cek maksimal 20 halaman untuk setiap jilid (lebih cepat)
                val maxPagesToCheck = 20
                var consecutiveEmptyCount = 0
                val maxConsecutiveEmpty = 3
                
                for (pageNum in 1..maxPagesToCheck) {
                    try {
                        val result = apiService.getPageDetail(jilidId, pageNum, token)
                        
                        var pageFound = false
                        result.onSuccess { response ->
                            if (response.pageDetail.isNotEmpty()) {
                                availablePages.add(pageNum)
                                consecutiveEmptyCount = 0
                                pageFound = true
                                Log.d(TAG, "✓ Page $pageNum available")
                            }
                        }
                        
                        if (!pageFound) {
                            consecutiveEmptyCount++
                            if (consecutiveEmptyCount >= maxConsecutiveEmpty) {
                                Log.d(TAG, "Stopping check after $consecutiveEmptyCount consecutive empty pages")
                                break
                            }
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Error checking page $pageNum: ${e.message}")
                        consecutiveEmptyCount++
                        if (consecutiveEmptyCount >= maxConsecutiveEmpty) break
                    }
                }
                
                Log.d(TAG, "Found ${availablePages.size} available pages for jilid $jilidId")
                return@withContext availablePages
            } catch (e: Exception) {
                Log.e(TAG, "Error loading available pages: ${e.message}", e)
                return@withContext emptyList()
            }
        }
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
