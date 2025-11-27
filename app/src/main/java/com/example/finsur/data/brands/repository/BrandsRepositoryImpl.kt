package com.example.finsur.data.brands.repository

import com.example.finsur.data.brands.models.BrandDto
import com.example.finsur.data.brands.remote.BrandsApiService
import com.example.finsur.domain.brands.models.Brand
import com.example.finsur.domain.brands.repository.BrandsRepository
import com.example.finsur.domain.common.Result
import javax.inject.Inject

class BrandsRepositoryImpl @Inject constructor(
    private val apiService: BrandsApiService
) : BrandsRepository {

    override suspend fun getActiveBrands(): Result<List<Brand>> {
        return try {
            val response = apiService.getActiveBrands()
            if (response.isSuccessful) {
                val brands = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.Success(brands)
            } else {
                Result.Error(
                    Exception("Failed to get active brands: ${response.code()}"),
                    "Error al cargar marcas"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión al cargar marcas")
        }
    }

    private fun BrandDto.toDomain() = Brand(
        id = id,
        name = name,
        slug = slug,
        logoUrl = logoUrl,
        websiteUrl = websiteUrl,
        description = description,
        isFeatured = isFeatured,
        isActive = isActive
    )
}
