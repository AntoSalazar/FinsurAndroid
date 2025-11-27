package com.example.finsur.data.profile.models

import kotlinx.serialization.Serializable

@Serializable
data class FiscalRegimeDto(
    val id: Int,
    val descripcion: String
)
