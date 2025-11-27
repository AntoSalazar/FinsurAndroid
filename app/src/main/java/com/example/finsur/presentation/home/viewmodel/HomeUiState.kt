package com.example.finsur.presentation.home.viewmodel

import com.example.finsur.domain.brands.models.Brand
import com.example.finsur.domain.categories.models.Category
import com.example.finsur.domain.products.models.Product

sealed class FeaturedProductsState {
    data object Initial : FeaturedProductsState()
    data object Loading : FeaturedProductsState()
    data class Success(val products: List<Product>) : FeaturedProductsState()
    data class Error(val message: String) : FeaturedProductsState()
}

sealed class CategoriesState {
    data object Initial : CategoriesState()
    data object Loading : CategoriesState()
    data class Success(val categories: List<Category>) : CategoriesState()
    data class Error(val message: String) : CategoriesState()
}

sealed class BrandsState {
    data object Initial : BrandsState()
    data object Loading : BrandsState()
    data class Success(val brands: List<Brand>) : BrandsState()
    data class Error(val message: String) : BrandsState()
}

sealed class SearchState {
    data object Initial : SearchState()
    data object Loading : SearchState()
    data class Success(val products: List<Product>) : SearchState()
    data class Error(val message: String) : SearchState()
    data object Empty : SearchState()
}
