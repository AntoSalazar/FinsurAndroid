package com.example.finsur.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
fun PersonalInfoTab(
    viewModel: ProfileViewModel,
    modifier: Modifier = Modifier
) {
    val personalInfoForm by viewModel.personalInfoForm.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Información Personal",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = personalInfoForm.firstName,
            onValueChange = { viewModel.updatePersonalInfoField("firstName", it) },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = personalInfoForm.lastName,
            onValueChange = { viewModel.updatePersonalInfoField("lastName", it) },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = personalInfoForm.phoneNumber,
            onValueChange = { viewModel.updatePersonalInfoField("phoneNumber", it) },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { viewModel.savePersonalInfo() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Cambios")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Password Change Section
        Text(
            text = "Cambiar Contraseña",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        PasswordChangeForm(viewModel = viewModel)
    }
}
