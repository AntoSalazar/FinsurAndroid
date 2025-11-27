package com.example.finsur.domain.categories.models

data class Category(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val slug: String,
    val level: Int
)
