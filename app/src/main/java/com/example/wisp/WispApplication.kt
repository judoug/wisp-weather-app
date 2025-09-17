package com.example.wisp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WispApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Basic app initialization
        // Production features (analytics, performance monitoring, etc.) 
        // will be enabled when Firebase is configured
    }
}

