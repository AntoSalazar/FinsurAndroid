package com.example.finsur.domain.profile.usecases

import com.example.finsur.domain.common.Result
import com.example.finsur.domain.profile.repository.ProfileRepository
import javax.inject.Inject

class UpdateFiscalDataUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(
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
        return repository.updateFiscalData(
            userId, rfc, nombreRazonSocial, regimenFiscal,
            codigoPostalFiscal, esPersonaMoral, emailOp1, emailOp2,
            taxResidence, numRegIdTrib, fiscalStreet, fiscalExteriorNumber,
            fiscalInteriorNumber, fiscalNeighborhood, fiscalLocality,
            fiscalMunicipality, fiscalState, fiscalCountry
        )
    }
}
