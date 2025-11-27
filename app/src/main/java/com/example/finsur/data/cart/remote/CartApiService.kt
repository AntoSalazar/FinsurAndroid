package com.example.finsur.data.cart.remote

import com.example.finsur.data.cart.models.AddCartItemRequest
import com.example.finsur.data.cart.models.CartDto
import com.example.finsur.data.cart.models.CartResponse
import com.example.finsur.data.cart.models.UpdateCartItemRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CartApiService {
    @GET("carts/current")
    suspend fun getCurrentCart(): Response<CartDto>

    @POST("carts/items")
    suspend fun addItemToCart(
        @Body request: AddCartItemRequest
    ): Response<CartResponse>

    @PUT("carts/items/{itemId}")
    suspend fun updateCartItemQuantity(
        @Path("itemId") itemId: Int,
        @Body request: UpdateCartItemRequest
    ): Response<CartResponse>

    @DELETE("carts/items/{itemId}")
    suspend fun removeCartItem(
        @Path("itemId") itemId: Int
    ): Response<CartResponse>
}
