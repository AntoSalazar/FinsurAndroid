package com.example.finsur.domain.brands.usecases

import com.example.finsur.domain.brands.models.Brand
import com.example.finsur.domain.brands.repository.BrandsRepository
import com.example.finsur.domain.common.Result
import javax.inject.Inject

class GetActiveBrandsUseCase @Inject constructor(
    private val repository: BrandsRepository
) {
    suspend operator fun invoke(): Result<List<Brand>> {
        return repository.getActiveBrands()
    }
}
