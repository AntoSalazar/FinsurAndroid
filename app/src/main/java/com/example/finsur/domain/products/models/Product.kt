package com.example.finsur.domain.products.models

import com.example.finsur.domain.brands.models.Brand
import com.example.finsur.domain.categories.models.Category

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val summary: String?,
    val slug: String,
    val cover: String?,
    val isFeatured: Boolean,
    val isActive: Boolean,
    val category: Category?,
    val brand: Brand?,
    val skus: List<SKU>? = null,
    val images: List<String>? = null,
    val seoTitle: String?,
    val seoDescription: String?
)

data class SKU(
    val id: Int,
    val sku: String,
    val code: String?,
    val price: String,
    val salePrice: String?,
    val isActive: Boolean,
    val weight: String?,
    val weightUnit: String?
)
