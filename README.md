# FinsurAndroid

Finsur es una aplicación de comercio electrónico moderna para Android construida con Jetpack Compose y principios de Arquitectura Limpia.

## Características

- **Catálogo de Productos**: Navega por los productos con paginación y funcionalidad de búsqueda
- **Detalles del Producto**: Ve la información detallada del producto con una hermosa UX
- **Búsqueda**: Búsqueda de productos en tiempo real con resultados instantáneos
- **Autenticación**: Inicio de sesión y registro seguros
- **Carrito de Compras**: Agrega productos al carrito (marcador de posición por ahora)
- **Perfil de Usuario**: Administra la cuenta de usuario y las preferencias

## Arquitectura

Este proyecto sigue la **Arquitectura Limpia** con una clara separación de responsabilidades:

- **Capa de Presentación**: UI de Jetpack Compose, ViewModels
- **Capa de Dominio**: Lógica de negocio, Casos de Uso, Interfaces de Repositorio
- **Capa de Datos**: Implementaciones de Repositorio, Servicios de API, DTOs
- **Capa Núcleo**: Inyección de Dependencias, Configuración de Red

## Tecnologías

- **Kotlin**: Lenguaje de programación principal
- **Jetpack Compose**: Kit de herramientas de UI moderno
- **Hilt**: Inyección de dependencias
- **Retrofit**: Solicitudes de red
- **Coil**: Carga de imágenes
- **Corrutinas y Flow de Kotlin**: Programación asíncrona
- **Material 3**: Sistema de diseño

## Configuración

1. Clona el repositorio
2. Ábrelo en Android Studio
3. **Importante:** Actualmente, esta aplicación no tiene un backend de servidor desplegado. Para ejecutarla, deberás contactarme para que ejecute el backend a través de ngrok en mi computadora portátil.
4. Actualiza la URL base en `ApiConfig.kt` para que apunte a tu backend
5. Compila y ejecuta

## Estructura del Proyecto

```
app/src/main/java/com/example/finsur/
├── core/                 # Utilidades principales e inyección de dependencias (DI)
├── data/                 # Capa de datos (DTOs, servicios de API, repositorios)
├── domain/               # Capa de dominio (modelos, casos de uso, interfaces de repositorio)
├── presentation/         # Capa de presentación (UI, ViewModels)
└── ui/theme/            # Tema de la aplicación
```

## Contribuciones

Este es un proyecto privado. Por favor, contacta al equipo para obtener las pautas de contribución.

## Licencia

Propietario - Todos los derechos reservados