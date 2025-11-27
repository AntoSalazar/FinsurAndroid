package com.example.finsur.core.di

import android.content.Context
import com.example.finsur.core.auth.AuthStateManager
import com.example.finsur.core.auth.GoogleAuthManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideGoogleAuthManager(
        @ApplicationContext context: Context
    ): GoogleAuthManager {
        return GoogleAuthManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthStateManager(
        @ApplicationContext context: Context
    ): AuthStateManager {
        return AuthStateManager(context)
    }
}
