package com.example.finsur.data.categories.remote

import com.example.finsur.data.categories.models.CategoryDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CategoriesApiService {
    @GET("categories/level/{level}")
    suspend fun getCategoriesByLevel(
        @Path("level") level: Int
    ): Response<List<CategoryDto>>

    @GET("categories")
    suspend fun getAllCategories(): Response<List<CategoryDto>>
}
