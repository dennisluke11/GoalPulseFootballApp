package com.example.goalpulse.data.remote

import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException

object ErrorHandler {
    fun getErrorMessage(throwable: Throwable): String {
        return when (throwable) {
            is HttpException -> {
                val errorBody = try {
                    throwable.response()?.errorBody()?.string()
                } catch (e: Exception) {
                    null
                }
                
                val apiMessage = extractApiMessage(errorBody)
                
                when (throwable.code()) {
                    401 -> apiMessage ?: "Authentication failed. Your API key may be invalid. Please verify it in AppModule.kt"
                    403 -> {
                        when {
                            apiMessage != null -> apiMessage
                            errorBody?.contains("not subscribed", ignoreCase = true) == true -> {
                                "You are not subscribed to this API. Please check your subscription at https://dashboard.api-football.com"
                            }
                            else -> "Access forbidden. Your API key may be invalid, expired, or doesn't have the required subscription. Please verify your API key at https://dashboard.api-football.com"
                        }
                    }
                    404 -> apiMessage ?: "Resource not found. Please try again."
                    429 -> apiMessage ?: "Too many requests. Please wait a moment and try again."
                    500, 502, 503 -> apiMessage ?: "Server error. Please try again later."
                    else -> apiMessage ?: "Network error occurred (HTTP ${throwable.code()}). Please check your connection and try again."
                }
            }
            is UnknownHostException -> {
                "No internet connection. Please check your network settings."
            }
            is IOException -> {
                "Network error. Please check your internet connection."
            }
            else -> {
                val message = throwable.message ?: "An unexpected error occurred. Please try again."
                if (message.contains("403") || message.contains("Forbidden")) {
                    "Access forbidden. Please verify your API key is valid and has an active subscription at RapidAPI."
                } else {
                    message
                }
            }
        }
    }
    
    fun extractApiMessage(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null
        
        return try {
            if (errorBody.contains("\"message\"", ignoreCase = true)) {
                val messageStart = errorBody.indexOf("\"message\"") + 10
                val messageEnd = errorBody.indexOf("\"", messageStart + 1)
                if (messageEnd > messageStart) {
                    val message = errorBody.substring(messageStart, messageEnd)
                    if (message.contains("not subscribed", ignoreCase = true)) {
                        "You are not subscribed to this API. Please subscribe to API-Football at https://rapidapi.com/api-sports/api/api-football"
                    } else {
                        message
                    }
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}

