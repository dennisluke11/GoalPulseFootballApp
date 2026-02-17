package com.example.goalpulse.data.repository

import com.example.goalpulse.data.local.CacheManager
import com.example.goalpulse.data.model.Fixture
import com.example.goalpulse.data.remote.ApiFootballService
import com.example.goalpulse.data.remote.ErrorHandler

interface FixturesRepository {
    suspend fun getFixtures(leagueId: Int? = null, season: Int? = null, teamId: Int? = null, date: String? = null): Result<List<Fixture>>
}

class FixturesRepositoryImpl(
    private val apiService: ApiFootballService,
    private val apiKey: String,
    private val cacheManager: CacheManager
) : FixturesRepository {
    
    override suspend fun getFixtures(leagueId: Int?, season: Int?, teamId: Int?, date: String?): Result<List<Fixture>> {
        ApiKeyValidator.validate(apiKey).onFailure { return Result.failure(it) }
        
        val currentSeason = season ?: if (leagueId != null) SeasonCalculator.getCurrentSeason() else null
        val cacheKey = cacheManager.generateKey("fixtures", leagueId, currentSeason, teamId, date)
        val cached = cacheManager.get(cacheKey, Array<Fixture>::class.java)
        if (cached != null) {
            return Result.success(cached.toList())
        }
        
        return try {
            val response = apiService.getFixtures(
                apiKey = apiKey,
                leagueId = leagueId,
                season = currentSeason,
                teamId = teamId,
                date = date
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
}

