package com.example.finsur.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.finsur.presentation.profile.viewmodel.ProfileViewModel

@Composable
fun PasswordChangeForm(
    viewModel: ProfileViewModel,
    modifier: Modifier = Modifier
) {
    val passwordForm by viewModel.passwordForm.collectAsState()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = passwordForm.currentPassword,
            onValueChange = { viewModel.updatePasswordField("currentPassword", it) },
            label = { Text("Contraseña Actual") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = passwordForm.newPassword,
            onValueChange = { viewModel.updatePasswordField("newPassword", it) },
            label = { Text("Nueva Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = passwordForm.confirmPassword,
            onValueChange = { viewModel.updatePasswordField("confirmPassword", it) },
            label = { Text("Confirmar Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { viewModel.changePassword() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cambiar Contraseña")
        }
    }
}
