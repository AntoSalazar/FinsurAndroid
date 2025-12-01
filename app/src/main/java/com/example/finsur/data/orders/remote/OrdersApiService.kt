package com.example.finsur.data.orders.remote

import com.example.finsur.data.orders.models.OrderDto
import com.example.finsur.data.orders.models.OrderItemDto
import com.example.finsur.data.orders.models.OrderStatusDto
import com.example.finsur.data.orders.models.PaginatedOrdersResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OrdersApiService {
    @GET("orders/my-orders")
    suspend fun getMyOrders(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<PaginatedOrdersResponse>

    @GET("orders/{orderId}")
    suspend fun getOrderById(
        @Path("orderId") orderId: Int
    ): Response<OrderDto>

    @GET("orders/number/{orderNumber}")
    suspend fun getOrderByNumber(
        @Path("orderNumber") orderNumber: String
    ): Response<OrderDto>

    @GET("order-items/order/{orderId}")
    suspend fun getOrderItems(
        @Path("orderId") orderId: Int
    ): Response<List<OrderItemDto>>

    @GET("orders/status/{statusId}")
    suspend fun getOrdersByStatus(
        @Path("statusId") statusId: Int
    ): Response<List<OrderDto>>

    @GET("order-statuses/active")
    suspend fun getOrderStatuses(): Response<List<OrderStatusDto>>

    @POST("orders/my-orders/{orderId}/cancel")
    suspend fun cancelOrder(
        @Path("orderId") orderId: Int
    ): Response<OrderDto>
}
