package com.example.finsur.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.finsur.domain.profile.models.Address
import com.example.finsur.presentation.profile.viewmodel.AddressesState
import com.example.finsur.presentation.profile.viewmodel.ProfileViewModel
import com.example.finsur.presentation.profile.viewmodel.UpdateState

@Composable
fun AddressesTab(
    viewModel: ProfileViewModel,
    modifier: Modifier = Modifier
) {
    val addressesState by viewModel.addressesState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingAddress by remember { mutableStateOf<Address?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadAddresses()
    }

    // Show snackbar for update state
    LaunchedEffect(updateState) {
        when (updateState) {
            is UpdateState.Success -> {
                snackbarHostState.showSnackbar((updateState as UpdateState.Success).message)
                viewModel.clearUpdateState()
                showAddDialog = false
                editingAddress = null
            }
            is UpdateState.Error -> {
                snackbarHostState.showSnackbar((updateState as UpdateState.Error).message)
                viewModel.clearUpdateState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar dirección")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
        Text(
            text = "Mis Direcciones",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (addressesState) {
            is AddressesState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is AddressesState.Success -> {
                val addresses = (addressesState as AddressesState.Success).addresses
                if (addresses.isEmpty()) {
                    Text(
                        text = "No tienes direcciones guardadas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(addresses) { address ->
                            AddressCard(
                                address = address,
                                onEdit = { editingAddress = address },
                                onDelete = { viewModel.deleteAddress(address.id) },
                                onSetDefault = { viewModel.setDefaultAddress(address.id) }
                            )
                        }
                    }
                }
            }
            is AddressesState.Error -> {
                Text(
                    text = (addressesState as AddressesState.Error).message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            AddressesState.Initial -> {}
        }
        }
    }

    // Add/Edit Address Dialog
    if (showAddDialog) {
        AddressDialog(
            address = null,
            onDismiss = { showAddDialog = false },
            onSave = { formState ->
                viewModel.createAddress(formState)
            }
        )
    }

    if (editingAddress != null) {
        AddressDialog(
            address = editingAddress,
            onDismiss = { editingAddress = null },
            onSave = { formState ->
                editingAddress?.let { addr ->
                    viewModel.updateAddress(addr.id, formState)
                }
            }
        )
    }
}

@Composable
private fun AddressCard(
    address: Address,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = address.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = address.addressLine1,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (!address.addressLine2.isNullOrBlank()) {
                        Text(
                            text = address.addressLine2,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Text(
                        text = "${address.city}, ${address.state}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "C.P. ${address.postalCode}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tel: ${address.phoneNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar dirección"
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar dirección",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (address.isDefault) {
                Text(
                    text = "✓ Predeterminada",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                TextButton(
                    onClick = onSetDefault,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text("Establecer como predeterminada")
                }
            }
        }
    }
}
