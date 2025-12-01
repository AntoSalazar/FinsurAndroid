package com.example.finsur.domain.orders.usecases

import com.example.finsur.domain.common.Result
import com.example.finsur.domain.orders.models.Order
import com.example.finsur.domain.orders.repository.OrdersRepository
import javax.inject.Inject

class GetOrderByIdUseCase @Inject constructor(
    private val ordersRepository: OrdersRepository
) {
    suspend operator fun invoke(orderId: Int): Result<Order> {
        return ordersRepository.getOrderById(orderId)
    }
}
