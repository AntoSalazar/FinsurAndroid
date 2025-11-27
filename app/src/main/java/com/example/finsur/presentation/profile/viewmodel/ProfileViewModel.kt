package com.example.finsur.presentation.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finsur.domain.auth.usecases.LogoutUseCase
import com.example.finsur.domain.common.Result
import com.example.finsur.domain.profile.usecases.ChangePasswordUseCase
import com.example.finsur.domain.profile.usecases.CreateAddressUseCase
import com.example.finsur.domain.profile.usecases.DeleteAddressUseCase
import com.example.finsur.domain.profile.usecases.GetAddressesUseCase
import com.example.finsur.domain.profile.usecases.GetFiscalRegimesUseCase
import com.example.finsur.domain.profile.usecases.GetUserProfileUseCase
import com.example.finsur.domain.profile.usecases.SetDefaultAddressUseCase
import com.example.finsur.domain.profile.usecases.UpdateAddressUseCase
import com.example.finsur.domain.profile.usecases.UpdateFiscalDataUseCase
import com.example.finsur.domain.profile.usecases.UpdatePersonalInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updatePersonalInfoUseCase: UpdatePersonalInfoUseCase,
    private val updateFiscalDataUseCase: UpdateFiscalDataUseCase,
    private val getFiscalRegimesUseCase: GetFiscalRegimesUseCase,
    private val getAddressesUseCase: GetAddressesUseCase,
    private val createAddressUseCase: CreateAddressUseCase,
    private val updateAddressUseCase: UpdateAddressUseCase,
    private val setDefaultAddressUseCase: SetDefaultAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    // Profile state
    private val _profileState = MutableStateFlow<ProfileUiState>(ProfileUiState.Initial)
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    // Fiscal regimes state
    private val _fiscalRegimesState = MutableStateFlow<FiscalRegimesState>(FiscalRegimesState.Initial)
    val fiscalRegimesState: StateFlow<FiscalRegimesState> = _fiscalRegimesState.asStateFlow()

    // Addresses state
    private val _addressesState = MutableStateFlow<AddressesState>(AddressesState.Initial)
    val addressesState: StateFlow<AddressesState> = _addressesState.asStateFlow()

    // Update state (for showing loading/success/error during updates)
    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    // Form states
    private val _personalInfoForm = MutableStateFlow(PersonalInfoFormState())
    val personalInfoForm: StateFlow<PersonalInfoFormState> = _personalInfoForm.asStateFlow()

    private val _fiscalDataForm = MutableStateFlow(FiscalDataFormState())
    val fiscalDataForm: StateFlow<FiscalDataFormState> = _fiscalDataForm.asStateFlow()

    private val _passwordForm = MutableStateFlow(PasswordFormState())
    val passwordForm: StateFlow<PasswordFormState> = _passwordForm.asStateFlow()

    init {
        loadProfile()
        loadFiscalRegimes()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileUiState.Loading
            when (val result = getUserProfileUseCase()) {
                is Result.Success -> {
                    _profileState.value = ProfileUiState.Success(result.data)
                    // Populate form with current data
                    _personalInfoForm.value = PersonalInfoFormState(
                        firstName = result.data.firstName,
                        lastName = result.data.lastName,
                        phoneNumber = result.data.phoneNumber ?: ""
                    )
                    result.data.fiscalData?.let { fiscalData ->
                        _fiscalDataForm.value = FiscalDataFormState(
                            rfc = fiscalData.rfc ?: "",
                            nombreRazonSocial = fiscalData.nombreRazonSocial ?: "",
                            regimenFiscal = fiscalData.regimenFiscal ?: "",
                            codigoPostalFiscal = fiscalData.codigoPostalFiscal ?: "",
                            esPersonaMoral = fiscalData.esPersonaMoral,
                            emailOp1 = fiscalData.emailOp1 ?: "",
                            emailOp2 = fiscalData.emailOp2 ?: "",
                            taxResidence = fiscalData.taxResidence ?: "",
                            numRegIdTrib = fiscalData.numRegIdTrib ?: "",
                            fiscalStreet = fiscalData.fiscalStreet ?: "",
                            fiscalExteriorNumber = fiscalData.fiscalExteriorNumber ?: "",
                            fiscalInteriorNumber = fiscalData.fiscalInteriorNumber ?: "",
                            fiscalNeighborhood = fiscalData.fiscalNeighborhood ?: "",
                            fiscalLocality = fiscalData.fiscalLocality ?: "",
                            fiscalMunicipality = fiscalData.fiscalMunicipality ?: "",
                            fiscalState = fiscalData.fiscalState ?: "",
                            fiscalCountry = fiscalData.fiscalCountry ?: ""
                        )
                    }
                }
                is Result.Error -> {
                    _profileState.value = ProfileUiState.Error(
                        result.message ?: "Error al cargar perfil"
                    )
                }
            }
        }
    }

    fun loadFiscalRegimes() {
        viewModelScope.launch {
            _fiscalRegimesState.value = FiscalRegimesState.Loading
            when (val result = getFiscalRegimesUseCase()) {
                is Result.Success -> {
                    _fiscalRegimesState.value = FiscalRegimesState.Success(result.data)
                }
                is Result.Error -> {
                    _fiscalRegimesState.value = FiscalRegimesState.Error(
                        result.message ?: "Error al cargar regímenes fiscales"
                    )
                }
            }
        }
    }

    fun loadAddresses() {
        viewModelScope.launch {
            _addressesState.value = AddressesState.Loading
            when (val result = getAddressesUseCase()) {
                is Result.Success -> {
                    _addressesState.value = AddressesState.Success(result.data)
                }
                is Result.Error -> {
                    _addressesState.value = AddressesState.Error(
                        result.message ?: "Error al cargar direcciones"
                    )
                }
            }
        }
    }

    // Personal Info Form Updates
    fun updatePersonalInfoField(field: String, value: String) {
        _personalInfoForm.value = when (field) {
            "firstName" -> _personalInfoForm.value.copy(firstName = value)
            "lastName" -> _personalInfoForm.value.copy(lastName = value)
            "phoneNumber" -> _personalInfoForm.value.copy(phoneNumber = value)
            else -> _personalInfoForm.value
        }
    }

    fun savePersonalInfo() {
        val currentProfile = (_profileState.value as? ProfileUiState.Success)?.profile ?: return
        val form = _personalInfoForm.value

        viewModelScope.launch {
            _updateState.value = UpdateState.Loading
            when (val result = updatePersonalInfoUseCase(
                userId = currentProfile.id,
                firstName = form.firstName,
                lastName = form.lastName,
                phoneNumber = form.phoneNumber.ifBlank { null }
            )) {
                is Result.Success -> {
                    _profileState.value = ProfileUiState.Success(result.data)
                    _updateState.value = UpdateState.Success("Información actualizada correctamente")
                }
                is Result.Error -> {
                    _updateState.value = UpdateState.Error(
                        result.message ?: "Error al actualizar información"
                    )
                }
            }
        }
    }

    // Fiscal Data Form Updates
    fun updateFiscalDataField(field: String, value: String) {
        _fiscalDataForm.value = when (field) {
            "rfc" -> _fiscalDataForm.value.copy(rfc = value)
            "nombreRazonSocial" -> _fiscalDataForm.value.copy(nombreRazonSocial = value)
            "regimenFiscal" -> _fiscalDataForm.value.copy(regimenFiscal = value)
            "codigoPostalFiscal" -> _fiscalDataForm.value.copy(codigoPostalFiscal = value)
            "emailOp1" -> _fiscalDataForm.value.copy(emailOp1 = value)
            "emailOp2" -> _fiscalDataForm.value.copy(emailOp2 = value)
            "taxResidence" -> _fiscalDataForm.value.copy(taxResidence = value)
            "numRegIdTrib" -> _fiscalDataForm.value.copy(numRegIdTrib = value)
            "fiscalStreet" -> _fiscalDataForm.value.copy(fiscalStreet = value)
            "fiscalExteriorNumber" -> _fiscalDataForm.value.copy(fiscalExteriorNumber = value)
            "fiscalInteriorNumber" -> _fiscalDataForm.value.copy(fiscalInteriorNumber = value)
            "fiscalNeighborhood" -> _fiscalDataForm.value.copy(fiscalNeighborhood = value)
            "fiscalLocality" -> _fiscalDataForm.value.copy(fiscalLocality = value)
            "fiscalMunicipality" -> _fiscalDataForm.value.copy(fiscalMunicipality = value)
            "fiscalState" -> _fiscalDataForm.value.copy(fiscalState = value)
            "fiscalCountry" -> _fiscalDataForm.value.copy(fiscalCountry = value)
            else -> _fiscalDataForm.value
        }
    }

    fun updateEsPersonaMoral(value: Boolean) {
        _fiscalDataForm.value = _fiscalDataForm.value.copy(esPersonaMoral = value)
    }

    fun saveFiscalData() {
        val currentProfile = (_profileState.value as? ProfileUiState.Success)?.profile ?: return
        val form = _fiscalDataForm.value

        viewModelScope.launch {
            _updateState.value = UpdateState.Loading
            when (val result = updateFiscalDataUseCase(
                userId = currentProfile.id,
                rfc = form.rfc.ifBlank { null },
                nombreRazonSocial = form.nombreRazonSocial.ifBlank { null },
                regimenFiscal = form.regimenFiscal.ifBlank { null },
                codigoPostalFiscal = form.codigoPostalFiscal.ifBlank { null },
                esPersonaMoral = form.esPersonaMoral,
                emailOp1 = form.emailOp1.ifBlank { null },
                emailOp2 = form.emailOp2.ifBlank { null },
                taxResidence = form.taxResidence.ifBlank { null },
                numRegIdTrib = form.numRegIdTrib.ifBlank { null },
                fiscalStreet = form.fiscalStreet.ifBlank { null },
                fiscalExteriorNumber = form.fiscalExteriorNumber.ifBlank { null },
                fiscalInteriorNumber = form.fiscalInteriorNumber.ifBlank { null },
                fiscalNeighborhood = form.fiscalNeighborhood.ifBlank { null },
                fiscalLocality = form.fiscalLocality.ifBlank { null },
                fiscalMunicipality = form.fiscalMunicipality.ifBlank { null },
                fiscalState = form.fiscalState.ifBlank { null },
                fiscalCountry = form.fiscalCountry.ifBlank { null }
            )) {
                is Result.Success -> {
                    loadProfile() // Reload to get updated data
                    _updateState.value = UpdateState.Success("Datos fiscales actualizados correctamente")
                }
                is Result.Error -> {
                    _updateState.value = UpdateState.Error(
                        result.message ?: "Error al actualizar datos fiscales"
                    )
                }
            }
        }
    }

    // Password Change
    fun updatePasswordField(field: String, value: String) {
        _passwordForm.value = when (field) {
            "currentPassword" -> _passwordForm.value.copy(currentPassword = value)
            "newPassword" -> _passwordForm.value.copy(newPassword = value)
            "confirmPassword" -> _passwordForm.value.copy(confirmPassword = value)
            else -> _passwordForm.value
        }
    }

    fun changePassword() {
        val currentProfile = (_profileState.value as? ProfileUiState.Success)?.profile ?: return
        val form = _passwordForm.value

        // Validate passwords match
        if (form.newPassword != form.confirmPassword) {
            _updateState.value = UpdateState.Error("Las contraseñas no coinciden")
            return
        }

        viewModelScope.launch {
            _updateState.value = UpdateState.Loading
            when (val result = changePasswordUseCase(
                userId = currentProfile.id,
                currentPassword = form.currentPassword,
                newPassword = form.newPassword
            )) {
                is Result.Success -> {
                    _passwordForm.value = PasswordFormState() // Clear form
                    _updateState.value = UpdateState.Success("Contraseña actualizada correctamente")
                }
                is Result.Error -> {
                    _updateState.value = UpdateState.Error(
                        result.message ?: "Error al cambiar contraseña"
                    )
                }
            }
        }
    }

    // Address operations
    fun createAddress(form: AddressFormState) {
        viewModelScope.launch {
            _updateState.value = UpdateState.Loading
            when (val result = createAddressUseCase(
                title = form.title,
                addressLine1 = form.addressLine1,
                addressLine2 = form.addressLine2.ifBlank { null },
                country = form.country,
                city = form.city,
                state = form.state,
                postalCode = form.postalCode,
                landmark = form.landmark.ifBlank { null },
                phoneNumber = form.phoneNumber
            )) {
                is Result.Success -> {
                    loadAddresses() // Reload addresses
                    _updateState.value = UpdateState.Success("Dirección creada correctamente")
                }
                is Result.Error -> {
                    _updateState.value = UpdateState.Error(
                        result.message ?: "Error al crear dirección"
                    )
                }
            }
        }
    }

    fun updateAddress(addressId: Int, form: AddressFormState) {
        viewModelScope.launch {
            _updateState.value = UpdateState.Loading
            when (val result = updateAddressUseCase(
                addressId = addressId,
                title = form.title,
                addressLine1 = form.addressLine1,
                addressLine2 = form.addressLine2.ifBlank { null },
                country = form.country,
                city = form.city,
                state = form.state,
                postalCode = form.postalCode,
                landmark = form.landmark.ifBlank { null },
                phoneNumber = form.phoneNumber
            )) {
                is Result.Success -> {
                    loadAddresses() // Reload addresses
                    _updateState.value = UpdateState.Success("Dirección actualizada correctamente")
                }
                is Result.Error -> {
                    _updateState.value = UpdateState.Error(
                        result.message ?: "Error al actualizar dirección"
                    )
                }
            }
        }
    }

    fun setDefaultAddress(addressId: Int) {
        viewModelScope.launch {
            _updateState.value = UpdateState.Loading
            when (val result = setDefaultAddressUseCase(addressId)) {
                is Result.Success -> {
                    loadAddresses() // Reload addresses
                    _updateState.value = UpdateState.Success("Dirección predeterminada actualizada")
                }
                is Result.Error -> {
                    _updateState.value = UpdateState.Error(
                        result.message ?: "Error al establecer dirección predeterminada"
                    )
                }
            }
        }
    }

    fun deleteAddress(addressId: Int) {
        viewModelScope.launch {
            _updateState.value = UpdateState.Loading
            when (val result = deleteAddressUseCase(addressId)) {
                is Result.Success -> {
                    loadAddresses() // Reload addresses
                    _updateState.value = UpdateState.Success("Dirección eliminada correctamente")
                }
                is Result.Error -> {
                    _updateState.value = UpdateState.Error(
                        result.message ?: "Error al eliminar dirección"
                    )
                }
            }
        }
    }

    fun clearUpdateState() {
        _updateState.value = UpdateState.Idle
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            onSuccess()
        }
    }
}
