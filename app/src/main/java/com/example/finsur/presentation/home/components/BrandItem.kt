package com.example.finsur.presentation.home.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.finsur.domain.brands.models.Brand

@Composable
fun BrandItem(
    brand: Brand,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(120.dp, 80.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        brand.logoUrl?.let { logoUrl ->
            if (logoUrl.startsWith("data:image")) {
                // Handle base64 encoded images
                val bitmap = remember(logoUrl) {
                    decodeBase64Logo(logoUrl)
                }

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = brand.name,
                        modifier = Modifier.size(100.dp, 60.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    // Fallback to text if decoding fails
                    Text(
                        text = brand.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Handle regular URLs
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(logoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = brand.name,
                    modifier = Modifier.size(100.dp, 60.dp),
                    contentScale = ContentScale.Fit,
                    loading = {
                        Box(
                            modifier = Modifier.size(100.dp, 60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    },
                    error = {
                        // Fallback to text if image fails to load
                        Text(
                            text = brand.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                )
            }
        } ?: run {
            // Fallback to text if no logo
            Text(
                text = brand.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun decodeBase64Logo(base64String: String): Bitmap? {
    return try {
        val base64Data = base64String.substringAfter("base64,")
        val imageBytes = Base64.decode(base64Data, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    } catch (e: Exception) {
        null
    }
}
