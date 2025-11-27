package com.example.finsur.presentation.cart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finsur.domain.cart.repository.Result
import com.example.finsur.domain.cart.usecases.AddItemToCartUseCase
import com.example.finsur.domain.cart.usecases.GetCurrentCartUseCase
import com.example.finsur.domain.cart.usecases.RemoveCartItemUseCase
import com.example.finsur.domain.cart.usecases.UpdateCartItemQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCurrentCartUseCase: GetCurrentCartUseCase,
    private val addItemToCartUseCase: AddItemToCartUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase
) : ViewModel() {

    private val _cartUiState = MutableStateFlow<CartUiState>(CartUiState.Initial)
    val cartUiState: StateFlow<CartUiState> = _cartUiState.asStateFlow()

    init {
        loadCart()
    }

    fun loadCart() {
        viewModelScope.launch {
            _cartUiState.value = CartUiState.Loading
            when (val result = getCurrentCartUseCase()) {
                is Result.Success -> {
                    _cartUiState.value = CartUiState.Success(result.data)
                }
                is Result.Error -> {
                    _cartUiState.value = CartUiState.Error(
                        result.message ?: "Error al cargar el carrito"
                    )
                }
            }
        }
    }

    fun addItemToCart(
        productId: Int,
        skuId: Int,
        quantity: Int,
        unitPrice: String
    ) {
        viewModelScope.launch {
            _cartUiState.value = CartUiState.Loading
            when (val result = addItemToCartUseCase(productId, skuId, quantity, unitPrice)) {
                is Result.Success -> {
                    _cartUiState.value = CartUiState.Success(result.data)
                }
                is Result.Error -> {
                    _cartUiState.value = CartUiState.Error(
                        result.message ?: "Error al agregar el artículo"
                    )
                }
            }
        }
    }

    fun updateItemQuantity(itemId: Int, quantity: Int) {
        viewModelScope.launch {
            _cartUiState.value = CartUiState.Loading
            when (val result = updateCartItemQuantityUseCase(itemId, quantity)) {
                is Result.Success -> {
                    _cartUiState.value = CartUiState.Success(result.data)
                }
                is Result.Error -> {
                    _cartUiState.value = CartUiState.Error(
                        result.message ?: "Error al actualizar la cantidad"
                    )
                }
            }
        }
    }

    fun removeItem(itemId: Int) {
        viewModelScope.launch {
            _cartUiState.value = CartUiState.Loading
            when (val result = removeCartItemUseCase(itemId)) {
                is Result.Success -> {
                    _cartUiState.value = CartUiState.Success(result.data)
                }
                is Result.Error -> {
                    _cartUiState.value = CartUiState.Error(
                        result.message ?: "Error al eliminar el artículo"
                    )
                }
            }
        }
    }
}
