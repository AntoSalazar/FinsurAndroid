# FinsurAndroid

Finsur is a modern Android e-commerce application built with Jetpack Compose and Clean Architecture principles.

## Features

- **Product Catalog**: Browse products with pagination and search functionality
- **Product Details**: View detailed product information with beautiful UX
- **Search**: Real-time product search with instant results
- **Authentication**: Secure login and registration
- **Shopping Cart**: Add products to cart (placeholder for now)
- **User Profile**: Manage user account and preferences

## Architecture

This project follows **Clean Architecture** with clear separation of concerns:

- **Presentation Layer**: Jetpack Compose UI, ViewModels
- **Domain Layer**: Business logic, Use Cases, Repository Interfaces
- **Data Layer**: Repository Implementations, API Services, DTOs
- **Core Layer**: Dependency Injection, Network Configuration

## Tech Stack

- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern UI toolkit
- **Hilt**: Dependency injection
- **Retrofit**: Network requests
- **Coil**: Image loading
- **Kotlin Coroutines & Flow**: Asynchronous programming
- **Material 3**: Design system

## Setup

1. Clone the repository
2. Open in Android Studio
3. **Important:** This application currently does not have a deployed server-side backend. To run it, you will need to contact me to get the backend running via ngrok on my laptop.
4. Update the base URL in `ApiConfig.kt` to point to your backend
5. Build and run

## Project Structure

```
app/src/main/java/com/example/finsur/
├── core/                 # Core utilities & DI
├── data/                 # Data layer (DTOs, API services, repositories)
├── domain/               # Domain layer (models, use cases, repository interfaces)
├── presentation/         # Presentation layer (UI, ViewModels)
└── ui/theme/            # App theme
```

## Contributing

This is a private project. Please contact the team for contribution guidelines.

## License

Proprietary - All rights reserved
