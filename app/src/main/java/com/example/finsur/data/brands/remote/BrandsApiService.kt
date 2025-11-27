package com.example.finsur.data.brands.remote

import com.example.finsur.data.brands.models.BrandDto
import retrofit2.Response
import retrofit2.http.GET

interface BrandsApiService {
    @GET("brands/active")
    suspend fun getActiveBrands(): Response<List<BrandDto>>
}
