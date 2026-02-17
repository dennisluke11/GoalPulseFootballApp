package com.example.goalpulse

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class GoalPulseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@GoalPulseApplication)
            modules(com.example.goalpulse.di.appModule)
        }
    }
}

