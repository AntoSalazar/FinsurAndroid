package com.example.finsur.data.orders.repository

import com.example.finsur.data.orders.models.OrderDto
import com.example.finsur.data.orders.models.OrderItemDto
import com.example.finsur.data.orders.models.OrderProductDto
import com.example.finsur.data.orders.models.OrderSkuDto
import com.example.finsur.data.orders.models.OrderStatusDto
import com.example.finsur.data.orders.models.ProductImageDto
import com.example.finsur.data.orders.models.ShippingAddressDto
import com.example.finsur.data.orders.remote.OrdersApiService
import com.example.finsur.domain.common.Result
import com.example.finsur.domain.orders.models.Order
import com.example.finsur.domain.orders.models.OrderItem
import com.example.finsur.domain.orders.models.OrderProduct
import com.example.finsur.domain.orders.models.OrderSku
import com.example.finsur.domain.orders.models.OrderStatus
import com.example.finsur.domain.orders.models.PaginatedOrders
import com.example.finsur.domain.orders.models.ProductImage
import com.example.finsur.domain.orders.models.ShippingAddress
import com.example.finsur.domain.orders.repository.OrdersRepository
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(
    private val apiService: OrdersApiService
) : OrdersRepository {

    override suspend fun getMyOrders(page: Int, limit: Int): Result<PaginatedOrders> {
        return try {
            val response = apiService.getMyOrders(page, limit)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(
                        PaginatedOrders(
                            orders = body.data.map { it.toDomain() },
                            total = body.pagination.total,
                            page = body.pagination.page,
                            limit = body.pagination.limit,
                            pages = body.pagination.pages
                        )
                    )
                } else {
                    Result.Error(
                        Exception("Empty response"),
                        "No se pudieron cargar los pedidos"
                    )
                }
            } else {
                Result.Error(
                    Exception("Failed to get orders: ${response.code()}"),
                    "Error al cargar pedidos"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión al cargar pedidos")
        }
    }

    override suspend fun getOrderById(orderId: Int): Result<Order> {
        return try {
            val response = apiService.getOrderById(orderId)
            if (response.isSuccessful) {
                val order = response.body()?.toDomain()
                if (order != null) {
                    Result.Success(order)
                } else {
                    Result.Error(
                        Exception("Order not found"),
                        "Pedido no encontrado"
                    )
                }
            } else {
                Result.Error(
                    Exception("Failed to get order: ${response.code()}"),
                    "Error al cargar pedido"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión al cargar pedido")
        }
    }

    override suspend fun getOrderByNumber(orderNumber: String): Result<Order> {
        return try {
            val response = apiService.getOrderByNumber(orderNumber)
            if (response.isSuccessful) {
                val order = response.body()?.toDomain()
                if (order != null) {
                    Result.Success(order)
                } else {
                    Result.Error(
                        Exception("Order not found"),
                        "Pedido no encontrado"
                    )
                }
            } else {
                Result.Error(
                    Exception("Failed to get order: ${response.code()}"),
                    "Error al cargar pedido"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión al cargar pedido")
        }
    }

    override suspend fun getOrderItems(orderId: Int): Result<List<OrderItem>> {
        return try {
            val response = apiService.getOrderItems(orderId)
            if (response.isSuccessful) {
                val items = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.Success(items)
            } else {
                Result.Error(
                    Exception("Failed to get order items: ${response.code()}"),
                    "Error al cargar artículos del pedido"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión al cargar artículos")
        }
    }

    override suspend fun getOrdersByStatus(statusId: Int): Result<List<Order>> {
        return try {
            val response = apiService.getOrdersByStatus(statusId)
            if (response.isSuccessful) {
                val orders = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.Success(orders)
            } else {
                Result.Error(
                    Exception("Failed to get orders by status: ${response.code()}"),
                    "Error al cargar pedidos"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión al cargar pedidos")
        }
    }

    override suspend fun getOrderStatuses(): Result<List<OrderStatus>> {
        return try {
            val response = apiService.getOrderStatuses()
            if (response.isSuccessful) {
                val statuses = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.Success(statuses)
            } else {
                Result.Error(
                    Exception("Failed to get order statuses: ${response.code()}"),
                    "Error al cargar estados"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión al cargar estados")
        }
    }

    override suspend fun cancelOrder(orderId: Int): Result<Order> {
        return try {
            val response = apiService.cancelOrder(orderId)
            if (response.isSuccessful) {
                val order = response.body()?.toDomain()
                if (order != null) {
                    Result.Success(order)
                } else {
                    Result.Error(
                        Exception("Failed to cancel order"),
                        "Error al cancelar pedido"
                    )
                }
            } else {
                Result.Error(
                    Exception("Failed to cancel order: ${response.code()}"),
                    "Error al cancelar pedido"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión al cancelar pedido")
        }
    }

    private fun OrderDto.toDomain() = Order(
        id = id,
        orderNumber = orderNumber,
        statusId = statusId,
        subtotal = subtotal,
        taxAmount = taxAmount,
        shippingAmount = shippingAmount,
        total = total,
        createdAt = createdAt,
        notes = notes,
        status = status.toDomain(),
        items = items?.map { it.toDomain() },
        shippingAddress = shippingAddress?.toDomain()
    )

    private fun OrderStatusDto.toDomain() = OrderStatus(
        id = id,
        name = name,
        description = description,
        color = color,
        isActive = isActive
    )

    private fun OrderItemDto.toDomain() = OrderItem(
        id = id,
        orderId = orderId,
        productId = productId,
        skuId = skuId,
        productName = productName,
        quantity = quantity,
        unitPrice = unitPrice,
        totalPrice = totalPrice,
        product = product?.toDomain(),
        sku = sku?.toDomain()
    )

    private fun OrderProductDto.toDomain() = OrderProduct(
        id = id,
        name = name,
        cover = cover,
        images = images.map { it.toDomain() }
    )

    private fun ProductImageDto.toDomain() = ProductImage(
        imageUrl = imageUrl
    )

    private fun ShippingAddressDto.toDomain() = ShippingAddress(
        id = id,
        title = title,
        addressLine1 = addressLine1,
        addressLine2 = addressLine2,
        country = country,
        city = city,
        state = state,
        postalCode = postalCode,
        landmark = landmark,
        phoneNumber = phoneNumber
    )

    private fun OrderSkuDto.toDomain() = OrderSku(
        id = id,
        sku = sku,
        price = price
    )
}
