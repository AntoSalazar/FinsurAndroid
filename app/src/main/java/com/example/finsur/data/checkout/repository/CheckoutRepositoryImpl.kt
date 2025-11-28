package com.example.finsur.data.checkout.repository

import com.example.finsur.data.checkout.models.PaymentSheetRequestDto
import com.example.finsur.data.checkout.models.PaymentSheetResponseDto
import com.example.finsur.data.checkout.remote.CheckoutApiService
import com.example.finsur.domain.checkout.models.CheckoutRequest
import com.example.finsur.domain.checkout.models.PaymentSheetResponse
import com.example.finsur.domain.checkout.repository.CheckoutRepository
import com.example.finsur.domain.common.Result
import javax.inject.Inject

class CheckoutRepositoryImpl @Inject constructor(
    private val apiService: CheckoutApiService
) : CheckoutRepository {

    override suspend fun createPaymentSheet(request: CheckoutRequest): Result<PaymentSheetResponse> {
        return try {
            val requestDto = request.toDto()
            val response = apiService.createPaymentSheet(requestDto)

            if (response.isSuccessful) {
                val paymentSheet = response.body()?.toDomain()
                if (paymentSheet != null) {
                    Result.Success(paymentSheet)
                } else {
                    Result.Error(
                        exception = Exception("Empty response body"),
                        message = "Error al crear la sesión de pago"
                    )
                }
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Datos de pago inválidos"
                    401 -> "Por favor inicia sesión"
                    404 -> "Carrito no encontrado o vacío"
                    500 -> "Error del servidor. Inténtalo de nuevo"
                    else -> "Error al procesar el pago"
                }
                Result.Error(
                    exception = Exception("Payment sheet creation failed: ${response.code()}"),
                    message = errorMessage
                )
            }
        } catch (e: Exception) {
            Result.Error(
                exception = e,
                message = "Error de conexión"
            )
        }
    }

    private fun CheckoutRequest.toDto() = PaymentSheetRequestDto(
        shippingMethod = shippingMethod.value,
        shippingAddressId = shippingAddressId,
        warehouseId = warehouseId,
        notes = notes,
        couponCode = couponCode
    )

    private fun PaymentSheetResponseDto.toDomain() = PaymentSheetResponse(
        customer = customer,
        customerSessionClientSecret = customerSessionClientSecret,
        orderId = orderId,
        orderNumber = orderNumber,
        paymentIntent = paymentIntent,
        publishableKey = publishableKey
    )
}
