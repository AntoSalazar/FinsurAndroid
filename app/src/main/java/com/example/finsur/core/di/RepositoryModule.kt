package com.example.finsur.core.di

import com.example.finsur.data.auth.repository.AuthRepositoryImpl
import com.example.finsur.data.products.repository.ProductsRepositoryImpl
import com.example.finsur.data.categories.repository.CategoriesRepositoryImpl
import com.example.finsur.data.brands.repository.BrandsRepositoryImpl
import com.example.finsur.data.profile.repository.ProfileRepositoryImpl
import com.example.finsur.data.cart.repository.CartRepositoryImpl
import com.example.finsur.domain.auth.repository.AuthRepository
import com.example.finsur.domain.products.repository.ProductsRepository
import com.example.finsur.domain.categories.repository.CategoriesRepository
import com.example.finsur.domain.brands.repository.BrandsRepository
import com.example.finsur.domain.profile.repository.ProfileRepository
import com.example.finsur.domain.cart.repository.CartRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProductsRepository(
        productsRepositoryImpl: ProductsRepositoryImpl
    ): ProductsRepository

    @Binds
    @Singleton
    abstract fun bindCategoriesRepository(
        categoriesRepositoryImpl: CategoriesRepositoryImpl
    ): CategoriesRepository

    @Binds
    @Singleton
    abstract fun bindBrandsRepository(
        brandsRepositoryImpl: BrandsRepositoryImpl
    ): BrandsRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        cartRepositoryImpl: CartRepositoryImpl
    ): CartRepository
}
