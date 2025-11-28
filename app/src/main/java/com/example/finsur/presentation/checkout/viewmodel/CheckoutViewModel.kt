package com.example.finsur.presentation.checkout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finsur.domain.checkout.models.CheckoutRequest
import com.example.finsur.domain.checkout.models.ShippingMethod
import com.example.finsur.domain.checkout.usecases.CreatePaymentSheetUseCase
import com.example.finsur.domain.checkout.usecases.GetWarehousesUseCase
import com.example.finsur.domain.common.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val getWarehousesUseCase: GetWarehousesUseCase,
    private val createPaymentSheetUseCase: CreatePaymentSheetUseCase
) : ViewModel() {

    private val _warehousesUiState = MutableStateFlow<WarehousesUiState>(WarehousesUiState.Initial)
    val warehousesUiState: StateFlow<WarehousesUiState> = _warehousesUiState.asStateFlow()

    private val _paymentSheetUiState = MutableStateFlow<PaymentSheetUiState>(PaymentSheetUiState.Initial)
    val paymentSheetUiState: StateFlow<PaymentSheetUiState> = _paymentSheetUiState.asStateFlow()

    private val _formState = MutableStateFlow(CheckoutFormState())
    val formState: StateFlow<CheckoutFormState> = _formState.asStateFlow()

    private val _currentOrderNumber = MutableStateFlow<String?>(null)
    val currentOrderNumber: StateFlow<String?> = _currentOrderNumber.asStateFlow()

    fun loadWarehouses() {
        viewModelScope.launch {
            _warehousesUiState.value = WarehousesUiState.Loading
            when (val result = getWarehousesUseCase()) {
                is Result.Success -> {
                    _warehousesUiState.value = WarehousesUiState.Success(result.data)
                }
                is Result.Error -> {
                    _warehousesUiState.value = WarehousesUiState.Error(
                        result.message ?: "Error al cargar las sucursales"
                    )
                }
            }
        }
    }

    fun updateShippingMethod(method: String) {
        _formState.value = _formState.value.copy(
            selectedShippingMethod = method,
            selectedWarehouseId = null,
            pickupNotes = ""
        )

        // Load warehouses when pickup is selected
        if (method == "pickup" && _warehousesUiState.value is WarehousesUiState.Initial) {
            loadWarehouses()
        }
    }

    fun updateSelectedAddress(addressId: Int) {
        _formState.value = _formState.value.copy(selectedAddressId = addressId)
    }

    fun updateSelectedWarehouse(warehouseId: Int) {
        _formState.value = _formState.value.copy(selectedWarehouseId = warehouseId)
    }

    fun updatePickupNotes(notes: String) {
        _formState.value = _formState.value.copy(pickupNotes = notes)
    }

    fun updateCouponCode(code: String) {
        _formState.value = _formState.value.copy(couponCode = code)
    }

    fun proceedToPayment() {
        viewModelScope.launch {
            _paymentSheetUiState.value = PaymentSheetUiState.Loading

            val currentForm = _formState.value
            val shippingMethod = if (currentForm.selectedShippingMethod == "pickup") {
                ShippingMethod.PICKUP
            } else {
                ShippingMethod.SHIPPING
            }

            val request = CheckoutRequest(
                shippingMethod = shippingMethod,
                shippingAddressId = currentForm.selectedAddressId,
                warehouseId = currentForm.selectedWarehouseId,
                notes = currentForm.pickupNotes.ifBlank { null },
                couponCode = currentForm.couponCode.ifBlank { null }
            )

            when (val result = createPaymentSheetUseCase(request)) {
                is Result.Success -> {
                    _currentOrderNumber.value = result.data.orderNumber
                    _paymentSheetUiState.value = PaymentSheetUiState.Success(result.data)
                }
                is Result.Error -> {
                    _paymentSheetUiState.value = PaymentSheetUiState.Error(
                        result.message ?: "Error al crear la sesión de pago"
                    )
                }
            }
        }
    }

    fun resetPaymentSheetState() {
        _paymentSheetUiState.value = PaymentSheetUiState.Initial
    }
}
