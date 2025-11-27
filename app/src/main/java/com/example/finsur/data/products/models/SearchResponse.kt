package com.example.finsur.data.products.models

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val products: List<ProductDto>
)
