package com.example.finsur.presentation.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.finsur.domain.checkout.models.Warehouse
import com.example.finsur.domain.profile.models.Address
import com.example.finsur.presentation.checkout.viewmodel.*
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onNavigateBack: () -> Unit,
    onPaymentSuccess: (String) -> Unit,
    userAddresses: List<Address>,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val warehousesUiState by viewModel.warehousesUiState.collectAsState()
    val paymentSheetUiState by viewModel.paymentSheetUiState.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val currentOrderNumber by viewModel.currentOrderNumber.collectAsState()

    // Stripe PaymentSheet
    val paymentSheet = rememberPaymentSheet { result ->
        when (result) {
            is PaymentSheetResult.Canceled -> {
                viewModel.resetPaymentSheetState()
            }
            is PaymentSheetResult.Failed -> {
                viewModel.resetPaymentSheetState()
            }
            is PaymentSheetResult.Completed -> {
                onPaymentSuccess(currentOrderNumber ?: "")
            }
        }
    }

    // Handle payment sheet state changes
    LaunchedEffect(paymentSheetUiState) {
        when (val state = paymentSheetUiState) {
            is PaymentSheetUiState.Success -> {
                val paymentSheetData = state.paymentSheet

                // Validate publishable key
                if (paymentSheetData.publishableKey.isBlank()) {
                    // Reset state and show error
                    viewModel.resetPaymentSheetState()
                    return@LaunchedEffect
                }

                // Initialize Stripe with publishable key
                PaymentConfiguration.init(context, paymentSheetData.publishableKey)

                // Create customer configuration
                val customerConfig = PaymentSheet.CustomerConfiguration.createWithCustomerSession(
                    id = paymentSheetData.customer,
                    clientSecret = paymentSheetData.customerSessionClientSecret
                )

                // Create PaymentSheet configuration
                val configuration = PaymentSheet.Configuration(
                    merchantDisplayName = "Finsur",
                    customer = customerConfig,
                    allowsDelayedPaymentMethods = true
                )

                // Present payment sheet
                paymentSheet.presentWithPaymentIntent(
                    paymentIntentClientSecret = paymentSheetData.paymentIntent,
                    configuration = configuration
                )
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Checkout",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Shipping Method Selection
            item {
                ShippingMethodSection(
                    selectedMethod = formState.selectedShippingMethod,
                    onMethodSelected = { viewModel.updateShippingMethod(it) }
                )
            }

            // Show addresses for shipping or warehouses for pickup
            item {
                when (formState.selectedShippingMethod) {
                    "shipping" -> {
                        AddressSelectionSection(
                            addresses = userAddresses,
                            selectedAddressId = formState.selectedAddressId,
                            onAddressSelected = { viewModel.updateSelectedAddress(it) }
                        )
                    }
                    "pickup" -> {
                        when (val state = warehousesUiState) {
                            is WarehousesUiState.Loading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            is WarehousesUiState.Success -> {
                                WarehouseSelectionSection(
                                    warehouses = state.warehouses,
                                    selectedWarehouseId = formState.selectedWarehouseId,
                                    onWarehouseSelected = { viewModel.updateSelectedWarehouse(it) }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                PickupNotesSection(
                                    notes = formState.pickupNotes,
                                    onNotesChanged = { viewModel.updatePickupNotes(it) }
                                )
                            }
                            is WarehousesUiState.Error -> {
                                Text(
                                    text = state.message,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            else -> {}
                        }
                    }
                }
            }

            // Coupon Code
            item {
                CouponCodeSection(
                    couponCode = formState.couponCode,
                    onCouponCodeChanged = { viewModel.updateCouponCode(it) }
                )
            }

            // Payment Button
            item {
                val isFormValid = when (formState.selectedShippingMethod) {
                    "shipping" -> formState.selectedAddressId != null
                    "pickup" -> formState.selectedWarehouseId != null && formState.pickupNotes.isNotBlank()
                    else -> false
                }

                Button(
                    onClick = { viewModel.proceedToPayment() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid && paymentSheetUiState !is PaymentSheetUiState.Loading
                ) {
                    if (paymentSheetUiState is PaymentSheetUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Proceder al Pago")
                    }
                }
            }

            // Error message
            item {
                if (paymentSheetUiState is PaymentSheetUiState.Error) {
                    Text(
                        text = (paymentSheetUiState as PaymentSheetUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ShippingMethodSection(
    selectedMethod: String,
    onMethodSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .selectableGroup()
        ) {
            Text(
                text = "Método de Envío",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selectedMethod == "shipping",
                        onClick = { onMethodSelected("shipping") },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedMethod == "shipping",
                    onClick = null
                )
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Text("Envío a Domicilio")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selectedMethod == "pickup",
                        onClick = { onMethodSelected("pickup") },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedMethod == "pickup",
                    onClick = null
                )
                Icon(
                    imageVector = Icons.Default.Store,
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Text("Recoger en Sucursal")
            }
        }
    }
}

@Composable
fun AddressSelectionSection(
    addresses: List<Address>,
    selectedAddressId: Int?,
    onAddressSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Dirección de Envío",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (addresses.isEmpty()) {
                Text(
                    text = "No tienes direcciones guardadas. Agrega una dirección en tu perfil.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                addresses.forEach { address ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedAddressId == address.id,
                                onClick = { onAddressSelected(address.id) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        RadioButton(
                            selected = selectedAddressId == address.id,
                            onClick = null
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = address.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${address.addressLine1}, ${address.city}, ${address.state} ${address.postalCode}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            if (address.isDefault) {
                                Text(
                                    text = "Predeterminada",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WarehouseSelectionSection(
    warehouses: List<Warehouse>,
    selectedWarehouseId: Int?,
    onWarehouseSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Selecciona una Sucursal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            warehouses.filter { it.isActive }.forEach { warehouse ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedWarehouseId == warehouse.id,
                            onClick = { onWarehouseSelected(warehouse.id) },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    RadioButton(
                        selected = selectedWarehouseId == warehouse.id,
                        onClick = null
                    )
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                            text = warehouse.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${warehouse.address.addressLine1}, ${warehouse.address.city}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = warehouse.schedule,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = warehouse.phoneNumber,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PickupNotesSection(
    notes: String,
    onNotesChanged: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Información de Recogida",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Nombre de quien recogerá y teléfono de contacto",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ej: Juan Pérez, 555-1234") },
                minLines = 2
            )
        }
    }
}

@Composable
fun CouponCodeSection(
    couponCode: String,
    onCouponCodeChanged: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Código de Cupón (Opcional)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = couponCode,
                onValueChange = onCouponCodeChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ingresa tu código de cupón") }
            )
        }
    }
}
