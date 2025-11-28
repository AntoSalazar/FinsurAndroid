package com.example.finsur.data.checkout.remote

import com.example.finsur.data.checkout.models.WarehouseDto
import retrofit2.Response
import retrofit2.http.GET

interface WarehouseApiService {
    @GET("warehouses/")
    suspend fun getWarehouses(): Response<List<WarehouseDto>>
}
