package com.example.finsur.data.cart.repository

import com.example.finsur.data.cart.models.AddCartItemRequest
import com.example.finsur.data.cart.models.CartDto
import com.example.finsur.data.cart.models.CartItemDto
import com.example.finsur.data.cart.models.CartProductDto
import com.example.finsur.data.cart.models.UpdateCartItemRequest
import com.example.finsur.data.cart.remote.CartApiService
import com.example.finsur.domain.cart.models.Cart
import com.example.finsur.domain.cart.models.CartItem
import com.example.finsur.domain.cart.models.CartProduct
import com.example.finsur.domain.cart.repository.CartRepository
import com.example.finsur.domain.cart.repository.Result
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartApiService: CartApiService
) : CartRepository {

    override suspend fun getCurrentCart(): Result<Cart> {
        return try {
            val response = cartApiService.getCurrentCart()
            if (response.isSuccessful) {
                val cartDto = response.body()
                if (cartDto != null) {
                    Result.Success(cartDto.toDomain())
                } else {
                    Result.Error(
                        Exception("No cart data"),
                        "No se encontró el carrito"
                    )
                }
            } else {
                Result.Error(
                    Exception("Failed to get cart"),
                    response.errorBody()?.string() ?: "Error al obtener el carrito"
                )
            }
        } catch (e: Exception) {
            Result.Error(
                e,
                "Error de conexión. Por favor, verifica tu conexión a internet."
            )
        }
    }

    override suspend fun addItemToCart(
        productId: Int,
        skuId: Int,
        quantity: Int,
        unitPrice: String
    ): Result<Cart> {
        return try {
            val request = AddCartItemRequest(
                productId = productId,
                skuId = skuId,
                quantity = quantity,
                unitPrice = unitPrice
            )
            val response = cartApiService.addItemToCart(request)
            if (response.isSuccessful) {
                val cartResponse = response.body()
                if (cartResponse != null) {
                    Result.Success(cartResponse.cart.toDomain())
                } else {
                    Result.Error(
                        Exception("No cart data"),
                        "Error al agregar el artículo al carrito"
                    )
                }
            } else {
                Result.Error(
                    Exception("Failed to add item to cart"),
                    response.errorBody()?.string() ?: "Error al agregar el artículo"
                )
            }
        } catch (e: Exception) {
            Result.Error(
                e,
                "Error de conexión. Por favor, verifica tu conexión a internet."
            )
        }
    }

    override suspend fun updateCartItemQuantity(itemId: Int, quantity: Int): Result<Cart> {
        return try {
            val request = UpdateCartItemRequest(quantity = quantity)
            val response = cartApiService.updateCartItemQuantity(itemId, request)
            if (response.isSuccessful) {
                val cartResponse = response.body()
                if (cartResponse != null) {
                    Result.Success(cartResponse.cart.toDomain())
                } else {
                    Result.Error(
                        Exception("No cart data"),
                        "Error al actualizar la cantidad"
                    )
                }
            } else {
                Result.Error(
                    Exception("Failed to update cart item"),
                    response.errorBody()?.string() ?: "Error al actualizar la cantidad"
                )
            }
        } catch (e: Exception) {
            Result.Error(
                e,
                "Error de conexión. Por favor, verifica tu conexión a internet."
            )
        }
    }

    override suspend fun removeCartItem(itemId: Int): Result<Cart> {
        return try {
            val response = cartApiService.removeCartItem(itemId)
            if (response.isSuccessful) {
                val cartResponse = response.body()
                if (cartResponse != null) {
                    Result.Success(cartResponse.cart.toDomain())
                } else {
                    Result.Error(
                        Exception("No cart data"),
                        "Error al eliminar el artículo"
                    )
                }
            } else {
                Result.Error(
                    Exception("Failed to remove cart item"),
                    response.errorBody()?.string() ?: "Error al eliminar el artículo"
                )
            }
        } catch (e: Exception) {
            Result.Error(
                e,
                "Error de conexión. Por favor, verifica tu conexión a internet."
            )
        }
    }

    private fun CartDto.toDomain(): Cart {
        return Cart(
            id = id,
            userId = userId,
            sessionId = sessionId,
            subtotal = subtotal,
            taxAmount = taxAmount,
            discountAmount = discountAmount,
            shippingAmount = shippingAmount,
            total = total,
            couponCode = couponCode,
            items = items?.map { it.toDomain() } ?: emptyList(),
            createdAt = createdAt,
            updatedAt = updatedAt,
            expiresAt = expiresAt
        )
    }

    private fun CartItemDto.toDomain(): CartItem {
        return CartItem(
            id = id,
            productId = productId,
            skuId = skuId,
            quantity = quantity,
            unitPrice = unitPrice,
            product = product.toDomain()
        )
    }

    private fun CartProductDto.toDomain(): CartProduct {
        return CartProduct(
            id = id,
            name = name,
            cover = cover,
            slug = slug
        )
    }
}
