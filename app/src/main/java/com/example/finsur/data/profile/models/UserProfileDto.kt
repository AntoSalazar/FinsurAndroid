package com.example.finsur.data.profile.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val id: String,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    val username: String? = null,  // Make nullable - backend might return null
    val email: String,
    @SerialName("phone_number")
    val phoneNumber: String? = null,
    @SerialName("profile_picture_url")
    val profilePictureUrl: String? = null,
    val rfc: String? = null,
    @SerialName("nombre_razon_social")
    val nombreRazonSocial: String? = null,
    @SerialName("regimen_fiscal")
    val regimenFiscal: String? = null,
    @SerialName("codigo_postal_fiscal")
    val codigoPostalFiscal: String? = null,
    @SerialName("es_persona_moral")
    val esPersonaMoral: Boolean = false,
    @SerialName("email_op1")
    val emailOp1: String? = null,
    @SerialName("email_op2")
    val emailOp2: String? = null,
    @SerialName("tax_residence")
    val taxResidence: String? = null,
    @SerialName("num_reg_id_trib")
    val numRegIdTrib: String? = null,
    @SerialName("fiscal_street")
    val fiscalStreet: String? = null,
    @SerialName("fiscal_exterior_number")
    val fiscalExteriorNumber: String? = null,
    @SerialName("fiscal_interior_number")
    val fiscalInteriorNumber: String? = null,
    @SerialName("fiscal_neighborhood")
    val fiscalNeighborhood: String? = null,
    @SerialName("fiscal_locality")
    val fiscalLocality: String? = null,
    @SerialName("fiscal_municipality")
    val fiscalMunicipality: String? = null,
    @SerialName("fiscal_state")
    val fiscalState: String? = null,
    @SerialName("fiscal_country")
    val fiscalCountry: String? = null
)

@Serializable
data class UpdatePersonalInfoRequest(
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("phone_number")
    val phoneNumber: String?
)

@Serializable
data class UpdateFiscalDataRequest(
    val rfc: String?,
    @SerialName("nombre_razon_social")
    val nombreRazonSocial: String?,
    @SerialName("regimen_fiscal")
    val regimenFiscal: String?,
    @SerialName("codigo_postal_fiscal")
    val codigoPostalFiscal: String?,
    @SerialName("es_persona_moral")
    val esPersonaMoral: Boolean,
    @SerialName("email_op1")
    val emailOp1: String?,
    @SerialName("email_op2")
    val emailOp2: String?,
    @SerialName("tax_residence")
    val taxResidence: String?,
    @SerialName("num_reg_id_trib")
    val numRegIdTrib: String?,
    @SerialName("fiscal_street")
    val fiscalStreet: String?,
    @SerialName("fiscal_exterior_number")
    val fiscalExteriorNumber: String?,
    @SerialName("fiscal_interior_number")
    val fiscalInteriorNumber: String?,
    @SerialName("fiscal_neighborhood")
    val fiscalNeighborhood: String?,
    @SerialName("fiscal_locality")
    val fiscalLocality: String?,
    @SerialName("fiscal_municipality")
    val fiscalMunicipality: String?,
    @SerialName("fiscal_state")
    val fiscalState: String?,
    @SerialName("fiscal_country")
    val fiscalCountry: String?
)

@Serializable
data class ChangePasswordRequest(
    @SerialName("current_password")
    val currentPassword: String,
    @SerialName("new_password")
    val newPassword: String
)
