package com.example.finsur.domain.brands.repository

import com.example.finsur.domain.brands.models.Brand
import com.example.finsur.domain.common.Result

interface BrandsRepository {
    suspend fun getActiveBrands(): Result<List<Brand>>
}
