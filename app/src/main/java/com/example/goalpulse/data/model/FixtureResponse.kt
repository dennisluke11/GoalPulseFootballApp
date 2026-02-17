package com.example.goalpulse.data.model

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

data class FixtureResponse(
    @SerializedName("get") val endpoint: String?,
    @SerializedName("parameters") val parameters: Map<String, String>?,
    @JsonAdapter(ErrorsDeserializer::class)
    @SerializedName("errors") val errors: List<String>?,
    @SerializedName("results") val results: Int?,
    @SerializedName("paging") val paging: Paging?,
    @SerializedName("response") val response: List<Fixture>?
)

data class Fixture(
    @SerializedName("fixture") val fixture: FixtureInfo?,
    @SerializedName("league") val league: LeagueInfo?,
    @SerializedName("teams") val teams: Teams?,
    @SerializedName("goals") val goals: Goals?,
    @SerializedName("score") val score: Score?
)

data class FixtureInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("referee") val referee: String?,
    @SerializedName("timezone") val timezone: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("timestamp") val timestamp: Long?,
    @SerializedName("venue") val venue: Venue?,
    @SerializedName("status") val status: Status?
)

data class Status(
    @SerializedName("long") val long: String?,
    @SerializedName("short") val short: String?,
    @SerializedName("elapsed") val elapsed: Int?
)

data class Teams(
    @SerializedName("home") val home: TeamInfo?,
    @SerializedName("away") val away: TeamInfo?
)

data class Goals(
    @SerializedName("home") val home: Int?,
    @SerializedName("away") val away: Int?
)

data class Score(
    @SerializedName("fulltime") val fulltime: ScoreDetail?,
    @SerializedName("halftime") val halftime: ScoreDetail?
)

data class ScoreDetail(
    @SerializedName("home") val home: Int?,
    @SerializedName("away") val away: Int?
)

