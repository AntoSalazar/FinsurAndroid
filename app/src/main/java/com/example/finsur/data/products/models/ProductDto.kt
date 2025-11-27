package com.example.finsur.data.products.models

import com.example.finsur.data.brands.models.BrandDto
import com.example.finsur.data.categories.models.CategoryDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: Int,
    @SerialName("category_id")
    val categoryId: Int,
    @SerialName("brand_id")
    val brandId: Int,
    val name: String,
    val description: String,
    val summary: String? = null,
    val slug: String,
    val cover: String? = null,
    @SerialName("is_featured")
    val isFeatured: Boolean,
    @SerialName("is_active")
    val isActive: Boolean,
    @SerialName("seo_title")
    val seoTitle: String? = null,
    @SerialName("seo_description")
    val seoDescription: String? = null,
    val category: CategoryDto? = null,
    val brand: BrandDto? = null,
    val skus: List<SkuDto>? = null,
    val images: List<String>? = null
)

@Serializable
data class SkuDto(
    val id: Int,
    @SerialName("product_id")
    val productId: Int,
    val sku: String,
    val code: String? = null,
    val price: String,
    @SerialName("sale_price")
    val salePrice: String? = null,
    @SerialName("is_active")
    val isActive: Boolean,
    val weight: String? = null,
    @SerialName("weight_unit")
    val weightUnit: String? = null
)

@Serializable
data class PaginatedProductsResponse(
    val products: List<ProductDto>,
    val total: Int,
    val page: Int,
    val limit: Int,
    val pages: Int
)
