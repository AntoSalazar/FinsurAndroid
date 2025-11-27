package com.example.finsur.domain.brands.models

data class Brand(
    val id: Int,
    val name: String,
    val slug: String,
    val logoUrl: String?,
    val websiteUrl: String?,
    val description: String,
    val isFeatured: Boolean,
    val isActive: Boolean
)
