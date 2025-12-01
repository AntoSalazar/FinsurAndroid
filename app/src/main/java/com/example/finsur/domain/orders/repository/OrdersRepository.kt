package com.example.finsur.domain.orders.repository

import com.example.finsur.domain.common.Result
import com.example.finsur.domain.orders.models.Order
import com.example.finsur.domain.orders.models.OrderItem
import com.example.finsur.domain.orders.models.OrderStatus
import com.example.finsur.domain.orders.models.PaginatedOrders

interface OrdersRepository {
    suspend fun getMyOrders(page: Int = 1, limit: Int = 10): Result<PaginatedOrders>
    suspend fun getOrderById(orderId: Int): Result<Order>
    suspend fun getOrderByNumber(orderNumber: String): Result<Order>
    suspend fun getOrderItems(orderId: Int): Result<List<OrderItem>>
    suspend fun getOrdersByStatus(statusId: Int): Result<List<Order>>
    suspend fun getOrderStatuses(): Result<List<OrderStatus>>
    suspend fun cancelOrder(orderId: Int): Result<Order>
}
