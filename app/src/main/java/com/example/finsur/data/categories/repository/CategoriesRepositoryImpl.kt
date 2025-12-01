package com.example.finsur.data.categories.repository

import com.example.finsur.data.categories.models.CategoryDto
import com.example.finsur.data.categories.remote.CategoriesApiService
import com.example.finsur.domain.categories.models.Category
import com.example.finsur.domain.categories.repository.CategoriesRepository
import com.example.finsur.domain.common.Result
import javax.inject.Inject

class CategoriesRepositoryImpl @Inject constructor(
    private val apiService: CategoriesApiService
) : CategoriesRepository {

    override suspend fun getCategoriesByLevel(level: Int): Result<List<Category>> {
        return try {
            val response = apiService.getCategoriesByLevel(level)
            if (response.isSuccessful) {
                val categories = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.Success(categories)
            } else {
                Result.Error(
                    Exception("Failed to get categories: ${response.code()}"),
                    "Error al cargar categorías"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión al cargar categorías")
        }
    }

    override suspend fun getAllCategories(): Result<List<Category>> {
        return try {
            val response = apiService.getAllCategories()
            if (response.isSuccessful) {
                val categories = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.Success(categories)
            } else {
                Result.Error(
                    Exception("Failed to get categories: ${response.code()}"),
                    "Error al cargar categorías"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión al cargar categorías")
        }
    }

    private fun CategoryDto.toDomain() = Category(
        id = id,
        name = name,
        description = description,
        imageUrl = imageUrl,
        slug = slug,
        level = level
    )
}
