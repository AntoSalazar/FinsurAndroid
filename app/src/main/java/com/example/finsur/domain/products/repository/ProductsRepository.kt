package com.example.finsur.domain.products.repository

import com.example.finsur.domain.common.Result
import com.example.finsur.domain.products.models.Product

data class PaginatedProducts(
    val products: List<Product>,
    val total: Int,
    val page: Int,
    val limit: Int,
    val pages: Int
)

interface ProductsRepository {
    suspend fun getFeaturedProducts(limit: Int = 8): Result<List<Product>>
    suspend fun searchProducts(query: String, page: Int = 1, limit: Int = 10): Result<List<Product>>
    suspend fun getProducts(page: Int = 1, limit: Int = 10): Result<PaginatedProducts>
    suspend fun getProductById(productId: Int): Result<Product>
}
