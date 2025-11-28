package com.example.finsur.domain.checkout.repository

import com.example.finsur.domain.checkout.models.Warehouse
import com.example.finsur.domain.common.Result

interface WarehouseRepository {
    suspend fun getWarehouses(): Result<List<Warehouse>>
}
