package com.example.finsur.domain.products.usecases

import com.example.finsur.domain.common.Result
import com.example.finsur.domain.products.repository.PaginatedProducts
import com.example.finsur.domain.products.repository.ProductsRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: ProductsRepository
) {
    suspend operator fun invoke(page: Int = 1, limit: Int = 10): Result<PaginatedProducts> {
        return repository.getProducts(page, limit)
    }
}
