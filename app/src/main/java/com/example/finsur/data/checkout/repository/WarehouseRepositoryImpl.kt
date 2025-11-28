package com.example.finsur.data.checkout.repository

import com.example.finsur.data.checkout.models.AddressDto
import com.example.finsur.data.checkout.models.WarehouseDto
import com.example.finsur.data.checkout.remote.WarehouseApiService
import com.example.finsur.domain.checkout.models.Address
import com.example.finsur.domain.checkout.models.Warehouse
import com.example.finsur.domain.checkout.repository.WarehouseRepository
import com.example.finsur.domain.common.Result
import javax.inject.Inject

class WarehouseRepositoryImpl @Inject constructor(
    private val apiService: WarehouseApiService
) : WarehouseRepository {

    override suspend fun getWarehouses(): Result<List<Warehouse>> {
        return try {
            val response = apiService.getWarehouses()
            if (response.isSuccessful) {
                val warehouses = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.Success(warehouses)
            } else {
                Result.Error(
                    exception = Exception("Failed to get warehouses: ${response.code()}"),
                    message = "Error al cargar las sucursales"
                )
            }
        } catch (e: Exception) {
            Result.Error(
                exception = e,
                message = "Error de conexión"
            )
        }
    }

    private fun WarehouseDto.toDomain() = Warehouse(
        id = id,
        name = name,
        address = address.toDomain(),
        phoneNumber = phoneNumber,
        schedule = schedule,
        isActive = isActive
    )

    private fun AddressDto.toDomain() = Address(
        addressLine1 = addressLine1,
        addressLine2 = addressLine2,
        city = city,
        state = state,
        postalCode = postalCode
    )
}
