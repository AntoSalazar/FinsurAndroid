package com.example.finsur.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.finsur.domain.profile.models.Address
import com.example.finsur.presentation.profile.viewmodel.AddressFormState

@Composable
fun AddressDialog(
    address: Address? = null,  // null means create, non-null means edit
    onDismiss: () -> Unit,
    onSave: (AddressFormState) -> Unit,
    modifier: Modifier = Modifier
) {
    var formState by remember {
        mutableStateOf(
            if (address != null) {
                AddressFormState(
                    title = address.title,
                    addressLine1 = address.addressLine1,
                    addressLine2 = address.addressLine2 ?: "",
                    country = address.country,
                    city = address.city,
                    state = address.state,
                    postalCode = address.postalCode,
                    landmark = address.landmark ?: "",
                    phoneNumber = address.phoneNumber
                )
            } else {
                AddressFormState(country = "México")
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (address == null) "Nueva Dirección" else "Editar Dirección")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = formState.title,
                    onValueChange = { formState = formState.copy(title = it) },
                    label = { Text("Título *") },
                    placeholder = { Text("Ej: Casa, Oficina") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = formState.addressLine1,
                    onValueChange = { formState = formState.copy(addressLine1 = it) },
                    label = { Text("Dirección Línea 1 *") },
                    placeholder = { Text("Calle y número") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = formState.addressLine2,
                    onValueChange = { formState = formState.copy(addressLine2 = it) },
                    label = { Text("Dirección Línea 2") },
                    placeholder = { Text("Piso, departamento, etc.") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = formState.city,
                        onValueChange = { formState = formState.copy(city = it) },
                        label = { Text("Ciudad *") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = formState.state,
                        onValueChange = { formState = formState.copy(state = it) },
                        label = { Text("Estado *") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = formState.postalCode,
                        onValueChange = { formState = formState.copy(postalCode = it) },
                        label = { Text("C.P. *") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = formState.country,
                        onValueChange = { formState = formState.copy(country = it) },
                        label = { Text("País *") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = formState.landmark,
                    onValueChange = { formState = formState.copy(landmark = it) },
                    label = { Text("Referencias") },
                    placeholder = { Text("Puntos de referencia") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = formState.phoneNumber,
                    onValueChange = { formState = formState.copy(phoneNumber = it) },
                    label = { Text("Teléfono *") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(formState) }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
