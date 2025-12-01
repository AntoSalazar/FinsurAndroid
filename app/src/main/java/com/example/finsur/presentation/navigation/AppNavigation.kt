package com.example.finsur.presentation.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.finsur.presentation.auth.login.LoginScreen
import com.example.finsur.presentation.auth.register.RegisterScreen
import com.example.finsur.presentation.auth.viewmodel.AuthUiState
import com.example.finsur.presentation.auth.viewmodel.AuthViewModel
import com.example.finsur.presentation.cart.CartScreen
import com.example.finsur.presentation.cart.viewmodel.CartViewModel
import com.example.finsur.presentation.checkout.CheckoutScreen
import com.example.finsur.presentation.checkout.PaymentSuccessScreen
import com.example.finsur.presentation.components.BottomNavigationBar
import com.example.finsur.presentation.home.HomeScreen
import com.example.finsur.presentation.products.ProductsScreen
import com.example.finsur.presentation.profile.ProfileDashboardScreen
import com.example.finsur.presentation.profile.ModernProfileScreen
import com.example.finsur.presentation.profile.ProfileScreen as RealProfileScreen
import com.example.finsur.presentation.profile.components.AddressesTab
import com.example.finsur.presentation.profile.components.FiscalInfoTab
import com.example.finsur.presentation.profile.viewmodel.ProfileViewModel
import com.example.finsur.presentation.profile.viewmodel.AddressesState
import com.example.finsur.presentation.orders.OrderDetailScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authUiState by authViewModel.authUiState.collectAsState()

    val startDestination = when (authUiState) {
        is AuthUiState.Success -> Screen.Home.route
        else -> Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            MainScaffold(navController) {
                HomeScreen(
                    onNavigateToProductDetail = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId))
                    },
                    onNavigateToCategoryProducts = { categorySlug ->
                        navController.navigate(Screen.Products.route)
                    },
                    onNavigateToBrandProducts = { brandSlug ->
                        navController.navigate(Screen.Products.route)
                    },
                    onNavigateToSearch = {
                        navController.navigate(Screen.Products.route)
                    }
                )
            }
        }

        composable(Screen.Products.route) {
            MainScaffold(navController) {
                ProductsScreen(
                    onNavigateToProductDetail = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId))
                    }
                )
            }
        }

        composable(Screen.ProductDetail.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
            if (productId != null) {
                // Get shared CartViewModel from parent NavHost
                val parentEntry = navController.getBackStackEntry(Screen.Home.route)
                val sharedCartViewModel: CartViewModel = hiltViewModel(parentEntry)

                com.example.finsur.presentation.products.ProductDetailScreen(
                    productId = productId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    cartViewModel = sharedCartViewModel
                )
            }
        }

        composable(Screen.Cart.route) {
            // Get shared CartViewModel from parent NavHost
            val parentEntry = navController.getBackStackEntry(Screen.Home.route)
            val sharedCartViewModel: CartViewModel = hiltViewModel(parentEntry)

            MainScaffold(navController) {
                CartScreen(
                    onNavigateToProductDetail = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId))
                    },
                    onNavigateToCheckout = {
                        navController.navigate(Screen.Checkout.route)
                    },
                    viewModel = sharedCartViewModel
                )
            }
        }

        composable(Screen.Checkout.route) {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            val addressesState by profileViewModel.addressesState.collectAsState()

            // Load addresses when checkout screen is opened
            LaunchedEffect(Unit) {
                profileViewModel.loadAddresses()
            }

            val addresses = when (val state = addressesState) {
                is AddressesState.Success -> state.addresses
                else -> emptyList()
            }

            CheckoutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPaymentSuccess = { orderNumber ->
                    // Navigate to success screen
                    navController.navigate(Screen.PaymentSuccess.createRoute(orderNumber)) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
                userAddresses = addresses
            )
        }

        composable(Screen.PaymentSuccess.route) { backStackEntry ->
            val orderNumber = backStackEntry.arguments?.getString("orderNumber")
            PaymentSuccessScreen(
                orderNumber = orderNumber,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Profile.route) {
            MainScaffold(navController) {
                ProfileDashboardScreen(
                    onNavigateToOrders = {
                        navController.navigate("orders")
                    },
                    onNavigateToProfile = {
                        navController.navigate("profile_edit")
                    },
                    onNavigateToAddresses = {
                        navController.navigate("addresses")
                    },
                    onNavigateToFiscalData = {
                        navController.navigate("fiscal_data")
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        // Orders Screen
        composable("orders") {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Mis Pedidos") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, "Volver")
                            }
                        }
                    )
                }
            ) { padding ->
                ModernProfileScreen(
                    onNavigateToLogin = {},
                    onNavigateToOrderDetail = { orderId ->
                        navController.navigate(Screen.OrderDetail.createRoute(orderId))
                    },
                    modifier = Modifier.padding(padding)
                )
            }
        }

        // Profile Edit Screen
        composable("profile_edit") {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Mi Perfil") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, "Volver")
                            }
                        }
                    )
                }
            ) { padding ->
                RealProfileScreen(
                    onNavigateToLogin = {},
                    modifier = Modifier.padding(padding)
                )
            }
        }

        // Addresses Screen
        composable("addresses") {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Mis Direcciones") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                            }
                        }
                    )
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    AddressesTab(viewModel = profileViewModel)
                }
            }
        }

        // Fiscal Data Screen
        composable("fiscal_data") {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Datos Fiscales") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, "Volver")
                            }
                        }
                    )
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    FiscalInfoTab(viewModel = profileViewModel)
                }
            }
        }

        // Order Detail Screen
        composable(Screen.OrderDetail.route) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")?.toIntOrNull()
            if (orderId != null) {
                OrderDetailScreen(
                    orderId = orderId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
private fun MainScaffold(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        // Pop up to the start destination to avoid building up a large stack
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            content()
        }
    }
}
