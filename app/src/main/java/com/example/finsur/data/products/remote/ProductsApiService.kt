package com.example.finsur.data.products.remote

import com.example.finsur.data.products.models.PaginatedProductsResponse
import com.example.finsur.data.products.models.ProductDto
import com.example.finsur.data.products.models.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductsApiService {
    @GET("products/featured")
    suspend fun getFeaturedProducts(
        @Query("limit") limit: Int = 8
    ): Response<List<ProductDto>>

    @GET("products/find")
    suspend fun searchProducts(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<SearchResponse>

    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<PaginatedProductsResponse>

    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") productId: Int
    ): Response<ProductDto>

    @GET("products/category/{categoryId}")
    suspend fun getProductsByCategory(
        @Path("categoryId") categoryId: Int
    ): Response<List<ProductDto>>
}
