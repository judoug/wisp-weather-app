package com.example.wisp.di

import com.example.wisp.performance.QuestPerformanceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Quest-specific DI module for app-level dependencies.
 * 
 * Provides:
 * - Quest performance manager
 * - Quest-specific configurations
 * - VR/AR optimizations
 */
@Module
@InstallIn(SingletonComponent::class)
object QuestAppModule {
    
    /**
     * Provides Quest performance manager instance.
     */
    @Provides
    @Singleton
    fun provideQuestPerformanceManager(): QuestPerformanceManager {
        return QuestPerformanceManager()
    }
}
