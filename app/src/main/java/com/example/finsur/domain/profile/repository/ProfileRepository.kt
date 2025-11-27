package com.example.finsur.domain.profile.repository

import com.example.finsur.domain.common.Result
import com.example.finsur.domain.profile.models.Address
import com.example.finsur.domain.profile.models.FiscalRegime
import com.example.finsur.domain.profile.models.UserProfile
import java.io.File

interface ProfileRepository {
    // User profile operations
    suspend fun getUserProfile(): Result<UserProfile>

    suspend fun updatePersonalInfo(
        userId: String,
        firstName: String,
        lastName: String,
        phoneNumber: String?
    ): Result<UserProfile>

    suspend fun updateFiscalData(
        userId: String,
        rfc: String?,
        nombreRazonSocial: String?,
        regimenFiscal: String?,
        codigoPostalFiscal: String?,
        esPersonaMoral: Boolean,
        emailOp1: String?,
        emailOp2: String?,
        taxResidence: String?,
        numRegIdTrib: String?,
        fiscalStreet: String?,
        fiscalExteriorNumber: String?,
        fiscalInteriorNumber: String?,
        fiscalNeighborhood: String?,
        fiscalLocality: String?,
        fiscalMunicipality: String?,
        fiscalState: String?,
        fiscalCountry: String?
    ): Result<Unit>

    suspend fun changePassword(
        userId: String,
        currentPassword: String,
        newPassword: String
    ): Result<Unit>

    suspend fun uploadProfilePicture(
        userId: String,
        imageFile: File
    ): Result<UserProfile>

    suspend fun getFiscalRegimes(): Result<List<FiscalRegime>>

    // Address operations
    suspend fun getAddresses(): Result<List<Address>>

    suspend fun createAddress(
        title: String,
        addressLine1: String,
        addressLine2: String?,
        country: String,
        city: String,
        state: String,
        postalCode: String,
        landmark: String?,
        phoneNumber: String
    ): Result<Address>

    suspend fun updateAddress(
        addressId: Int,
        title: String,
        addressLine1: String,
        addressLine2: String?,
        country: String,
        city: String,
        state: String,
        postalCode: String,
        landmark: String?,
        phoneNumber: String
    ): Result<Address>

    suspend fun setDefaultAddress(addressId: Int): Result<Unit>

    suspend fun deleteAddress(addressId: Int): Result<Unit>
}
