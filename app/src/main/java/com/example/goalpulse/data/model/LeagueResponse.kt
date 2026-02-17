package com.example.goalpulse.data.model

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

data class LeagueResponse(
    @SerializedName("get") val endpoint: String?,
    @SerializedName("parameters") val parameters: Map<String, String>?,
    @JsonAdapter(ErrorsDeserializer::class)
    @SerializedName("errors") val errors: List<String>?,
    @SerializedName("results") val results: Int?,
    @SerializedName("paging") val paging: Paging?,
    @SerializedName("response") val response: List<League>?
)

data class Paging(
    @SerializedName("current") val current: Int?,
    @SerializedName("total") val total: Int?
)

data class League(
    @SerializedName("league") val league: LeagueInfo?,
    @SerializedName("country") val country: Country?
)

data class LeagueInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String?,
    @SerializedName("logo") val logo: String?
)

data class Country(
    @SerializedName("name") val name: String?,
    @SerializedName("code") val code: String?,
    @SerializedName("flag") val flag: String?
)

