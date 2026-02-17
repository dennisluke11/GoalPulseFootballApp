package com.example.goalpulse.data.repository

import com.example.goalpulse.data.local.CacheManager
import com.example.goalpulse.data.model.League
import com.example.goalpulse.data.remote.ApiFootballService
import com.example.goalpulse.data.remote.ErrorHandler

interface LeaguesRepository {
    suspend fun searchLeagues(query: String): Result<List<League>>
    suspend fun getAllLeagues(): Result<List<League>>
}

class LeaguesRepositoryImpl(
    private val apiService: ApiFootballService,
    private val apiKey: String,
    private val cacheManager: CacheManager
) : LeaguesRepository {
    
    override suspend fun searchLeagues(query: String): Result<List<League>> {
        ApiKeyValidator.validate(apiKey).onFailure { return Result.failure(it) }
        
        val cacheKey = cacheManager.generateKey("leagues", "search", query)
        val cached = cacheManager.get(cacheKey, Array<League>::class.java)
        if (cached != null) {
            return Result.success(cached.toList())
        }
        
        return try {
            val response = apiService.getLeagues(
                apiKey = apiKey,
                search = query
            )
            if (response.errors.isNullOrEmpty() && response.response != null) {
                cacheManager.put(cacheKey, response.response.toTypedArray())
                Result.success(response.response)
            } else {
                val errorMessage = response.errors?.joinToString() ?: "Unknown error"
                Result.failure(Exception(ErrorHandler.getErrorMessage(Exception(errorMessage))))
            }
        } catch (e: Exception) {
            Result.failure(Exception(ErrorHandler.getErrorMessage(e)))
        }
    }
    
    override suspend fun getAllLeagues(): Result<List<League>> {
        ApiKeyValidator.validate(apiKey).onFailure { return Result.failure(it) }
        
        val cacheKey = cacheManager.generateKey("leagues", "all")
        val cached = cacheManager.get(cacheKey, Array<League>::class.java)
        if (cached != null) {
            return Result.success(cached.toList())
        }
        
        return try {
            val response = apiService.getLeagues(apiKey = apiKey)
            if (response.errors.isNullOrEmpty() && response.response != null) {
                cacheManager.put(cacheKey, response.response.toTypedArray())
                Result.success(response.response)
            } else {
                val errorMessage = response.errors?.joinToString() ?: "Unknown error"
                Result.failure(Exception(ErrorHandler.getErrorMessage(Exception(errorMessage))))
            }
        } catch (e: Exception) {
            Result.failure(Exception(ErrorHandler.getErrorMessage(e)))
        }
    }
}

