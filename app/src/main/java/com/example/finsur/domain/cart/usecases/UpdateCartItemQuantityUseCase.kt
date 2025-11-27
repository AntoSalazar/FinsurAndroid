package com.example.finsur.domain.cart.usecases

import com.example.finsur.domain.cart.models.Cart
import com.example.finsur.domain.cart.repository.CartRepository
import com.example.finsur.domain.cart.repository.Result
import javax.inject.Inject

class UpdateCartItemQuantityUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(itemId: Int, quantity: Int): Result<Cart> {
        if (quantity <= 0) {
            return Result.Error(
                Exception("Invalid quantity"),
                "La cantidad debe ser mayor a 0"
            )
        }

        return cartRepository.updateCartItemQuantity(itemId, quantity)
    }
}
