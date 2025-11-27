# Finsur - Clean Architecture Guide

## 📚 Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Project Structure](#project-structure)
3. [Layers Explained](#layers-explained)
4. [Data Flow](#data-flow)
5. [Adding a New Feature](#adding-a-new-feature)
6. [Code Examples](#code-examples)
7. [Best Practices](#best-practices)

---

## 🏗️ Architecture Overview

This project follows **Clean Architecture** principles with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  (UI, ViewModels, Compose Screens, Navigation)              │
│  Depends on: Domain Layer                                    │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────┐
│                     DOMAIN LAYER                             │
│  (Use Cases, Business Logic, Repository Interfaces)         │
│  Depends on: Nothing (pure Kotlin)                           │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────┐
│                      DATA LAYER                              │
│  (Repository Implementations, API Services, DTOs)            │
│  Depends on: Domain Layer                                    │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────┐
│                      CORE LAYER                              │
│  (DI Modules, Network Setup, Configuration)                 │
│  Depends on: Nothing                                         │
└─────────────────────────────────────────────────────────────┘
```

**Key Principle:** Dependencies flow INWARD. Outer layers depend on inner layers, never the reverse.

---

## 📁 Project Structure

```
app/src/main/java/com/example/finsur/
│
├── core/                           # Core utilities & setup
│   ├── config/                     # API URLs, constants
│   │   └── ApiConfig.kt
│   ├── di/                         # Dependency Injection modules
│   │   ├── RepositoryModule.kt
│   │   └── AuthModule.kt
│   ├── network/                    # Network configuration
│   │   ├── NetworkModule.kt        # Retrofit, OkHttp setup
│   │   └── CookieJarImpl.kt       # Session management
│   └── auth/                       # Auth utilities
│       └── GoogleAuthManager.kt
│
├── data/                           # Data layer
│   └── [feature]/                  # Feature-specific data
│       ├── models/                 # DTOs (Data Transfer Objects)
│       │   └── [Feature]Dto.kt
│       ├── remote/                 # API services
│       │   └── [Feature]ApiService.kt
│       └── repository/             # Repository implementations
│           └── [Feature]RepositoryImpl.kt
│
├── domain/                         # Domain layer (Business logic)
│   └── [feature]/
│       ├── models/                 # Domain entities (pure Kotlin)
│       │   └── [Feature].kt
│       ├── repository/             # Repository interfaces
│       │   └── [Feature]Repository.kt
│       └── usecases/               # Use cases (business operations)
│           └── [Action]UseCase.kt
│
├── presentation/                   # Presentation layer (UI)
│   ├── [feature]/                  # Feature screens
│   │   ├── [ScreenName]Screen.kt
│   │   ├── components/             # Feature-specific components
│   │   └── viewmodel/              # ViewModels & UI states
│   │       ├── [Feature]ViewModel.kt
│   │       └── [Feature]UiState.kt
│   ├── components/                 # Shared UI components
│   │   ├── FinsurButton.kt
│   │   └── FinsurTextField.kt
│   └── navigation/                 # App navigation
│       ├── Screen.kt               # Route definitions
│       └── AppNavigation.kt        # NavHost setup
│
├── ui/theme/                       # App theme
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
│
├── FinsurApplication.kt            # Application class
└── MainActivity.kt                 # Main activity
```

---

## 🔍 Layers Explained

### 1. **Presentation Layer** (UI)

**Purpose:** Display data and handle user interactions

**Components:**
- **Screens:** Composable functions that render UI
- **ViewModels:** Manage UI state and business logic
- **UI States:** Sealed classes/data classes representing screen states
- **Components:** Reusable UI elements

**Example:**
```kotlin
// LoginScreen.kt
@Composable
fun LoginScreen(viewModel: AuthViewModel = hiltViewModel()) {
    val uiState by viewModel.authUiState.collectAsState()

    when (uiState) {
        is AuthUiState.Loading -> LoadingIndicator()
        is AuthUiState.Success -> NavigateToHome()
        is AuthUiState.Error -> ShowError()
    }
}

// AuthViewModel.kt
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    fun login() {
        viewModelScope.launch {
            when (val result = loginUseCase(email, password)) {
                is Result.Success -> _uiState.value = AuthUiState.Success(result.data)
                is Result.Error -> _uiState.value = AuthUiState.Error(result.message)
            }
        }
    }
}
```

**Rules:**
- ✅ Call use cases, never repositories directly
- ✅ Use StateFlow/Flow for reactive UI updates
- ✅ Handle UI logic only (no business logic)
- ❌ Never import data layer classes
- ❌ Never make direct API calls

---

### 2. **Domain Layer** (Business Logic)

**Purpose:** Contains business rules and application logic

**Components:**
- **Entities/Models:** Pure Kotlin data classes (domain models)
- **Repository Interfaces:** Define contracts for data operations
- **Use Cases:** Single-purpose business operations

**Example:**
```kotlin
// User.kt (Domain Model)
data class User(
    val id: Int,
    val username: String,
    val email: String,
    val permissions: List<String>
)

// AuthRepository.kt (Interface)
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun getUserProfile(): Result<User>
}

// LoginUseCase.kt
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        // Validation logic
        if (email.isBlank()) return Result.Error(Exception("Email required"))

        // Call repository
        return authRepository.login(email, password)
    }
}
```

**Rules:**
- ✅ Pure Kotlin (no Android dependencies)
- ✅ One use case = one business operation
- ✅ Add validation in use cases
- ❌ No framework dependencies (Android, Retrofit, etc.)
- ❌ No data layer imports

---

### 3. **Data Layer**

**Purpose:** Handle data operations (API, Database, Cache)

**Components:**
- **DTOs:** Data Transfer Objects (API response models)
- **API Services:** Retrofit interfaces
- **Repository Implementations:** Implement domain repository interfaces

**Example:**
```kotlin
// UserDto.kt (Data Model - matches API)
@Serializable
data class UserDto(
    @SerialName("id")
    val userId: Int,
    val username: String,
    val email: String?,
    val permissions: List<String>
)

// AuthApiService.kt
interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("users/profile")
    suspend fun getUserProfile(): Response<UserDto>
}

