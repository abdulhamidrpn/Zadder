package com.rpn.zadder.presentation.component

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.rpn.zadder.domain.model.UnsplashImage
import com.rpn.zadder.presentation.admob.AdmobBanner

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ImagesVerticalGrid(
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    images: LazyPagingItems<UnsplashImage>,
    favoriteImageIds: List<String>,
    onImageClick: (String) -> Unit,
    onImageDragStart: (UnsplashImage?) -> Unit,
    onImageDragEnd: () -> Unit,
    onToggleFavoriteStatus: (UnsplashImage) -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = images.loadState) {
        if (images.loadState.refresh is LoadState.Error && images.itemCount == 0) {
            images.retry()
            Toast.makeText(
                context,
                "Error: " + (images.loadState.refresh as LoadState.Error).error.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalStaggeredGrid(
            modifier = modifier,
            columns = StaggeredGridCells.Adaptive(160.dp),
            contentPadding = PaddingValues(10.dp),
            verticalItemSpacing = 10.dp,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            /*Show Banner ad after every 8 images*/
            /*val adViewIndex = 8
            val totalItems = images.itemCount + images.itemCount / adViewIndex // Account for ads
            items(
                count = totalItems
            ) { index ->
                if (index.plus(1) % adViewIndex.plus(1) == 0) { // Place ad after every 10 ImageCards
                    AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AdmobBanner(modifier = Modifier.fillMaxWidth())
                        }
                    }
                } else {
                    val imageIndex = index - index / adViewIndex.plus(1) // Adjust index for images
                    val image = images[imageIndex]
                    ImageCard(
                        animatedVisibilityScope = animatedVisibilityScope,
                        image = image,
                        modifier = Modifier
                            .clickable { image?.id?.let { onImageClick(it) } }
                            .pointerInput(Unit) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = { onImageDragStart(image) },
                                    onDragCancel = { onImageDragEnd() },
                                    onDragEnd = { onImageDragEnd() },
                                    onDrag = { _, _ -> }
                                )
                            },
                        onToggleFavoriteStatus = { image?.let { onToggleFavoriteStatus(it) } },
                        isFavorite = favoriteImageIds.contains(image?.id)
                    )
                }
            }*/

            item(){
                AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AdmobBanner(modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            items(
                count = images.itemCount,
                key = {
                    images[it]?.id ?: it
                }) { index ->
                val image = images[index]
                ImageCard(
                    animatedVisibilityScope = animatedVisibilityScope,
                    image = image,
                    modifier = Modifier
                        .clickable { image?.id?.let { onImageClick(it) } }
                        .pointerInput(Unit) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = { onImageDragStart(image) },
                                onDragCancel = { onImageDragEnd() },
                                onDragEnd = { onImageDragEnd() },
                                onDrag = { _, _ -> }
                            )
                        },
                    onToggleFavoriteStatus = { image?.let { onToggleFavoriteStatus(it) } },
                    isFavorite = favoriteImageIds.contains(image?.id)
                )
            }

            // Handling Pagination Errors
            item {
                if (images.loadState.append is LoadState.Error) {
                    val e = images.loadState.append as LoadState.Error
                    ErrorItem(
                        message = e.error.localizedMessage ?: "An error occurred",
                        modifier = Modifier.padding(16.dp)
                    ) {
                        images.retry()
                    }
                }
            }

            // Displaying loading indicator at the end
            item {
                if (images.loadState.append is LoadState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(36.dp)
                    )
                }
            }

        }

        if (images.loadState.refresh is LoadState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(36.dp)
            )
        }

        if (images.loadState.refresh is LoadState.Error) {
            val e = images.loadState.refresh as LoadState.Error
            ErrorItem(
                message = e.error.localizedMessage ?: "An error occurred",
                modifier = Modifier.align(Alignment.Center)
            ) {
                images.retry()
            }
        }
    }
}

@Composable
fun ErrorItem(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, color = Color.Red, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text(text = "Retry")
        }
    }
}


fun LazyPagingItems<Any>.retry() {
    when (loadState.refresh) {
        is LoadState.Error -> retry()
        is LoadState.NotLoading -> {
            if (loadState.append is LoadState.Error) retry()
        }

        else -> Unit
    }
}