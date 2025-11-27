package com.example.finsur.domain.categories.usecases

import com.example.finsur.domain.categories.models.Category
import com.example.finsur.domain.categories.repository.CategoriesRepository
import com.example.finsur.domain.common.Result
import javax.inject.Inject

class GetCategoriesByLevelUseCase @Inject constructor(
    private val repository: CategoriesRepository
) {
    suspend operator fun invoke(level: Int = 1): Result<List<Category>> {
        return repository.getCategoriesByLevel(level)
    }
}
