package com.example.finsur.data.checkout.remote

import com.example.finsur.data.checkout.models.PaymentSheetRequestDto
import com.example.finsur.data.checkout.models.PaymentSheetResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CheckoutApiService {
    @POST("payments/payment-sheet")
    suspend fun createPaymentSheet(
        @Body request: PaymentSheetRequestDto
    ): Response<PaymentSheetResponseDto>
}
