package com.example.finsur.presentation.products.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finsur.domain.common.Result
import com.example.finsur.domain.products.models.Product
import com.example.finsur.domain.products.usecases.GetProductByIdUseCase
import com.example.finsur.domain.products.usecases.GetProductsUseCase
import com.example.finsur.domain.products.usecases.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val searchProductsUseCase: SearchProductsUseCase
) : ViewModel() {

    private val _productsUiState = MutableStateFlow<ProductsUiState>(ProductsUiState.Initial)
    val productsUiState: StateFlow<ProductsUiState> = _productsUiState.asStateFlow()

    private val _productDetailUiState = MutableStateFlow<ProductDetailUiState?>(null)
    val productDetailUiState: StateFlow<ProductDetailUiState?> = _productDetailUiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var currentPage = 1
    private val pageLimit = 10
    private val allProducts = mutableListOf<Product>()

    init {
        loadProducts()
    }

    fun loadProducts(page: Int = 1) {
        viewModelScope.launch {
            if (page == 1) {
                _productsUiState.value = ProductsUiState.Loading
                allProducts.clear()
            }

            currentPage = page

            when (val result = getProductsUseCase(page, pageLimit)) {
                is Result.Success -> {
                    allProducts.addAll(result.data.products)
                    _productsUiState.value = ProductsUiState.Success(
                        products = allProducts.toList(),
                        total = result.data.total,
                        page = result.data.page,
                        pages = result.data.pages,
                        hasMore = result.data.page < result.data.pages
                    )
                }
                is Result.Error -> {
                    _productsUiState.value = ProductsUiState.Error(
                        result.message ?: "Error al cargar productos"
                    )
                }
            }
        }
    }

    fun loadMore() {
        val currentState = _productsUiState.value
        if (currentState is ProductsUiState.Success && currentState.hasMore) {
            loadProducts(currentPage + 1)
        }
    }

    fun searchProducts(query: String) {
        _searchQuery.value = query

        if (query.isBlank()) {
            loadProducts(1)
            return
        }

        viewModelScope.launch {
            _productsUiState.value = ProductsUiState.Loading
            allProducts.clear()

            when (val result = searchProductsUseCase(query)) {
                is Result.Success -> {
                    allProducts.addAll(result.data)
                    _productsUiState.value = ProductsUiState.Success(
                        products = allProducts.toList(),
                        total = result.data.size,
                        page = 1,
                        pages = 1,
                        hasMore = false
                    )
                }
                is Result.Error -> {
                    _productsUiState.value = ProductsUiState.Error(
                        result.message ?: "Error al buscar productos"
                    )
                }
            }
        }
    }

    fun loadProductDetail(productId: Int) {
        viewModelScope.launch {
            _productDetailUiState.value = ProductDetailUiState.Loading

            when (val result = getProductByIdUseCase(productId)) {
                is Result.Success -> {
                    _productDetailUiState.value = ProductDetailUiState.Success(result.data)
                }
                is Result.Error -> {
                    _productDetailUiState.value = ProductDetailUiState.Error(
                        result.message ?: "Error al cargar producto"
                    )
                }
            }
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        loadProducts(1)
    }
}
