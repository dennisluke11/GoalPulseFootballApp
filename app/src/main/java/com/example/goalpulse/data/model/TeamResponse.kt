package com.example.goalpulse.data.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

data class TeamResponse(
    @SerializedName("get") val endpoint: String?,
    @SerializedName("parameters") val parameters: Map<String, String>?,
    @JsonAdapter(ErrorsDeserializer::class)
    @SerializedName("errors") val errors: List<String>?,
    @SerializedName("results") val results: Int?,
    @SerializedName("paging") val paging: Paging?,
    @SerializedName("response") val response: List<Team>?
)

class ErrorsDeserializer : JsonDeserializer<List<String>?> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): List<String>? {
        if (json == null || json.isJsonNull) {
            return null
        }
        
        return try {
            when {
                json.isJsonArray -> {
                    val list = mutableListOf<String>()
                    json.asJsonArray.forEach { element ->
                        if (element.isJsonPrimitive) {
                            list.add(element.asString)
                        }
                    }
                    if (list.isEmpty()) null else list
                }
                json.isJsonObject -> {
                    val obj = json.asJsonObject
                    if (obj.size() == 0) {
                        null
                    } else {
                        val messages = mutableListOf<String>()
                        obj.entrySet().forEach { (key, value) ->
                            if (value.isJsonPrimitive) {
                                messages.add("$key: ${value.asString}")
                            } else {
                                messages.add("$key: ${value.toString()}")
                            }
                        }
                        if (messages.isEmpty()) null else messages
                    }
                }
                json.isJsonPrimitive && json.asJsonPrimitive.isString -> {
                    listOf(json.asString)
                }
                else -> {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}

data class Team(
    @SerializedName("team") val team: TeamInfo?,
    @SerializedName("venue") val venue: Venue?
)

data class TeamInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("code") val code: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("founded") val founded: Int?,
    @SerializedName("national") val national: Boolean?,
    @SerializedName("logo") val logo: String?
)

data class Venue(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("capacity") val capacity: Int?,
    @SerializedName("surface") val surface: String?,
    @SerializedName("image") val image: String?
)

