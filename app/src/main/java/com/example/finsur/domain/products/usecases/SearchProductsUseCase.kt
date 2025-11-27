package com.example.finsur.domain.products.usecases

import com.example.finsur.domain.common.Result
import com.example.finsur.domain.products.models.Product
import com.example.finsur.domain.products.repository.ProductsRepository
import javax.inject.Inject

class SearchProductsUseCase @Inject constructor(
    private val repository: ProductsRepository
) {
    suspend operator fun invoke(query: String, page: Int = 1, limit: Int = 10): Result<List<Product>> {
        if (query.isBlank()) {
            return Result.Success(emptyList())
        }
        return repository.searchProducts(query, page, limit)
    }
}
