package com.example.finsur.presentation.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Products : Screen("products")
    data object ProductDetail : Screen("products/{productId}") {
        fun createRoute(productId: Int) = "products/$productId"
    }
    data object Cart : Screen("cart")
    data object Checkout : Screen("checkout")
    data object PaymentSuccess : Screen("payment-success/{orderNumber}") {
        fun createRoute(orderNumber: String) = "payment-success/$orderNumber"
    }
    data object Profile : Screen("profile")
    data object OrderDetail : Screen("orders/{orderId}") {
        fun createRoute(orderId: Int) = "orders/$orderId"
    }
}
