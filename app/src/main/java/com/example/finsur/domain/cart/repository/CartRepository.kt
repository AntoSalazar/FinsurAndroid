package com.example.finsur.domain.cart.repository

import com.example.finsur.domain.cart.models.Cart

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception, val message: String? = null) : Result<Nothing>()
}

interface CartRepository {
    suspend fun getCurrentCart(): Result<Cart>
    suspend fun addItemToCart(
        productId: Int,
        skuId: Int,
        quantity: Int,
        unitPrice: String
    ): Result<Cart>
    suspend fun updateCartItemQuantity(itemId: Int, quantity: Int): Result<Cart>
    suspend fun removeCartItem(itemId: Int): Result<Cart>
}
