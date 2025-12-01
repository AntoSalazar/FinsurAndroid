package com.example.finsur.di

import com.example.finsur.data.orders.remote.OrdersApiService
import com.example.finsur.data.orders.repository.OrdersRepositoryImpl
import com.example.finsur.domain.orders.repository.OrdersRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OrdersModule {

    @Provides
    @Singleton
    fun provideOrdersApiService(retrofit: Retrofit): OrdersApiService {
        return retrofit.create(OrdersApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOrdersRepository(
        apiService: OrdersApiService
    ): OrdersRepository {
        return OrdersRepositoryImpl(apiService)
    }
}
