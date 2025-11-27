package com.example.finsur.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.finsur.presentation.profile.viewmodel.ProfileViewModel

@Composable
fun FiscalInfoTab(
    viewModel: ProfileViewModel,
    modifier: Modifier = Modifier
) {
    val fiscalDataForm by viewModel.fiscalDataForm.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Datos Fiscales",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        // Basic Fiscal Info
        OutlinedTextField(
            value = fiscalDataForm.rfc,
            onValueChange = { viewModel.updateFiscalDataField("rfc", it) },
            label = { Text("RFC") },
            placeholder = { Text("XAXX010101000") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = fiscalDataForm.nombreRazonSocial,
            onValueChange = { viewModel.updateFiscalDataField("nombreRazonSocial", it) },
            label = { Text("Nombre/Razón Social") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = fiscalDataForm.regimenFiscal,
            onValueChange = { viewModel.updateFiscalDataField("regimenFiscal", it) },
            label = { Text("Régimen Fiscal") },
            placeholder = { Text("612") },
            modifier = Modifier.fillMaxWidth()
        )

        // Additional Fiscal Info
        OutlinedTextField(
            value = fiscalDataForm.emailOp1,
            onValueChange = { viewModel.updateFiscalDataField("emailOp1", it) },
            label = { Text("Email Opcional 1") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = fiscalDataForm.emailOp2,
            onValueChange = { viewModel.updateFiscalDataField("emailOp2", it) },
            label = { Text("Email Opcional 2") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = fiscalDataForm.taxResidence,
            onValueChange = { viewModel.updateFiscalDataField("taxResidence", it) },
            label = { Text("Residencia Fiscal") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = fiscalDataForm.numRegIdTrib,
            onValueChange = { viewModel.updateFiscalDataField("numRegIdTrib", it) },
            label = { Text("Núm. Registro ID Tributaria") },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Dirección Fiscal",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = fiscalDataForm.fiscalStreet,
            onValueChange = { viewModel.updateFiscalDataField("fiscalStreet", it) },
            label = { Text("Calle") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = fiscalDataForm.fiscalExteriorNumber,
                onValueChange = { viewModel.updateFiscalDataField("fiscalExteriorNumber", it) },
                label = { Text("Núm. Exterior") },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = fiscalDataForm.fiscalInteriorNumber,
                onValueChange = { viewModel.updateFiscalDataField("fiscalInteriorNumber", it) },
                label = { Text("Núm. Interior") },
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = fiscalDataForm.fiscalNeighborhood,
            onValueChange = { viewModel.updateFiscalDataField("fiscalNeighborhood", it) },
            label = { Text("Colonia") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = fiscalDataForm.fiscalLocality,
            onValueChange = { viewModel.updateFiscalDataField("fiscalLocality", it) },
            label = { Text("Localidad") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = fiscalDataForm.fiscalMunicipality,
                onValueChange = { viewModel.updateFiscalDataField("fiscalMunicipality", it) },
                label = { Text("Municipio") },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = fiscalDataForm.fiscalState,
                onValueChange = { viewModel.updateFiscalDataField("fiscalState", it) },
                label = { Text("Estado") },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = fiscalDataForm.codigoPostalFiscal,
                onValueChange = { viewModel.updateFiscalDataField("codigoPostalFiscal", it) },
                label = { Text("C.P.") },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = fiscalDataForm.fiscalCountry,
                onValueChange = { viewModel.updateFiscalDataField("fiscalCountry", it) },
                label = { Text("País") },
                placeholder = { Text("MEX") },
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = { viewModel.saveFiscalData() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Datos Fiscales")
        }
    }
}
