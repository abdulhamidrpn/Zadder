package com.rpn.zadder.presentation.component

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rpn.zadder.domain.model.UnsplashImage

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ImageCard(
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    image: UnsplashImage?,
    isFavorite: Boolean,
    onToggleFavoriteStatus: () -> Unit
) {
    val imageRequest = ImageRequest.Builder(LocalContext.current)
        .data(image?.imageUrlSmall)
        .crossfade(true)
        .build()
    val aspectRatio: Float by remember {
        derivedStateOf { (image?.width?.toFloat() ?: 1f) / (image?.height?.toFloat() ?: 1f) }
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .then(modifier)
    ) {
        Box {
            AsyncImage(
                model = imageRequest,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
                    .sharedElement(
                        state = rememberSharedContentState(
                            key = "image-${image?.imageUrlSmall}"
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                    ),
            )
            FavoriteButton(
                isFavorite = isFavorite,
                onClick = onToggleFavoriteStatus,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
fun FavoriteButton(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onClick: () -> Unit
) {

    FilledIconToggleButton(
        checked = isFavorite,
        onCheckedChange = { onClick() },
        modifier = modifier,
        colors = IconButtonDefaults.iconToggleButtonColors(
            containerColor = Color.Black.copy(alpha = 0.1f),
            contentColor = Color.White
        )
    ) {
        if (isFavorite) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                tint = Color.Red,
                contentDescription = "Favorite"
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                tint = Color.White,
                contentDescription = "Add to Favorite"
            )
        }
    }
}