package com.example.finsur.data.profile.repository

import com.example.finsur.data.profile.models.AddressDto
import com.example.finsur.data.profile.models.ChangePasswordRequest
import com.example.finsur.data.profile.models.CreateAddressRequest
import com.example.finsur.data.profile.models.FiscalRegimeDto
import com.example.finsur.data.profile.models.UpdateAddressRequest
import com.example.finsur.data.profile.models.UpdateFiscalDataRequest
import com.example.finsur.data.profile.models.UpdatePersonalInfoRequest
import com.example.finsur.data.profile.models.UserProfileDto
import com.example.finsur.data.profile.remote.ProfileApiService
import com.example.finsur.domain.common.Result
import com.example.finsur.domain.profile.models.Address
import com.example.finsur.domain.profile.models.FiscalData
import com.example.finsur.domain.profile.models.FiscalRegime
import com.example.finsur.domain.profile.models.UserProfile
import com.example.finsur.domain.profile.repository.ProfileRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val apiService: ProfileApiService
) : ProfileRepository {

    override suspend fun getUserProfile(): Result<UserProfile> {
        return try {
            val response = apiService.getUserProfile()
            if (response.isSuccessful) {
                val profileDto = response.body()
                if (profileDto != null) {
                    Result.Success(profileDto.toDomain())
                } else {
                    Result.Error(Exception("No profile data"), "No se pudo obtener el perfil")
                }
            } else {
                // Better error messages
                val errorMsg = when (response.code()) {
                    401 -> "No autenticado. Por favor inicia sesión nuevamente"
                    403 -> "No tienes permisos para acceder a esta información"
                    404 -> "Perfil no encontrado"
                    500 -> "Error del servidor"
                    else -> "Error al obtener el perfil (${response.code()})"
                }
                Result.Error(
                    Exception("HTTP ${response.code()}: ${response.message()}"),
                    errorMsg
                )
            }
        } catch (e: Exception) {
            // Better error logging
            Result.Error(
                e,
                "Error de conexión: ${e.message ?: "Verifica tu conexión a internet"}"
            )
        }
    }

    override suspend fun updatePersonalInfo(
        userId: String,
        firstName: String,
        lastName: String,
        phoneNumber: String?
    ): Result<UserProfile> {
        return try {
            val request = UpdatePersonalInfoRequest(firstName, lastName, phoneNumber)
            val response = apiService.updatePersonalInfo(userId, request)
            if (response.isSuccessful) {
                val profileDto = response.body()
                if (profileDto != null) {
                    Result.Success(profileDto.toDomain())
                } else {
                    Result.Error(Exception("No data"), "Error al actualizar")
                }
            } else {
                Result.Error(
                    Exception("Failed to update: ${response.code()}"),
                    "Error al actualizar información personal"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión")
        }
    }

    override suspend fun updateFiscalData(
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
    ): Result<Unit> {
        return try {
            val request = UpdateFiscalDataRequest(
                rfc = rfc,
                nombreRazonSocial = nombreRazonSocial,
                regimenFiscal = regimenFiscal,
                codigoPostalFiscal = codigoPostalFiscal,
                esPersonaMoral = esPersonaMoral,
                emailOp1 = emailOp1,
                emailOp2 = emailOp2,
                taxResidence = taxResidence,
                numRegIdTrib = numRegIdTrib,
                fiscalStreet = fiscalStreet,
                fiscalExteriorNumber = fiscalExteriorNumber,
                fiscalInteriorNumber = fiscalInteriorNumber,
                fiscalNeighborhood = fiscalNeighborhood,
                fiscalLocality = fiscalLocality,
                fiscalMunicipality = fiscalMunicipality,
                fiscalState = fiscalState,
                fiscalCountry = fiscalCountry
            )
            val response = apiService.updateFiscalData(userId, request)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(
                    Exception("Failed to update fiscal data: ${response.code()}"),
                    "Error al actualizar datos fiscales"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión")
        }
    }

    override suspend fun changePassword(
        userId: String,
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        return try {
            val request = ChangePasswordRequest(currentPassword, newPassword)
            val response = apiService.changePassword(userId, request)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(
                    Exception("Failed to change password: ${response.code()}"),
                    "Error al cambiar contraseña"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión")
        }
    }

    override suspend fun uploadProfilePicture(
        userId: String,
        imageFile: File
    ): Result<UserProfile> {
        return try {
            val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData(
                "image",
                imageFile.name,
                requestBody
            )
            val response = apiService.uploadProfilePicture(userId, imagePart)
            if (response.isSuccessful) {
                val profileDto = response.body()
                if (profileDto != null) {
                    Result.Success(profileDto.toDomain())
                } else {
                    Result.Error(Exception("No data"), "Error al subir imagen")
                }
            } else {
                Result.Error(
                    Exception("Failed to upload picture: ${response.code()}"),
                    "Error al subir foto de perfil"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión")
        }
    }

    override suspend fun getFiscalRegimes(): Result<List<FiscalRegime>> {
        return try {
            val response = apiService.getFiscalRegimes()
            if (response.isSuccessful) {
                val regimes = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.Success(regimes)
            } else {
                Result.Error(
                    Exception("Failed to get regimes: ${response.code()}"),
                    "Error al obtener regímenes fiscales"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión")
        }
    }

    override suspend fun getAddresses(): Result<List<Address>> {
        return try {
            val response = apiService.getAddresses()
            if (response.isSuccessful) {
                val addresses = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.Success(addresses)
            } else {
                Result.Error(
                    Exception("Failed to get addresses: ${response.code()}"),
                    "Error al obtener direcciones"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión")
        }
    }

    override suspend fun createAddress(
        title: String,
        addressLine1: String,
        addressLine2: String?,
        country: String,
        city: String,
        state: String,
        postalCode: String,
        landmark: String?,
        phoneNumber: String
    ): Result<Address> {
        return try {
            val request = CreateAddressRequest(
                title, addressLine1, addressLine2, country,
                city, state, postalCode, landmark, phoneNumber
            )
            val response = apiService.createAddress(request)
            if (response.isSuccessful) {
                val address = response.body()?.toDomain()
                if (address != null) {
                    Result.Success(address)
                } else {
                    Result.Error(Exception("No data"), "Error al crear dirección")
                }
            } else {
                Result.Error(
                    Exception("Failed to create address: ${response.code()}"),
                    "Error al crear dirección"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión")
        }
    }

    override suspend fun updateAddress(
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
    ): Result<Address> {
        return try {
            val request = UpdateAddressRequest(
                title, addressLine1, addressLine2, country,
                city, state, postalCode, landmark, phoneNumber
            )
            val response = apiService.updateAddress(addressId, request)
            if (response.isSuccessful) {
                val address = response.body()?.toDomain()
                if (address != null) {
                    Result.Success(address)
                } else {
                    Result.Error(Exception("No data"), "Error al actualizar dirección")
                }
            } else {
                Result.Error(
                    Exception("Failed to update address: ${response.code()}"),
                    "Error al actualizar dirección"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión")
        }
    }

    override suspend fun setDefaultAddress(addressId: Int): Result<Unit> {
        return try {
            val response = apiService.setDefaultAddress(addressId)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(
                    Exception("Failed to set default: ${response.code()}"),
                    "Error al establecer dirección predeterminada"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión")
        }
    }

    override suspend fun deleteAddress(addressId: Int): Result<Unit> {
        return try {
            val response = apiService.deleteAddress(addressId)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(
                    Exception("Failed to delete address: ${response.code()}"),
                    "Error al eliminar dirección"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión")
        }
    }

    // Extension functions to convert DTOs to Domain models
    private fun UserProfileDto.toDomain() = UserProfile(
        id = id,
        firstName = firstName,
        lastName = lastName,
        username = username ?: email, // Fallback to email if username is null
        email = email,
        phoneNumber = phoneNumber,
        profilePictureUrl = profilePictureUrl,
        fiscalData = FiscalData(
            rfc = rfc,
            nombreRazonSocial = nombreRazonSocial,
            regimenFiscal = regimenFiscal,
            codigoPostalFiscal = codigoPostalFiscal,
            esPersonaMoral = esPersonaMoral,
            emailOp1 = emailOp1,
            emailOp2 = emailOp2,
            taxResidence = taxResidence,
            numRegIdTrib = numRegIdTrib,
            fiscalStreet = fiscalStreet,
            fiscalExteriorNumber = fiscalExteriorNumber,
            fiscalInteriorNumber = fiscalInteriorNumber,
            fiscalNeighborhood = fiscalNeighborhood,
            fiscalLocality = fiscalLocality,
            fiscalMunicipality = fiscalMunicipality,
            fiscalState = fiscalState,
            fiscalCountry = fiscalCountry
        )
    )

    private fun AddressDto.toDomain() = Address(
        id = id,
        userId = userId,
        title = title,
        addressLine1 = addressLine1,
        addressLine2 = addressLine2,
        country = country,
        city = city,
        state = state,
        postalCode = postalCode,
        landmark = landmark,
        phoneNumber = phoneNumber,
        isDefault = isDefault
    )

    private fun FiscalRegimeDto.toDomain() = FiscalRegime(
        id = id,
        descripcion = descripcion
    )
}
