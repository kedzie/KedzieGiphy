package com.kedzie.giphy.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage

@Composable
fun DetailScreen(url: String, imageLoader: ImageLoader) {
    SubcomposeAsyncImage(
        modifier = Modifier.fillMaxSize(),
        model = url,
        imageLoader = imageLoader,
        loading = {
            CircularProgressIndicator()
                  },
        contentDescription = "giphy image"
    )
}