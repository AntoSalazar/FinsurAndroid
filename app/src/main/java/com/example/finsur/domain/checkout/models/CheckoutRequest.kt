package com.example.finsur.domain.checkout.models

data class CheckoutRequest(
    val shippingMethod: ShippingMethod,
    val shippingAddressId: Int? = null,
    val warehouseId: Int? = null,
    val notes: String? = null,
    val couponCode: String? = null
) {
    fun validate(): String? {
        return when (shippingMethod) {
            ShippingMethod.SHIPPING -> {
                if (shippingAddressId == null) {
                    "Shipping address is required for delivery"
                } else null
            }
            ShippingMethod.PICKUP -> {
                when {
                    warehouseId == null -> "Warehouse is required for pickup"
                    notes.isNullOrBlank() -> "Notes are required for pickup orders"
                    else -> null
                }
            }
        }
    }
}
