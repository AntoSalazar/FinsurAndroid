package com.example.finsur.presentation.profile.viewmodel

import com.example.finsur.domain.profile.models.Address
import com.example.finsur.domain.profile.models.FiscalRegime
import com.example.finsur.domain.profile.models.UserProfile

sealed class ProfileUiState {
    data object Initial : ProfileUiState()
    data object Loading : ProfileUiState()
    data class Success(val profile: UserProfile) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

sealed class FiscalRegimesState {
    data object Initial : FiscalRegimesState()
    data object Loading : FiscalRegimesState()
    data class Success(val regimes: List<FiscalRegime>) : FiscalRegimesState()
    data class Error(val message: String) : FiscalRegimesState()
}

sealed class AddressesState {
    data object Initial : AddressesState()
    data object Loading : AddressesState()
    data class Success(val addresses: List<Address>) : AddressesState()
    data class Error(val message: String) : AddressesState()
}

sealed class UpdateState {
    data object Idle : UpdateState()
    data object Loading : UpdateState()
    data class Success(val message: String) : UpdateState()
    data class Error(val message: String) : UpdateState()
}

data class PersonalInfoFormState(
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = ""
)

data class FiscalDataFormState(
    val rfc: String = "",
    val nombreRazonSocial: String = "",
    val regimenFiscal: String = "",
    val codigoPostalFiscal: String = "",
    val esPersonaMoral: Boolean = false,
    val emailOp1: String = "",
    val emailOp2: String = "",
    val taxResidence: String = "",
    val numRegIdTrib: String = "",
    val fiscalStreet: String = "",
    val fiscalExteriorNumber: String = "",
    val fiscalInteriorNumber: String = "",
    val fiscalNeighborhood: String = "",
    val fiscalLocality: String = "",
    val fiscalMunicipality: String = "",
    val fiscalState: String = "",
    val fiscalCountry: String = ""
)

data class AddressFormState(
    val title: String = "",
    val addressLine1: String = "",
    val addressLine2: String = "",
    val country: String = "México",
    val city: String = "",
    val state: String = "",
    val postalCode: String = "",
    val landmark: String = "",
    val phoneNumber: String = ""
)

data class PasswordFormState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = ""
)
