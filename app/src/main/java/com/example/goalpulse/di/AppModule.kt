package com.example.goalpulse.di

import com.example.goalpulse.config.ApiConfig
import com.example.goalpulse.data.local.CacheManager
import com.example.goalpulse.data.remote.ApiFootballService
import com.example.goalpulse.data.remote.RetrofitClient
import com.example.goalpulse.data.repository.FixturesRepository
import com.example.goalpulse.data.repository.FixturesRepositoryImpl
import com.example.goalpulse.data.repository.LeaguesRepository
import com.example.goalpulse.data.repository.LeaguesRepositoryImpl
import com.example.goalpulse.data.repository.TeamsRepository
import com.example.goalpulse.data.repository.TeamsRepositoryImpl
import com.example.goalpulse.ui.viewmodel.FixturesViewModel
import com.example.goalpulse.ui.viewmodel.LeaguesViewModel
import com.example.goalpulse.ui.viewmodel.TeamsViewModel
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
    
    single<LeaguesRepository> {
        LeaguesRepositoryImpl(
            apiService = get(),
            apiKey = get(),
            cacheManager = get()
        )
    }
    
    single<TeamsRepository> {
        TeamsRepositoryImpl(
            apiService = get(),
            apiKey = get(),
            cacheManager = get()
        )
    }
    
    single<FixturesRepository> {
        FixturesRepositoryImpl(
            apiService = get(),
            apiKey = get(),
            cacheManager = get()
        )
    }
    
    viewModel {
        LeaguesViewModel(
            repository = get()
        )
    }
    
    viewModel {
        TeamsViewModel(
            repository = get()
        )
    }
    
    viewModel {
        FixturesViewModel(
            repository = get()
        )
    }
}