// AuthRepositoryImpl.kt
class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                getUserProfile() // Get full user data
            } else {
                Result.Error(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.Error(e, "Connection error")
        }
    }

    // Convert DTO to Domain Model
    private fun UserDto.toDomain() = User(
        id = userId,
        username = username,
        email = email ?: "",
        permissions = permissions
    )
}
```

**Rules:**
- ✅ DTOs match API response structure
- ✅ Convert DTOs to Domain Models before returning
- ✅ Handle errors and exceptions
- ✅ Use `@SerialName` for field mapping
- ❌ Never expose DTOs to domain/presentation layers

---

### 4. **Core Layer**

**Purpose:** Shared utilities and configuration

**Components:**
- **DI Modules:** Hilt/Dagger modules
- **Network Setup:** Retrofit, OkHttp configuration
- **Config:** API URLs, constants

**Example:**
```kotlin
// ApiConfig.kt
object ApiConfig {
    const val BASE_URL = "http://192.168.2.12:3000/api/v1/"
}

// NetworkModule.kt
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(ApiConfig.BASE_URL)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
}
```

---

## 🔄 Data Flow

### Example: User Login Flow

```
1. USER INTERACTION
   LoginScreen → Click "Iniciar Sesión"

2. PRESENTATION LAYER
   LoginScreen → AuthViewModel.login()

3. DOMAIN LAYER
   AuthViewModel → LoginUseCase(email, password)
   LoginUseCase → AuthRepository.login()

4. DATA LAYER
   AuthRepositoryImpl → AuthApiService.login()
   API Call → Backend → Response (200 OK)

5. BACK UP THE CHAIN
   AuthRepositoryImpl → Convert UserDto to User (domain model)
   AuthRepositoryImpl → Result.Success(user)
   LoginUseCase → Result.Success(user)
   AuthViewModel → Update UI state: AuthUiState.Success(user)
   LoginScreen → Navigate to HomeScreen
