package com.example.finsur.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finsur.domain.brands.usecases.GetActiveBrandsUseCase
import com.example.finsur.domain.categories.usecases.GetCategoriesByLevelUseCase
import com.example.finsur.domain.common.Result
import com.example.finsur.domain.products.usecases.GetFeaturedProductsUseCase
import com.example.finsur.domain.products.usecases.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFeaturedProductsUseCase: GetFeaturedProductsUseCase,
    private val getCategoriesByLevelUseCase: GetCategoriesByLevelUseCase,
    private val getActiveBrandsUseCase: GetActiveBrandsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase
) : ViewModel() {

    private val _featuredProductsState = MutableStateFlow<FeaturedProductsState>(FeaturedProductsState.Initial)
    val featuredProductsState: StateFlow<FeaturedProductsState> = _featuredProductsState.asStateFlow()

    private val _categoriesState = MutableStateFlow<CategoriesState>(CategoriesState.Initial)
    val categoriesState: StateFlow<CategoriesState> = _categoriesState.asStateFlow()

    private val _brandsState = MutableStateFlow<BrandsState>(BrandsState.Initial)
    val brandsState: StateFlow<BrandsState> = _brandsState.asStateFlow()

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadHomeData()
        observeSearchQuery()
    }

    private fun loadHomeData() {
        loadFeaturedProducts()
        loadCategories()
        loadBrands()
    }

    private fun loadFeaturedProducts() {
        viewModelScope.launch {
            _featuredProductsState.value = FeaturedProductsState.Loading
            when (val result = getFeaturedProductsUseCase(limit = 8)) {
                is Result.Success -> {
                    _featuredProductsState.value = FeaturedProductsState.Success(result.data)
                }
                is Result.Error -> {
                    _featuredProductsState.value = FeaturedProductsState.Error(
                        result.message ?: "Error al cargar productos destacados"
                    )
                }
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _categoriesState.value = CategoriesState.Loading
            when (val result = getCategoriesByLevelUseCase(level = 1)) {
                is Result.Success -> {
                    _categoriesState.value = CategoriesState.Success(result.data)
                }
                is Result.Error -> {
                    _categoriesState.value = CategoriesState.Error(
                        result.message ?: "Error al cargar categorías"
                    )
                }
            }
        }
    }

    private fun loadBrands() {
        viewModelScope.launch {
            _brandsState.value = BrandsState.Loading
            when (val result = getActiveBrandsUseCase()) {
                is Result.Success -> {
                    _brandsState.value = BrandsState.Success(result.data)
                }
                is Result.Error -> {
                    _brandsState.value = BrandsState.Error(
                        result.message ?: "Error al cargar marcas"
                    )
                }
            }
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(400)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isBlank()) {
                        _searchState.value = SearchState.Initial
                    } else {
                        searchProducts(query)
                    }
                }
        }
    }

    private suspend fun searchProducts(query: String) {
        _searchState.value = SearchState.Loading
        when (val result = searchProductsUseCase(query, page = 1, limit = 10)) {
            is Result.Success -> {
                if (result.data.isEmpty()) {
                    _searchState.value = SearchState.Empty
                } else {
                    _searchState.value = SearchState.Success(result.data)
                }
            }
            is Result.Error -> {
                _searchState.value = SearchState.Error(
                    result.message ?: "Error al buscar productos"
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchState.value = SearchState.Initial
    }

    fun retry() {
        loadHomeData()
    }
}
