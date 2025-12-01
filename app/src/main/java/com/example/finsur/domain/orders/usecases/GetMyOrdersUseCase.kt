package com.example.finsur.domain.orders.usecases

import com.example.finsur.domain.common.Result
import com.example.finsur.domain.orders.models.PaginatedOrders
import com.example.finsur.domain.orders.repository.OrdersRepository
import javax.inject.Inject

class GetMyOrdersUseCase @Inject constructor(
    private val repository: OrdersRepository
) {
    suspend operator fun invoke(page: Int = 1, limit: Int = 10): Result<PaginatedOrders> {
        return repository.getMyOrders(page, limit)
    }
}
