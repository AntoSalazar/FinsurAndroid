package com.example.finsur.data.categories.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: Int,
    val name: String,
    val description: String,
    @SerialName("image_url")
    val imageUrl: String? = null,
    val slug: String,
    val level: Int
)
