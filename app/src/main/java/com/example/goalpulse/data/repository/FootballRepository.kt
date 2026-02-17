package com.example.goalpulse.data.repository

import com.example.goalpulse.data.local.CacheManager
import com.example.goalpulse.data.model.Fixture
import com.example.goalpulse.data.model.League
import com.example.goalpulse.data.model.Team
import com.example.goalpulse.data.remote.ApiFootballService
import com.example.goalpulse.data.remote.ErrorHandler
import retrofit2.HttpException

interface FootballRepository {
    suspend fun searchLeagues(query: String): Result<List<League>>
    suspend fun getAllLeagues(): Result<List<League>>
    suspend fun searchTeams(query: String): Result<List<Team>>
    suspend fun getTeamsByLeague(leagueId: Int, season: Int? = null): Result<List<Team>>
    suspend fun getFixtures(leagueId: Int? = null, season: Int? = null, teamId: Int? = null, date: String? = null): Result<List<Fixture>>
    
    companion object {
        fun getCurrentSeason(): Int {
            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)
            val calculatedSeason = if (currentMonth >= 6) currentYear else currentYear - 1
            
            return when {
                calculatedSeason >= 2022 && calculatedSeason <= 2024 -> calculatedSeason
                calculatedSeason > 2024 -> 2024
                else -> 2024
            }
        }
    }
}

class FootballRepositoryImpl(
    private val apiService: ApiFootballService,
    private val apiKey: String,
    private val cacheManager: CacheManager
) : FootballRepository {
    
    private fun validateApiKey(): Result<Unit> {
        return if (apiKey == "YOUR_API_KEY_HERE" || apiKey.isBlank()) {
            Result.failure(Exception("Please configure your API key in AppModule.kt. Get your API key from https://rapidapi.com/api-sports/api/api-football"))
        } else {
            Result.success(Unit)
        }
    }
    
    override suspend fun searchLeagues(query: String): Result<List<League>> {
        validateApiKey().onFailure { return Result.failure(it) }
        
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
        validateApiKey().onFailure { return Result.failure(it) }
        
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
    
    override suspend fun searchTeams(query: String): Result<List<Team>> {
        validateApiKey().onFailure { return Result.failure(it) }
        
        val cacheKey = cacheManager.generateKey("teams", "search", query)
        val cached = cacheManager.get(cacheKey, Array<Team>::class.java)
        if (cached != null) {
            return Result.success(cached.toList())
        }
        
        return try {
            val response = apiService.searchTeams(
                apiKey = apiKey,
                name = query
            )
            if (response.errors.isNullOrEmpty() && response.response != null) {
                cacheManager.put(cacheKey, response.response.toTypedArray())
                Result.success(response.response)
            } else {
                val errorMessage = response.errors?.joinToString() ?: "Unknown error"
                Result.failure(Exception(ErrorHandler.getErrorMessage(Exception(errorMessage))))
            }
        } catch (e: com.google.gson.JsonSyntaxException) {
            val errorMsg = e.message ?: "Unknown JSON parsing error"
            Result.failure(Exception("API returned an unexpected response format. This usually means your API subscription doesn't include access to this endpoint. Please check your subscription at https://dashboard.api-football.com. Error: $errorMsg"))
        } catch (e: retrofit2.HttpException) {
            Result.failure(Exception(ErrorHandler.getErrorMessage(e)))
        } catch (e: Exception) {
            Result.failure(Exception(ErrorHandler.getErrorMessage(e)))
        }
    }
    
    override suspend fun getTeamsByLeague(leagueId: Int, season: Int?): Result<List<Team>> {
        validateApiKey().onFailure { return Result.failure(it) }
        val currentSeason = season ?: FootballRepository.getCurrentSeason()
        
        val cacheKey = cacheManager.generateKey("teams", "league", leagueId, currentSeason)
        val cached = cacheManager.get(cacheKey, Array<Team>::class.java)
        if (cached != null) {
            return Result.success(cached.toList())
        }
        
        return try {
            val response = apiService.getTeamsByLeague(
                apiKey = apiKey,
                leagueId = leagueId,
                season = currentSeason
            )
            
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                val errorMessage = ErrorHandler.extractApiMessage(errorBody) 
                    ?: "Request failed with code ${response.code()}"
                return Result.failure(Exception(
                    when (response.code()) {
                        401 -> "Authentication failed. Your API key may be invalid. Please verify it in AppModule.kt"
                        403 -> errorMessage.ifEmpty { "Access forbidden. Your API key may be invalid, expired, or doesn't have the required subscription. Please verify your API key at https://dashboard.api-football.com" }
                        404 -> errorMessage.ifEmpty { "Resource not found. Please try again." }
                        429 -> errorMessage.ifEmpty { "Too many requests. Please wait a moment and try again." }
                        500, 502, 503 -> errorMessage.ifEmpty { "Server error. Please try again later." }
                        else -> errorMessage.ifEmpty { "Network error occurred (HTTP ${response.code()}). Please check your connection and try again." }
                    }
                ))
            }
            
            val teamResponse = response.body()
            if (teamResponse == null) {
                return Result.failure(Exception("Empty response from API"))
            }
            
            if (teamResponse.errors.isNullOrEmpty() && teamResponse.response != null) {
                cacheManager.put(cacheKey, teamResponse.response.toTypedArray())
                Result.success(teamResponse.response)
            } else {
                val errorMessage = teamResponse.errors?.joinToString() 
                    ?: "No teams found for this league and season"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: com.google.gson.JsonSyntaxException) {
            val errorMsg = e.message ?: "Unknown JSON parsing error"
            Result.failure(Exception("API returned an unexpected response format. Please check Logcat for the actual API response. This usually means: 1) Your API subscription doesn't include this endpoint, 2) The season is not available, or 3) The API response structure changed. Check your subscription at https://dashboard.api-football.com. Error details: $errorMsg"))
        } catch (e: retrofit2.HttpException) {
            Result.failure(Exception(ErrorHandler.getErrorMessage(e)))
        } catch (e: Exception) {
            Result.failure(Exception(ErrorHandler.getErrorMessage(e)))
        }
    }
    
    override suspend fun getFixtures(leagueId: Int?, season: Int?, teamId: Int?, date: String?): Result<List<Fixture>> {
        validateApiKey().onFailure { return Result.failure(it) }
        
        val currentSeason = season ?: if (leagueId != null) FootballRepository.getCurrentSeason() else null
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

