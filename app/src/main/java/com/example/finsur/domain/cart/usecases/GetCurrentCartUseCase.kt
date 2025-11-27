package com.example.finsur.domain.cart.usecases

import com.example.finsur.domain.cart.models.Cart
import com.example.finsur.domain.cart.repository.CartRepository
import com.example.finsur.domain.cart.repository.Result
import javax.inject.Inject

class GetCurrentCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(): Result<Cart> {
        return cartRepository.getCurrentCart()
    }
}
