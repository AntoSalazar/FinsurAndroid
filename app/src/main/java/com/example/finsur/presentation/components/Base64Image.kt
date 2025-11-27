package com.example.finsur.presentation.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun ImageFromUrl(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    alpha: Float = 1f
) {
    if (imageUrl == null) return

    // Check if it's a base64 image
    if (imageUrl.startsWith("data:image")) {
        // Decode base64 image outside of composable context
        val bitmap = remember(imageUrl) {
            decodeBase64Image(imageUrl)
        }

        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale,
                alpha = alpha
            )
        } else {
            // Fallback to AsyncImage if base64 decoding fails
            AsyncImage(
                model = imageUrl,
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale,
                alpha = alpha
            )
        }
    } else {
        // Regular URL image
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
            alpha = alpha
        )
    }
}

private fun decodeBase64Image(base64String: String): Bitmap? {
    return try {
        val base64Data = base64String.substringAfter("base64,")
        val imageBytes = Base64.decode(base64Data, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    } catch (e: Exception) {
        null
    }
}
