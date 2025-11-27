package com.example.finsur.presentation.cart.viewmodel

import com.example.finsur.domain.cart.models.Cart

sealed class CartUiState {
    data object Initial : CartUiState()
    data object Loading : CartUiState()
    data class Success(val cart: Cart) : CartUiState()
    data class Error(val message: String) : CartUiState()
}
