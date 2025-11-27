package com.example.finsur.domain.products.usecases

import com.example.finsur.domain.common.Result
import com.example.finsur.domain.products.models.Product
import com.example.finsur.domain.products.repository.ProductsRepository
import javax.inject.Inject

class GetFeaturedProductsUseCase @Inject constructor(
    private val repository: ProductsRepository
) {
    suspend operator fun invoke(limit: Int = 8): Result<List<Product>> {
        return repository.getFeaturedProducts(limit)
    }
}
