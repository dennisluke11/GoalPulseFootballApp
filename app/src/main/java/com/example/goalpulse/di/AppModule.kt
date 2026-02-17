package com.example.goalpulse.di

import com.example.goalpulse.config.ApiConfig
import com.example.goalpulse.data.local.CacheManager
import com.example.goalpulse.data.remote.ApiFootballService
import com.example.goalpulse.data.remote.RetrofitClient
import com.example.goalpulse.data.repository.FootballRepository
import com.example.goalpulse.data.repository.FootballRepositoryImpl
import com.example.goalpulse.ui.viewmodel.FootballViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    
    single<String> { 
        ApiConfig.API_KEY
    }
    
    single<CacheManager> {
        CacheManager(androidContext())
    }
    
    single<ApiFootballService> {
        RetrofitClient.create(apiKey = get())
    }
    
    single<FootballRepository> {
        FootballRepositoryImpl(
            apiService = get(),
            apiKey = get(),
            cacheManager = get()
        )
    }
    
    viewModel {
        FootballViewModel(
            repository = get()
        )
    }
}

