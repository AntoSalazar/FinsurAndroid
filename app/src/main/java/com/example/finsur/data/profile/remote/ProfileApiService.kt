package com.example.finsur.data.profile.remote

import com.example.finsur.data.profile.models.AddressDto
import com.example.finsur.data.profile.models.ChangePasswordRequest
import com.example.finsur.data.profile.models.CreateAddressRequest
import com.example.finsur.data.profile.models.FiscalRegimeDto
import com.example.finsur.data.profile.models.UpdateAddressRequest
import com.example.finsur.data.profile.models.UpdateFiscalDataRequest
import com.example.finsur.data.profile.models.UpdatePersonalInfoRequest
import com.example.finsur.data.profile.models.UserProfileDto
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ProfileApiService {
    // Get user profile
    @GET("users/profile")
    suspend fun getUserProfile(): Response<UserProfileDto>

    // Update personal information
    @PUT("users/{userId}")
    suspend fun updatePersonalInfo(
        @Path("userId") userId: String,
        @Body request: UpdatePersonalInfoRequest
    ): Response<UserProfileDto>

    // Update fiscal data
    @PUT("users/{userId}/fiscal-data")
    suspend fun updateFiscalData(
        @Path("userId") userId: String,
        @Body request: UpdateFiscalDataRequest
    ): Response<ResponseBody>

    // Change password
    @PUT("users/{userId}/password")
    suspend fun changePassword(
        @Path("userId") userId: String,
        @Body request: ChangePasswordRequest
    ): Response<ResponseBody>

    // Upload profile picture
    @Multipart
    @POST("users/{userId}/profile-picture")
    suspend fun uploadProfilePicture(
        @Path("userId") userId: String,
        @Part image: MultipartBody.Part
    ): Response<UserProfileDto>

    // Get fiscal regimes
    @GET("e-invoice/regimen-fiscal")
    suspend fun getFiscalRegimes(): Response<List<FiscalRegimeDto>>

    // Address endpoints
    @GET("addresses/")
    suspend fun getAddresses(): Response<List<AddressDto>>

    @POST("addresses/")
    suspend fun createAddress(
        @Body request: CreateAddressRequest
    ): Response<AddressDto>

    @PUT("addresses/{addressId}")
    suspend fun updateAddress(
        @Path("addressId") addressId: Int,
        @Body request: UpdateAddressRequest
    ): Response<AddressDto>

    @PUT("addresses/{addressId}/default")
    suspend fun setDefaultAddress(
        @Path("addressId") addressId: Int
    ): Response<ResponseBody>

    @DELETE("addresses/{addressId}")
    suspend fun deleteAddress(
        @Path("addressId") addressId: Int
    ): Response<ResponseBody>
}
