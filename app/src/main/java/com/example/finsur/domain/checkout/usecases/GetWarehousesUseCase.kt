package com.example.finsur.domain.checkout.usecases

import com.example.finsur.domain.checkout.models.Warehouse
import com.example.finsur.domain.checkout.repository.WarehouseRepository
import com.example.finsur.domain.common.Result
import javax.inject.Inject

class GetWarehousesUseCase @Inject constructor(
    private val repository: WarehouseRepository
) {
    suspend operator fun invoke(): Result<List<Warehouse>> {
        return repository.getWarehouses()
    }
}
