package com.example.finsur.domain.products.usecases

import com.example.finsur.domain.common.Result
import com.example.finsur.domain.products.models.Product
import com.example.finsur.domain.products.repository.ProductsRepository
import javax.inject.Inject

class GetProductByIdUseCase @Inject constructor(
    private val repository: ProductsRepository
) {
    suspend operator fun invoke(productId: Int): Result<Product> {
        return repository.getProductById(productId)
    }
}