```

---

## ➕ Adding a New Feature

Let's add a **Products** feature as an example:

### Step 1: Create Folder Structure

```bash
mkdir -p app/src/main/java/com/example/finsur/data/products/{models,remote,repository}
mkdir -p app/src/main/java/com/example/finsur/domain/products/{models,repository,usecases}
mkdir -p app/src/main/java/com/example/finsur/presentation/products/{list,detail,viewmodel}
```

### Step 2: Define Domain Model (Entity)

```kotlin
// domain/products/models/Product.kt
package com.example.finsur.domain.products.models

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val stock: Int,
    val category: String
)
```

### Step 3: Create Data Models (DTOs)

```kotlin
// data/products/models/ProductDto.kt
package com.example.finsur.data.products.models

import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val image_url: String,  // Note: snake_case from API
    val stock: Int,
    val category: String
)
```

### Step 4: Create API Service

```kotlin
// data/products/remote/ProductsApiService.kt
package com.example.finsur.data.products.remote

import com.example.finsur.data.products.models.ProductDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductsApiService {
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<List<ProductDto>>

    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") productId: Int
    ): Response<ProductDto>
}
```

### Step 5: Create Repository Interface

```kotlin
// domain/products/repository/ProductsRepository.kt
package com.example.finsur.domain.products.repository

import com.example.finsur.domain.products.models.Product

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception, val message: String? = null) : Result<Nothing>()
}

interface ProductsRepository {
    suspend fun getProducts(page: Int, limit: Int): Result<List<Product>>
    suspend fun getProductById(productId: Int): Result<Product>
}
```

### Step 6: Implement Repository

```kotlin
// data/products/repository/ProductsRepositoryImpl.kt
package com.example.finsur.data.products.repository

import com.example.finsur.data.products.models.ProductDto
import com.example.finsur.data.products.remote.ProductsApiService
import com.example.finsur.domain.products.models.Product
import com.example.finsur.domain.products.repository.ProductsRepository
import com.example.finsur.domain.products.repository.Result
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor(
    private val apiService: ProductsApiService
) : ProductsRepository {

    override suspend fun getProducts(page: Int, limit: Int): Result<List<Product>> {
        return try {
            val response = apiService.getProducts(page, limit)
            if (response.isSuccessful) {
                val products = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.Success(products)
            } else {
                Result.Error(
                    Exception("Failed to get products"),
                    "Error al cargar productos"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión")
        }
    }

    override suspend fun getProductById(productId: Int): Result<Product> {
        return try {
            val response = apiService.getProductById(productId)
            if (response.isSuccessful) {
                val product = response.body()?.toDomain()
                if (product != null) {
                    Result.Success(product)
                } else {
                    Result.Error(Exception("No product data"))
                }
            } else {
                Result.Error(Exception("Failed to get product"))
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión")
        }
    }

    // Convert DTO to Domain Model
    private fun ProductDto.toDomain() = Product(
        id = id,
        name = name,
        description = description,
        price = price,
        imageUrl = image_url,  // Map snake_case to camelCase
        stock = stock,
        category = category
    )
}
```

### Step 7: Create Use Cases

```kotlin
// domain/products/usecases/GetProductsUseCase.kt
package com.example.finsur.domain.products.usecases

import com.example.finsur.domain.products.models.Product
import com.example.finsur.domain.products.repository.ProductsRepository
import com.example.finsur.domain.products.repository.Result
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: ProductsRepository
) {
    suspend operator fun invoke(page: Int = 1): Result<List<Product>> {
        return repository.getProducts(page, limit = 20)
    }
}

// domain/products/usecases/GetProductByIdUseCase.kt
class GetProductByIdUseCase @Inject constructor(
    private val repository: ProductsRepository
) {
    suspend operator fun invoke(productId: Int): Result<Product> {
        return repository.getProductById(productId)
    }
}
```

### Step 8: Create UI State

```kotlin
// presentation/products/viewmodel/ProductsUiState.kt
package com.example.finsur.presentation.products.viewmodel

import com.example.finsur.domain.products.models.Product

sealed class ProductsUiState {
    data object Initial : ProductsUiState()
    data object Loading : ProductsUiState()
    data class Success(val products: List<Product>) : ProductsUiState()
    data class Error(val message: String) : ProductsUiState()
}

sealed class ProductDetailUiState {
    data object Loading : ProductDetailUiState()
    data class Success(val product: Product) : ProductDetailUiState()
    data class Error(val message: String) : ProductDetailUiState()
}
```

### Step 9: Create ViewModel

```kotlin
// presentation/products/viewmodel/ProductsViewModel.kt
package com.example.finsur.presentation.products.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finsur.domain.products.repository.Result
import com.example.finsur.domain.products.usecases.GetProductsUseCase
import com.example.finsur.domain.products.usecases.GetProductByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getProductByIdUseCase: GetProductByIdUseCase
) : ViewModel() {

    private val _productsUiState = MutableStateFlow<ProductsUiState>(ProductsUiState.Initial)
    val productsUiState: StateFlow<ProductsUiState> = _productsUiState.asStateFlow()

    private val _productDetailUiState = MutableStateFlow<ProductDetailUiState?>(null)
    val productDetailUiState: StateFlow<ProductDetailUiState?> = _productDetailUiState.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _productsUiState.value = ProductsUiState.Loading
            when (val result = getProductsUseCase()) {
                is Result.Success -> {
                    _productsUiState.value = ProductsUiState.Success(result.data)
                }
                is Result.Error -> {
                    _productsUiState.value = ProductsUiState.Error(
                        result.message ?: "Error al cargar productos"
                    )
                }
            }
        }
    }

    fun loadProductDetail(productId: Int) {
        viewModelScope.launch {
            _productDetailUiState.value = ProductDetailUiState.Loading
            when (val result = getProductByIdUseCase(productId)) {
                is Result.Success -> {
                    _productDetailUiState.value = ProductDetailUiState.Success(result.data)
                }
                is Result.Error -> {
                    _productDetailUiState.value = ProductDetailUiState.Error(
                        result.message ?: "Error al cargar producto"
                    )
                }
            }
        }
    }
}
```

### Step 10: Create UI Screen

```kotlin
// presentation/products/list/ProductsListScreen.kt
package com.example.finsur.presentation.products.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.finsur.presentation.products.viewmodel.ProductsUiState
import com.example.finsur.presentation.products.viewmodel.ProductsViewModel

