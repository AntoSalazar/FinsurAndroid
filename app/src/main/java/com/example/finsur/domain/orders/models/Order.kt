package com.example.finsur.domain.orders.models

data class Order(
    val id: Int,
    val orderNumber: String,
    val statusId: Int,
    val subtotal: String,
    val taxAmount: String,
    val shippingAmount: String,
    val total: String,
    val createdAt: String,
    val notes: String?,
    val status: OrderStatus,
    val items: List<OrderItem>? = null,
    val shippingAddress: ShippingAddress? = null
)

data class OrderStatus(
    val id: Int,
    val name: String,
    val description: String?,
    val color: String?,
    val isActive: Boolean
)

data class OrderItem(
    val id: Int,
    val orderId: Int,
    val productId: Int,
    val skuId: Int,
    val productName: String,
    val quantity: Int,
    val unitPrice: String,
    val totalPrice: String,
    val product: OrderProduct?,
    val sku: OrderSku?
)

data class OrderProduct(
    val id: Int,
    val name: String,
    val cover: String?,
    val images: List<ProductImage>
)

data class ProductImage(
    val imageUrl: String
)

data class PaginatedOrders(
    val orders: List<Order>,
    val total: Int,
    val page: Int,
    val limit: Int,
    val pages: Int
)

data class ShippingAddress(
    val id: Int,
    val title: String,
    val addressLine1: String,
    val addressLine2: String?,
    val country: String,
    val city: String,
    val state: String,
    val postalCode: String,
    val landmark: String?,
    val phoneNumber: String
)

data class OrderSku(
    val id: Int,
    val sku: String,
    val price: String
)
