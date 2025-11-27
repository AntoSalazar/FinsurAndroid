package com.example.finsur.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.finsur.presentation.home.components.BrandItem
import com.example.finsur.presentation.home.components.CatalogDownloadSection
import com.example.finsur.presentation.home.components.CategoryCard
import com.example.finsur.presentation.home.components.HeroSection
import com.example.finsur.presentation.home.components.ProductCard
import com.example.finsur.presentation.home.components.ProductSearchBar
import com.example.finsur.presentation.home.viewmodel.BrandsState
import com.example.finsur.presentation.home.viewmodel.CategoriesState
import com.example.finsur.presentation.home.viewmodel.FeaturedProductsState
import com.example.finsur.presentation.home.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onNavigateToProductDetail: (productId: Int) -> Unit = {},
    onNavigateToCategoryProducts: (categorySlug: String) -> Unit = {},
    onNavigateToBrandProducts: (brandSlug: String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val featuredProductsState by viewModel.featuredProductsState.collectAsState()
    val categoriesState by viewModel.categoriesState.collectAsState()
    val brandsState by viewModel.brandsState.collectAsState()
    val searchState by viewModel.searchState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
            // Hero Section
            HeroSection()

            Spacer(modifier = Modifier.height(24.dp))

            // Search Bar
            ProductSearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                searchState = searchState,
                onProductClick = { product ->
                    viewModel.clearSearch()
                    onNavigateToProductDetail(product.id)
                },
                onClearSearch = { viewModel.clearSearch() },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Categories Section
            SectionTitle(
                title = "Categorías",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (categoriesState) {
                is CategoriesState.Loading -> {
                    LoadingIndicator()
                }
                is CategoriesState.Success -> {
                    val categories = (categoriesState as CategoriesState.Success).categories
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(categories) { category ->
                            CategoryCard(
                                category = category,
                                onClick = { onNavigateToCategoryProducts(category.slug) },
                                modifier = Modifier.width(180.dp)
                            )
                        }
                    }
                }
                is CategoriesState.Error -> {
                    ErrorMessage(
                        message = (categoriesState as CategoriesState.Error).message,
                        onRetry = { viewModel.retry() }
                    )
                }
                CategoriesState.Initial -> {}
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Featured Products Section
            SectionTitle(
                title = "Productos Destacados",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (featuredProductsState) {
                is FeaturedProductsState.Loading -> {
                    LoadingIndicator()
                }
                is FeaturedProductsState.Success -> {
                    val products = (featuredProductsState as FeaturedProductsState.Success).products
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(products) { product ->
                            ProductCard(
                                product = product,
                                onClick = { onNavigateToProductDetail(product.id) }
                            )
                        }
                    }
                }
                is FeaturedProductsState.Error -> {
                    ErrorMessage(
                        message = (featuredProductsState as FeaturedProductsState.Error).message,
                        onRetry = { viewModel.retry() }
                    )
                }
                FeaturedProductsState.Initial -> {}
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Catalog Download Section
            CatalogDownloadSection(
                onDownloadClick = {
                    // TODO: Implement catalog download
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Brands Carousel Section
            SectionTitle(
                title = "Nuestras Marcas",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (brandsState) {
                is BrandsState.Loading -> {
                    LoadingIndicator()
                }
                is BrandsState.Success -> {
                    val brands = (brandsState as BrandsState.Success).brands
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(brands) { brand ->
                            BrandItem(
                                brand = brand,
                                onClick = { onNavigateToBrandProducts(brand.slug) }
                            )
                        }
                    }
                }
                is BrandsState.Error -> {
                    ErrorMessage(
                        message = (brandsState as BrandsState.Error).message,
                        onRetry = { viewModel.retry() }
                    )
                }
                BrandsState.Initial -> {}
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
}

@Composable
private fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
    )
}

@Composable
private fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.material3.TextButton(onClick = onRetry) {
                Text("Reintentar")
            }
        }
    }
}
