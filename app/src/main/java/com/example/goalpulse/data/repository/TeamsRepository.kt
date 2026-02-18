package com.example.goalpulse.data.repository

import com.example.goalpulse.data.local.CacheManager
import com.example.goalpulse.data.model.Team
import com.example.goalpulse.data.remote.ApiFootballService
import com.example.goalpulse.data.remote.ErrorHandler
import retrofit2.HttpException

interface TeamsRepository {
    suspend fun searchTeams(query: String): Result<List<Team>>
    suspend fun getTeamsByLeague(leagueId: Int, season: Int? = null): Result<List<Team>>
}

class TeamsRepositoryImpl(
    private val apiService: ApiFootballService,
    private val apiKey: String,
    private val cacheManager: CacheManager
) : TeamsRepository {
    
    override suspend fun searchTeams(query: String): Result<List<Team>> {
        ApiKeyValidator.validate(apiKey).onFailure { return Result.failure(it) }
        
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
        } catch (e: HttpException) {
            Result.failure(Exception(ErrorHandler.getErrorMessage(e)))
        } catch (e: Exception) {
            Result.failure(Exception(ErrorHandler.getErrorMessage(e)))
        }
    }
    
    override suspend fun getTeamsByLeague(leagueId: Int, season: Int?): Result<List<Team>> {
        ApiKeyValidator.validate(apiKey).onFailure { return Result.failure(it) }
        val currentSeason = season ?: SeasonCalculator.getCurrentSeason()
        
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
        } catch (e: HttpException) {
            Result.failure(Exception(ErrorHandler.getErrorMessage(e)))
        } catch (e: Exception) {
            Result.failure(Exception(ErrorHandler.getErrorMessage(e)))
        }
    }
}



