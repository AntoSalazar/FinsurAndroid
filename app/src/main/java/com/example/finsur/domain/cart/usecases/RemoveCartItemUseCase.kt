package com.example.finsur.domain.cart.usecases

import com.example.finsur.domain.cart.models.Cart
import com.example.finsur.domain.cart.repository.CartRepository
import com.example.finsur.domain.cart.repository.Result
import javax.inject.Inject

class RemoveCartItemUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(itemId: Int): Result<Cart> {
        return cartRepository.removeCartItem(itemId)
    }
}
