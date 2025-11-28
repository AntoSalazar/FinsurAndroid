package com.example.finsur.domain.checkout.repository

import com.example.finsur.domain.checkout.models.CheckoutRequest
import com.example.finsur.domain.checkout.models.PaymentSheetResponse
import com.example.finsur.domain.common.Result

interface CheckoutRepository {
    suspend fun createPaymentSheet(request: CheckoutRequest): Result<PaymentSheetResponse>
}
