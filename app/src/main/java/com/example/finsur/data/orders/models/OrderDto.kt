package com.example.finsur.data.orders.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    val id: Int,
    @SerialName("order_number")
    val orderNumber: String,
    @SerialName("status_id")
    val statusId: Int,
    val subtotal: String,
    @SerialName("tax_amount")
    val taxAmount: String,
    @SerialName("shipping_amount")
    val shippingAmount: String,
    val total: String,
    @SerialName("created_at")
    val createdAt: String,
    val notes: String? = null,
    val status: OrderStatusDto,
    val items: List<OrderItemDto>? = null,
    @SerialName("shipping_address")
    val shippingAddress: ShippingAddressDto? = null
)

@Serializable
data class OrderStatusDto(
    val id: Int,
    val name: String,
    val description: String? = null,
    val color: String? = null,
    @SerialName("is_active")
    val isActive: Boolean
)

@Serializable
data class OrderItemDto(
    val id: Int,
    @SerialName("order_id")
    val orderId: Int,
    @SerialName("product_id")
    val productId: Int,
    @SerialName("sku_id")
    val skuId: Int,
    @SerialName("product_name")
    val productName: String,
    val quantity: Int,
    @SerialName("unit_price")
    val unitPrice: String,
    @SerialName("total_price")
    val totalPrice: String,
    val product: OrderProductDto? = null,
    val sku: OrderSkuDto? = null
)

@Serializable
data class OrderProductDto(
    val id: Int,
    val name: String,
    val cover: String? = null,
    val images: List<ProductImageDto> = emptyList()
)

@Serializable
data class ProductImageDto(
    @SerialName("image_url")
    val imageUrl: String
)

@Serializable
data class PaginatedOrdersResponse(
    val data: List<OrderDto>,
    val pagination: PaginationDto
)

@Serializable
data class PaginationDto(
    val total: Int,
    val page: Int,
    val limit: Int,
    val pages: Int
)

@Serializable
data class ShippingAddressDto(
    val id: Int,
    val title: String,
    @SerialName("address_line_1")
    val addressLine1: String,
    @SerialName("address_line_2")
    val addressLine2: String? = null,
    val country: String,
    val city: String,
    val state: String,
    @SerialName("postal_code")
    val postalCode: String,
    val landmark: String? = null,
    @SerialName("phone_number")
    val phoneNumber: String
)

@Serializable
data class OrderSkuDto(
    val id: Int,
    val sku: String,
    val price: String
)
