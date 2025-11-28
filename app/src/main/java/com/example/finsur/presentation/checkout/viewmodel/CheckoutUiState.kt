package com.example.finsur.presentation.checkout.viewmodel

import com.example.finsur.domain.checkout.models.PaymentSheetResponse
import com.example.finsur.domain.checkout.models.Warehouse

sealed class WarehousesUiState {
    data object Initial : WarehousesUiState()
    data object Loading : WarehousesUiState()
    data class Success(val warehouses: List<Warehouse>) : WarehousesUiState()
    data class Error(val message: String) : WarehousesUiState()
}

sealed class PaymentSheetUiState {
    data object Initial : PaymentSheetUiState()
    data object Loading : PaymentSheetUiState()
    data class Success(val paymentSheet: PaymentSheetResponse) : PaymentSheetUiState()
    data class Error(val message: String) : PaymentSheetUiState()
}

data class CheckoutFormState(
    val selectedShippingMethod: String = "shipping", // "shipping" or "pickup"
    val selectedAddressId: Int? = null,
    val selectedWarehouseId: Int? = null,
    val pickupNotes: String = "",
    val couponCode: String = ""
)
