package com.example.finsur.domain.cart.models

data class CartItem(
    val id: Int,
    val productId: Int,
    val skuId: Int,
    val quantity: Int,
    val unitPrice: String,
    val product: CartProduct
)

data class CartProduct(
    val id: Int,
    val name: String,
    val cover: String?,
    val slug: String
)
