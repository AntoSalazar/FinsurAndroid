package com.example.finsur.domain.checkout.usecases

import com.example.finsur.domain.checkout.models.CheckoutRequest
import com.example.finsur.domain.checkout.models.PaymentSheetResponse
import com.example.finsur.domain.checkout.repository.CheckoutRepository
import com.example.finsur.domain.common.Result
import javax.inject.Inject

class CreatePaymentSheetUseCase @Inject constructor(
    private val repository: CheckoutRepository
) {
    suspend operator fun invoke(request: CheckoutRequest): Result<PaymentSheetResponse> {
        // Validate the request
        val validationError = request.validate()
        if (validationError != null) {
            return Result.Error(
                exception = IllegalArgumentException(validationError),
                message = validationError
            )
        }

        return repository.createPaymentSheet(request)
    }
}
