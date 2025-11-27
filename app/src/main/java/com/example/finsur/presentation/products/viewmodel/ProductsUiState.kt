package com.example.finsur.presentation.products.viewmodel

import com.example.finsur.domain.products.models.Product

sealed class ProductsUiState {
    data object Initial : ProductsUiState()
    data object Loading : ProductsUiState()
    data class Success(
        val products: List<Product>,
        val total: Int,
        val page: Int,
        val pages: Int,
        val hasMore: Boolean
    ) : ProductsUiState()
    data class Error(val message: String) : ProductsUiState()
}

sealed class ProductDetailUiState {
    data object Loading : ProductDetailUiState()
    data class Success(val product: Product) : ProductDetailUiState()
    data class Error(val message: String) : ProductDetailUiState()
}
