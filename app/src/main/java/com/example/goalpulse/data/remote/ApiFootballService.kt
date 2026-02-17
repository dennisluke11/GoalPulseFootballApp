package com.example.goalpulse.data.remote

import com.example.goalpulse.data.model.FixtureResponse
import com.example.goalpulse.data.model.LeagueResponse
import com.example.goalpulse.data.model.TeamResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiFootballService {
    
    @GET("leagues")
    suspend fun getLeagues(
        @Header("x-apisports-key") apiKey: String,
        @Query("search") search: String? = null,
        @Query("country") country: String? = null
    ): LeagueResponse
    
    @GET("teams")
    suspend fun searchTeams(
        @Header("x-apisports-key") apiKey: String,
        @Query("name") name: String
    ): TeamResponse
    
    @GET("teams")
    suspend fun getTeamsByLeague(
        @Header("x-apisports-key") apiKey: String,
        @Query("league") leagueId: Int,
        @Query("season") season: Int
    ): Response<TeamResponse>
    
    @GET("fixtures")
    suspend fun getFixtures(
        @Header("x-apisports-key") apiKey: String,
        @Query("league") leagueId: Int? = null,
        @Query("season") season: Int? = null,
        @Query("team") teamId: Int? = null,
        @Query("date") date: String? = null
    ): FixtureResponse
}

