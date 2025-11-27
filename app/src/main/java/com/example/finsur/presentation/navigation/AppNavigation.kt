package com.example.finsur.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.example.finsur.presentation.components.BottomNavigationBar
import com.example.finsur.presentation.home.HomeScreen
import com.example.finsur.presentation.products.ProductsScreen
import com.example.finsur.presentation.profile.ProfileScreen as RealProfileScreen

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
                        // TODO: Navigate to category products screen
                        // navController.navigate(Screen.CategoryProducts.createRoute(categorySlug))
                    },
                    onNavigateToBrandProducts = { brandSlug ->
                        // TODO: Navigate to brand products screen
                        // navController.navigate(Screen.BrandProducts.createRoute(brandSlug))
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
                com.example.finsur.presentation.products.ProductDetailScreen(
                    productId = productId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(Screen.Cart.route) {
            MainScaffold(navController) {
                CartScreen()
            }
        }

        composable(Screen.Profile.route) {
            MainScaffold(navController) {
                RealProfileScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
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
