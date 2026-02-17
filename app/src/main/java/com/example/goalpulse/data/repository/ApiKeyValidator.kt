package com.example.goalpulse.data.repository

object ApiKeyValidator {
    fun validate(apiKey: String): Result<Unit> {
        return if (apiKey == "YOUR_API_KEY_HERE" || apiKey.isBlank()) {
            Result.failure(Exception("Please configure your API key in AppModule.kt. Get your API key from https://dashboard.api-football.com"))
        } else {
            Result.success(Unit)
        }
    }
}