@Composable
fun ProductsListScreen(
    onNavigateToDetail: (Int) -> Unit,
    viewModel: ProductsViewModel = hiltViewModel()
) {
    val uiState by viewModel.productsUiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Productos") })
        }
    ) { padding ->
        when (uiState) {
            is ProductsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ProductsUiState.Success -> {
                val products = (uiState as ProductsUiState.Success).products
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    items(products) { product ->
                        ProductItem(
                            product = product,
                            onClick = { onNavigateToDetail(product.id) }
                        )
                    }
                }
            }
            is ProductsUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text((uiState as ProductsUiState.Error).message)
                }
            }
            ProductsUiState.Initial -> {
                // Show nothing or placeholder
            }
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onClick
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Product image, name, price, etc.
            Column {
                Text(product.name, style = MaterialTheme.typography.titleMedium)
                Text("$${product.price}", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
```

### Step 11: Add Dependency Injection

```kotlin
// core/di/ProductsModule.kt
package com.example.finsur.core.di

import com.example.finsur.data.products.remote.ProductsApiService
import com.example.finsur.data.products.repository.ProductsRepositoryImpl
import com.example.finsur.domain.products.repository.ProductsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductsNetworkModule {
    @Provides
    @Singleton
    fun provideProductsApiService(retrofit: Retrofit): ProductsApiService {
        return retrofit.create(ProductsApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductsRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindProductsRepository(
        impl: ProductsRepositoryImpl
    ): ProductsRepository
}
```

### Step 12: Add Navigation

```kotlin
// presentation/navigation/Screen.kt
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Products : Screen("products")  // Add this
    data object ProductDetail : Screen("products/{productId}") {
        fun createRoute(productId: Int) = "products/$productId"
    }
}

// presentation/navigation/AppNavigation.kt
composable(Screen.Products.route) {
    ProductsListScreen(
        onNavigateToDetail = { productId ->
            navController.navigate(Screen.ProductDetail.createRoute(productId))
        }
    )
}

composable(Screen.ProductDetail.route) { backStackEntry ->
    val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
    if (productId != null) {
        ProductDetailScreen(productId = productId)
    }
}
```

---

## ✅ Best Practices

### 1. **Naming Conventions**

- **DTOs:** `[Feature]Dto` (e.g., `UserDto`, `ProductDto`)
- **Domain Models:** `[Feature]` (e.g., `User`, `Product`)
- **Use Cases:** `[Action][Feature]UseCase` (e.g., `GetProductsUseCase`)
- **ViewModels:** `[Feature]ViewModel` (e.g., `ProductsViewModel`)
- **Screens:** `[Feature][Action]Screen` (e.g., `ProductsListScreen`)
- **API Services:** `[Feature]ApiService` (e.g., `ProductsApiService`)

### 2. **Error Handling**

Always use the `Result` sealed class:
```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception, val message: String? = null) : Result<Nothing>()
}
```

### 3. **UI State Management**

Use sealed classes for UI states:
```kotlin
sealed class UiState {
    data object Initial : UiState()
    data object Loading : UiState()
    data class Success<T>(val data: T) : UiState()
    data class Error(val message: String) : UiState()
}
```

### 4. **Dependency Injection**

- Create separate modules for each feature
- Use `@Provides` for external classes (Retrofit, OkHttp)
- Use `@Binds` for your own interfaces/implementations

### 5. **Testing**

- **Domain Layer:** Easy to test (pure Kotlin)
- **Use Cases:** Test with mock repositories
- **ViewModels:** Test with mock use cases
- **Repository:** Test with mock API services

### 6. **Spanish Content, English Code**

- ✅ User-facing text in Spanish
- ✅ Code, variables, classes in English
- ✅ Comments in English

---

## 📋 Checklist for New Features

When adding a new feature, follow this checklist:

- [ ] **1. Domain Layer**
  - [ ] Create domain model in `domain/[feature]/models/`
  - [ ] Create repository interface in `domain/[feature]/repository/`
  - [ ] Create use cases in `domain/[feature]/usecases/`

- [ ] **2. Data Layer**
  - [ ] Create DTOs in `data/[feature]/models/`
  - [ ] Create API service in `data/[feature]/remote/`
  - [ ] Implement repository in `data/[feature]/repository/`
  - [ ] Add `toDomain()` extension functions

- [ ] **3. Presentation Layer**
  - [ ] Create UI state classes
  - [ ] Create ViewModel
  - [ ] Create Compose screens
  - [ ] Add to navigation

- [ ] **4. Dependency Injection**
  - [ ] Create DI modules in `core/di/`
  - [ ] Provide API service
  - [ ] Bind repository implementation

- [ ] **5. Testing**
  - [ ] Test use cases
  - [ ] Test ViewModels
  - [ ] Test repository implementations

---

## 🎯 Quick Reference

**Need to fetch data from API?**
1. Create DTO → API Service → Repository → Use Case → ViewModel → Screen

**Need to add a new screen?**
1. Create UI State → ViewModel → Screen → Add to Navigation

**Need to add business logic?**
1. Create Use Case in domain layer → Inject in ViewModel

**Need to modify API response mapping?**
1. Update DTO → Update `toDomain()` function in Repository

---

## 📞 Common Patterns

### Pattern 1: List + Detail Screens
```
ProductsListScreen → Shows all products
ProductDetailScreen → Shows single product
```

### Pattern 2: Form with Validation
```
ViewModel → Form state (data class)
UseCase → Validation logic
Repository → Submit to API
```

### Pattern 3: Pagination
```
ViewModel → Track page number
UseCase → Load more pages
Repository → Append to list
```

---

## 🚀 Summary

1. **Always follow Clean Architecture layers**
2. **Keep domain layer pure Kotlin**
3. **Convert DTOs to Domain models in repositories**
4. **Use sealed classes for states and results**
5. **One use case = one business operation**
6. **ViewModels only call use cases, never repositories**
7. **UI should be dumb - just display state**

---

**Questions?** Reference this document when adding new features!
