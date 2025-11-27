package com.example.finsur.domain.categories.repository

import com.example.finsur.domain.categories.models.Category
import com.example.finsur.domain.common.Result

interface CategoriesRepository {
    suspend fun getCategoriesByLevel(level: Int): Result<List<Category>>
}
