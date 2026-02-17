package com.example.goalpulse.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class CacheManager(private val context: Context) {
    
    private val gson = Gson()
    private val cacheDir = File(context.cacheDir, "api_cache")
    private val prefs: SharedPreferences = context.getSharedPreferences("cache_prefs", Context.MODE_PRIVATE)
    
    init {
        cacheDir.mkdirs()
    }
    
    suspend fun <T> get(key: String, type: Class<T>): T? = withContext(Dispatchers.IO) {
        try {
            val cacheFile = File(cacheDir, "$key.json")
            if (!cacheFile.exists()) return@withContext null
            
            val timestamp = prefs.getLong("${key}_timestamp", 0)
            val now = System.currentTimeMillis()
            val cacheAge = now - timestamp
            
            val cacheDuration = getCacheDuration(key)
            if (cacheAge > cacheDuration) {
                cacheFile.delete()
                prefs.edit().remove("${key}_timestamp").apply()
                return@withContext null
            }
            
            val json = cacheFile.readText()
            gson.fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun <T> put(key: String, data: T) = withContext(Dispatchers.IO) {
        try {
            val cacheFile = File(cacheDir, "$key.json")
            val json = gson.toJson(data)
            cacheFile.writeText(json)
            prefs.edit().putLong("${key}_timestamp", System.currentTimeMillis()).apply()
        } catch (e: Exception) {
        }
    }
    
    suspend fun clear() = withContext(Dispatchers.IO) {
        try {
            cacheDir.listFiles()?.forEach { it.delete() }
            prefs.edit().clear().apply()
        } catch (e: Exception) {
        }
    }
    
    private fun getCacheDuration(key: String): Long {
        return when {
            key.startsWith("leagues") -> 24 * 60 * 60 * 1000L
            key.startsWith("teams") -> 24 * 60 * 60 * 1000L
            key.startsWith("fixtures") -> 60 * 60 * 1000L
            else -> 24 * 60 * 60 * 1000L
        }
    }
    
    fun generateKey(prefix: String, vararg params: Any?): String {
        return "$prefix:${params.joinToString(":")}"
    }
}

