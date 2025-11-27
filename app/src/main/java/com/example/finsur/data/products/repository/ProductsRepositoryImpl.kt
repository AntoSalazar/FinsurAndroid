package com.example.finsur.data.products.repository

import com.example.finsur.data.brands.models.BrandDto
import com.example.finsur.data.categories.models.CategoryDto
import com.example.finsur.data.products.models.ProductDto
import com.example.finsur.data.products.models.SkuDto
import com.example.finsur.data.products.remote.ProductsApiService
import com.example.finsur.domain.brands.models.Brand
import com.example.finsur.domain.categories.models.Category
import com.example.finsur.domain.common.Result
import com.example.finsur.domain.products.models.Product
import com.example.finsur.domain.products.models.SKU
import com.example.finsur.domain.products.repository.PaginatedProducts
import com.example.finsur.domain.products.repository.ProductsRepository
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor(
    private val apiService: ProductsApiService
) : ProductsRepository {

    override suspend fun getFeaturedProducts(limit: Int): Result<List<Product>> {
        return try {
            val response = apiService.getFeaturedProducts(limit)
            if (response.isSuccessful) {
                val products = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.Success(products)
            } else {
                Result.Error(
                    Exception("Failed to get featured products: ${response.code()}"),
                    "Error al cargar productos destacados"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión al cargar productos")
        }
    }

    override suspend fun searchProducts(query: String, page: Int, limit: Int): Result<List<Product>> {
        return try {
            val response = apiService.searchProducts(query, page, limit)
            if (response.isSuccessful) {
                val products = response.body()?.products?.map { it.toDomain() } ?: emptyList()
                Result.Success(products)
            } else {
                Result.Error(
                    Exception("Failed to search products: ${response.code()}"),
                    "Error al buscar productos"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión al buscar productos")
        }
    }

    override suspend fun getProducts(page: Int, limit: Int): Result<PaginatedProducts> {
        return try {
            val response = apiService.getProducts(page, limit)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val paginatedProducts = PaginatedProducts(
                        products = body.products.map { it.toDomain() },
                        total = body.total,
                        page = body.page,
                        limit = body.limit,
                        pages = body.pages
                    )
                    Result.Success(paginatedProducts)
                } else {
                    Result.Error(
                        Exception("No data received"),
                        "No se recibieron datos"
                    )
                }
            } else {
                Result.Error(
                    Exception("Failed to get products: ${response.code()}"),
                    "Error al cargar productos"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión al cargar productos")
        }
    }

    override suspend fun getProductById(productId: Int): Result<Product> {
        return try {
            val response = apiService.getProductById(productId)
            if (response.isSuccessful) {
                val product = response.body()?.toDomain()
                if (product != null) {
                    Result.Success(product)
                } else {
                    Result.Error(
                        Exception("Product not found"),
                        "Producto no encontrado"
                    )
                }
            } else {
                Result.Error(
                    Exception("Failed to get product: ${response.code()}"),
                    "Error al cargar producto"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión al cargar producto")
        }
    }

    private fun ProductDto.toDomain() = Product(
        id = id,
        name = name,
        description = description,
        summary = summary,
        slug = slug,
        cover = cover,
        isFeatured = isFeatured,
        isActive = isActive,
        category = category?.toDomain(),
        brand = brand?.toDomain(),
        skus = skus?.map { it.toDomain() },
        images = images,
        seoTitle = seoTitle,
        seoDescription = seoDescription
    )

    private fun CategoryDto.toDomain() = Category(
        id = id,
        name = name,
        description = description,
        imageUrl = imageUrl,
        slug = slug,
        level = level
    )

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

    private fun SkuDto.toDomain() = SKU(
        id = id,
        sku = sku,
        code = code,
        price = price,
        salePrice = salePrice,
        isActive = isActive,
        weight = weight,
        weightUnit = weightUnit
    )
}
