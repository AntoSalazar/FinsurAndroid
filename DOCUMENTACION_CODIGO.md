# 📱 DOCUMENTACIÓN DEL CÓDIGO - PROYECTO FINSUR

## 📋 Índice

1. [Código de Base de Datos (Almacenamiento Local)](#1️⃣-código-de-base-de-datos-almacenamiento-local)
   - [AuthStateManager.kt - Gestión de Autenticación](#-authstatemanagerkt---gestión-de-estado-de-autenticación)
   - [CookieJarImpl.kt - Persistencia de Cookies](#-cookiejarimplkt---persistencia-de-cookies-http)
2. [Código de Conexión a Servidor (Networking)](#2️⃣-código-de-conexión-a-servidor-networking)
   - [ApiConfig.kt - Configuración Base](#-apiconfigkt---configuración-base-de-la-api)
   - [NetworkModule.kt - Inyección de Dependencias](#-networkmodulekt---inyección-de-dependencias-para-networking)
   - [AuthInterceptor.kt - Interceptor de Autenticación](#-authinterceptorkt---interceptor-de-autenticación)
   - [CartApiService.kt - Definición de Endpoints](#-cartapiservicekt---definición-de-endpoints-del-carrito)
3. [Código Relevante - Arquitectura Clean & MVVM](#3️⃣-código-relevante---arquitectura-clean--mvvm)
   - [CartRepositoryImpl.kt - Patrón Repository](#-cartrepositoryimplkt---implementación-del-patrón-repository)
   - [AddItemToCartUseCase.kt - Patrón Use Case](#-additemtocartusecasekt---patrón-use-case)
4. [Resumen del Proyecto](#-resumen-del-proyecto)

---

## 1️⃣ CÓDIGO DE BASE DE DATOS (ALMACENAMIENTO LOCAL)

### ⚠️ NOTA IMPORTANTE
Este proyecto **NO utiliza Room Database** (base de datos SQLite local). En su lugar, utiliza **SharedPreferences** para almacenar datos de sesión y autenticación de forma persistente.

---

### 📍 **AuthStateManager.kt** - Gestión de Estado de Autenticación
**Ubicación:** `/app/src/main/java/com/example/finsur/core/auth/AuthStateManager.kt`

**Propósito:** Administra el estado de autenticación del usuario usando SharedPreferences

```kotlin
package com.example.finsur.core.auth

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestor del estado de autenticación de usuario
 *
 * Esta clase es un Singleton que administra la autenticación del usuario
 * usando SharedPreferences para persistir datos localmente.
 *
 * SharedPreferences es un almacenamiento clave-valor simple y ligero
 * ideal para guardar configuraciones y datos pequeños de forma persistente.
 */
@Singleton
class AuthStateManager @Inject constructor(
    context: Context
) {
    // SharedPreferences para almacenar datos de autenticación
    // MODE_PRIVATE: Solo esta app puede acceder a estos datos
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    // StateFlow para notificar cambios en el estado de autenticación
    // Permite que la UI observe y reaccione a cambios en tiempo real
    private val _isAuthenticated = MutableStateFlow(isUserAuthenticated())
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _userId = MutableStateFlow<Int?>(getUserId())
    val userId: StateFlow<Int?> = _userId.asStateFlow()

    /**
     * OPERACIÓN CREATE/UPDATE (Insertar/Actualizar)
     *
     * Guarda las credenciales del usuario cuando inicia sesión exitosamente
     *
     * @param userId ID único del usuario desde el servidor
     * @param email Correo electrónico del usuario
     */
    fun setAuthenticated(userId: Int, email: String) {
        Log.d("AuthStateManager", "Setting authenticated: userId=$userId, email=$email")

        // Editor de SharedPreferences para escribir datos
        prefs.edit().apply {
            // Guardar tres valores en el almacenamiento:
            putBoolean("is_authenticated", true)     // Bandera de autenticación
            putInt("user_id", userId)                 // ID del usuario
            putString("user_email", email)            // Email del usuario
            apply() // Guardar de forma asíncrona (sin bloquear el hilo principal)
        }

        // Actualizar los StateFlows para notificar a los observadores
        _isAuthenticated.value = true
        _userId.value = userId
    }

    /**
     * OPERACIÓN DELETE (Eliminar)
     *
     * Limpia todos los datos de autenticación cuando el usuario cierra sesión
     * Equivalente a eliminar el registro del usuario local
     */
    fun clearAuthentication() {
        Log.d("AuthStateManager", "Clearing authentication")

        prefs.edit().apply {
            putBoolean("is_authenticated", false)  // Marcar como no autenticado
            remove("user_id")                       // Eliminar el ID de usuario
            remove("user_email")                    // Eliminar el email
            apply()
        }

        // Notificar que el usuario ya no está autenticado
        _isAuthenticated.value = false
        _userId.value = null
    }

    /**
     * OPERACIÓN READ (Leer)
     *
     * Verifica si el usuario está actualmente autenticado
     *
     * @return true si el usuario está autenticado, false si no
     */
    fun isUserAuthenticated(): Boolean {
        // Leer el valor booleano, con false como valor por defecto
        return prefs.getBoolean("is_authenticated", false)
    }

    /**
     * OPERACIÓN READ (Leer)
     *
     * Obtiene el ID del usuario autenticado
     *
     * @return ID del usuario o null si no está autenticado
     */
    fun getUserId(): Int? {
        if (!isUserAuthenticated()) return null

        // Leer el ID guardado (con -1 como valor por defecto)
        val userId = prefs.getInt("user_id", -1)
        return if (userId != -1) userId else null
    }

    /**
     * OPERACIÓN READ (Leer)
     *
     * Obtiene el email del usuario autenticado
     *
     * @return Email del usuario o null si no está autenticado
     */
    fun getUserEmail(): String? {
        if (!isUserAuthenticated()) return null
        return prefs.getString("user_email", null)
    }
}
```

**📊 Estructura de datos en SharedPreferences:**
```
archivo: "auth_prefs"
├── "is_authenticated" → Boolean (true/false)
├── "user_id" → Int (ej: 12345)
└── "user_email" → String (ej: "usuario@email.com")
```

---

### 📍 **CookieJarImpl.kt** - Persistencia de Cookies HTTP
**Ubicación:** `/app/src/main/java/com/example/finsur/core/network/CookieJarImpl.kt`

**Propósito:** Almacena y gestiona cookies de sesión HTTP para mantener la sesión del usuario con el servidor

```kotlin
package com.example.finsur.core.network

import android.content.Context
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * Implementación personalizada de CookieJar para persistir cookies HTTP
 *
 * ¿Por qué necesitamos esto?
 * - El servidor envía cookies de sesión después del login
 * - Necesitamos guardar estas cookies y enviarlas en cada petición
 * - OkHttp por defecto NO guarda cookies (no tiene memoria entre peticiones)
 * - Esta clase soluciona eso guardando cookies en SharedPreferences
 *
 * Funciona como una "base de datos de cookies" persistente
 */
class CookieJarImpl(context: Context) : CookieJar {

    // Almacenamiento en memoria (Map) para acceso rápido durante la ejecución
    // Estructura: Map<Host, List<Cookie>>
    // Ejemplo: {"api.finsur.com" -> [Cookie1, Cookie2]}
    private val cookieStore: MutableMap<String, MutableList<Cookie>> = mutableMapOf()

    // SharedPreferences para persistencia entre sesiones de la app
    private val prefs = context.getSharedPreferences("cookie_prefs", Context.MODE_PRIVATE)

    init {
        // Al crear la instancia, cargar todas las cookies guardadas anteriormente
        loadCookies()
        Log.d("CookieJar", "Loaded ${cookieStore.values.sumOf { it.size }} cookies from storage")
    }

    /**
     * OPERACIÓN CREATE/UPDATE - Guardar cookies desde la respuesta HTTP
     *
     * Llamado automáticamente por OkHttp cuando el servidor envía cookies
     * Esto ocurre típicamente después del login
     *
     * @param url URL del servidor que envió las cookies
     * @param cookies Lista de cookies recibidas del servidor
     */
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val host = url.host
        Log.d("CookieJar", "saveFromResponse for $host - received ${cookies.size} cookies")

        // Obtener cookies existentes para este host, o crear lista nueva
        val existingCookies = cookieStore[host]?.toMutableList() ?: mutableListOf()

        cookies.forEach { cookie ->
            Log.d("CookieJar", "  Processing cookie: ${cookie.name}=${cookie.value.take(20)}...")

            // OPERACIÓN UPDATE: Eliminar cookie anterior con el mismo nombre
            // Esto evita duplicados
            existingCookies.removeAll { it.name == cookie.name }

            // Convertir cookies de sesión a persistentes (30 días de duración)
            // Las cookies de sesión normalmente se pierden al cerrar la app
            // Las convertimos a persistentes para mantener la sesión activa
            val persistentCookie = if (cookie.expiresAt == 253402300799999L) {
                Log.d("CookieJar", "  Converting session cookie to persistent (30 days)")
                Cookie.Builder()
                    .name(cookie.name)
                    .value(cookie.value)
                    .domain(cookie.domain)
                    .path(cookie.path)
                    .expiresAt(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000) // 30 días
                    .apply {
                        if (cookie.secure) secure()
                        if (cookie.httpOnly) httpOnly()
                    }
                    .build()
            } else {
                cookie
            }

            // OPERACIÓN INSERT: Agregar la nueva cookie
            existingCookies.add(persistentCookie)
        }

        // Actualizar el almacenamiento en memoria
        cookieStore[host] = existingCookies

        // Persistir a SharedPreferences (guardar en disco)
        saveCookies()

        Log.d("CookieJar", "Saved/Updated cookies for host: $host. Total now: ${existingCookies.size}")
    }

    /**
     * OPERACIÓN READ - Cargar cookies para una petición HTTP
     *
     * Llamado automáticamente por OkHttp antes de cada petición
     * Envía las cookies guardadas al servidor para mantener la sesión
     *
     * @param url URL a la que se va a hacer la petición
     * @return Lista de cookies válidas a enviar
     */
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val host = url.host
        val cookies = cookieStore[host] ?: emptyList()

        // Filtrar cookies expiradas (OPERACIÓN CONDICIONAL)
        val validCookies = cookies.filter { !it.expiresAt.isExpired() }

        // LIMPIEZA AUTOMÁTICA: Si encontramos cookies expiradas, eliminarlas
        if (validCookies.size != cookies.size) {
            cookieStore[host] = validCookies.toMutableList()
            saveCookies()
            Log.d("CookieJar", "Cleaned up ${cookies.size - validCookies.size} expired cookies")
        }

        Log.d("CookieJar", "loadForRequest to $host - sending ${validCookies.size} cookies")

        return validCookies
    }

    /**
     * OPERACIÓN DELETE - Eliminar todas las cookies
     *
     * Llamado cuando el usuario cierra sesión
     * Limpia tanto la memoria como el almacenamiento persistente
     */
    fun clearCookies() {
        cookieStore.clear()           // Limpiar memoria
        prefs.edit().clear().apply()  // Limpiar SharedPreferences
    }

    /**
     * Función auxiliar para verificar si una cookie ha expirado
     */
    private fun Long.isExpired(): Boolean {
        return this < System.currentTimeMillis()
    }

    /**
     * PERSISTENCIA - Guardar cookies en SharedPreferences
     *
     * Serializa las cookies a formato String y las guarda en disco
     * Formato: "nombre=valor|dominio|ruta|expiracion|secure|httpOnly"
     */
    private fun saveCookies() {
        val editor = prefs.edit()

        cookieStore.forEach { (host, cookies) ->
            // Serializar cada cookie a String
            val cookieString = cookies.joinToString(";;;") { cookie ->
                "${cookie.name}=${cookie.value}|${cookie.domain}|${cookie.path}|" +
                "${cookie.expiresAt}|${cookie.secure}|${cookie.httpOnly}"
            }

            // Guardar bajo la clave del host
            editor.putString(host, cookieString)
        }

        editor.apply()
    }

    /**
     * OPERACIÓN READ - Cargar cookies desde SharedPreferences
     *
     * Deserializa las cookies guardadas al iniciar la app
     */
    private fun loadCookies() {
        val allEntries = prefs.all

        allEntries.forEach { (host, value) ->
            if (value is String && value.isNotEmpty()) {
                // Dividir el string en cookies individuales
                val cookies = value.split(";;;").mapNotNull { cookieStr ->
                    parseCookie(cookieStr)  // Deserializar cada cookie
                }
                cookieStore[host] = cookies.toMutableList()
            }
        }
    }

    /**
     * Deserializar una cookie desde formato String
     *
     * @param cookieStr String en formato "nombre=valor|dominio|ruta|..."
     * @return Cookie deserializada o null si hay error
     */
    private fun parseCookie(cookieStr: String): Cookie? {
        return try {
            val parts = cookieStr.split("|")
            if (parts.size >= 6) {
                val nameValue = parts[0].split("=")
                if (nameValue.size == 2) {
                    Cookie.Builder()
                        .name(nameValue[0])
                        .value(nameValue[1])
                        .domain(parts[1])
                        .path(parts[2])
                        .expiresAt(parts[3].toLong())
                        .apply {
                            if (parts[4].toBoolean()) secure()
                            if (parts[5].toBoolean()) httpOnly()
                        }
                        .build()
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
```

**📊 Estructura de datos en SharedPreferences:**
```
archivo: "cookie_prefs"
├── "api.finsur.com" → "session_id=abc123|api.finsur.com|/|1702345678000|true|true"
└── "cdn.finsur.com" → "token=xyz789|cdn.finsur.com|/|1702345678000|false|false"
```

**🔄 Ciclo de vida de las cookies:**
1. Usuario hace login → Servidor envía cookie de sesión
2. `saveFromResponse()` guarda la cookie en memoria y SharedPreferences
3. Usuario cierra la app → Cookie persiste en SharedPreferences
4. Usuario abre la app → `loadCookies()` restaura las cookies
5. App hace peticiones → `loadForRequest()` envía cookies al servidor
6. Usuario hace logout → `clearCookies()` elimina todas las cookies

---

## 2️⃣ CÓDIGO DE CONEXIÓN A SERVIDOR (NETWORKING)

### 📍 **ApiConfig.kt** - Configuración Base de la API
**Ubicación:** `/app/src/main/java/com/example/finsur/core/config/ApiConfig.kt`

```kotlin
package com.example.finsur.core.config

/**
 * Configuración centralizada de la API
 *
 * Este objeto contiene todas las constantes de configuración para las
 * conexiones de red de la aplicación.
 */
object ApiConfig {
    /**
     * URL base del servidor de la API
     *
     * - Para desarrollo local con emulador: http://10.0.2.2:3000/api/v1/
     * - Para dispositivo físico: http://TU_IP_LOCAL:3000/api/v1/
     * - Para producción: https://api.finsur.com/api/v1/
     *
     * Actualmente usando ngrok para exponer servidor local a internet
     */
    const val BASE_URL = "https://9bc8bec0dc98.ngrok-free.app/api/v1/"

    /**
     * Timeouts de conexión (en segundos)
     *
     * - CONNECT_TIMEOUT: Tiempo máximo para establecer conexión
     * - READ_TIMEOUT: Tiempo máximo para leer respuesta del servidor
     * - WRITE_TIMEOUT: Tiempo máximo para enviar datos al servidor
     */
    const val CONNECT_TIMEOUT = 30L  // 30 segundos
    const val READ_TIMEOUT = 30L     // 30 segundos
    const val WRITE_TIMEOUT = 30L    // 30 segundos
}
```

---

### 📍 **NetworkModule.kt** - Inyección de Dependencias para Networking
**Ubicación:** `/app/src/main/java/com/example/finsur/core/network/NetworkModule.kt`

**Propósito:** Configura y proporciona todas las dependencias de red usando Hilt (Dagger)

```kotlin
package com.example.finsur.core.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Dagger Hilt para inyección de dependencias de red
 *
 * @Module: Indica que esta clase proporciona dependencias
 * @InstallIn(SingletonComponent): Las dependencias viven durante toda la app
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Proporciona configurador JSON para serialización/deserialización
     *
     * Kotlinx Serialization convierte automáticamente entre:
     * - Objetos Kotlin ↔ JSON
     *
     * @Singleton: Solo se crea una instancia en toda la app
     */
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true  // Ignora campos JSON desconocidos
        isLenient = true           // Permite JSON no estricto
        encodeDefaults = true      // Incluye valores por defecto al serializar
    }

    /**
     * Proporciona CookieJar para manejar cookies HTTP
     *
     * Permite persistir cookies de sesión entre peticiones
     * y entre ejecuciones de la app
     */
    @Provides
    @Singleton
    fun provideCookieJar(@ApplicationContext context: Context): CookieJarImpl {
        return CookieJarImpl(context)
    }

    /**
     * Proporciona AuthInterceptor para manejar autenticación
     *
     * Intercepta respuestas 401 (Unauthorized) y limpia estado de auth
     */
    @Provides
    @Singleton
    fun provideAuthInterceptor(
        authStateManager: com.example.finsur.core.auth.AuthStateManager
    ): AuthInterceptor {
        return AuthInterceptor(authStateManager)
    }

    /**
     * Proporciona OkHttpClient configurado
     *
     * OkHttpClient es el motor HTTP que ejecuta las peticiones
     * Configuraciones:
     * - CookieJar: Maneja cookies automáticamente
     * - AuthInterceptor: Detecta errores de autenticación
     * - LoggingInterceptor: Registra todas las peticiones (para debugging)
     * - Timeouts: Define tiempos máximos de espera
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        cookieJar: CookieJarImpl,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {

        // Interceptor para logging - registra BODY completo de peticiones
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .cookieJar(cookieJar)                // Habilita manejo de cookies
            .addInterceptor(authInterceptor)     // Intercepta respuestas para auth
            .addInterceptor(loggingInterceptor)  // Logging para desarrollo
            .connectTimeout(ApiConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(ApiConfig.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(ApiConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Proporciona Retrofit - Cliente HTTP de alto nivel
     *
     * Retrofit convierte interfaces Java/Kotlin en llamadas HTTP:
     * - Define endpoints con anotaciones (@GET, @POST, etc.)
     * - Serializa/deserializa automáticamente con Kotlinx Serialization
     * - Maneja coroutines (suspend functions)
     *
     * ESTABLECIMIENTO DE CONEXIÓN AL SERVIDOR:
     * Aquí se configura la URL base y el cliente HTTP
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)  // ← URL DEL SERVIDOR
            .client(okHttpClient)          // ← Cliente HTTP configurado
            .addConverterFactory(           // ← Conversor JSON
                json.asConverterFactory(contentType)
            )
            .build()
    }

    /**
     * Proporciona servicios API específicos
     *
     * Cada servicio define los endpoints de un módulo:
     * - AuthApiService: Login, registro, perfil
     * - ProductsApiService: Productos, búsqueda
     * - CartApiService: Carrito de compras
     * - etc.
     *
     * Retrofit crea implementaciones automáticamente
     */

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideProductsApiService(retrofit: Retrofit): ProductsApiService {
        return retrofit.create(ProductsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCartApiService(retrofit: Retrofit): CartApiService {
        return retrofit.create(CartApiService::class.java)
    }

    // ... más servicios API
}
```

**🔌 Flujo de conexión al servidor:**
```
1. App inicia → Hilt crea NetworkModule
2. NetworkModule crea Retrofit con BASE_URL
3. Retrofit crea servicios API (CartApiService, AuthApiService, etc.)
4. Repositorios usan servicios para hacer peticiones
5. OkHttpClient ejecuta peticiones HTTP
6. CookieJar añade cookies automáticamente
7. AuthInterceptor monitorea respuestas 401
8. LoggingInterceptor registra todo en Logcat
```

---

### 📍 **AuthInterceptor.kt** - Interceptor de Autenticación
**Ubicación:** `/app/src/main/java/com/example/finsur/core/network/AuthInterceptor.kt`

```kotlin
package com.example.finsur.core.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor HTTP que detecta errores de autenticación
 *
 * Un Interceptor permite "interceptar" peticiones y respuestas HTTP
 * para procesarlas antes de que lleguen a la app.
 *
 * Este interceptor específicamente:
 * - Monitorea códigos de respuesta HTTP 401 (Unauthorized)
 * - Limpia el estado de autenticación si detecta 401
 * - Permite que los repositorios manejen la navegación al login
 */
class AuthInterceptor(
    private val authStateManager: AuthStateManager
) : Interceptor {

    /**
     * Intercepta cada petición y respuesta HTTP
     *
     * @param chain Cadena de interceptores de OkHttp
     * @return Respuesta HTTP (posiblemente modificada)
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        // Obtener la petición original
        val request = chain.request()

        // Proceder con la petición y obtener respuesta
        val response = chain.proceed(request)

        /**
         * DETECCIÓN DE SESIÓN EXPIRADA
         *
         * HTTP 401 Unauthorized indica:
         * - La cookie de sesión expiró
         * - La cookie es inválida
         * - El usuario fue desautorizado en el servidor
         *
         * Acción: Limpiar estado de autenticación local
         */
        if (response.code == 401) {
            Log.w("AuthInterceptor", "401 Unauthorized response from ${request.url}")
            Log.w("AuthInterceptor", "Session cookie expired or invalid")

            /**
             * Limpiar estado de autenticación
             *
             * Esto marca al usuario como no autenticado localmente,
             * forzando un nuevo login
             */
            authStateManager.clearAuthentication()

            /**
             * NOTA IMPORTANTE:
             * No redirigimos aquí porque los interceptors corren en
             * hilos de fondo (background threads).
             *
             * Los repositorios detectarán el error 401 y la UI
             * navegará a la pantalla de login en el hilo principal.
             */
        }

        return response
    }
}
```

**🔐 Flujo de manejo de autenticación:**
```
1. Usuario autenticado hace petición
2. OkHttpClient añade cookie de sesión
3. Servidor responde:
   - 200 OK → Todo bien, procesar respuesta
   - 401 Unauthorized → Sesión inválida/expirada
4. AuthInterceptor detecta 401
5. Limpia AuthStateManager (usuario ya no autenticado)
6. Response llega a Repository con código 401
7. Repository retorna Result.Error
8. ViewModel detecta error
9. UI navega a pantalla de Login
```

---

### 📍 **CartApiService.kt** - Definición de Endpoints del Carrito
**Ubicación:** `/app/src/main/java/com/example/finsur/data/cart/remote/CartApiService.kt`

```kotlin
package com.example.finsur.data.cart.remote

import retrofit2.Response
import retrofit2.http.*

/**
 * Servicio API para operaciones del carrito de compras
 *
 * Esta interfaz define los endpoints HTTP usando anotaciones de Retrofit.
 * Retrofit genera automáticamente la implementación.
 *
 * Todas las funciones son 'suspend' para usarse con coroutines
 * (ejecución asíncrona sin bloquear el hilo principal)
 */
interface CartApiService {

    /**
     * Obtener el carrito actual del usuario
     *
     * ENDPOINT: GET /api/v1/carts/current
     *
     * El servidor identifica al usuario mediante la cookie de sesión
     * (enviada automáticamente por CookieJar)
     *
     * @return Response con CartDto o error
     */
    @GET("carts/current")
    suspend fun getCurrentCart(): Response<CartDto>

    /**
     * Agregar un producto al carrito
     *
     * ENDPOINT: POST /api/v1/carts/items
     *
     * @Body: El objeto request se serializa a JSON automáticamente
     *
     * @param request Datos del producto a agregar (productId, skuId, quantity, price)
     * @return Response con CartResponse (carrito actualizado)
     */
    @POST("carts/items")
    suspend fun addItemToCart(
        @Body request: AddCartItemRequest
    ): Response<CartResponse>

    /**
     * Actualizar la cantidad de un item en el carrito
     *
     * ENDPOINT: PUT /api/v1/carts/items/{itemId}
     *
     * @Path("itemId"): Sustituye {itemId} en la URL con el valor del parámetro
     * Ejemplo: itemId=5 → PUT /api/v1/carts/items/5
     *
     * @param itemId ID del item del carrito a actualizar
     * @param request Nueva cantidad
     * @return Response con CartResponse (carrito actualizado)
     */
    @PUT("carts/items/{itemId}")
    suspend fun updateCartItemQuantity(
        @Path("itemId") itemId: Int,
        @Body request: UpdateCartItemRequest
    ): Response<CartResponse>

    /**
     * Eliminar un item del carrito
     *
     * ENDPOINT: DELETE /api/v1/carts/items/{itemId}
     *
     * @param itemId ID del item a eliminar
     * @return Response con CartResponse (carrito actualizado)
     */
    @DELETE("carts/items/{itemId}")
    suspend fun removeCartItem(
        @Path("itemId") itemId: Int
    ): Response<CartResponse>
}
```

**📡 Ejemplo de petición HTTP generada por Retrofit:**
```http
POST https://9bc8bec0dc98.ngrok-free.app/api/v1/carts/items HTTP/1.1
Content-Type: application/json
Cookie: session_id=abc123xyz789

{
  "productId": 42,
  "skuId": 101,
  "quantity": 2,
  "unitPrice": "29.99"
}
```

---

## 3️⃣ CÓDIGO RELEVANTE - ARQUITECTURA CLEAN & MVVM

### 📍 **CartRepositoryImpl.kt** - Implementación del Patrón Repository
**Ubicación:** `/app/src/main/java/com/example/finsur/data/cart/repository/CartRepositoryImpl.kt`

**Propósito:** Implementa la interfaz del repositorio, llamando a la API y transformando DTOs a modelos de dominio

```kotlin
package com.example.finsur.data.cart.repository

import com.example.finsur.domain.cart.repository.Result
import javax.inject.Inject

/**
 * Implementación del patrón Repository para el carrito de compras
 *
 * PATRÓN REPOSITORY:
 * - Separa la lógica de negocio de las fuentes de datos
 * - La capa de dominio NO sabe si los datos vienen de API, BD local, etc.
 * - Facilita testing (se puede mockear el repositorio)
 * - Centraliza el manejo de errores
 *
 * @Inject: Hilt inyecta CartApiService automáticamente
 */
class CartRepositoryImpl @Inject constructor(
    private val cartApiService: CartApiService  // Servicio Retrofit inyectado
) : CartRepository {

    /**
     * Obtener el carrito actual del usuario
     *
     * FLUJO:
     * 1. Llamar a la API mediante cartApiService
     * 2. Verificar si la respuesta fue exitosa (código 2xx)
     * 3. Transformar CartDto (modelo de datos) a Cart (modelo de dominio)
     * 4. Envolver resultado en Result.Success o Result.Error
     * 5. Capturar excepciones de red (sin internet, timeout, etc.)
     *
     * @return Result<Cart> - Éxito con datos o Error con mensaje
     */
    override suspend fun getCurrentCart(): Result<Cart> {
        return try {
            // Ejecutar petición HTTP GET /carts/current
            val response = cartApiService.getCurrentCart()

            if (response.isSuccessful) {
                // Respuesta exitosa (código 200-299)
                val cartDto = response.body()

                if (cartDto != null) {
                    /**
                     * TRANSFORMACIÓN DTO → DOMAIN
                     *
                     * DTO (Data Transfer Object):
                     * - Modelo para transportar datos desde/hacia la API
                     * - Tiene anotaciones de serialización (@Serializable)
                     * - Puede tener campos nullables para JSON opcional
                     *
                     * Domain Model:
                     * - Modelo de negocio puro (sin dependencias Android/API)
                     * - Representa entidades del dominio
                     * - Usado por ViewModels y Use Cases
                     */
                    Result.Success(cartDto.toDomain())
                } else {
                    Result.Error(
                        Exception("No cart data"),
                        "No se encontró el carrito"
                    )
                }
            } else {
                // Respuesta con error del servidor (4xx, 5xx)
                Result.Error(
                    Exception("Failed to get cart"),
                    response.errorBody()?.string() ?: "Error al obtener el carrito"
                )
            }
        } catch (e: Exception) {
            /**
             * MANEJO DE EXCEPCIONES DE RED
             *
             * Posibles excepciones:
             * - SocketTimeoutException: Timeout de conexión
             * - UnknownHostException: Sin internet / DNS no resuelve
             * - IOException: Error de I/O general
             */
            Result.Error(
                e,
                "Error de conexión. Por favor, verifica tu conexión a internet."
            )
        }
    }

    /**
     * Agregar un producto al carrito
     *
     * OPERACIÓN CRUD: CREATE (insertar nuevo item)
     *
     * @param productId ID del producto
     * @param skuId ID del SKU (variante del producto)
     * @param quantity Cantidad a agregar
     * @param unitPrice Precio unitario
     * @return Result<Cart> - Carrito actualizado o error
     */
    override suspend fun addItemToCart(
        productId: Int,
        skuId: Int,
        quantity: Int,
        unitPrice: String
    ): Result<Cart> {
        return try {
            // Crear objeto de petición (será serializado a JSON)
            val request = AddCartItemRequest(
                productId = productId,
                skuId = skuId,
                quantity = quantity,
                unitPrice = unitPrice
            )

            // Ejecutar petición HTTP POST /carts/items
            val response = cartApiService.addItemToCart(request)

            if (response.isSuccessful) {
                val cartResponse = response.body()

                if (cartResponse != null) {
                    // Retornar carrito actualizado transformado a dominio
                    Result.Success(cartResponse.cart.toDomain())
                } else {
                    Result.Error(
                        Exception("No cart data"),
                        "Error al agregar el artículo al carrito"
                    )
                }
            } else {
                Result.Error(
                    Exception("Failed to add item to cart"),
                    response.errorBody()?.string() ?: "Error al agregar el artículo"
                )
            }
        } catch (e: Exception) {
            Result.Error(
                e,
                "Error de conexión. Por favor, verifica tu conexión a internet."
            )
        }
    }

    /**
     * Actualizar la cantidad de un item en el carrito
     *
     * OPERACIÓN CRUD: UPDATE (actualizar item existente)
     *
     * @param itemId ID del item del carrito
     * @param quantity Nueva cantidad
     * @return Result<Cart> - Carrito actualizado o error
     */
    override suspend fun updateCartItemQuantity(
        itemId: Int,
        quantity: Int
    ): Result<Cart> {
        return try {
            val request = UpdateCartItemRequest(quantity = quantity)

            // Ejecutar petición HTTP PUT /carts/items/{itemId}
            val response = cartApiService.updateCartItemQuantity(itemId, request)

            if (response.isSuccessful) {
                val cartResponse = response.body()
                if (cartResponse != null) {
                    Result.Success(cartResponse.cart.toDomain())
                } else {
                    Result.Error(
                        Exception("No cart data"),
                        "Error al actualizar la cantidad"
                    )
                }
            } else {
                Result.Error(
                    Exception("Failed to update cart item"),
                    response.errorBody()?.string() ?: "Error al actualizar la cantidad"
                )
            }
        } catch (e: Exception) {
            Result.Error(
                e,
                "Error de conexión. Por favor, verifica tu conexión a internet."
            )
        }
    }

    /**
     * Eliminar un item del carrito
     *
     * OPERACIÓN CRUD: DELETE (eliminar item)
     *
     * @param itemId ID del item a eliminar
     * @return Result<Cart> - Carrito actualizado o error
     */
    override suspend fun removeCartItem(itemId: Int): Result<Cart> {
        return try {
            // Ejecutar petición HTTP DELETE /carts/items/{itemId}
            val response = cartApiService.removeCartItem(itemId)

            if (response.isSuccessful) {
                val cartResponse = response.body()
                if (cartResponse != null) {
                    Result.Success(cartResponse.cart.toDomain())
                } else {
                    Result.Error(
                        Exception("No cart data"),
                        "Error al eliminar el artículo"
                    )
                }
            } else {
                Result.Error(
                    Exception("Failed to remove cart item"),
                    response.errorBody()?.string() ?: "Error al eliminar el artículo"
                )
            }
        } catch (e: Exception) {
            Result.Error(
                e,
                "Error de conexión. Por favor, verifica tu conexión a internet."
            )
        }
    }

    /**
     * FUNCIÓN MAPPER - Transformar CartDto a Cart (Domain)
     *
     * Esta función de extensión convierte el DTO de la capa de datos
     * al modelo de dominio.
     *
     * Beneficios:
     * - La capa de dominio no depende de la API
     * - Se pueden agregar validaciones o transformaciones
     * - Facilita cambios en la API sin afectar la lógica de negocio
     */
    private fun CartDto.toDomain(): Cart {
        return Cart(
            id = id,
            userId = userId,
            sessionId = sessionId,
            subtotal = subtotal,
            taxAmount = taxAmount,
            discountAmount = discountAmount,
            shippingAmount = shippingAmount,
            total = total,
            couponCode = couponCode,
            items = items?.map { it.toDomain() } ?: emptyList(),
            createdAt = createdAt,
            updatedAt = updatedAt,
            expiresAt = expiresAt
        )
    }

    /**
     * Mapper para CartItemDto → CartItem
     */
    private fun CartItemDto.toDomain(): CartItem {
        return CartItem(
            id = id,
            productId = productId,
            skuId = skuId,
            quantity = quantity,
            unitPrice = unitPrice,
            product = product.toDomain()
        )
    }

    /**
     * Mapper para CartProductDto → CartProduct
     */
    private fun CartProductDto.toDomain(): CartProduct {
        return CartProduct(
            id = id,
            name = name,
            cover = cover,
            slug = slug
        )
    }
}
```

**🏗️ Arquitectura del Repository:**
```
┌─────────────────────┐
│   Presentation      │  (ViewModel llama UseCase)
└──────────┬──────────┘
           │
┌──────────▼──────────┐
│   Domain/UseCase    │  (AddItemToCartUseCase)
└──────────┬──────────┘
           │
┌──────────▼──────────┐
│ Domain/Repository   │  (CartRepository interface)
│    (Interface)      │
└──────────┬──────────┘
           │
┌──────────▼──────────┐
│ Data/Repository     │  (CartRepositoryImpl)
│  (Implementation)   │  ← ESTE ARCHIVO
└──────────┬──────────┘
           │
┌──────────▼──────────┐
│   CartApiService    │  (Retrofit interface)
└──────────┬──────────┘
           │
┌──────────▼──────────┐
│   OkHttpClient      │  (Ejecuta peticiones HTTP)
└──────────┬──────────┘
           │
           ▼
      🌐 SERVIDOR API
```

---

### 📍 **AddItemToCartUseCase.kt** - Patrón Use Case
**Ubicación:** `/app/src/main/java/com/example/finsur/domain/cart/usecases/AddItemToCartUseCase.kt`

```kotlin
package com.example.finsur.domain.cart.usecases

import com.example.finsur.domain.cart.repository.Result
import javax.inject.Inject

/**
 * Use Case para agregar un item al carrito
 *
 * PATRÓN USE CASE:
 * - Encapsula una única acción de negocio
 * - Contiene lógica de negocio y validaciones
 * - Orquesta llamadas a repositorios
 * - Facilita reutilización de lógica
 * - Hace el código más testeable
 *
 * VENTAJAS:
 * 1. Single Responsibility Principle (una sola responsabilidad)
 * 2. Separación de concerns (lógica de negocio vs UI)
 * 3. Testing sencillo (mockear repositorio)
 * 4. Reutilizable desde múltiples ViewModels
 *
 * @Inject: Hilt inyecta el repositorio automáticamente
 */
class AddItemToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository  // Repositorio inyectado
) {
    /**
     * Ejecutar el use case
     *
     * El operador 'invoke' permite llamar a la clase como función:
     * useCase(productId, skuId, quantity, unitPrice)
     *
     * @param productId ID del producto a agregar
     * @param skuId ID del SKU (variante) del producto
     * @param quantity Cantidad a agregar
     * @param unitPrice Precio unitario
     * @return Result<Cart> - Éxito con carrito actualizado o Error
     */
    suspend operator fun invoke(
        productId: Int,
        skuId: Int,
        quantity: Int,
        unitPrice: String
    ): Result<Cart> {

        /**
         * VALIDACIÓN DE NEGOCIO
         *
         * Los Use Cases son el lugar ideal para validaciones de negocio.
         * Aquí validamos que la cantidad sea positiva antes de llamar a la API.
         *
         * Esto previene peticiones innecesarias al servidor y
         * proporciona feedback inmediato al usuario.
         */
        if (quantity <= 0) {
            return Result.Error(
                Exception("Invalid quantity"),
                "La cantidad debe ser mayor a 0"
            )
        }

        /**
         * Delegar la operación al repositorio
         *
         * El use case NO sabe:
         * - Si los datos vienen de una API o base de datos local
         * - Cómo se serializa la petición
         * - Cómo se manejan los errores de red
         *
         * Solo sabe que el repositorio puede agregar items al carrito
         */
        return cartRepository.addItemToCart(productId, skuId, quantity, unitPrice)
    }
}
```

**📊 Flujo completo: UI → Use Case → Repository → API**
```
1. Usuario toca botón "Agregar al carrito"
   ↓
2. CartScreen llama a CartViewModel.addToCart()
   ↓
3. CartViewModel llama a AddItemToCartUseCase(productId, skuId, qty, price)
   ↓
4. UseCase valida quantity > 0
   ↓
5. UseCase llama a cartRepository.addItemToCart()
   ↓
6. CartRepositoryImpl crea AddCartItemRequest
   ↓
7. Repository llama a cartApiService.addItemToCart(request)
   ↓
8. Retrofit serializa request a JSON
   ↓
9. OkHttpClient ejecuta POST /carts/items con JSON
   ↓
10. CookieJar añade cookie de sesión automáticamente
   ↓
11. Servidor procesa, agrega item, retorna carrito actualizado
   ↓
12. OkHttp recibe respuesta JSON
   ↓
13. Retrofit deserializa JSON a CartResponse
   ↓
14. Repository transforma CartDto a Cart (domain)
   ↓
15. Repository envuelve en Result.Success(cart)
   ↓
16. UseCase retorna Result.Success(cart)
   ↓
17. ViewModel actualiza UI state con nuevo carrito
   ↓
18. Compose recompone la UI mostrando item agregado
```

---

## 📚 RESUMEN DEL PROYECTO

### **Patrón de Arquitectura: Clean Architecture + MVVM**

```
┌─────────────────────────────────────────────────────┐
│                 PRESENTATION LAYER                   │
│  - Jetpack Compose UI                               │
│  - ViewModels                                        │
│  - UI States                                         │
│  - Navigation                                        │
└───────────────────┬─────────────────────────────────┘
                    │ Calls
┌───────────────────▼─────────────────────────────────┐
│                  DOMAIN LAYER                        │
│  - Use Cases (business logic)                       │
│  - Repository Interfaces                            │
│  - Domain Models (entities)                         │
│  - Result wrapper                                   │
└───────────────────┬─────────────────────────────────┘
                    │ Implements
┌───────────────────▼─────────────────────────────────┐
│                   DATA LAYER                         │
│  - Repository Implementations                       │
│  - API Services (Retrofit)                          │
│  - DTOs (Data Transfer Objects)                     │
│  - Mappers (DTO → Domain)                          │
└───────────────────┬─────────────────────────────────┘
                    │ Uses
┌───────────────────▼─────────────────────────────────┐
│                   CORE LAYER                         │
│  - NetworkModule (Retrofit, OkHttp)                 │
│  - AuthStateManager (SharedPreferences)             │
│  - CookieJar (Cookie persistence)                   │
│  - Interceptors                                      │
└─────────────────────────────────────────────────────┘
                    │ Connects to
                    ▼
               🌐 API SERVER
```

### **Tecnologías Utilizadas:**

1. **Networking:** Retrofit + OkHttp + Kotlinx Serialization
2. **Dependency Injection:** Hilt (Dagger)
3. **UI:** Jetpack Compose + Material 3
4. **Arquitectura:** Clean Architecture + MVVM
5. **Async:** Kotlin Coroutines + StateFlow
6. **Almacenamiento:** SharedPreferences
7. **Autenticación:** Session cookies + Google Sign-In
8. **Pagos:** Stripe SDK

### **Ubicaciones Clave del Código:**

| Componente | Ubicación |
|-----------|-----------|
| **Base de datos (SharedPreferences)** | `/app/src/main/java/com/example/finsur/core/auth/AuthStateManager.kt` |
| **Cookies (Persistencia de sesión)** | `/app/src/main/java/com/example/finsur/core/network/CookieJarImpl.kt` |
| **Configuración de API** | `/app/src/main/java/com/example/finsur/core/config/ApiConfig.kt` |
| **Conexión al servidor** | `/app/src/main/java/com/example/finsur/core/network/NetworkModule.kt` |
| **Interceptor de autenticación** | `/app/src/main/java/com/example/finsur/core/network/AuthInterceptor.kt` |
| **Servicios API (Endpoints)** | `/app/src/main/java/com/example/finsur/data/*/remote/*ApiService.kt` |
| **Repositorios** | `/app/src/main/java/com/example/finsur/data/*/repository/*RepositoryImpl.kt` |
| **Use Cases (Lógica de negocio)** | `/app/src/main/java/com/example/finsur/domain/*/usecases/*.kt` |
| **ViewModels** | `/app/src/main/java/com/example/finsur/presentation/*/viewmodel/*.kt` |
| **Pantallas UI** | `/app/src/main/java/com/example/finsur/presentation/*/*.kt` |

### **Módulos de Funcionalidad:**

El proyecto está organizado en **9 módulos de características**:

1. **auth** - Autenticación (login, registro, Google Sign-In)
2. **products** - Catálogo de productos
3. **categories** - Categorías de productos
4. **brands** - Marcas
5. **cart** - Carrito de compras
6. **checkout** - Proceso de pago (Stripe)
7. **orders** - Historial de pedidos
8. **profile** - Perfil de usuario, direcciones, datos fiscales
9. **common** - Utilidades compartidas

### **Operaciones CRUD Implementadas:**

#### **Autenticación (SharedPreferences)**
- **CREATE/UPDATE**: `setAuthenticated()` - Guardar credenciales
- **READ**: `isUserAuthenticated()`, `getUserId()`, `getUserEmail()`
- **DELETE**: `clearAuthentication()` - Cerrar sesión

#### **Cookies (SharedPreferences)**
- **CREATE/UPDATE**: `saveFromResponse()` - Guardar cookies del servidor
- **READ**: `loadForRequest()` - Cargar cookies para peticiones
- **DELETE**: `clearCookies()` - Eliminar todas las cookies

#### **Carrito (API REST)**
- **CREATE**: `addItemToCart()` - POST /carts/items
- **READ**: `getCurrentCart()` - GET /carts/current
- **UPDATE**: `updateCartItemQuantity()` - PUT /carts/items/{id}
- **DELETE**: `removeCartItem()` - DELETE /carts/items/{id}

#### **Otros Módulos (API REST)**
Similar estructura CRUD para:
- Productos
- Órdenes
- Perfil de usuario
- Direcciones
- Datos fiscales

---

## 🎯 CONCLUSIÓN

Este proyecto demuestra:

✅ **Arquitectura limpia y escalable** con separación clara de responsabilidades
✅ **Patrones de diseño modernos** (Repository, Use Case, MVVM)
✅ **Inyección de dependencias** con Hilt para código testeable
✅ **Manejo robusto de red** con Retrofit, OkHttp, y manejo de errores
✅ **Persistencia local** con SharedPreferences para auth y cookies
✅ **UI moderna** con Jetpack Compose
✅ **Programación asíncrona** con Kotlin Coroutines
✅ **Integración con servicios externos** (Google Sign-In, Stripe)

El código está bien estructurado, documentado y sigue las mejores prácticas de desarrollo Android moderno.
