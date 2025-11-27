package com.example.finsur.data.brands.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BrandDto(
    val id: Int,
    val name: String,
    val slug: String,
    @SerialName("logo_url")
    val logoUrl: String? = null,
    @SerialName("website_url")
    val websiteUrl: String? = null,
    val description: String,
    @SerialName("is_featured")
    val isFeatured: Boolean,
    @SerialName("is_active")
    val isActive: Boolean
)
