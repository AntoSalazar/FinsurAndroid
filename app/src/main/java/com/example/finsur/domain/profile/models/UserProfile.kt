package com.example.finsur.domain.profile.models

data class UserProfile(
    val id: String,
    val firstName: String,
    val lastName: String,
    val username: String?,  // Make nullable to match DTO
    val email: String,
    val phoneNumber: String?,
    val profilePictureUrl: String?,
    val fiscalData: FiscalData?
)

data class FiscalData(
    val rfc: String?,
    val nombreRazonSocial: String?,
    val regimenFiscal: String?,
    val codigoPostalFiscal: String?,
    val esPersonaMoral: Boolean,
    val emailOp1: String?,
    val emailOp2: String?,
    val taxResidence: String?,
    val numRegIdTrib: String?,
    val fiscalStreet: String?,
    val fiscalExteriorNumber: String?,
    val fiscalInteriorNumber: String?,
    val fiscalNeighborhood: String?,
    val fiscalLocality: String?,
    val fiscalMunicipality: String?,
    val fiscalState: String?,
    val fiscalCountry: String?
)
